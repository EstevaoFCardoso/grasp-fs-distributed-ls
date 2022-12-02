package br.com.graspfs.ls.iwssr.service;

import br.com.graspfs.ls.iwssr.dto.DataSolution;
import br.com.graspfs.ls.iwssr.machinelearning.MachineLearning;
import br.com.graspfs.ls.iwssr.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.iwssr.util.LocalSearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

@Service
public class IwssrService {

    @Autowired
    KafkaSolutionsProducer kafkaSolutionsProducer;

    private static final Logger logg = LoggerFactory.getLogger(IwssrService.class);

    public void doIwssr(DataSolution data) throws Exception {

        DataSolution bestSolution;
        data.setLocalSearch(LocalSearchUtils.IWR);
        bestSolution = incrementalWrapperSequencialSearch(data);
        bestSolution.setIterationLocalSearch(data.getIterationLocalSearch()+1);
        bestSolution.setNeighborhood(data.getNeighborhood());
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        bestSolution.setIterationNeighborhood(data.getIterationNeighborhood());
        bestSolution.setLocalSearch(LocalSearchUtils.IWR);
        bestSolution.setSeedId(data.getSeedId());

        kafkaSolutionsProducer.send(bestSolution);

    }

    public static DataSolution incrementalWrapperSequencialSearch(DataSolution dataSolution) throws Exception {

        DataSolution bestSolution = updateSolution(dataSolution);

        var localSolutionAdd = updateSolution(dataSolution);
        var localSolutionReplace = updateSolution(dataSolution);

        for(int i = 1; i < localSolutionAdd.getRclfeatures().size(); i++){

            localSolutionAdd = updateSolution(addMovement(localSolutionAdd));
            localSolutionReplace = updateSolution(replaceMovement(localSolutionAdd));
            localSolutionReplace = replaceMovement(localSolutionReplace);

            if(localSolutionReplace.getF1Score() > bestSolution.getF1Score()){
                bestSolution = updateSolution(localSolutionReplace);
            }
        }
        System.out.println("#######################################");
        System.out.println("#######################################");
        logg.info("BESTSOLUTION FINAL:" + bestSolution.getF1Score());
        System.out.println("#######################################");
        System.out.println("#######################################");

        return bestSolution;
    }

    private static DataSolution addMovement(DataSolution solution) throws Exception {
        solution.getSolutionFeatures().add(solution.getRclfeatures().remove(0));
        float f1Score = MachineLearning.evaluateSolution(solution.getSolutionFeatures());
        solution.setF1Score(f1Score);
        return solution;
    }

    private static DataSolution replaceMovement(DataSolution solution) throws Exception {
        var bestReplace = updateSolution(solution);

        System.out.println("#######################################");
        logg.info("INITIAL SOLUTION :" + solution.getF1Score()+ " solution: " + solution.getSolutionFeatures());

        // criar arquivo para métrica
        BufferedWriter br = new BufferedWriter(new FileWriter("IWSSR_METRICS"+solution.getIterationLocalSearch().toString()));

        br.write("solutionFeatures;f1Score;neighborhood;iterationNeighborhood;localSearch;iterationLocalSearch;runnigTime");
        br.newLine();

        for(int i = 0; i<solution.getSolutionFeatures().size();i++){
            final var time = System.currentTimeMillis();
            System.out.println("solutionADD " + solution.getSolutionFeatures());

            var replacedSolution = updateSolution(solution);

            System.out.println("replacedSolution " + replacedSolution.getSolutionFeatures());

            replacedSolution.getSolutionFeatures().remove(i);

            System.out.println("solutionADD " + solution.getSolutionFeatures());
            System.out.println("replacedSolution " + replacedSolution.getSolutionFeatures());

            float f1Score = MachineLearning.evaluateSolution(replacedSolution.getSolutionFeatures());
            replacedSolution.setF1Score(f1Score);

            System.out.println("#######################################");
            logg.info("NEW SOLUTION :" + replacedSolution.getF1Score()+ " solution: " + replacedSolution.getSolutionFeatures());
            System.out.println("#######################################");

            replacedSolution.setRunnigTime(time);
            br.write(replacedSolution.getSolutionFeatures()+";"
                    +replacedSolution.getF1Score()+";"
                    +replacedSolution.getNeighborhood()+";"
                    +replacedSolution.getIterationNeighborhood()+";"
                    +replacedSolution.getLocalSearch()+";"
                    +replacedSolution.getIterationLocalSearch()+";"
                    +replacedSolution.getRunnigTime()
            );
            br.newLine();

            if(replacedSolution.getF1Score() > bestReplace.getF1Score()){
                System.out.println("#######################################");
                bestReplace = updateSolution(replacedSolution);
                logg.info("BESTSOLUTION :" + replacedSolution.getF1Score() + " solution: " + bestReplace.getSolutionFeatures());
                System.out.println("#######################################");
            }
        }
        br.close();
        return bestReplace;
    }

    private static DataSolution updateSolution(DataSolution solution){
        return DataSolution.builder()
                .seedId(solution.getSeedId())
                .rclfeatures(new ArrayList<>(solution.getRclfeatures()))
                .solutionFeatures(new ArrayList<>(solution.getSolutionFeatures()))
                .neighborhood(solution.getNeighborhood())
                .f1Score(solution.getF1Score())
                .runnigTime(solution.getRunnigTime())
                .iterationLocalSearch(solution.getIterationLocalSearch())
                .build();
    }

}
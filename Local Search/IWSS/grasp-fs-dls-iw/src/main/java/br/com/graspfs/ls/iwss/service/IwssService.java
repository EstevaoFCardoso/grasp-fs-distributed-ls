package br.com.graspfs.ls.iwss.service;

import br.com.graspfs.ls.iwss.dto.DataSolution;
import br.com.graspfs.ls.iwss.machinelearning.MachineLearning;
import br.com.graspfs.ls.iwss.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.iwss.util.LocalSearchUtils;
import br.com.graspfs.ls.iwss.util.PrintSolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

@Service
public class IwssService {

    @Autowired
    KafkaSolutionsProducer kafkaSolutionsProducer;
    private static BufferedWriter br;

    private boolean firstTime = true;
    public void doIwss(DataSolution seed) throws Exception {
        DataSolution bestSolution;
        DataSolution data = updateSolution(seed);
        data.setLocalSearch(LocalSearchUtils.IW);
        data.setNeighborhood(data.getNeighborhood());
        data.setIterationNeighborhood(data.getIterationNeighborhood());
        data.setLocalSearch(LocalSearchUtils.IW);
        data.setSeedId(data.getSeedId());
        br = new BufferedWriter(new FileWriter("IWSS_METRICS", true));
        if(firstTime) {
            br.write("solutionFeatures;f1Score;neighborhood;iterationNeighborhood;localSearch;iterationLocalSearch;runnigTime");
            br.newLine();
            firstTime = false;
        }
        bestSolution = incrementalWrapperSequencialSearch(data);
        br.close();
        bestSolution=updateSolution(resetDataSolution(seed, bestSolution));
        kafkaSolutionsProducer.send(bestSolution);

    }

    public DataSolution resetDataSolution(DataSolution seed, DataSolution data){
        int k = seed.getRclfeatures().size()+seed.getSolutionFeatures().size();
        data.setRclfeatures(new ArrayList<>());
        ArrayList<Integer> rclfeatures = new ArrayList<>();
        for (int i = 1; i <= k; i++){
            if (!checkFeatureinSolution(data, i)) {
                rclfeatures.add(i);
            }
        }
        data.setRclfeatures(rclfeatures);
        return data;
    }
    public boolean checkFeatureinSolution(DataSolution data, int feature){
        for (int featureInSolution : data.getSolutionFeatures()){
            if (featureInSolution==feature) {
                return true;
            }
        }
        return false;
    }

    public static DataSolution incrementalWrapperSequencialSearch(DataSolution dataSolution) throws Exception {
        DataSolution bestSolution = updateSolution(dataSolution);
        var localSolutionAdd = updateSolution(dataSolution);
        int n = localSolutionAdd.getRclfeatures().size();
        // Busca Sequencial
        for (int i = 0; i < n; i++) {
            localSolutionAdd.setIterationLocalSearch(i);
            localSolutionAdd = updateSolution(addMovement(localSolutionAdd));
            PrintSolution.logSolution(localSolutionAdd);

            if (localSolutionAdd.getF1Score() > bestSolution.getF1Score()) {
                bestSolution = updateSolution(localSolutionAdd);
            } else {
                System.out.println("Não houve melhoras!");
            }
        }
        br.close();
        return bestSolution;
    }

    private static DataSolution addMovement(DataSolution solution) throws Exception {
        solution.getSolutionFeatures().add(solution.getRclfeatures().remove(0));
        float f1Score = MachineLearning.evaluateSolution(solution.getSolutionFeatures());
        solution.setF1Score(f1Score);
        solution.setRunnigTime(System.currentTimeMillis());
        br.write(solution.getSolutionFeatures()+";"
                +solution.getF1Score()+";"
                +solution.getNeighborhood()+";"
                +solution.getIterationNeighborhood()+";"
                +solution.getLocalSearch()+";"
                +solution.getIterationLocalSearch()+";"
                +solution.getRunnigTime()
        );
        br.newLine();
        return solution;
    }

    private static DataSolution updateSolution(DataSolution solution){
        return DataSolution.builder()
                .seedId(solution.getSeedId())
                .rclfeatures(new ArrayList<>(solution.getRclfeatures()))
                .solutionFeatures(new ArrayList<>(solution.getSolutionFeatures()))
                .iterationNeighborhood(solution.getIterationNeighborhood())
                .classfier(solution.getClassfier())
                .neighborhood(solution.getNeighborhood())
                .f1Score(solution.getF1Score())
                .runnigTime(solution.getRunnigTime())
                .iterationLocalSearch(solution.getIterationLocalSearch())
                .localSearch(solution.getLocalSearch())
                .build();
    }
}
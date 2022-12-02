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

@Service
public class IwssService {

    @Autowired
    KafkaSolutionsProducer kafkaSolutionsProducer;

    public void doIwss(DataSolution data) throws Exception {

        DataSolution bestSolution;
        data.setLocalSearch(LocalSearchUtils.IW);
        bestSolution = incrementalWrapperSequencialSearch(data);
        bestSolution.setIterationLocalSearch(data.getIterationLocalSearch()+1);
        bestSolution.setNeighborhood(data.getNeighborhood());
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        bestSolution.setIterationNeighborhood(data.getIterationNeighborhood());
        bestSolution.setLocalSearch(LocalSearchUtils.IW);
        bestSolution.setSeedId(data.getSeedId());

        kafkaSolutionsProducer.send(bestSolution);

    }

    public static DataSolution incrementalWrapperSequencialSearch(DataSolution dataSolution) throws Exception {
        DataSolution bestSolution = updateSolution(dataSolution);

        // criar arquivo para métrica
        BufferedWriter br = new BufferedWriter(new FileWriter("IWSS_METRICS"+dataSolution.getIterationLocalSearch().toString()));

        br.write("solutionFeatures;f1Score;neighborhood;iterationNeighborhood;localSearch;iterationLocalSearch;runnigTime");
        br.newLine();

        // Busca Sequencial
        for (int i = 1; i < dataSolution.getRclfeatures().size(); i++) {
            final var time = System.currentTimeMillis();

            dataSolution = updateSolution(bestSolution);
            dataSolution.getSolutionFeatures().add(dataSolution.getRclfeatures().remove(0));

            float f1Score = MachineLearning.evaluateSolution(dataSolution.getSolutionFeatures());

            dataSolution.setF1Score(f1Score);
            dataSolution.setRunnigTime(time);
            br.write(dataSolution.getSolutionFeatures()+";"
                    +dataSolution.getF1Score()+";"
                    +dataSolution.getNeighborhood()+";"
                    +dataSolution.getIterationNeighborhood()+";"
                    +dataSolution.getLocalSearch()+";"
                    +dataSolution.getIterationLocalSearch()+";"
                    +dataSolution.getRunnigTime()
            );
            br.newLine();

            PrintSolution.logSolution(dataSolution);

            if (dataSolution.getF1Score() > bestSolution.getF1Score()) {
                bestSolution = updateSolution(dataSolution);
            } else {
                System.out.println("Não houve melhoras!");
            }
        }
        br.close();
        return bestSolution;
    }

    private static DataSolution updateSolution(DataSolution solution){
        return DataSolution.builder()
                .seedId(solution.getSeedId())
                .rclfeatures(solution.getRclfeatures())
                .solutionFeatures(solution.getSolutionFeatures())
                .neighborhood(solution.getNeighborhood())
                .f1Score(solution.getF1Score())
                .runnigTime(solution.getRunnigTime())
                .iterationLocalSearch(solution.getIterationLocalSearch())
                .build();

    }
}
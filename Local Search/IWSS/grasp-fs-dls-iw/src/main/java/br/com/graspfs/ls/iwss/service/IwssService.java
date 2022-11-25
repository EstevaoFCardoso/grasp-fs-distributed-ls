package br.com.graspfs.ls.iwss.service;

import br.com.graspfs.ls.iwss.dto.DataSolution;
import br.com.graspfs.ls.iwss.machinelearning.MachineLearning;
import br.com.graspfs.ls.iwss.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.iwss.util.LocalSearchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IwssService {

    @Autowired
    KafkaSolutionsProducer kafkaSolutionsProducer;

    public void doIwss(DataSolution data, Long time) throws Exception {

        DataSolution bestSolution;
        bestSolution = incrementalWrapperSequencialSearch(data,time);
        bestSolution.setLocalSearch(LocalSearchUtils.IW);
        bestSolution.setIterationLocalSearch(data.getIterationLocalSearch()+1);
        bestSolution.setNeighborhood(data.getNeighborhood());
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        bestSolution.setSeedId(data.getSeedId());

        kafkaSolutionsProducer.send(bestSolution);

    }

    public static DataSolution incrementalWrapperSequencialSearch(DataSolution dataSolution, Long time) throws Exception {
        DataSolution bestSolution = updateSolution(dataSolution);
        // Busca Sequencial
        for (int i = 1; i < dataSolution.getRclfeatures().size(); i++) {

            dataSolution = updateSolution(bestSolution);
            dataSolution.getSolutionFeatures().add(dataSolution.getRclfeatures().remove(0));

            float f1Score = MachineLearning.evaluateSolution(dataSolution.getSolutionFeatures());
            dataSolution.setF1Score(f1Score);

            if (dataSolution.getF1Score() > bestSolution.getF1Score()) {
                bestSolution = updateSolution(dataSolution);
            } else {
                System.out.println("Não houve melhoras!");
            }
        }
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

package br.com.graspfs.ls.iwssr.service;

import br.com.graspfs.ls.iwssr.dto.DataSolution;
import br.com.graspfs.ls.iwssr.machinelearning.MachineLearning;
import br.com.graspfs.ls.iwssr.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.iwssr.util.LocalSearchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class IwssrService {

    @Autowired
    KafkaSolutionsProducer kafkaSolutionsProducer;

    public void doIwssr(DataSolution data, Long time) throws Exception {

        DataSolution bestSolution;
        bestSolution = incrementalWrapperSequencialSearch(data,time);
        bestSolution.setLocalSearch(LocalSearchUtils.IWR);
        bestSolution.setIterationLocalSearch(data.getIterationLocalSearch()+1);
        bestSolution.setNeighborhood(data.getNeighborhood());
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        bestSolution.setSeedId(data.getSeedId());

        kafkaSolutionsProducer.send(bestSolution);

    }

    public static DataSolution incrementalWrapperSequencialSearch(DataSolution dataSolution, Long time) throws Exception {

        DataSolution bestSolution = updateSolution(dataSolution);

        var localSolutionAdd = updateSolution(dataSolution);
        var localSolutionReplace = updateSolution(dataSolution);
        //boolean firstMovement = true;

        for(int i = 1; i < localSolutionAdd.getRclfeatures().size(); i++){
            localSolutionAdd = updateSolution(addMovement(localSolutionAdd));
            localSolutionReplace = updateSolution(replaceMovement(localSolutionAdd));

            localSolutionReplace = replaceMovement(){

            }

        }

        return bestSolution;
    }

//    public static DataSolution incrementalWrapperSequencialSearch(DataSolution dataSolution, Long time) throws Exception {
//        DataSolution bestSolution = updateSolution(dataSolution);
//        var localSolution = updateSolution(dataSolution);
//        boolean firstMovement = true;
//
//        for (int i = 1; i < dataSolution.getRclfeatures().size(); i++) {
//            if(firstMovement){
//                // é adicionado um valor do rcl na minha solução
//                localSolution = updateSolution(addMovement(localSolution));
//
//                float f1Score = MachineLearning.evaluateSolution(localSolution.getSolutionFeatures());
//                // solução local é atualizada
//                localSolution.setF1Score(f1Score);
//                // caso a solução local seja maior do que a bestSolution atual a bestSolution é atualizada
//                if (localSolution.getF1Score() > bestSolution.getF1Score()) {
//                    bestSolution = updateSolution(localSolution);
//                } else {
//                    System.out.println("Não houve melhoras!");
//                }
//                // fim da primeira movimentação
//                firstMovement= false;
//            }else {
//                dataSolution = updateSolution(replaceMovement(dataSolution,localSolution));
//
//                float f1Score = MachineLearning.evaluateSolution(dataSolution.getSolutionFeatures());
//                dataSolution.setF1Score(f1Score);
//
//                if (dataSolution.getF1Score() > bestSolution.getF1Score()) {
//                    bestSolution = updateSolution(dataSolution);
//                } else {
//                    System.out.println("Não houve melhoras!");
//                }
//            }
//
//        }
//        return bestSolution;
//    }


    private static DataSolution addMovement(DataSolution solution) throws Exception {
        solution.getSolutionFeatures().add(solution.getRclfeatures().remove(0));
        float f1Score = MachineLearning.evaluateSolution(solution.getSolutionFeatures());
        solution.setF1Score(f1Score);
        return solution;
    }

    private static DataSolution replaceMovement(DataSolution solution) throws Exception {
        var bestReplace = updateSolution(solution);


        for(int i = 0; i<solution.getSolutionFeatures().size();i++){

            var replacedSolution = updateSolution(solution);

            replacedSolution.getSolutionFeatures().remove(i);

            float f1Score = MachineLearning.evaluateSolution(replacedSolution.getSolutionFeatures());
            replacedSolution.setF1Score(f1Score);

            if(replacedSolution.getF1Score() > bestReplace.getF1Score()){
                bestReplace = updateSolution(replacedSolution);
            }
        }
        return bestReplace;
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

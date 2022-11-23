package br.com.graspfs.ls.iwssr.service;

import br.com.graspfs.ls.iwssr.dto.DataSolution;
import br.com.graspfs.ls.iwssr.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.iwssr.util.LocalSearchUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class IwssrService {

    KafkaSolutionsProducer kafkaSolutionsProducer;

    public DataSolution cheangeOfFeatures(DataSolution data){
        var valueIndex = 0;
        int i = 0;
        DataSolution bestSolution= null;
        bestSolution.setF1Score(Float.valueOf(sumArray(data.getSolutionFeatures())));
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        do{
            Integer solution;
            data.getSolutionFeatures().add(data.getRclfeatures().get(0));
            data.getRclfeatures().remove(0);
            valueIndex = sumArray(data.getSolutionFeatures());
            if(data.getF1Score() < (float) valueIndex){
                bestSolution.setF1Score((float) valueIndex);
            }
            i++;
        }while(i<100);
        bestSolution.getSolutionFeatures().addAll(data.getSolutionFeatures());
        return bestSolution;
    }

    public DataSolution replaceOfFeatures(DataSolution data){
        var random = new Random();
        var valueIndex = 0;
        var positionReplace = 0;
        int i = 0;
        DataSolution bestSolution= null;
        bestSolution.setF1Score(Float.valueOf(sumArray(data.getSolutionFeatures())));
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());

        do{
            Integer solution;
            valueIndex = random.nextInt(data.getRclfeatures().size());
            positionReplace = random.nextInt(data.getSolutionFeatures().size());
            if((valueIndex>=0 && valueIndex<data.getRclfeatures().size()) &&(positionReplace >= 0 &&  positionReplace<data.getSolutionFeatures().size())){

                data.getSolutionFeatures().add(positionReplace,data.getRclfeatures().get(valueIndex));
                solution = sumArray(data.getSolutionFeatures());

                if(bestSolution.getF1Score() < solution){
                    bestSolution.setF1Score(Float.valueOf(solution));
                }

                i++;
            }
        }while(i<100);

        bestSolution.getSolutionFeatures().addAll(data.getSolutionFeatures());

        return bestSolution;
    }


    public Integer sumArray(ArrayList<Integer> solution){
        Integer bestSolution = 0;
        for(int i = 0; i<solution.size()-1; i++){
            bestSolution = solution.get(i) + bestSolution;
        }
        return bestSolution;
    }

    public void doIwssr(DataSolution data) {

        DataSolution bestSolutionChange;
        DataSolution bestSolutionReplace;

        bestSolutionChange = cheangeOfFeatures(data);
        bestSolutionReplace = replaceOfFeatures(data);
        if(bestSolutionChange.getF1Score() >bestSolutionReplace.getF1Score()){
            bestSolutionChange.setLocalSearch(LocalSearchUtils.IWR);
            bestSolutionChange.setIterationLocalSearch(data.getIterationLocalSearch()+1);
            bestSolutionChange.setNeighborhood(data.getNeighborhood());
            bestSolutionChange.getRclfeatures().addAll(data.getRclfeatures());
            bestSolutionChange.setSeedId(data.getSeedId());
            kafkaSolutionsProducer.send(bestSolutionChange);
        }else{
            bestSolutionReplace.setLocalSearch(LocalSearchUtils.IWR);
            bestSolutionReplace.setIterationLocalSearch(data.getIterationLocalSearch()+1);
            bestSolutionReplace.setNeighborhood(data.getNeighborhood());
            bestSolutionReplace.getRclfeatures().addAll(data.getRclfeatures());
            bestSolutionReplace.setSeedId(data.getSeedId());

            kafkaSolutionsProducer.send(bestSolutionReplace);
        }


    }
}

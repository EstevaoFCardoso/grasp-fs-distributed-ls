package br.com.graspfs.ls.iwss.service;

import br.com.graspfs.ls.iwss.dto.DataSolution;
import br.com.graspfs.ls.iwss.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.iwss.util.LocalSearchUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class IwssService {

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

    public Integer sumArray(ArrayList<Integer> solution){
        Integer bestSolution = 0;
        for(int i = 0; i<solution.size()-1; i++){
            bestSolution = solution.get(i) + bestSolution;
        }
        return bestSolution;
    }

    public void doIwss(DataSolution data) {

        DataSolution bestSolution;
        bestSolution = cheangeOfFeatures(data);
        bestSolution.setLocalSearch(LocalSearchUtils.IW);
        bestSolution.setIterationLocalSearch(data.getIterationLocalSearch()+1);
        bestSolution.setNeighborhood(data.getNeighborhood());
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        bestSolution.setSeedId(data.getSeedId());

        kafkaSolutionsProducer.send(bestSolution);

    }
}

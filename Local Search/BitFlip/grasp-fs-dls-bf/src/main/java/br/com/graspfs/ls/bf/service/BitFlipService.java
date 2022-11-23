package br.com.graspfs.ls.bf.service;

import br.com.graspfs.ls.bf.dto.DataSolution;
import br.com.graspfs.ls.bf.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.bf.util.LocalSearchUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class BitFlipService {

    KafkaSolutionsProducer kafkaSolutionsProducer;

    public Integer cheangeOfFeatures(DataSolution data){
        var random = new Random();
        var valueIndex = 0;
        int i = 0;
        Integer bestSolution = sumArray(data.getSolutionFeatures());

        do{
            Integer solution;
            valueIndex = random.nextInt(data.getRclfeatures().size());
            if(valueIndex>=0 && valueIndex<data.getRclfeatures().size()){
                data.getSolutionFeatures().add(valueIndex,data.getRclfeatures().get(valueIndex));
               solution = sumArray(data.getSolutionFeatures());
                if(bestSolution < solution){
                    bestSolution = solution;
                }
                i++;
            }
        }while(i<100);

        return bestSolution;
    }

    public Integer sumArray(ArrayList<Integer> solution){
        Integer bestSolution = 0;
        for(int i = 0; i<solution.size()-1; i++){
            bestSolution = solution.get(i) + bestSolution;
        }
        return bestSolution;
    }

    public void doBipFlip(DataSolution data, Long time){

        data.setLocalSearch(LocalSearchUtils.BF);
        data.setIterationLocalSearch(data.getIterationLocalSearch()+1);

        Integer bestSolution;
        bestSolution = cheangeOfFeatures(data);
        data.setRunnigTime(time);
        data.setF1Score(bestSolution.floatValue());
        kafkaSolutionsProducer.send(data);
    }


}

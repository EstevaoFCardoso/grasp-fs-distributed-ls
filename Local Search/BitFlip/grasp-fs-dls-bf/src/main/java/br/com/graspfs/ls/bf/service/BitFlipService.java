package br.com.graspfs.ls.bf.service;

import br.com.graspfs.ls.bf.consumer.KafkaBitFlipConsumer;
import br.com.graspfs.ls.bf.dto.DataSolution;
import br.com.graspfs.ls.bf.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.bf.util.LocalSearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class BitFlipService {

    private final Logger logg = LoggerFactory.getLogger(BitFlipService.class);

    KafkaSolutionsProducer kafkaSolutionsProducer;

    public DataSolution flipFeatures(DataSolution solution, Long time){
        var random = new Random();
        var valueIndex = 0;
        int i = 0;
        var positionReplace = 0;
        DataSolution bestSolution= solution;
        do{
            Integer sumFeatures;
            valueIndex = random.nextInt(solution.getRclfeatures().size());
            positionReplace = random.nextInt(solution.getSolutionFeatures().size());

            if(valueIndex>=0 && valueIndex<solution.getRclfeatures().size() && (positionReplace >= 0 &&  positionReplace<solution.getSolutionFeatures().size())){
                //bit flip
                solution.getSolutionFeatures().add(positionReplace,solution.getRclfeatures().remove(valueIndex));
                solution.getRclfeatures().add(valueIndex,solution.getRclfeatures().remove(positionReplace));
                //soma metrica
                sumFeatures = sumArray(solution.getSolutionFeatures());
                solution.setF1Score(Float.valueOf(sumFeatures));
                //verifica bestSolution
                solution.setRunnigTime(time-System.currentTimeMillis());
                if(bestSolution.getF1Score() < solution.getF1Score()){
                    bestSolution=solution;
                }
                i++;
            }
            logg.info("Message: "+ bestSolution.getF1Score() + " " + bestSolution.getRunnigTime() + " " + bestSolution.getNeighborhood() + " " + bestSolution.getSolutionFeatures() + " "
                    + bestSolution.getIterationLocalSearch());
        }while(i<100);
        return bestSolution;
    }

    public Integer sumArray(ArrayList<Integer> solution){
        Integer sumMetric = 0;
        for(int i = 0; i<solution.size()-1; i++){
            sumMetric = solution.get(i) + sumMetric;
        }
        return sumMetric;
    }

    public void doBipFlip(DataSolution data, Long time){
        DataSolution bestSolution;
        bestSolution = flipFeatures(data,time);
        bestSolution.setLocalSearch(LocalSearchUtils.BF);
        bestSolution.setIterationLocalSearch(data.getIterationLocalSearch()+1);
        bestSolution.setRunnigTime(time);
        bestSolution.setNeighborhood(data.getNeighborhood());
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        bestSolution.setSeedId(data.getSeedId());

        logg.info("BESTSOLUTION: "+ bestSolution.getF1Score() + " " + bestSolution.getRunnigTime() + " " + bestSolution.getNeighborhood() + " " + bestSolution.getSolutionFeatures() + " "
                + bestSolution.getIterationLocalSearch());

        kafkaSolutionsProducer.send(bestSolution);
    }


}

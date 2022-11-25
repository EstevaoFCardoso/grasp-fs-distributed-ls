package br.com.graspfs.ls.bf.service;

import br.com.graspfs.ls.bf.dto.DataSolution;
import br.com.graspfs.ls.bf.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.bf.util.LocalSearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;

@Component
public class BitFlipService {

    private final Logger logg = LoggerFactory.getLogger(BitFlipService.class);

    @Autowired
    KafkaSolutionsProducer kafkaSolutionsProducer;

    public DataSolution flipFeatures(DataSolution solution, Long time){
        var random = new Random();
        var valueIndex = 0;
        int i = 1;
        var positionReplace = 0;
        DataSolution bestSolution= updateSolution(solution);
        do{
            Integer sumFeatures;
            valueIndex = random.nextInt(solution.getRclfeatures().size()-1);
            positionReplace = random.nextInt(solution.getSolutionFeatures().size()-1);

            if(valueIndex>=0 && valueIndex<solution.getRclfeatures().size() && (positionReplace >= 0 &&  positionReplace<solution.getSolutionFeatures().size())){
                //bit flip
                solution.getSolutionFeatures().add(solution.getRclfeatures().remove(valueIndex));
                solution.getRclfeatures().add(solution.getSolutionFeatures().remove(positionReplace));
                solution.setIterationLocalSearch(solution.getIterationLocalSearch()+1);
                logg.info("Score: "+ solution.getF1Score() + " Solução: " + " " + solution.getSolutionFeatures() + " Iteração:"+ solution.getIterationLocalSearch());
                //soma metrica
                sumFeatures = sumArray(solution.getSolutionFeatures());
                solution.setF1Score(Float.valueOf(sumFeatures));
                solution.setRunnigTime(System.currentTimeMillis()-time);
                //verifica bestSolution
                if(bestSolution.getF1Score() < solution.getF1Score()){
                    bestSolution=updateSolution(solution);
                }
                i++;
            }
        }while(i<100);
        logg.info("BESTTSOLUTION: "+ bestSolution.getF1Score() + " " + bestSolution.getRunnigTime() + " " + bestSolution.getNeighborhood() + " " + bestSolution.getSolutionFeatures() + " "
                + bestSolution.getIterationLocalSearch());
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

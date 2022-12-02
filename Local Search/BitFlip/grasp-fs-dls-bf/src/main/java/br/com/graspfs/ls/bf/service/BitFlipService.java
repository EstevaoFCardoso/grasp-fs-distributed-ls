package br.com.graspfs.ls.bf.service;

import br.com.graspfs.ls.bf.dto.DataSolution;
import br.com.graspfs.ls.bf.machinelearning.MachineLearning;
import br.com.graspfs.ls.bf.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.bf.util.LocalSearchUtils;
import br.com.graspfs.ls.bf.util.PrintSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

@Component
public class BitFlipService {

    private final Logger logg = LoggerFactory.getLogger(BitFlipService.class);

    @Autowired
    KafkaSolutionsProducer kafkaSolutionsProducer;

    public DataSolution flipFeatures(DataSolution solution) throws Exception {

        var random = new Random();
        var valueIndex = 0;
        int i = 0;
        var positionReplace = 0;
        DataSolution bestSolution= updateSolution(solution);

        // criar arquivo para métrica
        BufferedWriter br = new BufferedWriter(new FileWriter("BIT-FLIP_METRICS"+solution.getIterationLocalSearch().toString()));

        br.write("solutionFeatures;f1Score;neighborhood;iterationNeighborhood;localSearch;iterationLocalSearch;runnigTime");
        br.newLine();

        try{
            PrintSolution.logSolution(solution);
            do{
                final var time = System.currentTimeMillis();
                float valueOfFeatures;
                valueIndex = random.nextInt(solution.getRclfeatures().size()-1);
                positionReplace = random.nextInt(solution.getSolutionFeatures().size()-1);

                if(valueIndex>=0 && valueIndex<solution.getRclfeatures().size() && (positionReplace >= 0 &&  positionReplace<solution.getSolutionFeatures().size())){

                    //bit flip
                    solution.getSolutionFeatures().add(solution.getRclfeatures().remove(valueIndex));
                    solution.getRclfeatures().add(solution.getSolutionFeatures().remove(positionReplace));
                    solution.setIterationLocalSearch(solution.getIterationLocalSearch()+1);

                    logg.info("Score: "+ solution.getF1Score() + " Solução: " + " " + solution.getSolutionFeatures() + " Iteração:"+ solution.getIterationLocalSearch());

                    //soma metrica
                    valueOfFeatures = MachineLearning.evaluateSolution(solution.getSolutionFeatures());//sumArray(solution.getSolutionFeatures());

                    solution.setF1Score(Float.valueOf(valueOfFeatures));
                    solution.setRunnigTime(time);


                    br.write(solution.getSolutionFeatures()+";"
                            +solution.getF1Score()+";"
                            +solution.getNeighborhood()+";"
                            +solution.getIterationNeighborhood()+";"
                            +solution.getLocalSearch()+";"
                            +solution.getIterationLocalSearch()+";"
                            +solution.getRunnigTime()
                    );
                    br.newLine();
                    //verifica bestSolution

                    PrintSolution.logSolution(solution);

                    if(bestSolution.getF1Score() < solution.getF1Score()){
                        bestSolution=updateSolution(solution);
                    }
                    i++;
                }
            } while(i<100);

            logg.info("BESTTSOLUTION: "+ bestSolution.getF1Score() + " " + bestSolution.getRunnigTime() + " " + bestSolution.getNeighborhood() + " " + bestSolution.getSolutionFeatures() + " "
                    + bestSolution.getIterationLocalSearch());

            br.close();
            return bestSolution;

        }catch (RuntimeException ex){
            logg.info("ERROR : "+ ex.getMessage());
            throw  new Exception("Erro na logica para o machine learning");
        }
    }

    public void doBipFlip(DataSolution data) throws Exception {
        DataSolution bestSolution;
        data.setLocalSearch(LocalSearchUtils.BF);
        bestSolution = flipFeatures(data);
        bestSolution.setIterationLocalSearch(data.getIterationLocalSearch()+1);
        bestSolution.setNeighborhood(data.getNeighborhood());
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        bestSolution.setIterationNeighborhood(data.getIterationNeighborhood());
        bestSolution.setSeedId(data.getSeedId());
        bestSolution.setLocalSearch(LocalSearchUtils.BF);
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

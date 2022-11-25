package br.com.graspfs.ls.bf.service;

import br.com.graspfs.ls.bf.dto.DataSolution;
import br.com.graspfs.ls.bf.producer.KafkaSolutionsProducer;
import br.com.graspfs.ls.bf.util.LocalSearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Random;

//@Service
public class BitFlipService {

    private static final Logger logg = LoggerFactory.getLogger(BitFlipService.class);

    KafkaSolutionsProducer kafkaSolutionsProducer;

    public static void main(String[] args) {
        ArrayList<Integer> seed = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            seed.add(i);
        }

        ArrayList<Integer> rcl = new ArrayList<>();
        for (int i = 6; i <= 20; i++) {
            rcl.add(i);
        }
        rcl.add(21);
        rcl.add(22);
        rcl.add(23);
        rcl.add(24);
        rcl.add(25);

        DataSolution ds = new DataSolution((long) 1,
                seed,
                rcl,
                "VND",
                1,
                "J48",
                0.50F,
                "BitFlip",
                500L,
                1
        );

        flipFeatures(ds, 1L);
    }


    public static DataSolution flipFeatures(DataSolution solution, Long time) {
        var random = new Random();
        var valueIndex = 0;
        int i = 0;
        var positionReplace = 0;
        DataSolution bestSolution = null;
        do {
            Integer sumFeatures;
            valueIndex = random.nextInt(solution.getRclfeatures().size());
            positionReplace = random.nextInt(solution.getSolutionFeatures().size());

            if (valueIndex >= 0 && valueIndex < solution.getRclfeatures().size() && (positionReplace >= 0 && positionReplace < solution.getSolutionFeatures().size())) {
                //bit flip
                solution.getSolutionFeatures().add(solution.getRclfeatures().remove(valueIndex));
                solution.getRclfeatures().add(solution.getSolutionFeatures().remove(positionReplace));
                //soma metrica
                sumFeatures = sumArray(solution.getSolutionFeatures());
                solution.setF1Score(Float.valueOf(sumFeatures));
                solution.setIterationLocalSearch(solution.getIterationLocalSearch() + 1);

                //verifica bestSolution
                solution.setRunnigTime(System.currentTimeMillis() - time);
                if (bestSolution != null) {
                    if (bestSolution.getF1Score() < solution.getF1Score()) {
//                        bestSolution = solution;
                        bestSolution = updateBestSolution(solution);
                    }
                } else {
//                    bestSolution = solution;
                    bestSolution = updateBestSolution(solution);
                }
                i++;
                logg.info("Solution: " + solution.getF1Score() + " " + solution.getRunnigTime() + " " + solution.getNeighborhood() + " " + solution.getSolutionFeatures() + " "
                        + solution.getIterationLocalSearch());
            }


        } while (i < 100);
        logg.info("Best Solution: " + bestSolution.getF1Score() + " " + bestSolution.getRunnigTime() + " " + bestSolution.getNeighborhood() + " " + bestSolution.getSolutionFeatures() + " "
                + bestSolution.getIterationLocalSearch());
        return bestSolution;
    }

    private static DataSolution updateBestSolution(DataSolution solution) {
        return new DataSolution(solution.getSeedId(),
                solution.getSolutionFeatures(),
                solution.getRclfeatures(),
                solution.getNeighborhood(),
                solution.getIterationNeighborhood(),
                solution.getClassfier(),
                solution.getF1Score(),
                solution.getLocalSearch(),
                solution.getRunnigTime(),
                solution.getIterationLocalSearch());
    }

    public static Integer sumArray(ArrayList<Integer> solution) {
        Integer sumMetric = 0;
        for (int i = 0; i < solution.size() - 1; i++) {
            sumMetric = solution.get(i) + sumMetric;
        }
        return sumMetric;
    }

    public void doBipFlip(DataSolution data, Long time) {
        DataSolution bestSolution;
        bestSolution = flipFeatures(data, time);
        bestSolution.setLocalSearch(LocalSearchUtils.BF);
        bestSolution.setIterationLocalSearch(data.getIterationLocalSearch() + 1);
        bestSolution.setRunnigTime(time);
        bestSolution.setNeighborhood(data.getNeighborhood());
        bestSolution.getRclfeatures().addAll(data.getRclfeatures());
        bestSolution.setSeedId(data.getSeedId());

        logg.info("BESTSOLUTION: " + bestSolution.getF1Score() + " " + bestSolution.getRunnigTime() + " " + bestSolution.getNeighborhood() + " " + bestSolution.getSolutionFeatures() + " "
                + bestSolution.getIterationLocalSearch());

        kafkaSolutionsProducer.send(bestSolution);
    }


}

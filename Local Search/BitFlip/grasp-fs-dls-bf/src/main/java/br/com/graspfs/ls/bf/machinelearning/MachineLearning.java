package br.com.graspfs.ls.bf.machinelearning;

import br.com.graspfs.ls.bf.util.MachineLearningUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

// https://github.com/sequincozes/TES
public class MachineLearning {

    public static double normalClass = 0; // isso aqui representa as classes com o valor N
    public static float evaluateSolution(ArrayList<Integer> features) throws Exception {

        Instances datasetTreinamento = MachineLearningUtils.lerDataset("ereno1ktrain.arff");
        Instances datasetTestes = MachineLearningUtils.lerDataset("ereno1ktest.arff");
        datasetTreinamento = MachineLearningUtils.selecionaFeatures(datasetTreinamento, features);
        datasetTestes = MachineLearningUtils.selecionaFeatures(datasetTestes, features);
        AbstractClassifier classificador = new J48(); // nova instância de um classificador qualquer
        AbstractClassifier classificadorTreinado = MachineLearningUtils.construir(datasetTreinamento, classificador);

        // Resultados
        float VP = 0; // quando o IDS diz que está acontecendo um ataque, e realmente está
        float VN = 0; // quando o IDS diz que NÃO está acontecendo um ataque, e realmente NÃO está
        float FP = 0; // quando o IDS diz que está acontecendo um ataque, PORÉM NÃO ESTÁ
        float FN = 0; // quando o IDS diz que NÃO está acontecendo um ataque, PORÉM ESTÁ!
        long beginNano = System.nanoTime();

        for (int i = 0; i < datasetTestes.size(); i++) { //percorre cada uma das amostras de teste
            try {
                Instance testando = datasetTestes.instance(i);
                double resultado = MachineLearningUtils.testarInstancia(classificadorTreinado, testando);
                double esperado = testando.classValue();
                if (resultado == esperado) { // já sabemos que o resultado é verdadeiro
                    if (resultado == normalClass) {
                        VN = VN + 1; // O IDS diz que NÃO está acontecendo um ataque, e realmente NÃO está
                    } else {
                        VP = VP + 1; // o IDS diz que está acontecendo um ataque, e realmente está
                    }
                } else { // sabemos que é um "falso"
                    if (resultado == normalClass) {
                        FN = FN + 1; // o IDS diz que NÃO está acontecendo um ataque, PORÉM ESTÁ!
                    } else {
                        FP = FP + 1; // o IDS diz que está acontecendo um ataque, PORÉM NÃO ESTÁ
                    }
                }

            } catch (ArrayIndexOutOfBoundsException a) {
                System.err.println("Erro: " + a.getLocalizedMessage());
                System.err.println("DICA: " + "Tem certeza que o número de classes está definido corretamente?");
                System.exit(1);
            } catch (Exception e) {
                System.err.println("Erro: " + e.getLocalizedMessage());
                System.exit(1);
            }
        }
        long endNano = System.nanoTime();
        float totalNano = Float.valueOf(endNano - beginNano) / 1000; // converte para microssegundos

        float f1score = calculateF1Score(datasetTestes,totalNano,VP,VN,FP,FN);

        //MachineLearningUtils.printResults(datasetTestes,totalNano,VP,VN,FP,FN);

        return f1score;

    }

    public static float calculateF1Score(Instances datasetTestes, float totalNano, float VP, float VN, float FP, float FN){
        float acuracia = (VP + VN) * 100 / (VP + VN + FP + FN); // quantos acertos o IDS teve
        float recall = (VP * 100) / (VP + FN); // quantas vezes eu acertei dentre as vezes REALMENTE ESTAVA acontecendo um ataque
        float precision = (VP * 100) / (VP + FP); // quantas vezes eu acertei dentre as vezes que eu DISSE que estava acontecendo
        float f1score = 2 * (recall * precision) / (recall + precision);
        return f1score;
    }

}

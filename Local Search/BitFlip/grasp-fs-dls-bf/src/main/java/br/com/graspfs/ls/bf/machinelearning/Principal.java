package br.com.graspfs.ls.bf.machinelearning;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;

// https://github.com/sequincozes/TES
public class Principal {

    public static double normalClass = 0; // isso aqui representa as classes com o valor N
    static boolean debug = false;

    public static void main(String args[]) throws Exception {
        int[] features = new int[]{1, 2, 3, 4, 5};
        System.out.println(evaluateSolution(features));
    }

    public static float evaluateSolution(int[] features) throws Exception {

        Instances datasetTreinamento = Auxiliar.lerDataset("ereno1ktrain.arff");
        Instances datasetTestes = Auxiliar.lerDataset("ereno1ktest.arff");
        datasetTreinamento = Auxiliar.selecionaFeatures(datasetTreinamento, features);
        datasetTestes = Auxiliar.selecionaFeatures(datasetTestes, features);
        AbstractClassifier classificador = new IBk(); // nova instância de um classificador qualquer
        AbstractClassifier classificadorTreinado = Auxiliar.construir(datasetTreinamento, classificador);

        // Resultados
        float VP = 0; // quando o IDS diz que está acontecendo um ataque, e realmente está
        float VN = 0; // quando o IDS diz que NÃO está acontecendo um ataque, e realmente NÃO está
        float FP = 0; // quando o IDS diz que está acontecendo um ataque, PORÉM NÃO ESTÁ
        float FN = 0; // quando o IDS diz que NÃO está acontecendo um ataque, PORÉM ESTÁ!
        long beginNano = System.nanoTime();

        for (int i = 0; i < datasetTestes.size(); i++) { //percorre cada uma das amostras de teste
            try {
                Instance testando = datasetTestes.instance(i);
                double resultado = Auxiliar.testarInstancia(classificadorTreinado, testando);
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
        if (debug) {
            System.out.println(" ### Tempo de Processamento");
            System.out.println("     - Tempo total de processamento: " + totalNano + " microssegundos");
            System.out.println("     - Tempo de processamento por amostra: " + totalNano / datasetTestes.size() + " microssegundos");

            System.out.println(" ### Desempenho na classificação");
        }

        float acuracia = (VP + VN) * 100 / (VP + VN + FP + FN); // quantos acertos o IDS teve
        float recall = (VP * 100) / (VP + FN); // quantas vezes eu acertei dentre as vezes REALMENTE ESTAVA acontecendo um ataque
        float precision = (VP * 100) / (VP + FP); // quantas vezes eu acertei dentre as vezes que eu DISSE que estava acontecendo
        float f1score = 2 * (recall * precision) / (recall + precision);
        if (debug) {
            System.out.println("     - VP: " + VP + ", VN: " + VN + ", FP: " + FP + ", VN: " + FN);
            System.out.println("     - F1-Score: " + f1score + "%");
            System.out.println("     - Recall: " + recall + "%");
            System.out.println("     - Precision: " + precision + "%");
            System.out.println("     - Accuracy: " + acuracia + "%");
        }
        return f1score;

    }


}

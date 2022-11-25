package br.com.graspfs.ls.bf.machinelearning;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Auxiliar {
    public static double normalClass = 0; // isso aqui representa as classes com o valor N

    public static double testarInstancia(AbstractClassifier classificador, Instance amostra) throws Exception {
        return classificador.classifyInstance(amostra);
    }

    public static AbstractClassifier construir(Instances treinamento, AbstractClassifier classificador) throws Exception {
        classificador.buildClassifier(treinamento);
        return classificador;
    }

    public static Instances lerDataset(String dataset) throws IOException {
        FileReader fr = new FileReader(dataset);
        BufferedReader br = new BufferedReader(fr);
        Instances datasetInstances = new Instances(br);
        datasetInstances.setClassIndex(datasetInstances.numAttributes() - 1);
        return datasetInstances;
    }

    public static Instances selecionaFeatures(Instances amostras,
                                              int[] features) {
        int totalFeatures = amostras.numAttributes();
//        System.out.println("Reduzindo de " + totalFeatures + " para " + features.length + " features.");

        Arrays.sort(features);
        for (int i = totalFeatures - 1; i > 0; i--) {
            if (totalFeatures <= features.length) {
                System.err.println("O número de features precisa ser maior que o filtro.");
                System.out.println("Reduzindo de " + totalFeatures+ " para " + features.length + " features. Amostra: " + i);

                System.exit(1);
                return amostras;
            }
            boolean deletar = true;
            for (int j : features) {
                if (i == j) {
                    deletar = false;
                }
            }
            if (deletar) {
                amostras.deleteAttributeAt(i - 1);
            }
        }
        amostras.setClassIndex(amostras.numAttributes() - 1);
        return amostras;
    }

    public static double classificarInstancias(AbstractClassifier classificador, Instances teste) {
        // Resultados
        float VP = 0; // quando o IDS diz que está acontecendo um ataque, e realmente está
        float VN = 0; // quando o IDS diz que NÃO está acontecendo um ataque, e realmente NÃO está
        float FP = 0; // quando o IDS diz que está acontecendo um ataque, PORÉM NÃO ESTÁ
        float FN = 0; // quando o IDS diz que NÃO está acontecendo um ataque, PORÉM ESTÁ!

        for (int i = 0; i < teste.size(); i++) { //percorre cada uma das amostras de teste
            try {
                Instance testando = teste.instance(i);
                double resultado = Auxiliar.testarInstancia(classificador, testando);
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
        float recall = (VP * 100) / (VP + FN); // quantas vezes eu acertei dentre as vezes REALMENTE ESTAVA acontecendo um ataque
        float precision = (VP * 100) / (VP + FP); // quantas vezes eu acertei dentre as vezes que eu DISSE que estava acontecendo
        float f1score = 2 * (recall * precision) / (recall + precision);

        return f1score;

    }



}

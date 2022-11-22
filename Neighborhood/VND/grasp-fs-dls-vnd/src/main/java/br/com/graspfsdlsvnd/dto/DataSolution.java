package br.com.graspfsdlsvnd.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;

@Data
@Builder
@Setter
public class DataSolution {

    private final Long seedId;// id da mensagem
    private final ArrayList<Integer> solutionFeatures;// []
    private final ArrayList<Integer> rclfeatures;// []
    private String neighborhood;// vnd
    private Integer iterationNeighborhood; //01
    private String classfier;// J48
    private Float f1Score;// 98%
    private String localSearch; // BF
    private Long runnigTime;// tempo de execução
    private Integer iterationLocalSearch; //01


}

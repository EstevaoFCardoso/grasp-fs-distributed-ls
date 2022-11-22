package br.com.graspfsdlsvnd.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class DataSolution {

    private String id;
    private final ArrayList<Integer> solutionFeatures;
    private final ArrayList<Integer> rclfeatures;

}

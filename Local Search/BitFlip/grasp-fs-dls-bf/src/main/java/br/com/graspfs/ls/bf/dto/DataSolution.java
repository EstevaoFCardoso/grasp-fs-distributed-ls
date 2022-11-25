package br.com.graspfs.ls.bf.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

//@Data
//@Builder
//@Getter
//@Setter
public class DataSolution {

    private  Long seedId;// id da mensagem
    private ArrayList<Integer> solutionFeatures;// []
    private ArrayList<Integer> rclfeatures;// []
    private String neighborhood;// vnd
    private Integer iterationNeighborhood; //01
    private String classfier;// J48
    private Float f1Score;// 98%
    private String localSearch; // BF
    private Long runnigTime;// tempo de execução
    private Integer iterationLocalSearch; //01

    public Long getSeedId() {
        return seedId;
    }

    public void setSeedId(Long seedId) {
        this.seedId = seedId;
    }

    public ArrayList<Integer> getSolutionFeatures() {
        return solutionFeatures;
    }

    public void setSolutionFeatures(ArrayList<Integer> solutionFeatures) {
        this.solutionFeatures = solutionFeatures;
    }

    public ArrayList<Integer> getRclfeatures() {
        return rclfeatures;
    }

    public void setRclfeatures(ArrayList<Integer> rclfeatures) {
        this.rclfeatures = rclfeatures;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Integer getIterationNeighborhood() {
        return iterationNeighborhood;
    }

    public void setIterationNeighborhood(Integer iterationNeighborhood) {
        this.iterationNeighborhood = iterationNeighborhood;
    }

    public String getClassfier() {
        return classfier;
    }

    public void setClassfier(String classfier) {
        this.classfier = classfier;
    }

    public Float getF1Score() {
        return f1Score;
    }

    public void setF1Score(Float f1Score) {
        this.f1Score = f1Score;
    }

    public String getLocalSearch() {
        return localSearch;
    }

    public void setLocalSearch(String localSearch) {
        this.localSearch = localSearch;
    }

    public Long getRunnigTime() {
        return runnigTime;
    }

    public void setRunnigTime(Long runnigTime) {
        this.runnigTime = runnigTime;
    }

    public Integer getIterationLocalSearch() {
        return iterationLocalSearch;
    }

    public void setIterationLocalSearch(Integer iterationLocalSearch) {
        this.iterationLocalSearch = iterationLocalSearch;
    }

    public DataSolution(Long seedId, ArrayList<Integer> solutionFeatures, ArrayList<Integer> rclfeatures, String neighborhood, Integer iterationNeighborhood, String classfier, Float f1Score, String localSearch, Long runnigTime, Integer iterationLocalSearch) {
        this.seedId = seedId;
        this.solutionFeatures = solutionFeatures;
        this.rclfeatures = rclfeatures;
        this.neighborhood = neighborhood;
        this.iterationNeighborhood = iterationNeighborhood;
        this.classfier = classfier;
        this.f1Score = f1Score;
        this.localSearch = localSearch;
        this.runnigTime = runnigTime;
        this.iterationLocalSearch = iterationLocalSearch;
    }



}

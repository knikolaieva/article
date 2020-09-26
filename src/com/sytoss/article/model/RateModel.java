package com.sytoss.article.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateModel {

    private List<AnalyzeModel> rate_1 = new ArrayList<>();
    private List<AnalyzeModel> rate0 = new ArrayList<>();
    private List<AnalyzeModel> rate1 = new ArrayList<>();
    private List<AnalyzeModel> rate2 = new ArrayList<>();
    private List<AnalyzeModel> rate3 = new ArrayList<>();
    private List<AnalyzeModel> rate4 = new ArrayList<>();

    private int expertRate_1Count;
    private int expertRate0Count;
    private int expertRate1Count;
    private int expertRate2Count;
    private int expertRate3Count;
    private int expertRate4Count;

    private double medianaRate_1_4 = Double.NaN;
    private double averageRate_1_4 = Double.NaN;

    private double medianaRate0_4 = Double.NaN;
    private double averageRate0_4 = Double.NaN;

    private double medianaRate1_4 = Double.NaN;
    private double averageRate1_4 = Double.NaN;

    private double medianaRate_1_0 = Double.NaN;
    private double averageRate_1_0 = Double.NaN;
}

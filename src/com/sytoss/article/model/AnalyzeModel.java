package com.sytoss.article.model;

import org.opencv.core.Point;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyzeModel implements Cloneable, Serializable {

    private static final long serialVersionUID = 319601821481056171L;

    private double id;
    private String nameOfPair;
    private String name;

    private int size_1_w;
    private int size_1_h;
    private int size_2_w;
    private int size_2_h;

    private int compression;
    private int expertRate;
    private RateModel rate;
    private ValueModel transformValue;

    private double[][] transformationMatrix;
    private double[][] homoghraphy;
    private double[][] homoghraphyInv;

    private double featuresDetected1Image;
    private double featuresDetected2Image;
    private double featuresDetected;
    private double nndrMatchingFeatures;
    private double ransacMatchingFeatures;

    private double featuresDetected1ImageTime;
    private double featuresDetected2ImageTime;
    private double nndrMatchingFeaturesTime;
    private double ransacMatchingFeaturesTime;

    private double scaleDelta;
    private double shearDelta;
    private double rotateDelta;
    private double pixelDelta;
    private double[] pixels;
    private double inversePixelDelta;
    private double[] inversePixels;

    private double featureUtillity;
    private double featureUtillityRansac;
    private int[] detectorValues;
    private double featureUtillityMatchingDetector;
    private double detectorCount;
    private double[] descriptorValues;
    private double descriptorCount;
    private double featureUtillityMatchingDescriptor;

    private Point[] initSynthesisPoints;
    private Point[] ransacSynthesisPoints;

    private double[] synthesisPointsValues;
    private double synthesisPointsValue;

    private double overlapArea1;
    private double overlapArea2;
    private double overlapRatioImg1;
    private double overlapRatioImg2;
    private double originalOverlapArea;
    private double originalOverlapRatioImg1;
    private double originalOverlapRatioImg2;

    private double keypointsMatchingCount1;
    private double keypointsMatchingCount2;
    private double nndrMatchingCount;

    private double[][] minimaxTransformed;
    private double[] minimaxDeltas;
    private double minimaxDelta;

    private double range1Count;
    private double range2Count;
    private double range3Count;
    private double range4Count;
    private double nonRange;

    private double range1CountInv;
    private double range2CountInv;
    private double range3CountInv;
    private double range4CountInv;
    private double nonRangeInv;

    private double a11Delta;
    private double a12Delta;
    private double a21Delta;
    private double a22Delta;
    private double aDelta;

    public AnalyzeModel(){
        this.expertRate = 4;
        this.rate = new RateModel();
        this.minimaxTransformed = new double[3][3];
    }

    public double getDegree(){
        return getTransformValue().getRotate();
    }

    public double getScale(){
        return getTransformValue().getScale();
    }

    public double getShear(){
        return getTransformValue().getShear();
    }

    public void setDegree(double value){
        getTransformValue().setRotate(value);
    }

    public void setScale(double value){
        getTransformValue().setScale(value);
    }

    public void setShear(double value){
        getTransformValue().setShear(value);
    }

    public double getNP1(){
        return getFeaturesDetected1Image();
    }

    public double getNP2(){
        return getFeaturesDetected2Image();
    }

    public double getNM(){
        return getNndrMatchingFeatures();
    }

    public double getNP(){
        return (getFeaturesDetected1Image() + getFeaturesDetected2Image()) / 2;
    }

    public double getDP1(){
        return getFeaturesDetected1Image() / (getSize_1_w() * getSize_1_h());
    }

    public double getDP2(){
        return getFeaturesDetected2Image() / (getSize_2_w() * getSize_2_h());
    }

    public double getOP1(){
        return getOverlapRatioImg1();
    }

    public double getOP2(){
        return getOriginalOverlapRatioImg2();
    }

    public double getNPO1(){
        return getKeypointsMatchingCount1();
    }

    public double getNPO2(){
        return getKeypointsMatchingCount2();
    }

    public double getDP(){
        return (getDP1() + getDP2()) / 2;
    }

    public double getNMO(){
        return getNndrMatchingCount();
    }

    public double getDPO1(){
        return getOverlapRatioImg1() > 0 ? getKeypointsMatchingCount1() / getOverlapRatioImg1() : 0;
    }

    public double getDPO2(){
        return getOverlapRatioImg2() > 0 ? getKeypointsMatchingCount2()/ getOverlapRatioImg2() : 0;
    }

    public double getNI(){
        return getRansacMatchingFeatures();
    }

    public double getPrecision(){
        return getNndrMatchingFeatures() > 0 ? getRansacMatchingFeatures()/getNndrMatchingFeatures() : 0;
    }

    public double getRecallO1(){
        return getKeypointsMatchingCount1() > 0 ? getRansacMatchingFeatures()/getKeypointsMatchingCount1() : 0;
    }

    public double getRecallO2(){
        return getKeypointsMatchingCount2() > 0 ? getRansacMatchingFeatures()/getKeypointsMatchingCount2() : 0;
    }

    public double getAvgErrIM(){
        return getPixelDelta();
    }

    public double getMaxErrIM(){
        if (getPixels() != null) {
            return Arrays.stream(getPixels()).max().getAsDouble();
        } else {
            return Double.NaN;
        }
    }

    public double getAvgErrCPM(){
        return getSynthesisPointsValue();
    }

    public double getMaxErrCPM(){
        if (getSynthesisPointsValues() != null) {
            return Arrays.stream(getSynthesisPointsValues()).max().getAsDouble();
        } else {
            return Double.NaN;
        }
    }

    public double getAvgErrPar(){
        return getMinimaxDelta();
    }

    public double getNP_CC2(){
        return getDetectorCount();
    }

    public double getNP_DSCC2(){
        return getDescriptorCount();
    }

    public double getDetRep(){
        return getFeaturesDetected1Image() > 0 ? getDetectorCount() / getFeaturesDetected1Image() : 0;
    }

    public double getDesRep(){
        return getDetectorCount() > 0 ? getDescriptorCount() / getDetectorCount() : 0;
    }

    public double getDetDesRep(){
        return getFeaturesDetected1Image() > 0 ? getDescriptorCount() / getFeaturesDetected1Image() : 0;
    }

    public double getDesT1(){
        return getFeaturesDetected1ImageTime() / 1000000000.0;
    }

    public double getDesT2(){
        return getFeaturesDetected2ImageTime() / 1000000000.0;
    }

    public double getDesT(){
        return (getDesT1() + getDesT2()) / 2;
    }

    public double getMatchT(){
        return getNndrMatchingFeaturesTime() / 1000000000.0;
    }

    public double getInlierT(){
        return getRansacMatchingFeaturesTime() / 1000000000.0;
    }

    public double getAvgDesT(){
        return getFeaturesDetected1Image() > 0 && getFeaturesDetected2Image() > 0 ?
                (((getFeaturesDetected1ImageTime() / getFeaturesDetected1Image()) + (getFeaturesDetected2ImageTime() / getFeaturesDetected2Image())) / 2) / 1000000.0 : 0;
    }

    public double getAvgMatchT(){
        return getNndrMatchingFeatures() > 0 ? (getNndrMatchingFeaturesTime() / getNndrMatchingFeatures()) / 1000000.0 : 0;
    }

    public double getAvgInlierT(){
        return getRansacMatchingFeatures() > 0 ? (getRansacMatchingFeaturesTime() / getRansacMatchingFeatures()) / 1000000.0 : 0;
    }

    public double getER4(){
        return getRate().getExpertRate4Count();
    }

    public double getER_1_0(){
        return getRate().getExpertRate_1Count() + getRate().getExpertRate0Count();
    }

    public String showMat(double[][] mat){
        DecimalFormat df = new DecimalFormat("#.####");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < mat.length; i ++){
            for (int j = 0; j < mat[i].length; j ++){
                stringBuilder
                        .append(df.format(mat[i][j]))
                        .append(" ");
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalyzeModel that = (AnalyzeModel) o;
        return ransacMatchingFeatures == that.ransacMatchingFeatures &&
                Objects.equals(name, that.name) &&
                check(transformationMatrix, that.transformationMatrix) &&
                check(homoghraphy, that.homoghraphy);
    }

    public boolean check(double[][] array1, double[][] array2){
        int i, j;
        for (i = 0; i < array1.length; i++)
            for (j = 0; j < array1[i].length; j++)
                if (array1[i][j] != array2[i][j])
                    return false;
        return true;
    }

    public double getTotalTime(){
        return (featuresDetected1ImageTime + featuresDetected2ImageTime + nndrMatchingFeaturesTime + ransacMatchingFeaturesTime) / 1000000000.0;
    }
}

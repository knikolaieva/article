package com.sytoss.article.model;

import org.opencv.core.Mat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WrapImageModel {

    private Mat normalizedImage1Transparent;
    private Mat normalizedImage2Transparent;
    private double overlapArea;

    public WrapImageModel(Mat normalizedImage1Transparent, Mat normalizedImage2Transparent, double overlapArea) {
        this.normalizedImage1Transparent = normalizedImage1Transparent;
        this.normalizedImage2Transparent = normalizedImage2Transparent;
        this.overlapArea = overlapArea;
    }
}

package com.sytoss.article.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomographyModel {

    private MatOfDMatch matches;
    private Mat matrix;

    public HomographyModel(MatOfDMatch matches, Mat matrix) {
        this.matches = matches;
        this.matrix = matrix;
    }
}

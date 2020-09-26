package com.sytoss.article.model;

import org.opencv.features2d.Feature2D;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlgorithmModel {

    private Feature2D feature2D;
    private String nameType;

    public AlgorithmModel(Feature2D feature2D, String nameType) {
        this.feature2D = feature2D;
        this.nameType = nameType;
    }
}

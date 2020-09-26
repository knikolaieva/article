package com.sytoss.article.model;

import java.io.File;

import org.opencv.core.Mat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageModel {

	private Mat mat;
	private File file;

	public ImageModel(Mat image, File file) {
		this.mat = image;
		this.file = file;
	}
}

package com.sytoss.article.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public final class ArrayUtils {

    private ArrayUtils(){
        //
    }

    public static Mat fromArray(double[][] matrix) {
        Mat M = new Mat(3, 3, CvType.CV_32F);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                M.put(i, j, matrix[i][j]);
            }
        }
        return M;
    }

    public static double[][] toArray(Mat mat) {
        double[][] matrix = new double[mat.cols()][mat.rows()];
        for (int i = 0; i < mat.cols(); i++) {
            for (int j = 0; j < mat.rows(); j++) {
                matrix[i][j] = mat.get(i, j)[0];
            }
        }
        return matrix;
    }
}

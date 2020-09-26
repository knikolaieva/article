package com.sytoss.article.utils;

public final class DataInitializer {

    public static double[] getScaleData(){
        return new double[]{0.2, 0.3, 0.4, 0.5, 0.6, 1.0, 1.4, 2, 3 , 4, 5 , 6, 7 ,8};
    }//

    public static double[] getShearData(){
        return new double[]{-0.6, -0.5, -0.4, -0.3, -0.2 , -0.1, 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6};
    }

    public static double[] getRotateData(){
        double[] rotates = new double[19];
        int index = 0;
        int maxRotate = 91;
        for (int rotate = -90; rotate < maxRotate; rotate += 10, index++) {
            rotates[index] = rotate;
        }
        return rotates;
    }
}

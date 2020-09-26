package com.sytoss.article;

import com.sytoss.article.app.resolution.SynthesizedImagesAppResolution;
import com.sytoss.article.utils.DataInitializer;

public class ResolutionSynthesizedImagesApplication {

    public static void main(String[] args) {
        double[] scales = DataInitializer.getScaleData();
        double[] shears = new double[]{0.0};//DataInitializer.getShearData();
        double[] rotates = new double[]{0.0}; //DataInitializer.getRotateData();

        String SYTOSS_NURE_png_Pairs50_scalePath = "out/SYTOSS_NURE_png_Pairs50_scale/result_building/tmp";

        SynthesizedImagesAppResolution appResolution = new SynthesizedImagesAppResolution(scales, shears, rotates);
        appResolution.execute(SYTOSS_NURE_png_Pairs50_scalePath);
    }

}

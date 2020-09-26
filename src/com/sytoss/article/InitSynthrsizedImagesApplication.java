package com.sytoss.article;

import org.opencv.core.Core;

import com.sytoss.article.app.initializer.AppInitializer;
import com.sytoss.article.app.initializer.SynthesizedImagesAppInitializer;
import com.sytoss.article.utils.DataInitializer;

public class InitSynthrsizedImagesApplication extends InitApplication {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            String root = "assets/SYTOSS_NURE_pngImages50/";
            String path = "texture_artificial";
            String resultFolder = "D:\\Dev\\SYTOSS_NURE_png_Pairs50_scale\\result_";
            double[] scales = DataInitializer.getScaleData();
            double[] shears = DataInitializer.getShearData();
            double[] rotates = DataInitializer.getRotateData();

            AppInitializer app = new SynthesizedImagesAppInitializer(root + path, scales, new double[]{0}, new double[]{0});
            run(app, resultFolder, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

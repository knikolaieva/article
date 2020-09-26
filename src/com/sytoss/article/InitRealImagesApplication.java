package com.sytoss.article;

import java.io.File;

import org.opencv.core.Core;

import com.sytoss.article.app.initializer.AppInitializer;
import com.sytoss.article.app.initializer.RealImagesAppInitializer;
import com.sytoss.article.app.initializer.SynthesizedImagesAppInitializer;
import com.sytoss.article.utils.DataInitializer;

public class InitRealImagesApplication extends InitApplication {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        try {
            String[] paths = new String[]{"building", "graffiti_picture_outside", "picture_inside", "texture_artificial", "texture_nature"};
            String root = "assets/dataset/07_27/";
            String resultFolder = "out/09_22_100/result_";
            for (String path : paths) {
                AppInitializer app = new RealImagesAppInitializer(root + path);
                run(app, resultFolder, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

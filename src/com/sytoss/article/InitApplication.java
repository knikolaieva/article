package com.sytoss.article;

import java.io.File;

import org.opencv.core.Core;

import com.sytoss.article.app.initializer.AppInitializer;
import com.sytoss.article.app.initializer.RealImagesAppInitializer;

public class InitApplication {

    protected static void run(AppInitializer app, String resultFolder, String path) {
        File file = new File(resultFolder + path + "/");
        boolean mkdir = file.mkdir();
        app.execute(file.getPath() + "/");
    }
}

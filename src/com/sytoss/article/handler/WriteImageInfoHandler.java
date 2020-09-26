package com.sytoss.article.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import com.sytoss.article.model.AnalyzeModel;

public class WriteImageInfoHandler {

    private String path;

    public WriteImageInfoHandler(String path) {
        this.path = path;
    }

    public void execute(String name, List<AnalyzeModel> result){
        File imageFolder2 = new File(path);
        if (!imageFolder2.exists()) imageFolder2.mkdir();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path +"\\" + name + ".tmp"));
            outputStream.writeObject(result);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.sytoss.article.app.initializer;

import static com.sytoss.article.utils.AlgorithmHelper.calculateAllArea;
import static org.apache.commons.io.FileUtils.readFileToString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.sytoss.article.handler.AlgorithmHandler;
import com.sytoss.article.handler.WriteImageInfoHandler;
import com.sytoss.article.model.AlgorithmModel;
import com.sytoss.article.model.AnalyzeModel;
import com.sytoss.article.model.ImageModel;
import com.sytoss.article.utils.DocumentUtils;

import javafx.util.Pair;
import me.tongfei.progressbar.ProgressBar;

public class RealImagesAppInitializer extends AppInitializer {

    public RealImagesAppInitializer(String fileFolder) {
        super(fileFolder);
    }

    public void execute(String path) {
        List<ImageModel> images = getImages();
        int initialMax = getAlgorithms().size() * images.size();
        try (ProgressBar progressBar = new ProgressBar("Image processing", initialMax)) {
            if (images.isEmpty()) {
                return;
            }

            images.sort(Comparator.comparing(o -> o.getFile().getName()));

            for (int i = 0; i < images.size(); i++){
                File image1File = images.get(i).getFile();
                String name1 = image1File.getName();

                String experimentNumber1 = getExperimentNumber(name1);

                String index1 = name1.substring(name1.lastIndexOf("_")+1, name1.lastIndexOf("_")+2);
                String format1 = name1.substring(name1.lastIndexOf(".") + 1);
                ImageModel image1 = images.get(i);
                if (name1.endsWith("0." + format1)){
                    for (int j = i + 1; j < images.size(); j++){
                        File image2File = images.get(j).getFile();
                        String name2 = image2File.getName();
                        String experimentNumber2 = getExperimentNumber(name2);
                        String format2 = name2.substring(name2.lastIndexOf(".") + 1);
                        String index2 = name2.substring(name2.indexOf("_") + 1, name2.indexOf("_") + 2);
                        ImageModel image2 = images.get(j);
                        if (experimentNumber1.equals(experimentNumber2) && !name2.endsWith("0." + format2)) {
                            String pathImages = path  + "\\" + name1 + "_" + name2;
                            File imageFolder = new File(pathImages);
                            imageFolder.mkdir();
                            double[][] matrix = getOriginalMatrix(image1File.getParent() + "\\matrix\\H_" + index1 + "_" + index2);
                            List<AnalyzeModel> result = processImage(progressBar, image1.getMat(), image2.getMat(), pathImages, matrix);
                            fillResults(path, result, image1, image2);
                        }
                    }
                }
            }
        }
    }

    private List<AnalyzeModel> processImage(ProgressBar progressBar, Mat image1, Mat image2, String path, double[][] matrix) {
        List<AnalyzeModel> result = new ArrayList<>();
        Pair<Double, Double> overlapArea;
        double overlapArea1 = 0;
        double overlapArea2 = 0;
        if (matrix != null) {
            overlapArea = calculateOriginalOverlapArea(image1, image2, path, matrix);
            double allArea1 = calculateAllArea(image1);
            double allArea2 = calculateAllArea(image2);
            overlapArea1 = (overlapArea.getValue() / allArea1) * 100;
            overlapArea2 = (overlapArea.getKey() / allArea2) * 100;
        }
        for (AlgorithmModel algorithm : getAlgorithms()) {
            progressBar.stepBy(2);
            progressBar.setExtraMessage(algorithm.getNameType());
            AnalyzeModel analyzeModel = new AnalyzeModel();
            analyzeModel.setName(algorithm.getNameType());
            analyzeModel.setTransformationMatrix(matrix);
            analyzeModel.setOriginalOverlapRatioImg1(overlapArea1);
            analyzeModel.setOriginalOverlapRatioImg2(overlapArea2);

            AlgorithmHandler algorithmHandler = new AlgorithmHandler(algorithm, analyzeModel, image1, image2);
            algorithmHandler.setPath(path);
            try {
                algorithmHandler.execute();
                result.add(algorithmHandler.getAnalyzeModel());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fillMiniMaxParamsOfMatrix(result, matrix);
        return result;
    }

    private void fillResults(String path, List<AnalyzeModel> result, ImageModel image1, ImageModel image2){
        try {
            String name1 = image1.getFile().getName();
            String name2 = image2.getFile().getName();
            String pathImages = path  + "\\" + name1 + "_" + name2;
            String pathImagesResult = path + "\\" + "tmp";//  + name1;
            WriteImageInfoHandler writeImageInfoHandler = new WriteImageInfoHandler(pathImagesResult);
            writeImageInfoHandler.execute(name1 + "_" + name2 + "_list", result);
            FileOutputStream out = new FileOutputStream(new File(path + "/table_"+name1 + "_" + name2+".docx"));
            XWPFDocument document = new XWPFDocument();
            Imgcodecs.imwrite(pathImages + "/image1_original.jpg", image1.getMat());
            Imgcodecs.imwrite(pathImages + "/image2_original.jpg", image2.getMat());
            DocumentUtils.writeTitle(document, image1.getFile(), image2.getFile(), pathImages, image1.getMat().size(), image2.getMat().size());
            DocumentUtils.writeExperimentOfMinimaxDelta(document, result);
            DocumentUtils.writeExperimentOfOverlapArea(document, result);
            DocumentUtils.writeExperimentOfTime(document, result);
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[][] getOriginalMatrix(String path){
        double[][] matrix;
        try {
            File matrixPath = new File(path);
            String matrixString = readFileToString(matrixPath, "UTF-8");
            String[] rows = matrixString.split("\n");
            matrix = new double[3][3];
            for (int n = 0; n < 3; n++){
                String[] cols = rows[n].split(" ");
                for (int m = 0; m < 3; m++) {
                    matrix[n][m] = Double.valueOf(cols[m]);
                }
            }
        } catch (IOException e) {
            matrix = null;
        }
        return matrix;
    }

    private String getExperimentNumber(String name){
        int resultIndex = name.lastIndexOf("_");
        int lenght = 0;
        for (int k=name.lastIndexOf("_") - 1 ; k>=0; k--){
            if (Character.isDigit(name.charAt(k))){
                resultIndex = k;
                lenght ++;
            } else {
                break;
            }
        }

        return name.substring(resultIndex, resultIndex + lenght);
    }
}

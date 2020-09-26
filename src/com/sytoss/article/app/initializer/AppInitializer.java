package com.sytoss.article.app.initializer;

import static com.sytoss.article.utils.AlgorithmHelper.*;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.opencv.imgproc.Imgproc.*;

import java.io.*;
import java.util.*;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.xfeatures2d.*;

import com.sytoss.article.model.*;
import com.sytoss.article.utils.ArrayUtils;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AppInitializer {

    private static final String[] POSSIBLE_EXTENSIONS  = {"png", "jpg", "jpeg", "tif", "bmp", "pmm", "arw"};
    private List<AlgorithmModel> algorithms = getAlgorithms();
    private List<ImageModel> images = new ArrayList<>();

    public AppInitializer(String fileFolder) {
        Collection<File> files = FileUtils.listFiles(new File(fileFolder), POSSIBLE_EXTENSIONS, false);
        for (File file : files) {
            this.images.add(new ImageModel(Imgcodecs.imread(file.getPath(), Imgcodecs.IMREAD_COLOR), file));
        }
    }

    public abstract void execute(String path);

    protected Pair<Double, Double> calculateOriginalOverlapArea(Mat image1, Mat image2, String path, double [][] original){
        Mat matrix = ArrayUtils.fromArray(original);
        Mat img1 = new Mat();
        cvtColor(image1, img1, COLOR_BGR2BGRA);
        Mat img2 = new Mat();
        cvtColor(image2, img2, COLOR_BGR2BGRA);

        WrapImageModel wrapImageModel1 = wrapImage1(img1, img2, matrix, "_ORIGINAL", path);
        WrapImageModel wrapImageModel2 = wrapImage2(img1, img2, matrix, "_ORIGINAL", path);

        return new Pair<>(wrapImageModel1.getOverlapArea(), wrapImageModel2.getOverlapArea());
    }

    protected void fillMiniMaxParamsOfMatrix(List<AnalyzeModel> result, double[][] original){
        double [][] h = new double[9][result.size()+1];
        int col = 0;
        int row;
        if (original != null) {
            for (AnalyzeModel algorithm : result) {
                double[][] transformed = algorithm.getHomoghraphy();
                row = 0;
                if (transformed != null) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            h[row][col] = transformed[i][j];
                            h[row][result.size()] = original[i][j];
                            row++;
                        }
                    }
                    col++;
                }
            }

            row = 0;
            int n, m;
            double[][] minimaxOriginal = new double[3][3];
            for (double[] hi : h) {
                double max = Arrays.stream(hi).max().getAsDouble();
                double min = Arrays.stream(hi).min().getAsDouble();
                n = row == 0 || row == 1 || row == 2 ? 0 : row == 3 || row == 4 || row == 5 ? 1 : 2;
                m = row == 0 || row == 3 || row == 6 ? 0 : row == 1 || row == 4 || row == 7 ? 1 : 2;
                col = 0;
                for (double i : hi) {
                    double value = (max - min) > 0 ? (i - min) / (max - min) : 0;
                    if (col == 8) {
                        minimaxOriginal[n][m] = value;
                        break;
                    } else {
                        AnalyzeModel model = result.get(col);
                        double[][] minimaxTransformed = model.getMinimaxTransformed();
                        minimaxTransformed[n][m] = value;
                    }
                    col++;
                }
                row++;
            }


            for (AnalyzeModel analyzeModel : result) {
                double[] minimaxDeltas = new double[9];
                double minimaxDelta = 0;
                double[][] minimaxTransformed = analyzeModel.getMinimaxTransformed();
                int k =0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        double value = Math.abs(minimaxOriginal[i][j] - minimaxTransformed[i][j]);
                        if (value != Double.NaN) {
                            minimaxDelta += value;
                            minimaxDeltas[k++] = value;
                        }
                    }
                }
                analyzeModel.setMinimaxDelta(minimaxDelta/9);
                analyzeModel.setMinimaxDeltas(minimaxDeltas);
            }
        }
    }

    protected List<AlgorithmModel> getAlgorithms() {
        SURF surf128 = SURF.create();
        surf128.setHessianThreshold(100);
        surf128.setExtended(true);
        SURF surf64 = SURF.create();
        surf64.setHessianThreshold(100);
        SIFT sift = SIFT.create();
        ORB orb = ORB.create();
        orb.setMaxFeatures(100000);
        ORB orb1000 = ORB.create();
        orb1000.setMaxFeatures(1000);
        BRISK brisk = BRISK.create();
        KAZE kaze = KAZE.create();
        kaze.setNOctaveLayers(3);
        kaze.setExtended(true);
        AKAZE akaze = AKAZE.create();
        akaze.setNOctaveLayers(3);
        //another possible algorithms
        LATCH latch = LATCH.create();
        VGG vgg = VGG.create();
        LUCID lucid = LUCID.create();
        DAISY daisy = DAISY.create();
        FREAK freak = FREAK.create();

        return new ArrayList<>(Arrays.asList(
                new AlgorithmModel(sift, "SIFT"),
                new AlgorithmModel(surf128, "SURF128"),
                new AlgorithmModel(surf64, "SURF64"),
                new AlgorithmModel(orb, "ORB"),
                new AlgorithmModel(orb1000, "ORB1000"),
                new AlgorithmModel(brisk, "BRISK"),
                new AlgorithmModel(kaze, "KAZE"),
                new AlgorithmModel(akaze, "AKAZE")
        ));
    }

}

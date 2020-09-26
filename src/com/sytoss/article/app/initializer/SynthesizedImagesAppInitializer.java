package com.sytoss.article.app.initializer;

import static com.sytoss.article.handler.AlgorithmHandler.RED;
import static com.sytoss.article.utils.AlgorithmHelper.calculateAllArea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.sytoss.article.handler.AlgorithmHandler;
import com.sytoss.article.handler.WriteImageInfoHandler;
import com.sytoss.article.model.AlgorithmModel;
import com.sytoss.article.model.AnalyzeModel;
import com.sytoss.article.model.ImageModel;
import com.sytoss.article.model.ValueModel;
import com.sytoss.article.utils.AffineTransformUtils;
import com.sytoss.article.utils.ArrayUtils;
import com.sytoss.article.utils.DocumentUtils;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import me.tongfei.progressbar.ProgressBar;

@Getter
@Setter
public class SynthesizedImagesAppInitializer extends AppInitializer {

    private double[] scales;
    private double[] shears;
    private double[] rotates;

    public SynthesizedImagesAppInitializer(String fileFolder, double[] scales, double[] shears, double[] rotates) {
        super(fileFolder);
        this.scales = scales;
        this.shears = shears;
        this.rotates = rotates;
    }

    public void execute(String path) {
        List<ImageModel> images = getImages();
        int all = images.size() * scales.length * shears.length * rotates.length * getAlgorithms().size();
        try (ProgressBar progressBar = new ProgressBar("Image", all)) {
            for (ImageModel image : images) {
                List<AnalyzeModel> result = processImage(image, progressBar, path);
                fillResults(path, result, image);
            }
        }
    }

    private List<AnalyzeModel> processImage(ImageModel image, ProgressBar progressBar, String path) {
        List<AnalyzeModel> result = new ArrayList<>();

        File imageFolder = new File(path + image.getFile().getName() + "_syntez");
        imageFolder.mkdir();
        Mat image1 = image.getMat();
        int range = 0;
        for (double scale : scales) {
            File imageFolder2 = new File(imageFolder + "/" + image.getFile().getName() + "_k(" + scale + ")");
            imageFolder2.mkdir();
            double z = scale;
//            double z = 1;
            Size finalSize = new Size((image1.width()*z), (image1.height()*z));
            Mat scaledModel = new Mat();
            Mat scaleMatrix = AffineTransformUtils.scaleXY(image1, scaledModel, scale, finalSize);
            for (double shear : shears) {
                Mat scaledShearedModel = new Mat();
                Mat shearMatrix = AffineTransformUtils.shear(scaledModel, scaledShearedModel, shear, 0, finalSize);
                for (double rotate : rotates) {
                    ValueModel valueModel = new ValueModel(scale, shear, rotate);
                    Mat scaleShearedRotatedModel = new Mat();
                    Mat rotateMatrix = AffineTransformUtils.rotate(scaledShearedModel, scaleShearedRotatedModel, rotate, 1, finalSize);
                    //   draw(imageFolder.getPath(), scaleShearedRotatedModel, "generated_image_of" + name + "_" + valueModel.toString());

                    Mat M2 = new Mat(3, 3, CvType.CV_64FC1);
                    Core.gemm(shearMatrix, scaleMatrix, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 0, M2);
                    Mat resultMatrix = new Mat(3, 3, CvType.CV_64FC1);
                    Core.gemm(rotateMatrix, M2, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 0, resultMatrix);

                    Mat image2 = new Mat();
                    Imgproc.warpPerspective(image1, image2, resultMatrix, finalSize, Imgproc.INTER_CUBIC, Core.BORDER_CONSTANT, RED);
                    double[][] matrix = ArrayUtils.toArray(resultMatrix);
                    double overlapArea1 = 0;
                    double overlapArea2 = 0;
                    if (matrix != null) {
                        Pair<Double, Double> overlapArea = calculateOriginalOverlapArea(image1, image2, imageFolder2.getPath(), matrix);
                        double allArea1 = calculateAllArea(image1);
                        double allArea2 = calculateAllArea(image2);
                        overlapArea1 = (overlapArea.getValue() / allArea1) * 100;
                        overlapArea2 = (overlapArea.getKey() / allArea2) * 100;
                    }
                    for (AlgorithmModel algorithm : getAlgorithms()) {
                        progressBar.step();
                        progressBar.setExtraMessage(valueModel.toString() + ", " + algorithm.getNameType());
                        AnalyzeModel analyzeModel = new AnalyzeModel();
                        analyzeModel.setTransformValue(valueModel);
                        analyzeModel.setName(algorithm.getNameType());
                        analyzeModel.setTransformationMatrix(ArrayUtils.toArray(resultMatrix));
                        analyzeModel.setOriginalOverlapRatioImg1(overlapArea1);
                        analyzeModel.setOriginalOverlapRatioImg2(overlapArea2);
                        AlgorithmHandler algorithmHandler = new AlgorithmHandler(algorithm, analyzeModel, image1, image2);
                        algorithmHandler.setPath(imageFolder2.getPath());
                        try {
                            algorithmHandler.execute();
                            result.add(algorithmHandler.getAnalyzeModel());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    fillMiniMaxParamsOfMatrix(result.subList(range, result.size()), matrix);
                    range += 8;
                    try {
                        Files.walk(Paths.get(imageFolder2.getPath() + "\\image normalization"))
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::delete);
                    } catch (IOException e)   {
                        System.out.println();
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }


    private void fillResults(String path, List<AnalyzeModel> result, ImageModel image){
        String name = image.getFile().getName();
        String pathImagesResult = path + "\\" + name;
        WriteImageInfoHandler writeImageInfoHandler = new WriteImageInfoHandler(pathImagesResult);
        writeImageInfoHandler.execute(image.getFile().getName() + "_list", result);
        try {
            FileOutputStream out = new FileOutputStream(new File(path + "/table_"+name+".docx"));
            XWPFDocument document = new XWPFDocument();
            DocumentUtils.writeExperimentOfOverlapAreaSynthesized(document, result);
            DocumentUtils.writeExperimentOfMinimaxDeltaSynthesized(document, result);
            DocumentUtils.writeExperimentOfTimeSynthesized(document, result);
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

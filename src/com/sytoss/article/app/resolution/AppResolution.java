package com.sytoss.article.app.resolution;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sytoss.article.model.*;
import com.sytoss.article.utils.DocumentUtils;

import lombok.Getter;

@Getter
public class AppResolution {

    protected static final String[] ALGORITHM_NAMES = new String[]{"SIFT", "SURF128", "SURF64", "ORB", "ORB1000", "BRISK", "KAZE", "AKAZE"};

    private static final String[] POSSIBLE_EXTENSIONS = {"tmp"};
    private int[] compressions = new int[]{0, 2, 4, 6, 8, 10, 12, 14};
    private List<AnalyzeModel> allResult = new ArrayList<>();

    private List<AnalyzeModel> surf128 = new ArrayList<>();
    private List<AnalyzeModel> surf64 = new ArrayList<>();
    private List<AnalyzeModel> sift = new ArrayList<>();
    private List<AnalyzeModel> orb1000 = new ArrayList<>();
    private List<AnalyzeModel> orb = new ArrayList<>();
    private List<AnalyzeModel> kaze = new ArrayList<>();
    private List<AnalyzeModel> akaze = new ArrayList<>();
    private List<AnalyzeModel> brisk = new ArrayList<>();
    private List<AnalyzeModel> lucid = new ArrayList<>();
    private List<AnalyzeModel> latch = new ArrayList<>();
    private List<AnalyzeModel> freak = new ArrayList<>();

    protected void execute(String... paths) {
        for (String path : paths) {
            File fileFolder = new File(path);
            Collection<File> files = FileUtils.listFiles(fileFolder, POSSIBLE_EXTENSIONS, false);
            for (File file : files) {
                try {
                    String name = file.getName();
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
                    List<AnalyzeModel> input = (List<AnalyzeModel>) inputStream.readObject();
                    inputStream.close();
                    input.forEach(analyzeModel -> analyzeModel.setNameOfPair(name));
                    fillRates(name, input);
                    allResult.addAll(input);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fillRates(String name, List<AnalyzeModel> input) {
        int i1 = name.indexOf("rate(") + 5;
        int i2 = name.indexOf(").tmp");
        if (i1 != -1 && i2 != -1 && input.size() == 8) {
            String ratesString = name.substring(i1, i2);
            String[] split = ratesString.split(",");
            for (int i = 0; i < 8; i++) {
                input.get(i).setExpertRate(Integer.valueOf(split[i]));
            }
        }
    }

    public void writeExperimentData(File resultFile) {
        try {
            FileOutputStream out = new FileOutputStream(resultFile);
            XWPFDocument document = new XWPFDocument();
            List<AnalyzeModel> models = new ArrayList<>();
            models.addAll(sift);
            models.addAll(surf128);
            models.addAll(surf64);
            models.addAll(orb);
            models.addAll(orb1000);
            models.addAll(brisk);
            models.addAll(kaze);
            models.addAll(akaze);
            DocumentUtils.writeAverageExperimentOfOverlapArea(document, models);
            DocumentUtils.writeAverageExperiment_1(document, models);
            DocumentUtils.writeAverageExperiment_2(document, models);
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFeaturesData(String documentPath) {
        try {
            FileOutputStream out = new FileOutputStream(new File(documentPath));
            XWPFDocument document = new XWPFDocument();
            DocumentUtils.writeFeaturesData(document, surf128);
            DocumentUtils.writeFeaturesData(document, surf64);
            DocumentUtils.writeFeaturesData(document, sift);
            DocumentUtils.writeFeaturesData(document, brisk);
            DocumentUtils.writeFeaturesData(document, orb);
            DocumentUtils.writeFeaturesData(document, orb1000);
            DocumentUtils.writeFeaturesData(document, kaze);
            DocumentUtils.writeFeaturesData(document, akaze);
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<AnalyzeModel> getExperimentsByName(String name) {
        List<AnalyzeModel> experiments;
        switch (name) {
            case "SIFT":
                experiments = sift;
                break;
            case "SURF128":
                experiments = surf128;
                break;
            case "SURF64":
                experiments = surf64;
                break;
            case "ORB":
                experiments = orb;
                break;
            case "ORB1000":
                experiments = orb1000;
                break;
            case "BRISK":
                experiments = brisk;
                break;
            case "KAZE":
                experiments = kaze;
                break;
            case "AKAZE":
                experiments = akaze;
                break;
            case "LUCID":
                experiments = lucid;
                break;
            case "LATCH":
                experiments = latch;
                break;
            case "FREAK":
                experiments = freak;
                break;
            default:
                experiments = null;
                break;
        }
        return experiments;
    }

    protected AnalyzeModel calculateAvarageParams(List<AnalyzeModel> list) {
        double deltaH = 0;
        double deltaMu = 0;
        double deltaAlpha = 0;
        double deltaPixel = 0;
        double deltaPixelInv = 0;
        double deltaA11 = 0;
        double deltaA12 = 0;
        double deltaA21 = 0;
        double deltaA22 = 0;
        double deltaA = 0;
        double deltaRange1Count = 0;
        double deltaRange2Count = 0;
        double deltaRange3Count = 0;
        double deltaRange4Count = 0;
        double deltaRange5Count = 0;

        double deltaRange1CountInv = 0;
        double deltaRange2CountInv = 0;
        double deltaRange3CountInv = 0;
        double deltaRange4CountInv = 0;
        double deltaRange5CountInv = 0;

        double featureUtillity = 0;
        double featureUtillityMatchingDetector = 0;
        double featureUtillityMatchingDescriptor = 0;

        boolean pixelLess = list.stream().allMatch(i -> i.getPixelDelta() < 0);
        boolean pixelLessInv = list.stream().allMatch(i -> i.getInversePixelDelta() < 0);
        for (AnalyzeModel model : list) {
            if (!pixelLess) {
                if (model.getPixelDelta() > 0 && model.getExpertRate() > 0) {
                    deltaPixel += model.getPixelDelta();
                }
            } else {
                deltaPixel = -1.0;
            }

            if (!pixelLessInv) {
                if (model.getInversePixelDelta() > 0) {
                    deltaPixelInv += model.getInversePixelDelta();
                }
            } else {
                deltaPixelInv = -1.0;
            }
            double[][] homoghraphy = model.getHomoghraphy();
            if (homoghraphy != null) {
                double a11 = homoghraphy[0][0];
                double a12 = homoghraphy[0][1];
                double a21 = homoghraphy[1][0];
                double a22 = homoghraphy[1][1];
                double[][] transformation = model.getTransformationMatrix();
                if (transformation != null) {
                    double b11 = transformation[0][0];
                    double b12 = transformation[0][1];
                    double b21 = transformation[1][0];
                    double b22 = transformation[1][1];

                    double _a11 = (Math.abs(b11 - a11) * 100) / b11;
                    double _a12 = (Math.abs(b12 - a12) * 100) / b12;
                    double _a21 = (Math.abs(b21 - a21) * 100) / b21;
                    double _a22 = (Math.abs(b22 - a22) * 100) / b22;
                    double averageA = (_a11 + _a12 + _a21 + _a22) / 4;
                    deltaA11 += _a11;
                    deltaA12 += _a12;
                    deltaA21 += _a21;
                    deltaA22 += _a22;
                    deltaA += averageA;
                }
                double[] ranges = new double[5];
                if (model.getPixels() != null) {
                    ranges = calculateRangeCount(model.getPixels());
                }

                double[] rangesInv = new double[5];
                if (model.getInversePixels() != null) {
                    rangesInv = calculateRangeCount(model.getInversePixels());
                }

                double h = ((a21 * a11) + (a22 * a12)) / ((a11 * a22) - (a12 * a21));
                double alpha = Math.atan(-(a21 / a22));
                double degree = Math.toDegrees(alpha);
                double mu = a22 / Math.cos(alpha);

                // double lamda = (mu*a11)/(a22+(a21*h));
                ValueModel valueModel = model.getTransformValue();
                if (valueModel != null) {
                    double deltaSc = Math.abs(Math.abs(valueModel.getScale()) - Math.abs(mu));
                    double deltaSh = Math.abs(Math.abs(valueModel.getShear()) - Math.abs(h));
                    double deltaAl = Math.abs(Math.abs(valueModel.getRotate()) - Math.abs(degree));

                    deltaMu += deltaSc;
                    deltaH += deltaSh;
                    deltaAlpha += deltaAl;
                }

                deltaRange1Count += ranges[0];
                deltaRange2Count += ranges[1];
                deltaRange3Count += ranges[2];
                deltaRange4Count += ranges[3];
                deltaRange5Count += ranges[4];

                deltaRange1CountInv += rangesInv[0];
                deltaRange2CountInv += rangesInv[1];
                deltaRange3CountInv += rangesInv[2];
                deltaRange4CountInv += rangesInv[3];
                deltaRange5CountInv += rangesInv[4];
            }
            featureUtillity += model.getFeatureUtillity();
            featureUtillityMatchingDetector += model.getFeatureUtillityMatchingDetector();
            featureUtillityMatchingDescriptor += model.getFeatureUtillityMatchingDescriptor();
        }

        double muRes = deltaMu > 0 ? deltaMu / list.size() : deltaMu;
        double hRes = deltaH > 0 ? deltaH / list.size() : deltaH;
        double alphaRes = deltaAlpha > 0 ? deltaAlpha / list.size() : deltaAlpha;
        double pixelRes = deltaPixel > 0 ? deltaPixel / list.size() : deltaPixel;
        double pixelResInv = deltaPixelInv > 0 ? deltaPixelInv / list.size() : deltaPixelInv;
        double a11Res = deltaA11 > 0 ? deltaA11 / list.size() : deltaA11;
        double a12Res = deltaA12 > 0 ? deltaA12 / list.size() : deltaA12;
        double a21Res = deltaA21 > 0 ? deltaA21 / list.size() : deltaA21;
        double a22Res = deltaA22 > 0 ? deltaA22 / list.size() : deltaA22;
        double aRes = deltaA > 0 ? deltaA / list.size() : deltaA;
        double range1Res = deltaRange1Count > 0 ? deltaRange1Count / list.size() : deltaRange1Count;
        double range2Res = deltaRange2Count > 0 ? deltaRange2Count / list.size() : deltaRange2Count;
        double range3Res = deltaRange3Count > 0 ? deltaRange3Count / list.size() : deltaRange3Count;
        double range4Res = deltaRange4Count > 0 ? deltaRange4Count / list.size() : deltaRange4Count;
        double range5Res = deltaRange5Count > 0 ? deltaRange5Count / list.size() : deltaRange5Count;

        double range1ResInv = deltaRange1CountInv > 0 ? deltaRange1CountInv / list.size() : deltaRange1CountInv;
        double range2ResInv = deltaRange2CountInv > 0 ? deltaRange2CountInv / list.size() : deltaRange2CountInv;
        double range3ResInv = deltaRange3CountInv > 0 ? deltaRange3CountInv / list.size() : deltaRange3CountInv;
        double range4ResInv = deltaRange4CountInv > 0 ? deltaRange4CountInv / list.size() : deltaRange4CountInv;
        double range5ResInv = deltaRange5CountInv > 0 ? deltaRange5CountInv / list.size() : deltaRange5CountInv;

        double featureUtillityRes = featureUtillity > 0 ? featureUtillity / list.size() : featureUtillity;
        double featureUtillityMatchingDetectorRes = featureUtillityMatchingDetector > 0 ? featureUtillityMatchingDetector / list.size() : featureUtillityMatchingDetector;
        double featureUtillityMatchingDescriptorRes = featureUtillityMatchingDescriptor > 0 ? featureUtillityMatchingDescriptor / list.size() : featureUtillityMatchingDescriptor;

        AnalyzeModel result = new AnalyzeModel();
        result.setTransformValue(list.get(0).getTransformValue());
        result.setCompression(list.get(0).getCompression());
        result.setScaleDelta(muRes);
        result.setShearDelta(hRes);
        result.setRotateDelta(alphaRes);
        result.setPixelDelta(pixelRes);
        result.setInversePixelDelta(pixelResInv);

        result.setA11Delta(a11Res);
        result.setA12Delta(a12Res);
        result.setA21Delta(a21Res);
        result.setA22Delta(a22Res);
        result.setADelta(aRes);

        result.setRange1Count(range1Res);
        result.setRange2Count(range2Res);
        result.setRange3Count(range3Res);
        result.setRange4Count(range4Res);
        result.setNonRange(range5Res);

        result.setRange1CountInv(range1ResInv);
        result.setRange2CountInv(range2ResInv);
        result.setRange3CountInv(range3ResInv);
        result.setRange4CountInv(range4ResInv);
        result.setNonRangeInv(range5ResInv);

        result.setFeaturesDetected1Image(list.stream().mapToDouble(AnalyzeModel::getFeaturesDetected1Image).average().getAsDouble());
        result.setFeaturesDetected2Image(list.stream().mapToDouble(AnalyzeModel::getFeaturesDetected2Image).average().getAsDouble());
        result.setFeaturesDetected(list.stream().mapToDouble(AnalyzeModel::getFeaturesDetected).average().getAsDouble());
        result.setNndrMatchingFeatures(list.stream().mapToDouble(AnalyzeModel::getNndrMatchingFeatures).average().getAsDouble());
        result.setRansacMatchingFeatures(list.stream().mapToDouble(AnalyzeModel::getRansacMatchingFeatures).average().getAsDouble());

        result.setSynthesisPointsValue(list.stream().mapToDouble(AnalyzeModel::getSynthesisPointsValue).average().getAsDouble());
        result.setMinimaxDelta(list.stream().mapToDouble(AnalyzeModel::getMinimaxDelta).average().getAsDouble());
        result.setOverlapArea1(list.stream().mapToDouble(AnalyzeModel::getOverlapArea1).average().getAsDouble());
        result.setOverlapArea2(list.stream().mapToDouble(AnalyzeModel::getOverlapArea2).average().getAsDouble());
        result.setOriginalOverlapRatioImg1(list.stream().mapToDouble(AnalyzeModel::getOriginalOverlapRatioImg1).average().getAsDouble());
        result.setOriginalOverlapRatioImg2(list.stream().mapToDouble(AnalyzeModel::getOriginalOverlapRatioImg2).average().getAsDouble());
        result.setOverlapRatioImg1(list.stream().mapToDouble(AnalyzeModel::getOverlapRatioImg1).average().getAsDouble());
        result.setOverlapRatioImg2(list.stream().mapToDouble(AnalyzeModel::getOverlapRatioImg2).average().getAsDouble());

        result.setFeaturesDetected1ImageTime(list.stream().mapToDouble(AnalyzeModel::getFeaturesDetected1ImageTime).average().getAsDouble());
        result.setFeaturesDetected2ImageTime(list.stream().mapToDouble(AnalyzeModel::getFeaturesDetected2ImageTime).average().getAsDouble());
        result.setNndrMatchingFeaturesTime(list.stream().mapToDouble(AnalyzeModel::getNndrMatchingFeaturesTime).average().getAsDouble());
        result.setRansacMatchingFeaturesTime(list.stream().mapToDouble(AnalyzeModel::getRansacMatchingFeaturesTime).average().getAsDouble());

        result.setFeatureUtillity(featureUtillityRes);
        result.setFeatureUtillityMatchingDetector(featureUtillityMatchingDetectorRes);
        result.setFeatureUtillityMatchingDescriptor(featureUtillityMatchingDescriptorRes);
        result.setKeypointsMatchingCount1(list.stream().mapToDouble(AnalyzeModel::getKeypointsMatchingCount1).average().getAsDouble());
        result.setKeypointsMatchingCount2(list.stream().mapToDouble(AnalyzeModel::getKeypointsMatchingCount2).average().getAsDouble());
        result.setNndrMatchingCount(list.stream().mapToDouble(AnalyzeModel::getNndrMatchingCount).average().getAsDouble());
        return result;
    }

    private double[] calculateRangeCount(double[] pixels) {
        double[] rangesCount = new double[5];
        for (double pixel : pixels) {
            if (pixel >= 0 && pixel <= 1.4) {
                rangesCount[0]++;
            } else if (pixel > 1.4 && pixel <= 2.4) {
                rangesCount[1]++;
            } else if (pixel > 2.4 && pixel <= 3.4) {
                rangesCount[2]++;
            } else if (pixel > 3.4) {
                rangesCount[3]++;
            } else {
                rangesCount[4]++;
            }
        }
        return rangesCount;
    }
}

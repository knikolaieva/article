package com.sytoss.article.handler;

import static com.sytoss.article.utils.AlgorithmHelper.*;
import static com.sytoss.article.utils.AlgorithmHelper.wrapImage2;
import static org.opencv.core.Core.*;
import static org.opencv.imgproc.Imgproc.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sytoss.article.model.*;
import com.sytoss.article.utils.AlgorithmHelper;
import com.sytoss.article.utils.ArrayUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlgorithmHandler {


    private static final double FEATURE_COUNT_MATCHING_DESCRIPTOR_TRESHOLD = 0.2;
    private static final double NNDR_RATIO_TRESHOLD = 0.75;

    public static Scalar RED = new Scalar(0, 0, 255, 255);
    public static Scalar FILL = new Scalar(0, 0, 0, 0);
    public static Scalar WHITE = new Scalar(255, 255, 255, 255);
    public static Scalar GREEN = new Scalar(0, 255, 0);
    public static Scalar BLUE = new Scalar(255, 0, 0, 255);
    private final static int KNN_MATCH_COUNT = 2;
    private AlgorithmModel algorithm;
    private Feature2D feature2D;
    private Mat image1;
    private Mat image2;
    private MatOfKeyPoint image1Keypoints;
    private MatOfKeyPoint image2Keypoints;
    private HomographyModel homography;
    private ValueModel valueModel;
    private String path;
    private AnalyzeModel analyzeModel;
    private List<DMatch> matchesNNDR;

    public AlgorithmHandler(AlgorithmModel algorithm, AnalyzeModel analyzeModel, Mat image1, Mat image2) {
        this.algorithm = algorithm;
        this.feature2D = algorithm.getFeature2D();
        this.analyzeModel = analyzeModel;
        this.valueModel = analyzeModel.getTransformValue();
        this.image1 = image1;
        this.image2 = image2;
        this.image1Keypoints = new MatOfKeyPoint();
        this.image2Keypoints = new MatOfKeyPoint();
        this.homography = new HomographyModel(new MatOfDMatch(), Mat.zeros(3, 3, CvType.CV_32F));
        analyzeModel.setSize_1_w(image1.width());
        analyzeModel.setSize_1_h(image1.height());
        analyzeModel.setSize_2_w(image2.width());
        analyzeModel.setSize_2_h(image2.height());
    }

    public void execute() {
        long start, end;
        MatOfKeyPoint image1Descriptors = new MatOfKeyPoint();
        MatOfKeyPoint image2Descriptors = new MatOfKeyPoint();
        if ("LUCID".equals(analyzeModel.getName()) ||
                "LATCH".equals(analyzeModel.getName()) ||
                "FREAK".equals(analyzeModel.getName())) {
            ORB orb = ORB.create();
            orb.setMaxFeatures(100000);

            start = System.nanoTime();
            orb.detect(getImage1(), getImage1Keypoints());
            getFeature2D().compute(getImage1(), getImage1Keypoints(), image1Descriptors);
            end = System.nanoTime();
            analyzeModel.setFeaturesDetected1ImageTime(end - start);

            start = System.nanoTime();
            orb.detect(getImage2(), getImage2Keypoints());
            getFeature2D().compute(getImage2(), getImage2Keypoints(), image2Descriptors);
            end = System.nanoTime();
            analyzeModel.setFeaturesDetected2ImageTime(end - start);
        } else {
            start = System.nanoTime();
            getFeature2D().detectAndCompute(getImage1(), new Mat(), getImage1Keypoints(), image1Descriptors);
            end = System.nanoTime();
            analyzeModel.setFeaturesDetected1ImageTime(end - start);

            start = System.nanoTime();
            getFeature2D().detectAndCompute(getImage2(), new Mat(), getImage2Keypoints(), image2Descriptors);
            end = System.nanoTime();
            analyzeModel.setFeaturesDetected2ImageTime(end - start);
        }
        long image1Keypoints = getImage1Keypoints().total();
        analyzeModel.setFeaturesDetected1Image(image1Keypoints);
        double image2Keypoints = getImage2Keypoints().total();
        analyzeModel.setFeaturesDetected2Image((long) image2Keypoints);

        int[] result = calculateFeatureCountMatchingDetector(getImage1Keypoints().toArray(), getImage2Keypoints().toArray());
        double statistic = 0;
        for (int value : result) {
            if (value > 0) {
                statistic++;
            }
        }
        double res = statistic / image1Keypoints;
        analyzeModel.setDetectorCount(statistic);
        analyzeModel.setDetectorValues(result);
        analyzeModel.setFeatureUtillityMatchingDetector(res);

        double[] result2 = calculateFeatureCountMatchingDescriptor(image1Descriptors, image2Descriptors);
        double statistic2 = 0;
        for (double value : result2) {
            if (value >= 0 && value <= FEATURE_COUNT_MATCHING_DESCRIPTOR_TRESHOLD) {
                statistic2++;
            }
        }
        double res2 = statistic2 / image1Keypoints;
        analyzeModel.setDescriptorCount(statistic2);
        analyzeModel.setDescriptorValues(result2);
        analyzeModel.setFeatureUtillityMatchingDescriptor(res2);

        File file = new File(getPath() + "/keypoints matches");
        file.mkdir();
        drawKeypoints(algorithm.getNameType(), file);
        try {
            if (!image1Descriptors.empty() && !image2Descriptors.empty() && image2Keypoints > 1 && image1Keypoints > 1) {
                start = System.nanoTime();
                List<MatOfDMatch> detectedMatches = match(getAlgorithm().getFeature2D(), image1Descriptors, image2Descriptors);
                if (detectedMatches.size() > 0) {
                    MatOfDMatch matches = filterMatchesByNNDR(detectedMatches);
                    end = System.nanoTime();
                    analyzeModel.setFeaturesDetected(detectedMatches.size());
                    matchesNNDR = matches.toList();
                    analyzeModel.setNndrMatchingFeaturesTime(end - start);
                    analyzeModel.setNndrMatchingFeatures((int) matches.total());
                    draw(matches, "NNDR_" + algorithm.getNameType() + "_NM(" + matches.total() + ")", file);

                    if (matches.rows() > 3) {
                        this.homography = filterMatchesByHomography(matches, getImage1Keypoints().toArray(), getImage2Keypoints().toArray());
                        double featuresDetected = homography.getMatches().total();
                        analyzeModel.setRansacMatchingFeatures((int) featuresDetected);
                        analyzeModel.setFeatureUtillity(featuresDetected / image2Keypoints);
                        analyzeModel.setFeatureUtillityRansac(featuresDetected / matches.total());
                        draw(homography.getMatches(), "RANSAC_" + algorithm.getNameType() + "_NI(" + homography.getMatches().total() + ")", file);
                    }
                    normalization();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void calculateSynthesisPointsValue(Point[] initSynthesisPoints, Point[] ransacSynthesisPoints) {
        double[] results = new double[4];
        double result = 0;
        for (int i = 0; i < 4; i++) {
            double xPow2 = Math.pow((initSynthesisPoints[i].x - ransacSynthesisPoints[i].x), 2);
            double yPow2 = Math.pow((initSynthesisPoints[i].y - ransacSynthesisPoints[i].y), 2);
            double value = Math.sqrt(xPow2 + yPow2);
            result += value;
            results[i] = value;
        }
        analyzeModel.setSynthesisPointsValue(result / 4);
        analyzeModel.setSynthesisPointsValues(results);
    }

    private Point[] getSynthesisPoints(Point[] points, double[][] matrix) {
        Point[] result = new Point[4];
        if (matrix != null) {
            double H0 = matrix[0][0];
            double H1 = matrix[0][1];
            double H2 = matrix[0][2];

            double H3 = matrix[1][0];
            double H4 = matrix[1][1];
            double H5 = matrix[1][2];

            double H6 = matrix[2][0];
            double H7 = matrix[2][1];
            double H8 = matrix[2][2];

            int index = 0;
            for (Point point : points) {
                double _z = ((H6 * point.x) + (H7 * point.y) + H8);
                double x = (((H0 * point.x) + (H1 * point.y) + H2) / _z);
                double y =  (((H3 * point.x) + (H4 * point.y) + H5) / _z);
                result[index] = new Point(x, y);
                index++;
            }
        }
        return result;
    }

    private int[] calculateFeatureCountMatchingDetector(KeyPoint[] image1Keypoints, KeyPoint[] image2Keypoints) {
        double[][] matrix = analyzeModel.getTransformationMatrix();
        int[] result = new int[image1Keypoints.length];
        if (matrix != null && valueModel != null) {
            int index = 0;
            for (KeyPoint keyPoint1 : image1Keypoints) {
                Point p1 = keyPoint1.pt;
                double H0 = matrix[0][0];
                double H1 = matrix[0][1];
                double H2 = matrix[0][2];

                double H3 = matrix[1][0];
                double H4 = matrix[1][1];
                double H5 = matrix[1][2];

                double H6 = matrix[2][0];
                double H7 = matrix[2][1];
                double H8 = 1;

                double _z = ((H6 * p1.x) + (H7 * p1.y) + H8);
                int x = (int) (((H0 * p1.x) + (H1 * p1.y) + H2) / _z);
                int y = (int) (((H3 * p1.x) + (H4 * p1.y) + H5) / _z);
                int w = (int) (2 * valueModel.getScale());
                for (int i = -w; i <= w; i++) {
                    for (int j = -w; j <= w; j++) {
                        int new_x = x + i;
                        int new_y = y + j;
                        for (KeyPoint keyPoint2 : image2Keypoints) {
                            Point p2 = keyPoint2.pt;
                            int _x = (int) p2.x;
                            int _y = (int) p2.y;
                            if (new_x > -1 && new_y > -1 && new_x == _x && new_y == _y) {
                                result[index]++;
                            }
                        }
                    }
                }
                index++;
            }
        }
        return result;
    }

    private double[] calculateFeatureCountMatchingDescriptor(MatOfKeyPoint image1Descriptors, MatOfKeyPoint image2Descriptors) {
        double[][] matrix = analyzeModel.getTransformationMatrix();
        KeyPoint[] image1Keypoints = getImage1Keypoints().toArray();
        KeyPoint[] image2Keypoints = getImage2Keypoints().toArray();

        double[] result = new double[image1Keypoints.length];
        if (matrix != null && valueModel != null) {
            for (int i = 0; i < result.length; i++) {
                result[i] = -1;
            }
            int index = 0;
            for (KeyPoint keyPoint1 : image1Keypoints) {
                Point p1 = keyPoint1.pt;
                double H0 = matrix[0][0];
                double H1 = matrix[0][1];
                double H2 = matrix[0][2];

                double H3 = matrix[1][0];
                double H4 = matrix[1][1];
                double H5 = matrix[1][2];

                double H6 = matrix[2][0];
                double H7 = matrix[2][1];
                double H8 = matrix[2][2];

                int countFeatures = image1Descriptors.rows();
                int descSize = image1Descriptors.cols();

                double _z = ((H6 * p1.x) + (H7 * p1.y) + H8);
                int x = (int) (((H0 * p1.x) + (H1 * p1.y) + H2) / _z);
                int y = (int) (((H3 * p1.x) + (H4 * p1.y) + H5) / _z);
                int w = (int) (2 * valueModel.getScale());
                for (int i = -w; i <= w; i++) {
                    for (int j = -w; j <= w; j++) {
                        int new_x = x + i;
                        int new_y = y + j;
                        int index2 = 0;
                        for (KeyPoint keyPoint2 : image2Keypoints) {
                            Point p2 = keyPoint2.pt;
                            int _x = (int) p2.x;
                            int _y = (int) p2.y;
                            if (new_x > -1 && new_y > -1 && new_x == _x && new_y == _y) {
                                double sum = 0;
                                for (int d = 0; d < descSize; d++) {
                                    sum += Math.abs(image1Descriptors.get(index, d)[0] - image2Descriptors.get(index2, d)[0]);
                                }
                                if (result[index] == -1 || sum < result[index]) {
                                    double results = sum / descSize;
                                    if ("ORB".equals(analyzeModel.getName()) ||
                                            "ORB1000".equals(analyzeModel.getName()) ||
                                            "BRISK".equals(analyzeModel.getName()) ||
                                            "AKAZE".equals(analyzeModel.getName()) ||
                                            "SIFT".equals(analyzeModel.getName())) {
                                        results = results / 255;
                                    }
                                    result[index] = results;
                                }
                            }
                            index2++;
                        }
                    }
                }
                index++;
            }
        }
        return result;
    }

    private double[] calculateFeatureCountMatchingRansac(MatOfDMatch matches, MatOfKeyPoint image1Descriptors, MatOfKeyPoint image2Descriptors) {
        double[] result = new double[(int) matches.total()];
        for (int i = 0; i < result.length; i++) {
            result[i] = -1;
        }
        int index = 0;
        for (DMatch match : matches.toList()) {
            int descSize = image1Descriptors.cols();
            double sum = 0;
            for (int d = 0; d < descSize; d++) {
                sum += Math.abs(image1Descriptors.get(match.queryIdx, d)[0] - image2Descriptors.get(match.trainIdx, d)[0]);
            }
            if (result[index] == -1 || sum < result[index]) {
                double results = sum / descSize;
                double results2 = match.distance / descSize;
                if ("ORB".equals(analyzeModel.getName()) ||
                        "ORB1000".equals(analyzeModel.getName()) ||
                        "BRISK".equals(analyzeModel.getName()) ||
                        "AKAZE".equals(analyzeModel.getName()) ||
                        "SIFT".equals(analyzeModel.getName())) {
                    results = results / 255;
                    results2 = results2 / 255;
                }
                result[index] = results;
            }
        }

        return result;
    }

    private void draw(MatOfDMatch matches, String name, File file) {
        Mat matchoutput = new Mat(image2.rows() * 2, image2.cols() * 2, Imgcodecs.IMREAD_COLOR);
        matchoutput.setTo(new Scalar(255, 255, 255));
        Features2d.drawMatches(getImage1(), getImage1Keypoints(), getImage2(), getImage2Keypoints(), matches, matchoutput, GREEN, BLUE, new MatOfByte(), Features2d.DrawMatchesFlags_NOT_DRAW_SINGLE_POINTS);
        Imgproc.putText(matchoutput, name, new Point(matchoutput.width() / 8, 30), FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
        Imgcodecs.imwrite(file + "/" + name + (valueModel != null ? "_" +valueModel.toString() : "") + ".jpg", matchoutput);
    }

    private void drawKeypoints(String name, File file) {
        if (valueModel == null) {
            Mat matchoutput1 = new Mat(image1.rows(), image1.cols(), Imgcodecs.IMREAD_COLOR);
            Features2d.drawKeypoints(getImage1(), getImage1Keypoints(), matchoutput1);
            Imgproc.putText(matchoutput1, "1_" + name + "_NP(" + getImage1Keypoints().total() + ")", new Point(matchoutput1.width() / 4, 30), FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
            Imgcodecs.imwrite(file + "/1_" + name + "_NP(" + getImage1Keypoints().total() + ")"  + (valueModel != null ? "_" +valueModel.toString() : "") + ".jpg", matchoutput1);

            Mat matchoutput2 = new Mat(image2.rows() , image2.cols(), Imgcodecs.IMREAD_COLOR);
            Features2d.drawKeypoints(getImage2(), getImage2Keypoints(), matchoutput2);
            Imgproc.putText(matchoutput2, "2_" + name + "_NP(" + getImage2Keypoints().total() + ")", new Point(matchoutput2.width() / 4, 30), FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
            Imgcodecs.imwrite(file + "/2_" + name + "_NP(" + getImage2Keypoints().total() + ")"  + (valueModel != null ?  "_" +valueModel.toString() : "") + ".jpg", matchoutput2);
        } else {
            Mat matchoutput2 = new Mat(image2.rows() , image2.cols(), Imgcodecs.IMREAD_COLOR);
            Features2d.drawKeypoints(getImage2(), getImage2Keypoints(), matchoutput2, GREEN);
            Imgcodecs.imwrite(file + "/img2_" + name + "_NP(" + getImage2Keypoints().total() + ")"  + (valueModel != null ?  "_" +valueModel.toString() : "") + ".jpg", matchoutput2);
        }
    }

    private List<MatOfDMatch> match(Feature2D algorithm, Mat desc1, Mat desc2) {
        List<MatOfDMatch> matches = new ArrayList<>();
        BFMatcher bfMatcher;
        if ("Feature2D.ORB".equals(algorithm.getDefaultName()) ||
                "Feature2D.BRISK".equals(algorithm.getDefaultName()) ||
                "Feature2D.AKAZE".equals(algorithm.getDefaultName())) {
            bfMatcher = BFMatcher.create(NORM_HAMMING, false);
        } else {
            bfMatcher = BFMatcher.create(NORM_L1, false);
        }
        bfMatcher.knnMatch(desc1, desc2, matches, KNN_MATCH_COUNT);
        return matches;
    }

    private MatOfDMatch match2(Feature2D algorithm, Mat desc1, Mat desc2) {
        MatOfDMatch matches = new MatOfDMatch();
        BFMatcher bfMatcher;
        if ("Feature2D.ORB".equals(algorithm.getDefaultName()) ||
                "Feature2D.BRISK".equals(algorithm.getDefaultName()) ||
                "Feature2D.AKAZE".equals(algorithm.getDefaultName())) {
            bfMatcher = BFMatcher.create(NORM_HAMMING, true);
        } else {
            bfMatcher = BFMatcher.create(NORM_L1, true);
        }
        bfMatcher.match(desc1, desc2, matches);
        return matches;
    }

    private MatOfDMatch filterMatchesByNNDR(List<MatOfDMatch> matches_original) {
        LinkedList<DMatch> matches_filtered = new LinkedList<>();
        MatOfDMatch matOfDMatch = new MatOfDMatch();
        for (MatOfDMatch matofDMatch : matches_original) {
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch bestMatch = dmatcharray[0];
            DMatch betterMatch = dmatcharray[1];
            if (bestMatch.distance <= NNDR_RATIO_TRESHOLD * betterMatch.distance) {
                matches_filtered.addLast(bestMatch);
            }
        }

        matOfDMatch.fromList(matches_filtered);
        return matOfDMatch;
    }

    private HomographyModel filterMatchesByHomography(MatOfDMatch matches, KeyPoint[] k1, KeyPoint[] k2) {
        List<Point> lp1 = new ArrayList<>();
        List<Point> lp2 = new ArrayList<>();

        List<DMatch> dMatches = matches.toList();
        for (DMatch match : dMatches) {
            Point kk1 = k1[match.queryIdx].pt;
            Point kk2 = k2[match.trainIdx].pt;
            lp1.add(kk1);
            lp2.add(kk2);
        }

        MatOfPoint2f srcPoints = new MatOfPoint2f(lp1.toArray(new Point[0]));
        MatOfPoint2f dstPoints = new MatOfPoint2f(lp2.toArray(new Point[0]));

        Mat mask = new Mat();
        long start = System.nanoTime();
        Mat homography = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.FM_RANSAC, 3, mask, 2000);
        long end = System.nanoTime();
        analyzeModel.setRansacMatchingFeaturesTime(end - start);

        List<DMatch> resultList = new LinkedList<>();
        int size = (int) mask.size().height;
        for (int i = 0; i < size; i++) {
            if (mask.get(i, 0)[0] == 1) {
                DMatch d = dMatches.get(i);
                resultList.add(d);
            }
        }

        MatOfDMatch result = new MatOfDMatch();
        result.fromList(resultList);
        return new HomographyModel(result, homography);
    }

    public void normalization() {
        Mat matrix = this.homography.getMatrix();
        double[][] homography = new double[3][3];
        if (matrix != null && matrix.cols() > 0 && matrix.rows() > 0 && this.homography.getMatches().total() > 0) {
            homography = ArrayUtils.toArray(matrix);
            Mat matrixInv = matrix.inv();
            analyzeModel.setHomoghraphy(homography);
            analyzeModel.setHomoghraphyInv(ArrayUtils.toArray(matrixInv));

            Point[] points = new Point[4];
            points[0] = new Point(0, 0);
            points[1] = new Point(getImage1().width(), 0);
            points[2] = new Point(getImage1().width(), getImage1().height());
            points[3] = new Point(0, getImage1().height());

            if (analyzeModel.getTransformationMatrix() != null) {
                Point[] init = getSynthesisPoints(points, analyzeModel.getTransformationMatrix());
                Point[] ransac = getSynthesisPoints(init, analyzeModel.getHomoghraphyInv());
                calculateSynthesisPointsValue(points, ransac);
            }
            executeNormalization();
        } else {
            analyzeModel.setPixelDelta(-1);
            analyzeModel.setInversePixelDelta(-1);
            analyzeModel.setHomoghraphy(homography);
        }
    }

    private void executeNormalization() {
        String name = (valueModel != null ?  valueModel.toString() + "_" : "") + getAnalyzeModel().getName();
        List<DMatch> matchesByHomography = getHomography().getMatches().toList();
        Mat matrix = getHomography().getMatrix();
        List<Point> queryPoints = new LinkedList<>();
        List<Point> trainPoints = new LinkedList<>();

        Mat img1 = new Mat();
        cvtColor(getImage1(), img1, COLOR_BGR2BGRA);
        setImage1(img1);
        Mat img2 = new Mat();
        cvtColor(getImage2(), img2, COLOR_BGR2BGRA);
        setImage2(img2);

        KeyPoint[] k1 = getImage1Keypoints().toArray();
        KeyPoint[] k2 = getImage2Keypoints().toArray();
        for (DMatch match : matchesByHomography) {
            KeyPoint query = k1[match.queryIdx];
            Point kk1 = query.pt;
            KeyPoint train = k2[match.trainIdx];
            Point kk2 = train.pt;
            queryPoints.add(kk1);
            trainPoints.add(kk2);
        }

        double[] pixels = new double[matchesByHomography.size()];

        WrapImageModel wrapImageModel1 = wrapImage1(img1, img2, matrix, name, path);
        WrapImageModel wrapImageModel2 = wrapImage2(img1, img2, matrix, name, path);
        double[] invPixels = new double[matchesByHomography.size()];
        Features2d.drawKeypoints(getImage1(), new MatOfKeyPoint(), img1, RED);
        Features2d.drawKeypoints(getImage2(), new MatOfKeyPoint(), img2, RED);

        Mat cuttingNormalizedImage1 = new Mat();
        Mat cuttingNormalizedImage2 = new Mat();
        Imgproc.warpPerspective(img1, cuttingNormalizedImage1, matrix, img2.size(), Imgproc.INTER_LINEAR, BORDER_CONSTANT, RED);
        Imgproc.warpPerspective(img2, cuttingNormalizedImage2, matrix.inv(), img1.size(), Imgproc.INTER_LINEAR, BORDER_CONSTANT, RED);

        File file = new File(path + "/keypoints normalization");
        file.mkdir();
        Imgcodecs.imwrite(file.getPath() + "/norm_Img1_direct_" + name + ".jpg", img2);
        Imgcodecs.imwrite(file.getPath() + "/norm_Img2_direct_" + name + ".jpg", cuttingNormalizedImage1);

        Imgcodecs.imwrite(file.getPath() + "/norm_Img1_inverse_" + name + ".jpg", img1);
        Imgcodecs.imwrite(file.getPath() + "/norm_Img2_inverse_" + name + ".jpg", cuttingNormalizedImage2);

        try {
            File f1 = new File(file.getPath() + "/norm_Img1_direct_" + name +".jpg");
            File f2 = new File(file.getPath() + "/norm_Img2_direct_" + name +".jpg");
            BufferedImage imgA = ImageIO.read(f1);
            BufferedImage imgB = ImageIO.read(f2);
            Graphics2D g = combine(imgA, imgB, file.getPath(),   "keypoints_match_direct_" + name );
            g.dispose();
            File f3 = new File(file.getPath() + "/norm_Img1_inverse_" + name +".jpg");
            File f4 = new File(file.getPath() + "/norm_Img2_inverse_" + name +".jpg");
            BufferedImage imgC = ImageIO.read(f3);
            BufferedImage imgD = ImageIO.read(f4);
            Graphics2D g2 = combine(imgC, imgD, file.getPath(),   "keypoints_match_inverse_" + name );
            g.dispose();
            g2.dispose();
            Files.delete(Paths.get(file.getPath() + "/norm_Img1_direct_" + name +".jpg"));
            Files.delete(Paths.get(file.getPath() + "/norm_Img2_direct_" + name +".jpg"));
            Files.delete(Paths.get(file.getPath() + "/norm_Img1_inverse_" + name +".jpg"));
            Files.delete(Paths.get(file.getPath() + "/norm_Img2_inverse_" + name +".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mat i1 = Imgcodecs.imread(file.getPath() + "/keypoints_match_direct_" + name +".jpg", Imgcodecs.IMREAD_COLOR);
        Mat i2 = Imgcodecs.imread(file.getPath() + "/keypoints_match_inverse_" + name +".jpg", Imgcodecs.IMREAD_COLOR);
        for (int i = 0; i < matchesByHomography.size(); i++) {
            Point queryPoint = queryPoints.get(i);
            Point trainPoint = trainPoints.get(i);
            double H0 = matrix.get(0, 0)[0];
            double H1 = matrix.get(0, 1)[0];
            double H2 = matrix.get(0, 2)[0];

            double H3 = matrix.get(1, 0)[0];
            double H4 = matrix.get(1, 1)[0];
            double H5 = matrix.get(1, 2)[0];

            double H6 = matrix.get(2, 0)[0];
            double H7 = matrix.get(2, 1)[0];
            double H8 = matrix.get(2, 2)[0];

            double _z = ((H6 * queryPoint.x) + (H7 * queryPoint.y) + H8);
            double _x = (((H0 * queryPoint.x) + (H1 * queryPoint.y) + H2) / _z);
            double _y = (((H3 * queryPoint.x) + (H4 * queryPoint.y) + H5) / _z);

            Point translate = new Point(_x, _y);
            Imgproc.drawMarker(i1, translate, BLUE, MARKER_TILTED_CROSS, 7, 1, 4);
            Imgproc.drawMarker(i1, trainPoint, RED, MARKER_CROSS, 5, 1, 4);

            double xPow2 = Math.pow((translate.x - trainPoint.x), 2);
            double yPow2 = Math.pow((translate.y - trainPoint.y), 2);
            double pixel = Math.sqrt(xPow2 + yPow2);
            pixels[i] = pixel;

            Mat homographyInversion = matrix.inv();
            double a = homographyInversion.get(0, 0)[0];
            double b = homographyInversion.get(0, 1)[0];
            double p = homographyInversion.get(0, 2)[0];

            double c = homographyInversion.get(1, 0)[0];
            double d = homographyInversion.get(1, 1)[0];
            double q = homographyInversion.get(1, 2)[0];

            double m = homographyInversion.get(2, 0)[0];
            double n = homographyInversion.get(2, 1)[0];
            double s = homographyInversion.get(2, 2)[0];

            double _z_1 = ((m * trainPoint.x) + (n * trainPoint.y) + s);
            double _x_1 = (((a * trainPoint.x) + (b * trainPoint.y) + p) / _z_1);
            double _y_1 = (((c * trainPoint.x) + (d * trainPoint.y) + q) / _z_1);

            Point translate2 = new Point(_x_1, _y_1);
            Imgproc.drawMarker(i2, translate2, BLUE, MARKER_TILTED_CROSS, 7, 1, 4);
            Imgproc.drawMarker(i2, queryPoint, RED, MARKER_CROSS, 5, 1, 4);
            double _xPow2 = Math.pow((translate2.x - queryPoint.x), 2);
            double _yPow2 = Math.pow((translate2.y - queryPoint.y), 2);
            double pixelInv = Math.sqrt(_xPow2 + _yPow2);
            invPixels[i] = pixelInv;
        }
        Imgproc.putText(i1, "RED -  key points of image2 (inliers);", new Point(2, i1.height() - 15), FONT_HERSHEY_PLAIN, 0.5, new Scalar(0, 255, 255), 1);
        Imgproc.putText(i2, "RED -  key points of image1 (inliers);", new Point(2, i2.height() - 15), FONT_HERSHEY_PLAIN, 0.5, new Scalar(0, 255, 255), 1);

        Imgproc.putText(i1, "BLUE - key points of image1 (inliers) normalized with direct matrix.", new Point(2, i1.height() - 5), FONT_HERSHEY_PLAIN, 0.5, new Scalar(0, 255, 255), 1);
        Imgproc.putText(i2, "BLUE - key points of image2 (inliers) normalized with inverse matrix.", new Point(2, i2.height() - 5), FONT_HERSHEY_PLAIN, 0.5, new Scalar(0, 255, 255), 1);

        Imgcodecs.imwrite(file.getPath() + "/keypoints_match_direct_" + name + ".jpg", i1);
        Imgcodecs.imwrite(file.getPath() + "/keypoints_match_inverse_" + name + ".jpg", i2);

        analyzeModel.setPixels(pixels);
        analyzeModel.setInversePixels(invPixels);
        analyzeModel.setPixelDelta(Arrays.stream(pixels).average().getAsDouble());
        analyzeModel.setInversePixelDelta(Arrays.stream(invPixels).average().getAsDouble());
        analyzeModel.setOverlapArea1(wrapImageModel2.getOverlapArea());
        analyzeModel.setOverlapArea2(wrapImageModel1.getOverlapArea());
        analyzeModel.setOverlapRatioImg1((wrapImageModel2.getOverlapArea() / (AlgorithmHelper.calculateAllArea(img1))) * 100);
        analyzeModel.setOverlapRatioImg2((wrapImageModel1.getOverlapArea() / (AlgorithmHelper.calculateAllArea(img2))) * 100);

        Mat normalizedImage2 = new Mat();
        Imgproc.warpPerspective(img2, normalizedImage2, matrix.inv(), img1.size(), Imgproc.INTER_LINEAR, BORDER_CONSTANT, FILL);
        calculateKeypointsMatching1(img1, normalizedImage2);

        Mat normalizedImage1 = new Mat();
        Imgproc.warpPerspective(img1, normalizedImage1, matrix, img2.size(), Imgproc.INTER_LINEAR, BORDER_CONSTANT, FILL);
        calculateKeypointsMatching2(img2, normalizedImage1);
    }

    private void calculateKeypointsMatching1( Mat imgA, Mat imgB){
        KeyPoint[] k1 = getImage1Keypoints().toArray();
        double keypointsMatchingCount1 = 0;
        double nndrMatchingCount = 0;


        List<KeyPoint> list1 = new ArrayList<>(Arrays.asList(k1));
        List<DMatch> list2 = new ArrayList<>(matchesNNDR);
        for (int i = 0; i < imgA.rows(); i++) {
            for (int j = 0; j < imgA.cols(); j++) {
                if (!isTransparent(imgB, i, j)) {
                    int x = j;
                    int y = i;

                    Iterator<KeyPoint> iterator1 = list1.iterator();
                    while (iterator1.hasNext()) {
                        KeyPoint keypoint = iterator1.next();
                        if (Math.round(keypoint.pt.x) == x && Math.round(keypoint.pt.y) == y) {
                            keypointsMatchingCount1++;
                            iterator1.remove();
                        }
                    }

                    Iterator<DMatch> iterator2 = list2.iterator();
                    while (iterator2.hasNext()) {
                        DMatch dMatch = iterator2.next();
                        if (Math.round(k1[dMatch.queryIdx].pt.x) == x && Math.round(k1[dMatch.queryIdx].pt.y) == y) {
                            nndrMatchingCount++;
                            iterator2.remove();
                        }
                    }

                }
            }
        }

        analyzeModel.setNndrMatchingCount(nndrMatchingCount);
        analyzeModel.setKeypointsMatchingCount1(keypointsMatchingCount1);
    }

    private void calculateKeypointsMatching2(Mat imgA, Mat imgB){
        KeyPoint[] k2 = getImage2Keypoints().toArray();
        double keypointsMatchingCount2 = 0;

        List<KeyPoint> list = new ArrayList<>(Arrays.asList(k2));
        for (int i = 0; i < imgA.rows(); i++) {
            for (int j = 0; j < imgA.cols(); j++) {
                if (!isTransparent(imgB, i, j)) {
                    int x = j;
                    int y = i;

                    Iterator<KeyPoint> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        KeyPoint keypoint = iterator.next();
                        if ((int) (keypoint.pt.x) == x && (int) (keypoint.pt.y) == y) {
                            keypointsMatchingCount2++;
                            iterator.remove();
                        }
                    }

                }
            }
        }

        analyzeModel.setKeypointsMatchingCount2(keypointsMatchingCount2);
    }
}

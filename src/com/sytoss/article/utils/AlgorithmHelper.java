package com.sytoss.article.utils;

import static com.sytoss.article.handler.AlgorithmHandler.FILL;
import static com.sytoss.article.handler.AlgorithmHandler.RED;
import static com.sytoss.article.handler.AlgorithmHandler.WHITE;
import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.Core.perspectiveTransform;
import static org.opencv.imgproc.Imgproc.FONT_HERSHEY_PLAIN;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.getPerspectiveTransform;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sytoss.article.model.WrapImageModel;

public class AlgorithmHelper {

    public static Graphics2D combine(BufferedImage imgA, BufferedImage imgB, String path, String name) throws IOException {
        float alpha = 0.5f;
        int compositeRule = AlphaComposite.SRC_OVER;
        AlphaComposite ac;
        int imgW = Math.max(imgA.getWidth(), imgB.getWidth());
        int imgH = Math.max(imgA.getHeight(), imgB.getHeight());
        BufferedImage overlay = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = overlay.createGraphics();
        ac = AlphaComposite.getInstance(compositeRule, alpha);
        g.drawImage(imgA, 0, 0, null);
        g.setComposite(ac);
        g.drawImage(imgB, 0, 0, null);
        File resultFile = new File(path + "/" + name + ".jpg");
        ImageIO.write(overlay, "jpg", resultFile);
        return g;
    }

    public static double calculateOverlapArea(Mat imgA, Mat imgB){
        double overlapArea = 0;
        for (int i = 0; i < imgA.rows(); i++) {
            for (int j = 0; j < imgA.cols(); j++) {
                if (!isTransparent(imgA, i, j) && !isTransparent(imgB, i, j)) {
                    overlapArea++;
                }
            }
        }

        return overlapArea;
    }

    public static double calculateAllArea(Mat imgA){
        double overlapArea = 0;
        for (int i = 0; i < imgA.rows(); i++) {
            for (int j = 0; j < imgA.cols(); j++) {
                overlapArea++;
            }
        }

        return overlapArea;
    }

    public static boolean isTransparent(Mat image, int x, int y) {
        double[] pixel = image.get(x, y);
        return pixel[3] == 0;
    }

    public static WrapImageModel wrapImage1(Mat image1, Mat image2, Mat matrix, String name, String path){
        File file1 = new File(path + "\\image normalization");
        file1.mkdir();
        File file2 = new File(path + "\\overlap");
        file2.mkdir();
        int resWidth = image1.width() > image2.width() ? image1.width() : image2.width();
        int reHeight = image1.height() > image2.height() ? image1.height() : image2.height();
        Size finalSize = new Size(resWidth, reHeight);
        Mat normalizedImage = new Mat();
        Mat normalizedImage2 = new Mat();
        Mat normalizedImage3 = new Mat();
        Mat normalizedImage4 = new Mat();
        Mat zeros = Mat.zeros(3, 3, matrix.type());
        zeros.put(0,0, 1);
        zeros.put(1,1, 1);
        zeros.put(2,2, 1);
        Imgproc.warpPerspective(image2, normalizedImage, zeros, finalSize, Imgproc.INTER_LINEAR, BORDER_CONSTANT, FILL);
        Imgproc.warpPerspective(image1, normalizedImage2, matrix, finalSize, Imgproc.INTER_LINEAR, BORDER_CONSTANT, FILL);
        Imgproc.warpPerspective(image2, normalizedImage3, zeros, finalSize, Imgproc.INTER_LINEAR, BORDER_CONSTANT, WHITE);
        Imgproc.warpPerspective(image1, normalizedImage4, matrix, finalSize, Imgproc.INTER_LINEAR, BORDER_CONSTANT, RED);

        combineDirect(name, path, normalizedImage3, normalizedImage4);

        return new WrapImageModel(normalizedImage, normalizedImage2, calculateOverlapArea(normalizedImage, normalizedImage2));
    }

    public static WrapImageModel wrapImage2(Mat image1, Mat image2, Mat matrix, String name, String path){
        File file1 = new File(path + "\\image normalization");
        file1.mkdir();
        File file2 = new File(path + "\\overlap");
        file2.mkdir();
        int resWidth = image1.width() > image1.width() ? image1.width() : image1.width();
        int reHeight = image1.height() > image1.height() ? image1.height() : image1.height();
        MatOfPoint2f inputQuad = new MatOfPoint2f();
        org.opencv.core.Point[] points1 = new org.opencv.core.Point[4];
        points1[0] = new org.opencv.core.Point(0,0);
        points1[1] = new org.opencv.core.Point(image1.cols(),0);
        points1[2] = new org.opencv.core.Point(0,image1.rows());
        points1[3] = new org.opencv.core.Point(image1.cols(),image1.rows());
        inputQuad.fromArray(points1);
        MatOfPoint2f outputQuad = new MatOfPoint2f();
        perspectiveTransform(inputQuad, outputQuad, matrix);

        org.opencv.core.Point[] points0 = outputQuad.toArray();
        MatOfPoint2f inputCorners = new MatOfPoint2f();
        org.opencv.core.Point[] points3 = new org.opencv.core.Point[4];
        points3[0] = new org.opencv.core.Point(0,0);
        points3[1] =  new org.opencv.core.Point(image2.cols(),0);
        points3[2] =  new org.opencv.core.Point(0,image2.rows());
        points3[3] =  new org.opencv.core.Point(image2.cols(),image2.rows());
        inputCorners.fromArray(points3);
        MatOfPoint2f outputCorners = new MatOfPoint2f();
        perspectiveTransform(inputCorners, outputCorners, matrix.inv());
        Rect br = boundingRect(outputCorners);

        Mat normalizedImageInv = new Mat();
        Mat normalizedImageInv2 = new Mat();
        Mat normalizedImageInv3 = new Mat();
        Mat normalizedImageInv4 = new Mat();
        Size size = new Size(resWidth * 2, reHeight * 2);
        if (br.height <= size.height && br.width <= size.width){
            Mat zeros = Mat.zeros(3, 3, matrix.type());
            zeros.put(0,0, 1);
            zeros.put(1,1, 1);
            zeros.put(2,2, 1);
            Imgproc.warpPerspective(image2, normalizedImageInv, matrix.inv(), size, Imgproc.INTER_LINEAR, BORDER_CONSTANT, FILL);
            Imgproc.warpPerspective(image1, normalizedImageInv2, zeros, size, Imgproc.INTER_LINEAR, BORDER_CONSTANT, FILL);
            Imgproc.warpPerspective(image2, normalizedImageInv3, matrix.inv(), size, Imgproc.INTER_LINEAR, BORDER_CONSTANT, RED);
            Imgproc.warpPerspective(image1, normalizedImageInv4, zeros, size, Imgproc.INTER_LINEAR, BORDER_CONSTANT, WHITE);
        } else {
            if (br.x < -100) {
                double x = br.x;
                double y = br.y;
                br.x = -100;
                br.y = (int) ((-100 * y) / x);
            }

            if (br.y < -100) {
                double x = br.x;
                double y = br.y;
                br.y = -100;
                br.x = (int) ((-100 * x) / y);
            }

            org.opencv.core.Point[] points4 = new org.opencv.core.Point[4];
            for (int i = 0; i < 4; i++) {
                org.opencv.core.Point p = points1[i];
                points4[i] = new org.opencv.core.Point(p.x - br.x, p.y - br.x);
            }
            MatOfPoint2f m1 = new MatOfPoint2f();
            m1.fromArray(points0);
            MatOfPoint2f m2 = new MatOfPoint2f();
            m2.fromArray(points4);
            Mat M = getPerspectiveTransform(m1, m2);

            MatOfPoint2f m3 = new MatOfPoint2f();
            m3.fromArray(points1);
            Mat M2 = getPerspectiveTransform(m3, m2);

            Imgproc.warpPerspective(image2, normalizedImageInv, M, size, Imgproc.INTER_LINEAR, BORDER_CONSTANT, FILL);
            Imgproc.warpPerspective(image1, normalizedImageInv2, M2, size, Imgproc.INTER_LINEAR, BORDER_CONSTANT, FILL);
            Imgproc.warpPerspective(image2, normalizedImageInv3, M, size, Imgproc.INTER_LINEAR, BORDER_CONSTANT, RED);
            Imgproc.warpPerspective(image1, normalizedImageInv4, M2, size, Imgproc.INTER_LINEAR, BORDER_CONSTANT, WHITE);
        }
        combineInverse(name, path, normalizedImageInv3, normalizedImageInv4);
        return new WrapImageModel(normalizedImageInv, normalizedImageInv2, calculateOverlapArea(normalizedImageInv, normalizedImageInv2));
    }

    public static void combineDirect(String name, String path, Mat normalizedImage1, Mat normalizedImage2){
        File file1 = new File(path + "\\image normalization");
        File file2 = new File(path + "\\overlap");

        Imgproc.putText(normalizedImage2, name, new Point(normalizedImage1.width() / 8, 30), FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
        Imgcodecs.imwrite(file1.getPath() + "/norm_Img2_direct_" + name + ".png", normalizedImage1);
        Imgcodecs.imwrite(file1.getPath() + "/norm_Img1_direct_" + name + ".jpg", normalizedImage2);
        try {
            File f1 = new File(file1.getPath() + "/norm_Img2_direct_" + name +".png");
            File f2 = new File(file1.getPath() + "/norm_Img1_direct_" + name +".jpg");
            BufferedImage imgA = ImageIO.read(f1);
            BufferedImage imgB = ImageIO.read(f2);
            Graphics2D g = combine(imgA, imgB, file2.getPath(),   "overlap_direct_" + name );
            g.dispose();
            Files.delete(Paths.get(file1.getPath() + "/norm_Img2_direct_" + name +".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void combineInverse(String name, String path, Mat normalizedImage1, Mat normalizedImage2){
        File file1 = new File(path + "\\image normalization");
        File file2 = new File(path + "\\overlap");

        Imgproc.putText(normalizedImage2, name, new Point(normalizedImage1.width() / 8, 30), FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
        Imgcodecs.imwrite(file1.getPath() + "/norm_Img2_inverse_" + name + ".jpg", normalizedImage1);
        Imgcodecs.imwrite(file1.getPath() + "/norm_Img1_inverse_" + name + ".png", normalizedImage2);
        try {
            File f1 = new File(file1.getPath() + "/norm_Img2_inverse_" + name +".jpg");
            File f2 = new File(file1.getPath() + "/norm_Img1_inverse_" + name +".png");
            BufferedImage imgA = ImageIO.read(f1);
            BufferedImage imgB = ImageIO.read(f2);
            Graphics2D g = combine(imgA, imgB, file2.getPath(),  "overlap_inverse_" +  name);
            g.dispose();
            Files.delete(Paths.get(file1.getPath() + "/norm_Img1_inverse_" + name +".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

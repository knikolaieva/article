package com.sytoss.article.utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class AffineTransformUtils {

	public static final Scalar COLOR = new Scalar(0, 0, 255);

	public static Mat rotate(Mat imgIn, Mat imgOut, double angle, double scale,  Size finalSize) {
		Mat rotationMatrix = Imgproc.getRotationMatrix2D(new Point(imgIn.width() / 2, imgIn.height() / 2), angle, scale);
		Mat rotate = Mat.zeros(3,3, CvType.CV_64FC1);
		rotate.put(0,0,rotationMatrix.get(0,0));
		rotate.put(0,1,rotationMatrix.get(0,1));
		rotate.put(0,2,rotationMatrix.get(0,2));

		rotate.put(1,0,rotationMatrix.get(1,0));
		rotate.put(1,1,rotationMatrix.get(1,1));
		rotate.put(1,2,rotationMatrix.get(1,2));
		rotate.put(2, 2, 1);

		//Size finalSize = new Size(imgIn.width(), imgIn.height());
		Imgproc.warpPerspective(imgIn, imgOut, rotate, finalSize, Imgproc.INTER_CUBIC, Core.BORDER_CONSTANT, COLOR);
		return rotate;
	}

	public static Mat shear(Mat imgIn, Mat imgOut, double x, double y, Size finalSize) {
		Mat shear = Mat.zeros(3,3, CvType.CV_64FC1);
		shear.put(0, 0, 1);
		shear.put(0, 1, x);
		shear.put(0,2, - x * (finalSize.height/2));

		shear.put(1, 0, y);
		shear.put(1, 1, 1);
		shear.put(1,2, -y * (finalSize.width/2) );
		shear.put(2, 2, 1);


		Imgproc.warpPerspective(imgIn, imgOut, shear, finalSize, Imgproc.INTER_CUBIC, Core.BORDER_CONSTANT, COLOR);
		return shear;
	}

	public static Mat scaleXY(Mat imgIn, Mat imgOut, double value, Size finalSize) {
		Mat matrix2D = Imgproc.getRotationMatrix2D(new Point(imgIn.width() / 2, imgIn.height() / 2), 0, value);

		Mat scale = Mat.zeros(3,3, CvType.CV_64FC1);
		scale.put(0, 0, matrix2D.get(0,0));
		scale.put(0, 1, matrix2D.get(0,1));
		scale.put(0,2,  0);

		scale.put(1, 0, matrix2D.get(1,0));
		scale.put(1, 1, matrix2D.get(1,1));
		scale.put(1,2,  0);

		scale.put(2, 2, 1);


		Imgproc.warpPerspective(imgIn, imgOut, scale, finalSize, Imgproc.INTER_CUBIC, Core.BORDER_CONSTANT, COLOR);
		return scale;
	}

	public static Mat scaleY(Mat imgIn, Mat imgOut, double value2, Size finalSize) {
		Mat matrix2D = Imgproc.getRotationMatrix2D(new Point(imgIn.width() / 2, imgIn.height() / 2), 0, value2);

		Mat scale = Mat.zeros(3,3, CvType.CV_64FC1);
		scale.put(0, 0, 1);
		scale.put(1, 1, value2);
		scale.put(2, 2, 1);

		scale.put(0,2, 0);
		scale.put(1,2,  matrix2D.get(1,2));

		Imgproc.warpPerspective(imgIn, imgOut, scale, finalSize, Imgproc.INTER_CUBIC, Core.BORDER_CONSTANT, COLOR);
		return scale;
	}

	public static void transform(Mat imgIn, Mat imgOut, Mat M) {
		Size finalSize = new Size(imgIn.width(), imgIn.height());
		Imgproc.warpPerspective(imgIn, imgOut, M, finalSize, Imgproc.INTER_CUBIC, Core.BORDER_CONSTANT, COLOR);
	}

	public static void scale(Mat imgIn, Mat imgOut, double dx, double dy) {
		Size finalSize = new Size(dx * imgIn.width(), dy * imgIn.height());
		Imgproc.resize(imgIn, imgOut, finalSize);
	}
}

package hue;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class FilterColor {
	public static double getPercentBlue(Mat img) {
		Mat imgHsv = new Mat();
		Imgproc.cvtColor(img, imgHsv, Imgproc.COLOR_BGR2HSV);
		Mat blue = new Mat();

		final Scalar minBlue = new Scalar(100, 100, 0);
		final Scalar maxBlue = new Scalar(135, 255, 255);
		Core.inRange(imgHsv, minBlue, maxBlue, blue);

		double image_size = imgHsv.cols() * imgHsv.rows();
		double blue_percent = ((double) Core.countNonZero(blue)) / image_size;
		return blue_percent;
	}

	public static double getPercentWhite(Mat img) {
		Mat imgHsv = new Mat();
		Imgproc.cvtColor(img, imgHsv, Imgproc.COLOR_BGR2HSV);
		Mat white = new Mat();

		final Scalar minBlue = new Scalar(50, 0, 100);
		final Scalar maxBlue = new Scalar(180, 40, 255);
		Core.inRange(imgHsv, minBlue, maxBlue, white);
		double image_size = imgHsv.cols() * imgHsv.rows();
		double white_percent = ((double) Core.countNonZero(white)) / image_size;
		return white_percent;
	}
}

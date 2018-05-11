package hue;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

public class UniformCheck extends JFrame {
	private JFrame jFrame = new JFrame();
	private static JPanel contentPane = new JPanel();
	private static JLabel jLabel;
	private static String fileUniform = "D:\\UTC\\DoAn\\code_demo\\computer_vision\\detect_person\\detect_person\\uniform.png";

	UniformCheck() {
		setTitle("UniformCheck");
		setSize(400, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setLocationRelativeTo(null);

		JLabel label1 = new JLabel(new ImageIcon(createAwtImage(checkUniformPerson())));
		getContentPane().add(label1);
		validate();
	}

	private static double getPercent(Mat img) {
		Mat imgHsv = new Mat();
		Imgproc.cvtColor(img, imgHsv, Imgproc.COLOR_BGR2HSV);
		Mat blue = new Mat();

		final Scalar minBlue = new Scalar(100, 100, 0);
		final Scalar maxBlue = new Scalar(135, 255, 255);
		Core.inRange(imgHsv, minBlue, maxBlue, blue);
		// Imgproc.imshow("blue", blue);
		// ... do the same for blue, green, etc only changing the Scalar values
		// and the Mat
		double image_size = imgHsv.cols() * imgHsv.rows();
		double blue_percent = ((double) Core.countNonZero(blue)) / image_size;
		return blue_percent;
	}

	private static double checkUniform(Mat people, Mat uniformMask) {

		Mat peopleHSV = new Mat();
		Mat blue = new Mat();
		Rect rect = new Rect(0, people.rows() / 6, people.cols(), people.rows() / 3);
		Mat crop_img = new Mat(people, rect);
		Imgproc.cvtColor(crop_img, peopleHSV, Imgproc.COLOR_BGR2HSV);

		Scalar minBlue = new Scalar(100, 100, 0);
		Scalar maxBlue = new Scalar(135, 255, 255);
		Core.inRange(peopleHSV, minBlue, maxBlue, blue);

		Imgproc.resize(blue, blue, uniformMask.size());
		Size sizeMat = blue.size();
		int s = 0;
		for (int i = 0; i < blue.cols(); i++) {
			for (int j = 0; j < blue.rows(); j++) {
				double[] data1 = blue.get(j, i);
				double[] data2 = uniformMask.get(j, i);

				if (data1[0] == data2[0] && data1[1] == data2[1] && data1[2] == data2[2]) {
					s++;
				}
			}
		}
		return s * 1.0 / (blue.cols() * blue.rows());

	}

	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		UniformCheck unfi = new UniformCheck();
		unfi.setVisible(true);

	}

	private static Mat checkUniformPerson() {
		Mat img = Imgcodecs.imread("D:\\video_do_an\\dung_1.jpg");
		Mat uniform = Imgcodecs.imread(fileUniform);
		final HOGDescriptor hog = new HOGDescriptor();
		final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
		hog.setSVMDetector(descriptors);
		final MatOfRect foundPersons = new MatOfRect();
		final MatOfByte mem = new MatOfByte();
		final MatOfDouble foundWeights = new MatOfDouble();
		final Size winStride = new Size(8, 8);
		final Size padding = new Size(32, 32);
		final Point rectPoint1 = new Point();
		final Point rectPoint2 = new Point();
		final Point fontPoint = new Point(24, 24);
		final Scalar rectColor = new Scalar(0, 255, 0);
		final Scalar faceColor = new Scalar(0, 0, 255);
		final Scalar fontColor = new Scalar(255, 255, 255);
		final Graphics g;

		hog.detectMultiScale(img, foundPersons, foundWeights, 0.0, winStride, padding, 1.05, 2.0, false);

		if (foundPersons.rows() > 0) {
			final List<Double> weightList = foundWeights.toList();
			final List<Rect> rectList = foundPersons.toList();
			// final List<Rect> personDetected = new
			// ArrayList<Rect>();
			int index = 0;
			for (final Rect rect : rectList) {
				rectPoint1.x = rect.x;
				rectPoint1.y = rect.y;
				rectPoint2.x = rect.x + rect.width;
				rectPoint2.y = rect.y + rect.height;
				float scale = (float) ((float) (rectPoint2.x - rectPoint1.x) / (rectPoint2.y - rectPoint1.y));
				// Draw rectangle around fond object

				Imgproc.rectangle(img, rectPoint1, rectPoint2, rectColor, 2);
				Rect rectPerson = new Rect((int) rectPoint1.x, (int) rectPoint1.y, rect.width, rect.height);
				Mat image_roi = new Mat(img, rectPerson);

				double check = getPercent(image_roi);
				// double checkUniform = checkUniform(image_roi, uniform);

				fontPoint.x = rect.x;
				// illustration
				fontPoint.y = rect.y - 4;
				System.out.println("kq : " + check);
			}
			Imgcodecs.imencode(".bmp", img, mem);
			return img;

		}
		return null;

	}

	public static BufferedImage createAwtImage(Mat mat) {

		int type = 0;
		if (mat.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else if (mat.channels() == 3) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		} else {
			return null;
		}

		BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
		WritableRaster raster = image.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		mat.get(0, 0, data);

		return image;
	}

}

package hue;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
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
import javax.swing.BorderFactory;
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
	private static Graphics g;
	private static String pathImg = "D:\\video_do_an\\dung_1.jpg";
	private static String fileUniform = "D:\\UTC\\DoAn\\code_demo\\computer_vision\\detect_person\\detect_person\\uniform.png";
	private static String test = "D:\\UTC\\DoAn\\code_demo\\code_java\\ServerDetect\\data_result\\Img_1527237400141.PNG";

	UniformCheck() {
		setTitle("UniformCheck");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(1, 4));

		Mat img = Imgcodecs.imread(pathImg);
		Mat detect_img = checkUniformPerson();
		JLabel l1 = new JLabel(new ImageIcon(createAwtImage(img)));
		JLabel l2 = new JLabel(new ImageIcon(createAwtImage(detect_img)));
		JLabel l3 = new JLabel(new ImageIcon(createAwtImage(getMat(detect_img))));
		JLabel l4 = new JLabel(new ImageIcon(createAwtImage(getMatWhite(detect_img))));

		getContentPane().add(l1);
		getContentPane().add(l2);
		getContentPane().add(l3);
		getContentPane().add(l4);
		validate();
	}

	private static Mat getMat(Mat img) {
		Mat imgHsv = new Mat();
		Imgproc.cvtColor(img, imgHsv, Imgproc.COLOR_BGR2HSV);
		Mat blue = new Mat();
		final Scalar minBlue = new Scalar(100, 100, 0);
		final Scalar maxBlue = new Scalar(135, 255, 255);
		Core.inRange(imgHsv, minBlue, maxBlue, blue);
		double image_size = imgHsv.cols() * imgHsv.rows();
		double blue_percent = ((double) Core.countNonZero(blue)) / image_size;
		System.out.println("blue_percent: " + blue_percent);
		return blue;
	}

	private static Mat getMatWhite(Mat img) {
		Mat imgHsv = new Mat();
		Imgproc.cvtColor(img, imgHsv, Imgproc.COLOR_BGR2HSV);
		Mat white = new Mat();

		final Scalar minBlue = new Scalar(50, 0, 100);
		final Scalar maxBlue = new Scalar(180, 40, 255);
		Core.inRange(imgHsv, minBlue, maxBlue, white);
		double image_size = imgHsv.cols() * imgHsv.rows();
		double white_percent = ((double) Core.countNonZero(white)) / image_size;
		System.out.println("white_percent: " + white_percent);
		return white;
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
		Mat img = Imgcodecs.imread(pathImg);
//		Mat uniform = Imgcodecs.imread(fileUniform);
		final HOGDescriptor hog = new HOGDescriptor();
		final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
		hog.setSVMDetector(descriptors);
		final MatOfRect foundPersons = new MatOfRect();
		final MatOfDouble foundWeights = new MatOfDouble();
		final Size winStride = new Size(8, 8);
		final Size padding = new Size(32, 32);
		final Point rectPoint1 = new Point();
		final Point rectPoint2 = new Point();
		final Scalar rectColor = new Scalar(0, 255, 0);

		hog.detectMultiScale(img, foundPersons, foundWeights, 0.0, winStride, padding, 1.05, 2.0, false);

		if (foundPersons.rows() > 0) {
			final List<Rect> rectList = foundPersons.toList();
			for (final Rect rect : rectList) {
				rectPoint1.x = rect.x;
				rectPoint1.y = rect.y;
				rectPoint2.x = rect.x + rect.width;
				rectPoint2.y = rect.y + rect.height;
				// Draw rectangle around fond object

				Imgproc.rectangle(img, rectPoint1, rectPoint2, rectColor, 2);
				Rect rectPerson = new Rect((int) rectPoint1.x, (int) rectPoint1.y, rect.width, rect.height);
				Mat image_roi = null;
				image_roi = new Mat(img, rectPerson);

				return image_roi;
			}

			return null;

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

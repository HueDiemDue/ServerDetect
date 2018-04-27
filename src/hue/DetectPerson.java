package hue;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
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
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import models.HeapList;

public class DetectPerson extends JFrame {
	private static JPanel contentPane = new JPanel();
	private static String filePath = "D:\\UTC\\DoAn\\code\\Videos\\src\\gui\\haarcascade_fullbody.xml";
	private static String filePathVideo = "D:\\UTC\\DoAn\\code_demo\\code_java\\DetectVideo\\src\\video_2.mp4";

	public DetectPerson(JPanel contentPane) {
		setTitle("Video");
		setSize(600, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(contentPane);
		validate();
	}

	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		if (!Paths.get(filePathVideo).toFile().exists()) {
			System.out.println("File " + filePath + " does not exist!");
			return;
		} else {
			DetectPerson fm = new DetectPerson(contentPane);
			fm.setVisible(true);
			detectPerson(filePathVideo,contentPane);
		}

	}

	public static void detectPerson(String filePath,JPanel contentPane) throws IOException {
		Mat img = new Mat();

		final HOGDescriptor hog = new HOGDescriptor();
		final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
		hog.setSVMDetector(descriptors);
		Vector<Rect> found, found_filtered;

		final MatOfRect foundPersons = new MatOfRect();
		final MatOfByte mem = new MatOfByte();
		final MatOfDouble foundWeights = new MatOfDouble();
		final Size winStride = new Size(8, 8);
		final Size padding = new Size(32, 32);
		final Point rectPoint1 = new Point();
		final Point rectPoint2 = new Point();
		final Point fontPoint = new Point(24, 24);

		final VideoCapture videoCapture = new VideoCapture(filePath);
		final Size frameSize = new Size((int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),
				(int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));

		// Preliminaries for faces detection
		CascadeClassifier faceDetector = new CascadeClassifier(
				"D:\\UTC\\DoAn\\code_demo\\code_java\\ServerDetect\\haarcascade_fullbody.xml");
		MatOfRect foundFaces = new MatOfRect();

		// Misc variables
		final Scalar rectColor = new Scalar(0, 255, 0);
		final Scalar faceColor = new Scalar(0, 0, 255);
		final Scalar fontColor = new Scalar(255, 255, 255);

		// Algorithm variables
		HeapList<MatOfRect> previousDetections = new HeapList<MatOfRect>(2);
		int framesNoPeople = 0;
		int soldePersons = 0;
		int faces = 0;
		int persons = 0;
		Graphics g;
		int framesWithPeople = 0;
		int frames = 0;

		if (!videoCapture.isOpened()) {
			System.out.println("Camera Error!");
			return;
		} else {
			while (videoCapture.read(img)) {
				hog.detectMultiScale(img, foundPersons, foundWeights, 0.0, winStride, padding, 1.05, 2.0, false);
				if (foundPersons.rows() > 0) {
					framesWithPeople++;
					g = contentPane.getGraphics();
					final List<Double> weightList = foundWeights.toList();
					final List<Rect> rectList = foundPersons.toList();
					int index = 0;
					for (final Rect rect : rectList) {
						rectPoint1.x = rect.x;
						rectPoint1.y = rect.y;
						rectPoint2.x = rect.x + rect.width;
						rectPoint2.y = rect.y + rect.height;
						// Draw rectangle around fond object
						Imgproc.rectangle(img, rectPoint1, rectPoint2, rectColor, 2);
						fontPoint.x = rect.x;
						// illustration
						fontPoint.y = rect.y - 4;
						// Print weight
						// illustration
						Imgproc.putText(img, String.format("%1.2f", weightList.get(index)), fontPoint,
								Core.FONT_HERSHEY_PLAIN, 1.5, fontColor, 2, Core.LINE_AA, false);
						index++;
						// định dạng ảnh khi vẽ lên jpanel
						Imgcodecs.imencode(".bmp", img, mem);

						// Highgui.imencode(".jpg", frame, mem);
						// ảnh của webcam là một array
						Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

						BufferedImage buff = (BufferedImage) im;

						
					}
				}

				frames++;
			}
		}
	}
}

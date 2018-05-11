package test;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

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

import hue.DetectPerson;

public class Benchmark extends JFrame {
	private static JPanel contentPane = new JPanel();
	private static String title = "Server Detection";
	private static String filePathVideo = "E:\\FFOutput\\video_do_an_3.mp4";

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// TODO Auto-generated method stub

		if (!Paths.get(filePathVideo).toFile().exists()) {
			System.out.println("File " + filePathVideo + " does not exist!");
			return;
		} else {
			Benchmark fm = new Benchmark(contentPane);
			fm.setVisible(true);
			detect(filePathVideo);
		}

		PeopleCount.detect(filePathVideo);

	}

	public Benchmark(JPanel contentPane) {
		setTitle(title);
		setSize(400, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setLocationRelativeTo(null);
		add(contentPane);
		validate();
	}

	public static void detect(String url) {
		final VideoCapture videoCapture = new VideoCapture(url);
		final Size frameSize = new Size((int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),
				(int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));

		System.out.println(String.format("Resolution: %s", frameSize));

		final FourCC fourCC = new FourCC("X264");
		// final VideoWriter videoWriter = new VideoWriter(outputFile,
		// fourCC.toInt(), videoCapture.get(Videoio.CAP_PROP_FPS),
		// frameSize, true);
		Mat mat = new Mat();
		Mat img = new Mat();

		// Preliminaries for person detection
		final HOGDescriptor hog = new HOGDescriptor();
		final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
		hog.setSVMDetector(descriptors);

		final MatOfRect foundPersons = new MatOfRect();
		final MatOfDouble foundWeights = new MatOfDouble();
		final MatOfByte men = new MatOfByte();
		final Size winStride = new Size(8, 8);
		final Size padding = new Size(32, 32);
		final Point rectPoint1 = new Point();
		final Point rectPoint2 = new Point();
		final Point fontPoint = new Point(24, 24);

		// Preliminaries for faces detection
		CascadeClassifier faceDetector = new CascadeClassifier(
				"C:\\Program Files (x86)\\OpenCV340\\sources\\data\\haarcascades\\haarcascade_frontalcatface.xml");
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
		Point lineStartPoint = new Point(0, 450);
		Point lineEndPoint = new Point(frameSize.width, 450);

		if (!videoCapture.isOpened()) {
			System.out.println("Camera Error!");
			return;
		} else {
			System.out.println("Camera is ready!");

			while (true) {
				// ve len JPanel
				if (videoCapture.grab()) {
					try {
						// Imgproc.equalizeHist(img,mat);
						videoCapture.retrieve(mat);

						g = contentPane.getGraphics();

						// Persons detection
						hog.detectMultiScale(mat, foundPersons, foundWeights, 0.0, winStride, padding, 1.05, 2.0,
								false);
						Imgproc.line(mat, lineStartPoint, lineEndPoint, new Scalar(255, 12, 0), 2);

						// Faces detection
						faceDetector.detectMultiScale(mat, foundFaces);

						if (foundPersons.rows() > 0) {

							List<Double> weightList = foundWeights.toList();
							List<Rect> rectList = foundPersons.toList();

							for (Rect rect : rectList) { // Draws rectangles
															// around people
								rectPoint1.x = rect.x;
								rectPoint1.y = rect.y;
								rectPoint2.x = rect.x + rect.width;
								rectPoint2.y = rect.y + rect.height;
								// Draw rectangle around fond object
								Imgproc.rectangle(mat, rectPoint1, rectPoint2, rectColor, 2);
								// CHECKSTYLE:ON MagicNumber
							}
							soldePersons += PeopleTrack.countNewPersons(foundPersons, previousDetections,
									foundFaces);
							for (Rect rect : foundFaces.toArray()) {
								// Draw rectangles around faces
								rectPoint1.x = rect.x;
								rectPoint1.y = rect.y;
								rectPoint2.x = rect.x + rect.width;
								rectPoint2.y = rect.y + rect.height;

								Imgproc.rectangle(mat, rectPoint1, rectPoint2, faceColor);
							}
							// định dạng ảnh khi vẽ lên jpanel
							Imgcodecs.imencode(".bmp", mat, men);
							// ảnh của webcam là một array
							Image im = ImageIO.read(new ByteArrayInputStream(men.toArray()));
							BufferedImage buff = (BufferedImage) im;
							// vẽ chuỗi ảnh của webcam vào jpanel
							g.drawImage(buff, 0, 0, buff.getWidth(), buff.getHeight(), null);
							Imgproc.putText(mat, String.format("People counted : %d", soldePersons), fontPoint,
									Core.FONT_HERSHEY_PLAIN, 0.8, fontColor, 2, Core.LINE_AA, false);

							previousDetections.queue(new MatOfRect(foundPersons));
							System.out.println("Persons counted : " + soldePersons);

						} else {
							// framesNoPeople++;
						}
						System.out.println("Persons counted : " + soldePersons);
					} catch (Exception ex) {
						System.out.println("Error");
					}
				}
				// CHECKSTYLE:ON MagicNumber

			}

		}
	}
}

package hue;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import models.HeapList;

public class DetectPerson extends JFrame {
	private static JPanel contentPane = new JPanel();
	private static String title = "Server Detection";
	private static String filePath = "D:\\UTC\\DoAn\\code\\Videos\\src\\gui\\haarcascade_fullbody.xml";
	private static String filePathVideo = "E:\\FFOutput\\video_do_an_3.mp4";
	private static String filePathImg = "D:\\UTC\\DoAn\\code_demo\\code_java\\ServerDetect\\data_result";

	public DetectPerson(JPanel contentPane) {
		setTitle(title);
		setSize(400, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setLocationRelativeTo(null);
		add(contentPane);
		validate();
	}

	private static double getPercent(Mat img) {
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

	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		try {
			// mo cong ket noi
			System.out.println("Server is running....");
			ServerSocket sk = new ServerSocket(7819);
			// listen tu cong ket noi
			Socket client = sk.accept();// cai nay no la client nhe kophai

			// nhan du lieu
			DataInputStream is = new DataInputStream(client.getInputStream());
			DataOutputStream os = new DataOutputStream(client.getOutputStream());
			// gui object di
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());

			// date now
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();

			if (is.readUTF().equals("connect")) {
				System.out.println("Have a device connect to server!");
				// gui di
				os.writeUTF("Connected successfully!!");

				if (!Paths.get(filePathVideo).toFile().exists()) {
					System.out.println("File " + filePathVideo + " does not exist!");
					return;
				} else {
					DetectPerson fm = new DetectPerson(contentPane);
					fm.setVisible(true);
					detectPerson(filePathVideo, oos);
					oos.close();
				}
				// gui du lieu

				System.out.println("sending object done!!!");
			}

			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void detectPerson(String filePath, ObjectOutputStream oos) throws IOException {
		// set camera
		Mat img = new Mat();
		Mat mat = new Mat();
		final VideoCapture videoCapture = new VideoCapture(filePath);
		final Size frameSize = new Size((int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),
				(int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));

		videoCapture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 420);
		videoCapture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 600);

		// hog
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

		// Misc variables
		final Scalar trueColor = new Scalar(0, 255, 0);
		final Scalar falseColor = new Scalar(0, 0, 255);
		final Scalar fontColor = new Scalar(255, 255, 255);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		Graphics g;
		int frames = 0;
		final List<Rect> rected = new ArrayList<>();

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
					if (frames % 10 == 0) {
						try {
							Imgproc.equalizeHist(mat, img);
							videoCapture.retrieve(img);
							g = contentPane.getGraphics();

							hog.detectMultiScale(img, foundPersons, foundWeights, 0.0, winStride, padding, 1.05, 2.0,
									false);

							// draw a line
							Imgproc.line(img, lineStartPoint, lineEndPoint, new Scalar(255, 12, 0), 2);

							if (foundPersons.rows() > 0) {

								final List<Rect> rectList = foundPersons.toList();

								float scale = 0;

								for (final Rect rect : rectList) {
									rectPoint1.x = rect.x;
									rectPoint1.y = rect.y;
									rectPoint2.x = rect.x + rect.width;
									rectPoint2.y = rect.y + rect.height;
									scale = (float) ((float) (rectPoint2.x - rectPoint1.x)
											/ (rectPoint2.y - rectPoint1.y));

									if (rectPoint2.y > 400 && rectPoint2.y < 550 && scale > 0.48 && scale < 0.52) {
										Rect rectPerson = new Rect((int) rectPoint1.x, (int) rectPoint1.y, rect.width,
												rect.height);
										rected.add(rectPerson);

										Mat image_roi = new Mat(img, rectPerson);
										double check = getPercent(image_roi);

										if (check > 0.05 && check < 0.09) {
											Imgproc.rectangle(img, rectPoint1, rectPoint2, trueColor, 2);
										} else {
											Imgproc.rectangle(img, rectPoint1, rectPoint2, falseColor, 2);

											// gui du lieu
											System.out.println(System.currentTimeMillis());
											String filePathName = "data_result\\Img_" + System.currentTimeMillis()
													+ ".png";

											Imgcodecs.imwrite(filePathName, image_roi);
											String datetime = dateFormat.format(date);
											System.out.println(datetime);

											FileInputStream fis = new FileInputStream(filePathName);
											byte[] buffer = new byte[fis.available() + 2];
											fis.read(buffer);

											oos.writeObject(datetime);
											oos.writeObject(buffer);
											oos.flush();
											

										}

										System.out.println(" thong so : " + getPercent(image_roi));

									}
								}

								// định dạng ảnh khi vẽ lên jpanel
								Imgcodecs.imencode(".bmp", img, mem);
								// ảnh của webcam là một array
								Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
								BufferedImage buff = (BufferedImage) im;
								// vẽ chuỗi ảnh của webcam vào jpanel
								g.drawImage(buff, 0, 0, buff.getWidth(), buff.getHeight(), null);

							}
						} catch (Exception ex) {
							System.out.println("Error");
						}
					}
					frames++;
				}
				// System.out.println("All frame: " + frames);

			}

		}
	}

}

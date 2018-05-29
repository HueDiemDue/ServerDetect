package hue;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

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
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class ThreadConnectServer extends Thread {
	protected Socket socket;
	private static String filePathVideo = "E:\\FFOutput\\video_do_an_3.mp4";
	private static JPanel contentPane = new JPanel();

	public ThreadConnectServer(Socket clientSocket) {
		this.socket = clientSocket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		// nhan du lieu
		DataInputStream is;
		try {
			is = new DataInputStream(socket.getInputStream());

			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			// gui object di
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

			// if (is.readUTF().equals("connect")) {
			System.out.println("Have a device connect to server!");
			// gui di
			os.writeUTF("Connected successfully!!");
			if (!Paths.get(filePathVideo).toFile().exists()) {
				System.out.println("File " + filePathVideo + " does not exist!");
				return;
			} else {
				DetectPerson fm = new DetectPerson(contentPane);
				fm.setVisible(true);
				detectPerson(filePathVideo, contentPane, oos);
			}
			//
			// try {
			//// System.out.println("Value Client: " + DetectPerson.checkImg);
			// while (DetectPerson.checkImg == true) {
			// System.out.println("Value Client: " + DetectPerson.pathImg + "_"
			// + DetectPerson.timer);
			//
			// FileInputStream fis = new FileInputStream(DetectPerson.pathImg);
			// byte[] buffer = new byte[fis.available() + 2];
			// fis.read(buffer);
			//// System.out.println("sending object done!!!");
			// Thread.sleep(50);
			//
			// oos.writeObject(DetectPerson.timer);
			// oos.writeObject(buffer);
			// oos.flush();
			//
			// }
			// } catch (InterruptedException e) {
			// System.out.println("Thread interrupted.");
			// }

			// }
			oos.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void detectPerson(String filePath, JPanel contentPane, ObjectOutputStream oos) throws IOException {
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
							Imgproc.line(img, lineStartPoint, lineEndPoint, fontColor, 2);

							if (foundPersons.rows() > 0) {

								final List<Rect> rectList = foundPersons.toList();

								float scale = 0;
								// try {
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
										Mat image_roi = new Mat(img, rectPerson);
										double check_blue = FilterColor.getPercentBlue(image_roi);
										double check_white = FilterColor.getPercentWhite(image_roi);

										if (check_blue > 0.05 && check_blue < 0.09 && check_white > 0.15) {
											Imgproc.rectangle(img, rectPoint1, rectPoint2, trueColor, 2);
											DetectPerson.checkImg = false;

										} else {
											Imgproc.rectangle(img, rectPoint1, rectPoint2, falseColor, 2);
											System.out.println(" thong so: " + check_blue + "_" + check_white);

											// gui du lieu

											// System.out.println(System.currentTimeMillis());
											String filePathName = "data_result\\Img_" + System.currentTimeMillis()
													+ ".png";

											Imgcodecs.imwrite(filePathName, image_roi);
											String datetime = dateFormat.format(date);
											System.out.println(datetime);

											FileInputStream fis = new FileInputStream(filePathName);
											byte[] buffer = new byte[fis.available() + 2];
											fis.read(buffer);
											// System.out.println("sending
											// object done!!!");
											Thread.sleep(50);

											oos.writeObject(datetime);
											oos.writeObject(buffer);
											oos.flush();

										}
									}
								}
								// } catch (InterruptedException e) {
								// System.out.println("Thread interrupted.");
								// }

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
					// DetectPerson.checkImg = false;
				}
				// System.out.println("All frame: " + frames);
				//
			}
		}
	}

}

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

			
//			if (is.readUTF().equals("connect")) {
				System.out.println("Have a device connect to server!");
				// gui di
				os.writeUTF("Connected successfully!!");
				
			
				try {
					System.out.println("Value Client: " + DetectPerson.checkImg);
					while (DetectPerson.checkImg == true) {
						System.out.println("Value Client: " + DetectPerson.pathImg + "_" + DetectPerson.timer);
						FileInputStream fis = new FileInputStream(DetectPerson.pathImg);
						byte[] buffer = new byte[fis.available() + 2];
						fis.read(buffer);
						System.out.println("sending object done!!!");
						Thread.sleep(50);

						oos.writeObject(DetectPerson.timer);
						oos.writeObject(buffer);
						oos.flush();
						
					}
				} catch (InterruptedException e) {
					System.out.println("Thread interrupted.");
				}

//			}
//			 oos.close();
//			 socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
}

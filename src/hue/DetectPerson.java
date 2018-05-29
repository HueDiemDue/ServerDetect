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
import java.util.logging.Handler;

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
	public static String filePathVideo = "E:\\FFOutput\\video_do_an_3.mp4";
	private static String filePathImg = "D:\\UTC\\DoAn\\code_demo\\code_java\\ServerDetect\\data_result";
	public static String pathImg = "";
	public static boolean checkImg = false;
	public static String timer = "";

	public DetectPerson(JPanel contentPane) {
		setTitle(title);
		setSize(400, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setLocationRelativeTo(null);
		add(contentPane);
		validate();
	}

	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		ServerSocket sk = null;
		Socket client = null;
		// hai thread chay song son detect va ket noi server
//		new ThreadDetect().start();
		try {
			// mo cong ket noi
			System.out.println("Server is running....");
			sk = new ServerSocket(7819);
			while (true) {
				try {
					client = sk.accept();
				} catch (IOException e) {
					System.out.println("I/O error: " + e);
				}
				// new thread for a client

				new ThreadConnectServer(client).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

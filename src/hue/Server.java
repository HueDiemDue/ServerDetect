package hue;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
	public static void main(String[] args) {
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

				// gui du lieu
				String datetime = dateFormat.format(date);
				System.out.println(datetime);
				// luu file anh
				// send
				FileInputStream fis = new FileInputStream("D:\\hue.jpg");
				byte[] buffer = new byte[fis.available() + 5];
				fis.read(buffer);

				oos.writeObject(datetime);
				oos.writeObject(buffer);
				oos.flush();

				// }
				// oos.close();

				System.out.println("sending object done!!!");
			}

			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

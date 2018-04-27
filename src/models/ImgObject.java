package models;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ImgObject implements Serializable {
	private static final long serialVersionUID = -5399605122490343339L;

	private String time = "";
	private String data = "";

	public ImgObject(String time, String data) {
		super();
		this.time = time;
		this.data = data;
	}

	public ImgObject(String time) {
		super();
		this.time = time;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.time);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
//        ImgObject imgObject = (ImgObject)in.readObject();
        this.time = (String) in.readObject();
    }
}
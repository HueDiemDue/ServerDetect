package test;

public class MainThread {

	public static String test = "hue";
	public static int i = 0;
	public static boolean check = false;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread1 R1 = new Thread1("Thread-1");
		R1.start();

		Thread2 R2 = new Thread2("Thread-2");
		R2.start();

	}

	
}

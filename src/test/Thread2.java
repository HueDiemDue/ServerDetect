package test;

public class Thread2 extends Thread {
	private Thread t;
	private String threadName;

	Thread2(String name) {
		threadName = name;
		System.out.println("Creating " + threadName);
	}

	public void run() {
		System.out.println("Running " + threadName);
		System.out.println(MainThread.check +" thread2");
		try {
			while (MainThread.check == true) {
				
				System.out.println("Thread2: " + MainThread.test );
				// De thread ngung trong choc lat.
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			System.out.println("Thread " + threadName + " interrupted.");
		}
		System.out.println("Thread " + threadName + " exiting.");

	}

	public void start() {
		System.out.println("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
}

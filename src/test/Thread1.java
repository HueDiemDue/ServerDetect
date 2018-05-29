package test;

public class Thread1 extends Thread {
	private Thread t;
	private String threadName;

	Thread1(String name) {
		threadName = name;
		System.out.println("Creating " + threadName);
	}

	public void run() {
		System.out.println("Running " + threadName);

		int i = MainThread.i;
		int j = 0;
		
			while (j < 20) {

				for (i = 0; i < 15; i++) {
					if (i % 3 ==0) {
						try {
						MainThread.check = true;
						System.out.println("Thread1: " + "hue" + ", " + i);
						MainThread.test = "hue" + "" + i;
						// De thread ngung trong choc lat.
						Thread.sleep(50);
						} catch (InterruptedException e) {
							System.out.println("Thread " + threadName + " interrupted.");
						}
					}
					else{
						MainThread.check = false;
					}
				}
				
				System.out.println("ddddd");
				j++;
				
			}
			MainThread.check = false;

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

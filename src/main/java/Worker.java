

import org.apache.log4j.Logger;


public class Worker {

	// private KeepAlive keepAlive;
	private HandleSteal handleSteal;
	private int sleep = 6;
	private static Logger logger = Logger.getLogger(Worker.class);

	public Worker(HandleSteal handleSteal, int sleep) {
		super();
		// this.keepAlive = keepAlive;
		this.handleSteal = handleSteal;
		this.sleep = sleep;
	}

	public void run() {
		String printStr = "";

		while (true) {

			// step: Íµ»¨¶¯×÷
			handleSteal.run();

			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// logger.info(e.getMessage());
				System.out.println(e.getMessage());
			}
		}
	}

}

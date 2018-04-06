import org.apache.log4j.Logger;
public class Worker {
	private HandleSteal handleSteal;
	private int sleep = 6;
	private static Logger logger = Logger.getLogger(Worker.class);
	private long var_watch = 0; // 20s

	public Worker(HandleSteal handleSteal) {
		super();
		this.handleSteal = handleSteal;
	}

	public void run() {
		String printStr = "";

		while (true) {

			// step 1:打印中间观察量
			if (var_watch >= 3000) {

				handleSteal.initCounterFromRedis();
				printStr = "mode=" + handleSteal.getMode() + ",modeTime=" + handleSteal.getModeTime()
						+ ",delayCompare=" + handleSteal.getDelayCompare()+ ",beforeCompare=" + handleSteal.getBeforeCompare()
						+ ",delayCompare2=" + handleSteal.getDelayCompare2()+ ",beforeCompare2=" + handleSteal.getBeforeCompare2()
						+",before=" + handleSteal.getBefore();

				logger.info(printStr);
				var_watch = 0;
			} else {
				var_watch = var_watch + 1;
			}

			// step: 偷花动作
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

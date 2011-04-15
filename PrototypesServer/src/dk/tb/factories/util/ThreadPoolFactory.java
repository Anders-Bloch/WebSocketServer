package dk.tb.factories.util;

public class ThreadPoolFactory {
	
	private ThreadPoolFactory() {}
	
	public static ServerThreadPool getThreadPool() {
		return new ServerThreadPoolImpl();
	}
}

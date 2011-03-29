package dk.tb.factories.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerThreadPoolImpl implements ServerThreadPool {

	private final int poolSize = 50;
	private final int maxPoolSize = 110;
	private final long keepAliveTime = 10;
    private final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(50);
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(poolSize,maxPoolSize,keepAliveTime,TimeUnit.SECONDS,queue);
	
	@Override
	public void runTask(Runnable task) {
		threadPool.execute(task);
	}

	@Override
	public void shutDown() {
		threadPool.shutdown();
	}

}

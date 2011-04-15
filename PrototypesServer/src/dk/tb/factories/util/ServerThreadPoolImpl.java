package dk.tb.factories.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerThreadPoolImpl implements ServerThreadPool {

	//Meget h¿jt pga. JMeter tests
	private final int poolSize = 200;
	private final int maxPoolSize = 300;
	private final long keepAliveTime = 10;
    private final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(300);
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

package dk.tb.factories.util;

public interface ThreadPool {
	public void runTask(Runnable task);
	public void shutDown();
}

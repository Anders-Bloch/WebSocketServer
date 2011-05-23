package dk.tb.server.util;

public interface ThreadPool {
	public void runTask(Runnable task);
	public void shutDown();
}

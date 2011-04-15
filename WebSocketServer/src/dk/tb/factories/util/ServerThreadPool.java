package dk.tb.factories.util;

public interface ServerThreadPool {
	public void runTask(Runnable task);
	public void shutDown();
}

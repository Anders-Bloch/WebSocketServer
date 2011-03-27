package dk.tb.factories;

import dk.tb.factories.util.ServerThreadPool;
import dk.tb.handlers.RequestHandler;

public interface ServerFactory {
	public RequestHandler getRequestHandler();
	public ServerThreadPool getThreadPool();
}

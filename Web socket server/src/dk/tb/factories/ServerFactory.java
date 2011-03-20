package dk.tb.factories;

import dk.tb.handlers.RequestHandler;

public interface ServerFactory {
	public RequestHandler getRequestHandler();
}

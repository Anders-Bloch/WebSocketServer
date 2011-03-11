package dk.tb.factories;

import dk.tb.handler.RequestHandler;

public interface ServerFactory {
	public RequestHandler getRequestHandler();
}

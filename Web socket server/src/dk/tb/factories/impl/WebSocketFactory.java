package dk.tb.factories.impl;

import dk.tb.factories.ServerFactory;
import dk.tb.handler.RequestHandler;
import dk.tb.handler.impl.WebSocketRequestHandler;
import dk.tb.server.ClientPool;

public class WebSocketFactory implements ServerFactory {
	
	private ClientPool pool = new ClientPool();

	@Override
	public RequestHandler getRequestHandler() {
		return new WebSocketRequestHandler(pool);
	}

}

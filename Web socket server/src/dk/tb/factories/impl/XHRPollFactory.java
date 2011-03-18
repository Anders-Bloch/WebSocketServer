package dk.tb.factories.impl;

import dk.tb.factories.ServerFactory;
import dk.tb.handler.RequestHandler;
import dk.tb.handler.impl.XHRPollRequestHandler;
import dk.tb.pool.ClientPool;
import dk.tb.pool.impl.SimpleClientPool;

public class XHRPollFactory implements ServerFactory {
	
	private ClientPool pool = new SimpleClientPool();
	
	@Override
	public RequestHandler getRequestHandler() {
		return new XHRPollRequestHandler(pool);
	}

}

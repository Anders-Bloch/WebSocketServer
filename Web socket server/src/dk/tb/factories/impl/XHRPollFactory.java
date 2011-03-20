package dk.tb.factories.impl;

import java.io.IOException;
import java.net.Socket;

import dk.tb.clients.Client;
import dk.tb.clients.impl.XHRPollClient;
import dk.tb.factories.PoolAndClientFactory;
import dk.tb.factories.ServerFactory;
import dk.tb.handlers.RequestHandler;
import dk.tb.handlers.impl.XHRPollRequestHandler;
import dk.tb.pools.ClientPool;
import dk.tb.pools.impl.SimpleClientPool;

public class XHRPollFactory implements ServerFactory {
	
	private final ClientPool pool = new SimpleClientPool();
	
	@Override
	public RequestHandler getRequestHandler() {
		return new XHRPollRequestHandler(new PoolAndClientFactory() {
			
			@Override
			public ClientPool getPool() {
				return pool;
			}
			
			@Override
			public Client getNewClient(Socket socket) throws IOException {
				return new XHRPollClient(socket);
			}
		});
	}

}

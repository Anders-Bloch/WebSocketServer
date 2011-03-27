package dk.tb.factories.impl;

import java.io.IOException;
import java.net.Socket;

import dk.tb.clients.Client;
import dk.tb.clients.impl.IFrameClient;
import dk.tb.factories.PoolAndClientFactory;
import dk.tb.factories.ServerFactory;
import dk.tb.factories.util.ServerThreadPool;
import dk.tb.factories.util.ThreadPoolFactory;
import dk.tb.handlers.RequestHandler;
import dk.tb.handlers.impl.IFrameRequestHandler;
import dk.tb.pools.ClientPool;
import dk.tb.pools.impl.CommonClientPool;

public class IFrameFactory implements ServerFactory {
	
	private final ClientPool pool = new CommonClientPool();
	private final ServerThreadPool threadPool = ThreadPoolFactory.getThreadPool();
	
	@Override
	public RequestHandler getRequestHandler() {
		return new IFrameRequestHandler(new PoolAndClientFactory() {
				@Override
				public ClientPool getPool() {
					return pool;
				}
				
				@Override
				public Client getNewClient(Socket socket) throws IOException {
					return new IFrameClient(socket.getOutputStream());
				}
				
				@Override
				public ServerThreadPool getThreadPool() {
					return threadPool;
				}
			}
		);
	}

	@Override
	public ServerThreadPool getThreadPool() {
		return threadPool;
	}
}

package dk.tb.handlers.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import dk.tb.clients.Client;
import dk.tb.factories.PoolAndClientFactory;
import dk.tb.handlers.handshakes.Handshake;
import dk.tb.handlers.handshakes.Handshake76;
import dk.tb.handlers.util.Keys;

public class WebSocketRequestHandler extends AbstractRequestHandler {

	private Handshake handshake;
	
	public WebSocketRequestHandler(PoolAndClientFactory factory) {
		super(factory);
		handshake = new Handshake76();
	}

	@Override
	public void handleRun() throws IOException {
		writeOutput(out, header);
		startClient();
	}

	private void writeOutput(OutputStream out, Map<Keys, String> header) throws IOException {
		byte[] bytes = handshake.createResponseHandshake(header);
		out.write(bytes);
		out.flush();
	}

	private void startClient() throws IOException {
		Client client = factory.getNewClient(socket);
		pool.addClient(client);
		factory.getThreadPool().runTask((Runnable)client);
		//((Thread)client).start(); Udskiftet med thread pool
	}
}

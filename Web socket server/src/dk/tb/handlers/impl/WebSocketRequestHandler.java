package dk.tb.handlers.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.Client;
import dk.tb.factories.PoolAndClientFactory;
import dk.tb.handlers.handshakes.Handshake;
import dk.tb.handlers.handshakes.Handshake76;
import dk.tb.handlers.util.Keys;

public class WebSocketRequestHandler extends AbstractRequestHandler {
	
	private final Logger logger = LoggerFactory.getLogger(WebSocketRequestHandler.class);
	
	public WebSocketRequestHandler(PoolAndClientFactory factory) {
		super(factory);
	}

	@Override
	public void handleRun() throws IOException {
		writeOutput(out, header);
		startClient();
	}

	private void writeOutput(OutputStream out, Map<Keys, String> header) throws IOException {
		Handshake handshake = new Handshake76();
		byte[] bytes = handshake.createResponseHandshake(header);
		for (int i = 0; i < bytes.length; i++) {
			out.write(bytes[i]);
			System.out.println(bytes[i]);
		}
		out.flush();
	}

	private void startClient() throws IOException {
		Client client = factory.getNewClient(socket);
		pool.addClient(client);
		factory.getThreadPool().runTask((Runnable)client);
		//((Thread)client).start(); Udskiftet med thread pool
	}
}

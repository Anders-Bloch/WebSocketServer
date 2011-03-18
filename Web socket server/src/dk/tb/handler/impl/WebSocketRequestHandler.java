package dk.tb.handler.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.impl.WebSocketClient;
import dk.tb.pool.ClientPool;
import dk.tb.server.util.Keys;
import dk.tb.websocket.handshakes.Handshake;
import dk.tb.websocket.handshakes.Handshake76;

public class WebSocketRequestHandler extends AbstractRequestHandler {
	
	private final Logger logger = LoggerFactory.getLogger(WebSocketRequestHandler.class);
	private ClientPool pool;
	
	public WebSocketRequestHandler(ClientPool pool) {
		this.pool = pool;
	}

	@Override
	public void handleRun() throws IOException {
		writeOutput(out, header);
		startClient(out, in);
	}

	private void writeOutput(OutputStream out, Map<Keys, String> header) throws IOException {
		Handshake handshake = new Handshake76();
		byte[] bytes = handshake.createResponseHandshake(header);
		for (int i = 0; i < bytes.length; i++) {
			out.write(bytes[i]);
		}
		out.flush();
	}

	private void startClient(OutputStream out, BufferedReader in) {
		WebSocketClient client = new WebSocketClient(pool,out,in);
		pool.addClient(client);
		client.start();
	}
}

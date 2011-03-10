package dk.tb.handler.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.handler.RequestHandler;
import dk.tb.server.Client;
import dk.tb.server.ClientPool;
import dk.tb.server.Handshake;
import dk.tb.server.handshakes.Handshake76;
import dk.tb.server.util.RequestHeaderMap;

public class WebSocketRequestHandler implements RequestHandler {
	
	private final Logger logger = LoggerFactory.getLogger(WebSocketRequestHandler.class);
	private Socket socket;
	private ClientPool pool;
	
	public WebSocketRequestHandler(ClientPool pool) {
		this.pool = pool;
	}
	
	@Override
	public void handleRequest(Socket socket) throws IOException {
		logger.info("handle request called");
		this.socket = socket;
	}

	@Override
	public void run() {
		logger.info("Request handler running!");
		try {
			OutputStream out = socket.getOutputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			StringBuilder builder = readInput(in);
			
			writeOutput(out, builder);
			
			startClient(out, in);
		
		} catch (IOException e) {
			logger.info(e.getMessage());
		} 
	}

	private void startClient(OutputStream out, BufferedReader in) {
		Client client = new Client(pool,out,in);
		pool.addClient(client);
		client.start();
	}

	private void writeOutput(OutputStream out, StringBuilder builder) throws IOException {
		Handshake handshake = new Handshake76();
		byte[] bytes = handshake.createResponseHandshake(new RequestHeaderMap().extractHeader(builder.toString()));
		for (int i = 0; i < bytes.length; i++) {
			out.write(bytes[i]);
		}
	}

	private StringBuilder readInput(BufferedReader in) throws IOException {
		StringBuilder builder = new StringBuilder();
		while(in.ready()) {
			builder.append(Character.toChars(in.read()));
		}
		return builder;
	}
}

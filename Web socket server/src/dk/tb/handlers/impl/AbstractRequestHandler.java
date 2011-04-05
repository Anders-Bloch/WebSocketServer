package dk.tb.handlers.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.factories.PoolAndClientFactory;
import dk.tb.handlers.RequestHandler;
import dk.tb.handlers.util.Keys;
import dk.tb.handlers.util.RequestHeaderMap;
import dk.tb.handlers.util.ResourceRequestHandler;
import dk.tb.pools.ClientPool;

public abstract class AbstractRequestHandler implements RequestHandler {
	
	private final ResourceRequestHandler resourceHandler = new ResourceRequestHandler();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final PoolAndClientFactory factory;
	protected final ClientPool pool;

	protected Socket socket;
	protected OutputStream out = null;

	protected Map<Keys, String> header;
	protected String path;
	
	public AbstractRequestHandler(PoolAndClientFactory factory) {
		this.pool = factory.getPool();
		this.factory = factory;
	}
	
	@Override
	public void run() {
		try {
			out = socket.getOutputStream();
			StringBuilder builder = readInput(
					new BufferedReader(new InputStreamReader(socket.getInputStream())));
			if(builder.toString().length() < 10) {
				out.write("HTTP/1.1 400 OK\r\nExpires: -1\r\nCache-Control: private, max-age=0\r\nContent-Type: application/json; charset=UTF-8".getBytes());
				out.flush();
				socket.close();
			} else {
				header = new RequestHeaderMap().extractHeader(builder.toString());
				path = header.get(Keys.PATH);
				if(path != null && resourceHandler.isResourceRequest(path)) {
					handleResourceRequest();
				} else {
					handleRun();
				}
			}
		} catch(IOException e) {
			logger.error(e.getMessage());
		}
	}

	public abstract void handleRun() throws IOException;

	private void handleResourceRequest() throws IOException {
		String output = resourceHandler.resolveResource(path);
		out.write(output.getBytes());
		out.flush();
		socket.close();
	}
	
	@Override
	public void handleRequest(Socket socket) throws IOException {
		this.socket = socket;
	}

	/*protected StringBuilder readInput(BufferedReader in) throws IOException {
		StringBuilder builder = new StringBuilder();
		while(builder.indexOf("\r\n\r\n") == -1) {
			builder.append(Character.toChars(in.read()));
		}
		if(builder.indexOf("WebSocket") != -1) {
			for(int i = 0; i < 8;i++){
				builder.append(Character.toChars(in.read()));
			}
		}
		return builder;
	}*/

	protected StringBuilder readInput(BufferedReader in) throws IOException {
		StringBuilder builder = new StringBuilder();
		while(in.ready()) {
			builder.append(Character.toChars(in.read()));
		}
		return builder;
	}
}

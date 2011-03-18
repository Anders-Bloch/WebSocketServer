package dk.tb.handler.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.handler.RequestHandler;
import dk.tb.server.util.Keys;
import dk.tb.server.util.RequestHeaderMap;
import dk.tb.server.util.ResourceRequestHandler;

public abstract class AbstractRequestHandler implements RequestHandler {
	
	private final ResourceRequestHandler resourceHandler = new ResourceRequestHandler();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected Socket socket;
	protected OutputStream out = null;
	protected BufferedReader in = null;
	protected String path;
	protected Map<Keys, String> header;
	
	@Override
	public void run() {
		try {
			out = socket.getOutputStream();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			StringBuilder builder = readInput(in);
			
			header = new RequestHeaderMap().extractHeader(builder.toString());
			path = header.get(Keys.PATH);
			if(path != null && resourceHandler.isResourceRequest(path)) {
				String output = resourceHandler.resolveResource(path);
				out.write(output.getBytes());
				out.flush();
				out.close();
				in.close();
				socket.close();
			} else {
				handleRun();
			}
		} catch(IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	public abstract void handleRun() throws IOException;
	
	@Override
	public void handleRequest(Socket socket) throws IOException {
		this.socket = socket;
	}

	protected StringBuilder readInput(BufferedReader in) throws IOException {
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
	}
}

package dk.tb.clients.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.Client;

public class XHRPollClient implements Client {
	
	private final Logger logger = LoggerFactory.getLogger(XHRPollClient.class);
	private final Socket socket;
	
	public XHRPollClient(Socket socket) {
		this.socket = socket;
	}
	
	public void event(String event) throws IOException {
		logger.info("Writing to stream, object:" + socket);
		logger.info("Text update event: " + event);
		OutputStream out = socket.getOutputStream();
		out.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain; charset=utf-8\r\nContent-Length: ".getBytes());
		out.write(event.length());
		out.write("\r\n\r\n".getBytes());
		out.write(event.getBytes());
		out.flush();
		socket.close();
	}
}

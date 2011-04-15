package dk.tb.handlers.impl;

import java.io.IOException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.Client;
import dk.tb.factories.PoolAndClientFactory;

public class XHRPollRequestHandler extends AbstractRequestHandler {
	
	private final Logger logger = LoggerFactory.getLogger(XHRPollRequestHandler.class);
	
	public XHRPollRequestHandler(PoolAndClientFactory factory) {
		super(factory);
	}

	@Override
	public void handleRun() throws IOException {
		if(path.contains("/xhrLongPolling?text")) {
			String text = URLDecoder.decode(path.substring(path.indexOf("text=")+5, path.length()));
			logger.info("Text update event: " + text);
			pool.callAll(text);
			out.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain; charset=utf-8\r\nContent-Length: 0\r\nConnection: Keep-Alive\r\n\r\n".getBytes());
			out.flush();
			socket.close();
		} else if(path.contains("/xhrLongPolling?Connect")) {
			Client client = factory.getNewClient(socket);
			pool.addClient(client);
			logger.info("New client added");
		}
	}
}

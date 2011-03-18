package dk.tb.handler.impl;

import java.io.IOException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.Client;
import dk.tb.clients.impl.XHRPollClient;
import dk.tb.pool.ClientPool;

public class XHRPollRequestHandler extends AbstractRequestHandler {
	
	private final Logger logger = LoggerFactory.getLogger(XHRPollRequestHandler.class);
	private ClientPool pool;
	
	public XHRPollRequestHandler(ClientPool pool) {
		this.pool = pool;
	}

	@Override
	public void handleRun() throws IOException {
		if(path.contains("/xhrLongPolling?text")) {
			String text = URLDecoder.decode(path.substring(path.indexOf("text=")+5, path.length()));
			logger.info("Text update event: " + text);
			pool.callAll(text);
		} else if(path.contains("/xhrLongPolling?Connect")) {
			Client client = new XHRPollClient(socket);
			pool.addClient(client);
			logger.info("New client added");
		}
	}
}

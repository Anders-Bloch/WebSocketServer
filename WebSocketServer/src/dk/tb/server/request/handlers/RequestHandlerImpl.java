package dk.tb.server.request.handlers;

import java.io.IOException;
import java.net.Socket;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.server.request.RequestHandler;
import dk.tb.server.request.RequestStrategy;

public class RequestHandlerImpl implements RequestHandler {
	
	@Inject @Default private RequestStrategy requestStrategy;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void setSocket(Socket socket) {
	}

	@Override
	public void run() {
		try {
			requestStrategy.request();
		} catch(IOException e) {
			logger.error(e.getMessage());
		}
	}
}

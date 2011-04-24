package dk.tb.requesthandlers;

import java.io.IOException;
import java.net.Socket;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.server.request.strategy.RequestStrategyFactory;

@Named
public class RequestHandlerImpl implements RequestHandler {
	
	@Inject private Instance<RequestStrategyFactory> factoryInstance;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void addSocket(Socket socket) {
	}

	@Override
	public void run() {
		try {
			factoryInstance.get().getRequestStrategy().request();
		} catch(IOException e) {
			logger.error(e.getMessage());
		}
	}
}

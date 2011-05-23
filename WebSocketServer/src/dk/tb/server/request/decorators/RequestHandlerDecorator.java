package dk.tb.server.request.decorators;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.server.request.RequestHandler;
import dk.tb.server.request.RequestObject;

@Decorator
public class RequestHandlerDecorator implements RequestHandler {

	@Inject @Delegate RequestHandler requestHandler;
	@Inject @Bound BoundRequestContext requestContext;
	@Inject @Default RequestObject requestObject;
	
	private Socket socket;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		Map<String, Object> storage = new HashMap<String, Object>();
		try{
			requestContext.associate(storage);
			requestContext.activate();
			try {
				requestObject.setUpObject(socket);
				this.socket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			requestHandler.run();
		} finally {
			requestContext.invalidate();
			requestContext.deactivate();
			requestContext.dissociate(storage);
		}
	}
}

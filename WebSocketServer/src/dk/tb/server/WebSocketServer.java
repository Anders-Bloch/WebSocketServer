package dk.tb.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.server.request.RequestHandler;
import dk.tb.server.util.ThreadPool;

@Singleton
public class WebSocketServer {

	@Inject Instance<RequestHandler> handlerInstance;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void runServer(	@Observes ContainerInitialized event, 
							ThreadPool threadPool,
							WeldContainer weld) throws IOException {

		ServerSocket serverSocket = new ServerSocket(80);
		logger.info("Running server!");
		while(true) {
			Socket socket = serverSocket.accept();
			logger.info("accept new socket!");
			RequestHandler requestHandler = handlerInstance.get();
			requestHandler.setSocket(socket);
			logger.info("Socket added to request handler");
			threadPool.runTask(requestHandler);
		}
	}
}

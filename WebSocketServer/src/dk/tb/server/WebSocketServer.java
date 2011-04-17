package dk.tb.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.factories.util.ServerThreadPool;
import dk.tb.requesthandlers.Handler;

@Singleton
public class WebSocketServer {

	@Inject Instance<Handler> handlerInstance;
	@Inject ServerThreadPool threadPool;

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void runServer(@Observes ContainerInitialized event) throws IOException {
		logger.info("Starting server");
		ServerSocket serverSocket = new ServerSocket(80);
		serverSocket.setReceiveBufferSize(255);
		while(true) {
			logger.info("Running server!");
			Socket socket = serverSocket.accept();
			socket.setSendBufferSize(255);
			System.out.println(socket.getSendBufferSize());
			System.out.println(socket.getReceiveBufferSize());
			Handler requestHandler = handlerInstance.get();
			requestHandler.handleRequest(socket);
			threadPool.runTask(requestHandler);
		}
	}
}

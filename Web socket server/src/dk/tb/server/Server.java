package dk.tb.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.factories.ServerFactory;
import dk.tb.factories.ServerFactoryInit;
import dk.tb.factories.ServerFactoryInit.ServerInitParam;
import dk.tb.handlers.RequestHandler;

public class Server {
	
	private final static Logger logger = LoggerFactory.getLogger(Server.class);
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		try {
			new Server().init(ServerInitParam.getParam(args[0]));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	public void init(ServerInitParam param) throws IOException {
		ServerFactory factory = ServerFactoryInit.getFactory(param);
		ServerSocket server = new ServerSocket(80);
		logger.info("Starting server");
		while(true) {
			logger.info("Wating for client");
			Socket clientSocket = server.accept();
			logger.info("Client accepted");
			RequestHandler requestHandler = factory.getRequestHandler();
			requestHandler.handleRequest(clientSocket);
			factory.getThreadPool().runTask(requestHandler);
			//new Thread(requestHandler).start(); Udskriftet med thread pool
			logger.info("Request turned over to handler");
		}
	}
}

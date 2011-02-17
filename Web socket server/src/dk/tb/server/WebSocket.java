package dk.tb.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocket {
	
	private final static Logger logger = LoggerFactory.getLogger(WebSocket.class);
	private final ClientPool pool = new ClientPool();
	
	public WebSocket() {
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new WebSocket().init();
	}
	
	public void init() throws IOException {
		ServerSocket server = new ServerSocket(80);
		logger.info("Starting server");
		while(true) {
			logger.info("Wating for client");
			Socket clientSocket = server.accept();
			logger.info("Client accepted");
			Client client = new Client(clientSocket,pool);
			pool.addClient(client);
			client.start();
		}
	}
}

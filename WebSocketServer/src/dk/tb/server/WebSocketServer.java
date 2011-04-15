package dk.tb.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import dk.tb.factories.util.ServerThreadPool;
import dk.tb.factories.util.ThreadPoolFactory;


public class WebSocketServer {

	ServerThreadPool threadPool = ThreadPoolFactory.getThreadPool();
	
	private Set<Client> clients = Collections.synchronizedSet(new HashSet<Client>()); 
	
	public WebSocketServer() throws IOException {
		ServerSocket serverSocket = new ServerSocket(80);
		while(true) {
			Socket socket = serverSocket.accept();
			Client c = new Client(socket, this);
			clients.add(c);
			threadPool.runTask(c);
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new WebSocketServer();
	}
	
	public void callAll(String s) {
		Set<Client> clientsToRemove = new HashSet<Client>();
		for (Client c : clients) {
			try {
				c.event(s);
			} catch(Exception e) {
				clientsToRemove.add(c);
			}
		}
		clients.removeAll(clientsToRemove);
	}
}

package dk.tb.server;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPool {
	
	private final Logger logger = LoggerFactory.getLogger(ClientPool.class);
	
	private Set<Client> clients = new HashSet<Client>();
	private Set<Client> clientsToRemove = new HashSet<Client>();
	
	public ClientPool() {
	}
	
	public void addClient(Client client) {
		logger.info("Client added to pool: " + client);
		clients.add(client);
	}

	public void removeClient(Client client) {
		clientsToRemove.add(client);
	}
	
	public synchronized void callAll(String event) {
		logger.info("Call all clients");
		for (Client client : clients) {
			client.event(event);
		}
		if(clientsToRemove.size() > 0) {
			for (Client client : clientsToRemove) {
				clients.remove(client);
				logger.info("Client removed from pool: " + client);
			}
			clientsToRemove.clear();
			logger.info("Clients left in pool: " + clients.size());
		}
	}
}

package dk.tb.pool.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.Client;
import dk.tb.pool.ClientPool;


public class CommonClientPool implements ClientPool {
	
	private final Logger logger = LoggerFactory.getLogger(CommonClientPool.class);
	
	private Set<Client> clients = new HashSet<Client>();
	private Set<Client> clientsToRemove = new HashSet<Client>();
	
	public void addClient(Client client) {
		logger.info("Client added to pool: " + client);
		clients.add(client);
	}

	public synchronized void callAll(String event) {
		logger.info("Call all clients");
		for (Client client : clients) {
			try {
				client.event(event);
			} catch (IOException e) {
				logger.warn("Output write failed! Removing client from client pool.");
				clientsToRemove.add(client);
			}
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
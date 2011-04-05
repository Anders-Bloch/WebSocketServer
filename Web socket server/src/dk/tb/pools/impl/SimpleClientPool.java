package dk.tb.pools.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.Client;
import dk.tb.pools.ClientPool;


public class SimpleClientPool implements ClientPool {
	
	private final Logger logger = LoggerFactory.getLogger(SimpleClientPool.class);
	
	private Set<Client> clients = new HashSet<Client>();
	
	public synchronized void addClient(Client client) {
		logger.info("Client added to pool: " + client);
		clients.add(client);
	}
	
	public synchronized void callAll(String event) {
		logger.info("Call all clients");
		for (Client client : clients) {
			try {
				client.event(event);
			} catch (IOException e) {
				logger.error(e.getMessage());
				//Do nothing - just continue.
			}
		}
		clients.clear();
	}
}

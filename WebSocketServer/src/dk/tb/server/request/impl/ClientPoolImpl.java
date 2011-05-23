package dk.tb.server.request.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import dk.tb.server.ClientEvent;
import dk.tb.server.request.Client;
import dk.tb.server.request.ClientPool;

@Singleton
class ClientPoolImpl implements ClientPool, ClientEvent {
	
	private Map<String, Set<Client>> pool = new HashMap<String, Set<Client>>();
	
	@Override
	public synchronized void addClient(Client client, String servletUri) {
		Set<Client> clients = pool.get(servletUri);
		if(clients == null) {
			clients = new HashSet<Client>();
			pool.put(servletUri, clients);
		}
		clients.add(client);
	}

	@Override
	public synchronized void callClients(String message, String servletUri, String clientId) {
		Set<Client> clients = pool.get(servletUri);
		Set<Client> clientsToRemove = new HashSet<Client>();
		for (Client c : clients) {
			try {
				if(!clientId.equalsIgnoreCase(c.hashCode()+"")) {
					c.event(message);
				}
			} catch(Exception e) {
				clientsToRemove.add(c);
			}
		}
		clients.removeAll(clientsToRemove);
	}

}

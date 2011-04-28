package dk.tb.pools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import dk.tb.server.Client;

@Singleton
public class ClientPoolImpl implements ClientPool {
	
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

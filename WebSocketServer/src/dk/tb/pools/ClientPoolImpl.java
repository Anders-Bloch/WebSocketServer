package dk.tb.pools;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import dk.tb.server.Client;

@Singleton
public class ClientPoolImpl implements ClientPool {
	
	private Set<Client> clients = Collections.synchronizedSet(new HashSet<Client>());

	@Override
	public synchronized void addClient(Client client) {
		clients.add(client);
	}

	@Override
	public synchronized void callClients(String message) {
		Set<Client> clientsToRemove = new HashSet<Client>();
		for (Client c : clients) {
			try {
				c.event(message);
			} catch(Exception e) {
				clientsToRemove.add(c);
			}
		}
		clients.removeAll(clientsToRemove);
	}

}

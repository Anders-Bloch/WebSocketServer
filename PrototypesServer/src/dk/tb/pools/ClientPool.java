package dk.tb.pools;

import dk.tb.clients.Client;

public interface ClientPool {
	
	public void addClient(Client client);
	
	public void callAll(String event);
	
}

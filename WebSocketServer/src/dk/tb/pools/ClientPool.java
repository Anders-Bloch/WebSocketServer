package dk.tb.pools;

import dk.tb.server.Client;

public interface ClientPool {
	public void addClient(Client client);
	public void callClients(String message);
}

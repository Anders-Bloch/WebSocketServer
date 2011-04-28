package dk.tb.pools;

import dk.tb.server.Client;

public interface ClientPool {
	public void addClient(Client client, String servletUri);
	public void callClients(String message, String servletUri, String clientId);
}

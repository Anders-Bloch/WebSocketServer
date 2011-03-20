package dk.tb.factories;

import java.io.IOException;
import java.net.Socket;

import dk.tb.clients.Client;
import dk.tb.pools.ClientPool;

public interface PoolAndClientFactory {
	public ClientPool getPool();
	public Client getNewClient(Socket socket) throws IOException;
}

package dk.tb.server.request;


public interface ClientPool {
	public void addClient(Client client, String servletUri);
}

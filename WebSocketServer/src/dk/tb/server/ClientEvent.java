package dk.tb.server;

public interface ClientEvent {
	public void callClients(String message, String servletUri, String clientId);
}

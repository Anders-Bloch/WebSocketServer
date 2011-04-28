package dk.tb.server;

import java.io.IOException;

import dk.tb.servlets.WebSocketServlet;

public interface Client {
	public void run() throws IOException;
	public void event(String message) throws IOException;
	public WebSocketServlet getWebSocketServlet();
}

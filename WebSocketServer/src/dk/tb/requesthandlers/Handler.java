package dk.tb.requesthandlers;

import java.net.Socket;

public interface Handler extends Runnable {
	public void handleRequest(Socket socket);
}

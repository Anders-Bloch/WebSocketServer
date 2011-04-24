package dk.tb.requesthandlers;

import java.net.Socket;

public interface RequestHandler extends Runnable {
	public void addSocket(Socket socket);
	public void run();
}

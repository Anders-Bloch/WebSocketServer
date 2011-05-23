package dk.tb.server.request;

import java.net.Socket;

public interface RequestHandler extends Runnable {
	public void setSocket(Socket socket);
	public void run();
}

package dk.tb.handler;

import java.io.IOException;
import java.net.Socket;

public interface RequestHandler extends Runnable {
	public void handleRequest(Socket socket) throws IOException;
}
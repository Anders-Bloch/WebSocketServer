package dk.tb.handlers;

import java.io.IOException;
import java.net.Socket;

import dk.tb.factories.PoolAndClientFactory;

public interface RequestHandler extends Runnable {
	public void handleRequest(Socket socket) throws IOException;
}
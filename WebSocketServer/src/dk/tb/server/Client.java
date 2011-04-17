package dk.tb.server;

import java.io.IOException;
import java.net.Socket;

public interface Client extends Runnable {
	public void setFields(Socket socket);
	public void event(String message) throws IOException;
}

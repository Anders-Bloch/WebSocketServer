package dk.tb.server.request;

import java.io.IOException;

public interface Client {
	public void run() throws IOException;
	public void event(String message) throws IOException;
}

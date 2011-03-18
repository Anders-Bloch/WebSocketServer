package dk.tb.clients;

import java.io.IOException;

public interface Client {
	public void event(String event) throws IOException;
}

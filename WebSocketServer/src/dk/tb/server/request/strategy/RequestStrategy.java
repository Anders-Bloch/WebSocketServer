package dk.tb.server.request.strategy;

import java.io.IOException;

import dk.tb.server.util.RequestType;

public interface RequestStrategy {
	public RequestType getType();
	public void request() throws IOException;
}

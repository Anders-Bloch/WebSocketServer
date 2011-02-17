package dk.tb.server;

import java.util.Map;

import dk.tb.server.util.Keys;

public interface Handshake {
	public byte[] createResponseHandshake(Map<Keys, String> requestMap);
}

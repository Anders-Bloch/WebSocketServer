package dk.tb.handlers.handshakes;

import java.util.Map;

import dk.tb.handlers.util.Keys;

public interface Handshake {
	public byte[] createResponseHandshake(Map<Keys, String> requestMap);
}

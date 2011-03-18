package dk.tb.websocket.handshakes;

import java.util.Map;

import dk.tb.server.util.Keys;

public interface Handshake {
	public byte[] createResponseHandshake(Map<Keys, String> requestMap);
}

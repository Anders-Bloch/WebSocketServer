package dk.tb.handshake;

import java.util.Map;

import dk.tb.server.Keys;

public interface Handshake {
	public byte[] createResponseHandshake(Map<Keys, String> requestMap, byte[] key3);
}

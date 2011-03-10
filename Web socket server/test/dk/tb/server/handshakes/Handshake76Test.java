package dk.tb.server.handshakes;

import java.util.Map;

import org.junit.*;

import dk.tb.server.util.Keys;
import dk.tb.server.util.RequestHeaderMap;


public class Handshake76Test {
	
	private String correctResponse = "7284848047494649324948493287101988311199107101116328011411111611199111108327297110100115104971071011310851121031149710010158328710198831119910710111613106711111011010199116105111110583285112103114971001011310831019945871019883111991071011164579114105103105110583211011710810813108310199458710198831119910710111645761119997116105111110583211911558474710811199971081041111151164713101310127205494-111-20-11-39-9521134-11266221";
	
	@Test
	public void canExtractCorrectPath() {
		Handshake76 handshake = new Handshake76();
		RequestHeaderMap headerMapper = new RequestHeaderMap();
		Map<Keys, String> map =  headerMapper.extractHeader(getClientHandshakeString());
		Assert.assertNotNull(handshake.createResponseHandshake(map));
		byte[] bytes = handshake.createResponseHandshake(map);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			builder.append((int)bytes[i]);
		}
		Assert.assertEquals(correctResponse,builder.toString());
	}
	
	@Test
	/*public void getResponseKey() {
		Handshake76 handshake = new Handshake76();
		byte[] bytes = handshake.getResponseKey(
				handshake.getKey("21 xq120c! o287Q 5@5a"), 
				handshake.getKey("2  s9 98  6 b 3Q6 Y  355 D"), 
				"8AP]ïv".getBytes());
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			builder.append((int)bytes[i]);
		}
		Assert.assertEquals(
				"127205494-111-20-11-39-9521134-11266221",
				builder.toString());
	}*/
	
	private String getClientHandshakeString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GET / HTTP/1.1\r\n");
		builder.append("Upgrade: WebSocket\r\n");
		builder.append("Connection: Upgrade\r\n");
		builder.append("Host: localhost\r\n");
		builder.append("Origin: null\r\n");
		builder.append("Sec-WebSocket-Key1: 21 xq120c! o287Q 5@5a\r\n");
		builder.append("Sec-WebSocket-Key2: 2  s9 98  6 b 3Q6 Y  355 D\r\n");
		builder.append("\r\n");
		builder.append("8AP]ïv");
		return builder.toString();
	}
}

package dk.tb.server.util;

import java.util.Map;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.handlers.util.Keys;
import dk.tb.handlers.util.RequestHeaderMap;


public class RequestHeaderMapTest {
	
	final Logger logger = LoggerFactory.getLogger(RequestHeaderMapTest.class);
	
	@Test
	public void canExtractCorrectPath() {
		logger.info("Running test one");
		RequestHeaderMap map = new RequestHeaderMap();
		Map<Keys, String> result = map.extractHeader(getClientHandshakeString());
		Assert.assertTrue(result.containsKey(Keys.PATH));
		Assert.assertEquals("/",result.get(Keys.PATH));
		StringBuilder builder = new StringBuilder();
		builder.append("GET /localhost/test HTTP/1.1\r\n");
		builder.append("Upgrade: WebSocket\r\n");
		result = map.extractHeader(builder.toString());
		Assert.assertTrue(result.containsKey(Keys.PATH));
		Assert.assertEquals("/localhost/test",result.get(Keys.PATH));
	}
	
	@Test
	public void canExtractCorrectUpgrade() {
		RequestHeaderMap map = new RequestHeaderMap();
		Map<Keys, String> result = map.extractHeader(getClientHandshakeString());
		Assert.assertTrue(result.containsKey(Keys.UPGRADE));
		Assert.assertEquals("WebSocket",result.get(Keys.UPGRADE));
	}

	@Test
	public void canExtractCorrectConnection() {
		RequestHeaderMap map = new RequestHeaderMap();
		Map<Keys, String> result = map.extractHeader(getClientHandshakeString());
		Assert.assertTrue(result.containsKey(Keys.CONN));
		Assert.assertEquals("Upgrade",result.get(Keys.CONN));
	}
	
	@Test
	public void canExtractCorrectHost() {
		RequestHeaderMap map = new RequestHeaderMap();
		Map<Keys, String> result = map.extractHeader(getClientHandshakeString());
		Assert.assertTrue(result.containsKey(Keys.HOST));
		Assert.assertEquals("localhost",result.get(Keys.HOST));
	}

	@Test
	public void canExtractCorrectOrigin() {
		RequestHeaderMap map = new RequestHeaderMap();
		Map<Keys, String> result = map.extractHeader(getClientHandshakeStringSafari());
		Assert.assertTrue(result.containsKey(Keys.ORIGIN));
		Assert.assertEquals("file://",result.get(Keys.ORIGIN));
	}
	
	@Test
	public void canExtractCorrectKey1() {
		RequestHeaderMap map = new RequestHeaderMap();
		Map<Keys, String> result = map.extractHeader(getClientHandshakeString());
		Assert.assertTrue(result.containsKey(Keys.KEY1));
		Assert.assertEquals("14 d43 71M8i 458",result.get(Keys.KEY1));
	}

	@Test
	public void canExtractCorrectKey2() {
		RequestHeaderMap map = new RequestHeaderMap();
		Map<Keys, String> result = map.extractHeader(getClientHandshakeString());
		Assert.assertTrue(result.containsKey(Keys.KEY2));
		Assert.assertEquals("n za 1z843I {268Z  7  M7v7<",result.get(Keys.KEY2));
	}

	@Test
	public void canExtractCorrectKey3() {
		RequestHeaderMap map = new RequestHeaderMap();
		Map<Keys, String> result = map.extractHeader(getClientHandshakeString());
		Assert.assertTrue(result.containsKey(Keys.KEY3));
		Assert.assertEquals("_–Ù¿Œ%∑√",result.get(Keys.KEY3));
	}
	
	private String getClientHandshakeString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GET / HTTP/1.1\r\n");
		builder.append("Upgrade: WebSocket\r\n");
		builder.append("Connection: Upgrade\r\n");
		builder.append("Host: localhost\r\n");
		builder.append("Origin: test\r\n");
		builder.append("Sec-WebSocket-Key1: 14 d43 71M8i 458\r\n");
		builder.append("Sec-WebSocket-Key2: n za 1z843I {268Z  7  M7v7<\r\n");
		builder.append("\r\n");
		builder.append("_–Ù¿Œ%∑√");
		return builder.toString();
	}

	private String getClientHandshakeStringSafari() {
		StringBuilder builder = new StringBuilder();
		builder.append("GET / HTTP/1.1\r\n");
		builder.append("Upgrade: WebSocket\r\n");
		builder.append("Connection: Upgrade\r\n");
		builder.append("Host: localhost\r\n");
		builder.append("Origin: file://\r\n");
		builder.append("Sec-WebSocket-Key1: 14 d43 71M8i 458\r\n");
		builder.append("Sec-WebSocket-Key2: n za 1z843I {268Z  7  M7v7<\r\n");
		builder.append("\r\n");
		builder.append("_–Ù¿Œ%∑√");
		return builder.toString();
	}
}

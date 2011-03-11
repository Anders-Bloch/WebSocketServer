package dk.tb.server.handshakes;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.server.Handshake;
import dk.tb.server.util.Keys;

public class Handshake76 implements Handshake {
	
	final Logger logger = LoggerFactory.getLogger(Handshake76.class);
	
	public byte[] createResponseHandshake(Map<Keys, String> requestMap) {
		byte[] resKey = getResponseKey(
				getPart(requestMap.get(Keys.KEY1)), 
				getPart(requestMap.get(Keys.KEY2)),
				requestMap.get(Keys.KEY3).getBytes());
		byte[] header = getHeader(requestMap).getBytes();
		int totalBytes = header.length + resKey.length;
		byte[] result = new byte[totalBytes];
		for (int i = 0; i < header.length; i++) {
			result[i] = header[i];
		}
		for (int i = 0; i < resKey.length; i++) {
			result[header.length + i] = resKey[i];
		}
		return result;
	}
	
	private String getHeader(Map<Keys, String> requestMap) {
		StringBuilder builder = new StringBuilder();
		builder.append("HTTP/1.1 101 WebSocket Protocol Handshake" + "\r\n");
		builder.append(Keys.UPGRADE.getKey()+": " + requestMap.get(Keys.UPGRADE) + "\r\n");
		builder.append(Keys.CONN.getKey()+": " + requestMap.get(Keys.CONN) + "\r\n");
		builder.append(Keys.SEC_ORIGIN.getKey()+": " + requestMap.get(Keys.ORIGIN) + "\r\n");
		builder.append(Keys.SEC_LOCATION.getKey()+": " + "ws://localhost/" + "\r\n");
		builder.append("\r\n");
		return builder.toString();
	}
	
	public long getKey(String s) {
		char[] chars = s.toCharArray();
		String numbers = "";
		int numberOfSpaces = 0;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if(c == ' ') {
				numberOfSpaces++;
			}
			if(c != ' ' && c >= '0' && c <= '9') {
				numbers += c;
			}
		}
		return Long.parseLong(numbers) / numberOfSpaces;
	}
	
	private byte[] getPart(String key) {
		long keyNumber = Long.parseLong(key.replaceAll("[^0-9]",""));
		long keySpace = key.split("\u0020").length - 1;
		long part = new Long(keyNumber / keySpace);
		return new byte[] {
				(byte)( part >> 24 ),
				(byte)( (part << 8) >> 24 ),
				(byte)( (part << 16) >> 24 ),
				(byte)( (part << 24) >> 24 )      
		};
	}
	
	public byte[] getResponseKey(byte[] key1, byte[] key2, byte[] key3) {
		byte[] bytes = new byte[16];
		//byte[] key1Bytes = new BigInteger(key1+"").toByteArray();
		//byte[] key2Bytes = new BigInteger(key2+"").toByteArray();
		logger.info("Key1 length: " + key1.length);
		logger.info("Key2 length: " + key2.length);
		logger.info("Key3 length: " + key3.length);
		int keyIndex = 0;
		//Add first key
		for (int i = 0; i < key1.length; i++, keyIndex++) {
			bytes[keyIndex] = key1[i];
		}
		//Add second key
		for (int i = 0; i < key2.length; i++, keyIndex++) {
			bytes[keyIndex] = key2[i];
		}
		//Add third key
		for (int i = 0; i < key3.length; i++, keyIndex++) {
			bytes[keyIndex] = key3[i];
		}
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(bytes);
        } catch (Exception ex) {
            logger.error("getMD5: " + ex.getMessage());
        }
        return null;
	}
}

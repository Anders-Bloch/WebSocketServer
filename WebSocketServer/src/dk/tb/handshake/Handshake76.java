package dk.tb.handshake;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import dk.tb.server.Keys;
import dk.tb.server.RequestObject;

public class Handshake76 implements Handshake {
	
	@Inject RequestObject requestObject;
	
	public byte[] createResponseHandshake() {
		Map<Keys, String> requestMap = requestObject.getHeaderMap();
		byte[] resKey = getResponseKey(
				getPart(requestMap.get(Keys.KEY1)), 
				getPart(requestMap.get(Keys.KEY2)),
				getKey3());
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

	private byte[] getKey3() {
		int length = requestObject.getRequesAsBytes().length;
		return Arrays.copyOfRange(
				requestObject.getRequesAsBytes(),length - 8, length) ;
	}
	
	private String getHeader(Map<Keys, String> requestMap) {
		StringBuilder builder = new StringBuilder();
		builder.append("HTTP/1.1 101 WebSocket Protocol Handshake" + "\r\n");
		builder.append(Keys.UPGRADE.getKey()+": " + requestMap.get(Keys.UPGRADE) + "\r\n");
		builder.append(Keys.CONN.getKey()+": " + requestMap.get(Keys.CONN) + "\r\n");
		builder.append(Keys.SEC_ORIGIN.getKey()+": " + requestMap.get(Keys.ORIGIN) + "\r\n");
		builder.append(Keys.SEC_LOCATION.getKey()+": " + "ws://" + requestMap.get(Keys.HOST) + requestMap.get(Keys.PATH) + "\r\n");
		builder.append("\r\n");
		return builder.toString();
	}
	
	
	private byte[] getPart(String key) {
		long keyNumber = Long.parseLong(key.replaceAll("[^0-9]",""));
		long keySpace = key.split("\u0020").length - 1;
		Long part = new Long(keyNumber / keySpace);
		return new byte[] {
				(byte)( part >> 24 ),
				(byte)( (part << 8) >> 24 ),
				(byte)( (part << 16) >> 24 ),
				(byte)( (part << 24) >> 24 )      
		};
	}
	
	private byte[] getResponseKey(byte[] key1, byte[] key2, byte[] key3) {
		byte[] bytes = new byte[16];
		int keyIndex = 0;
		//Add first key
		for (int i = 0; i < key1.length; i++, keyIndex++) {
			bytes[keyIndex] = key1[i];
		}
		//Add second key
		for (int i = 0; i < key2.length; i++, keyIndex++) {
			bytes[keyIndex] = key2[i];
		}

		for (int i = 0; i < key3.length; i++, keyIndex++) {
			bytes[keyIndex] = key3[i];
		}
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(bytes);
        } catch (Exception ex) {
        	System.out.println("getMD5: " + ex.getMessage());
        }
        return null;
	}
}

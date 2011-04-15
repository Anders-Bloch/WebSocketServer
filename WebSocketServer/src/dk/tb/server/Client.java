package dk.tb.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Map;

public class Client implements Runnable {
	
	private Socket socket;
	private WebSocketServer server;
	
	public Client(Socket socket, WebSocketServer server) {
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		InputStream in;
		try {
			in = socket.getInputStream();
			StringBuffer buffer = new StringBuffer();
			byte[] key3 = new byte[8];
			StringBuffer buffer2 = new StringBuffer();
			while(checkEnd(buffer)) {
				buffer.append(Character.toChars(in.read()));
			}
			if(buffer.toString().contains("WebSocket")) {
				for (int i = 0; i < 8; i++) {
					key3[i] = (byte)in.read();
				}
			}
			System.out.print(buffer.toString());
			System.out.println(buffer2.toString());
			System.out.println("--");
			RequestHeaderMap headerMap = new RequestHeaderMap();
			Map<Keys, String> header = headerMap.extractHeader(buffer.toString());
			byte[] response = createResponseHandshake(header, key3);
			for (int i = 0; i < response.length; i++) {
				try {
					System.out.print(Character.toChars(response[i]));
				} catch(Exception e) {
					System.out.println("");
					System.out.println(response[i]);
					System.out.println("");
				}
			}
			socket.getOutputStream().write(response);
			StringBuffer inBuffer = new StringBuffer();
			while(true) {
				byte b = (byte)in.read();
				System.out.println((int)b);
				if((int)b == -1) {
					if(inBuffer.length() == 0) break;
					server.callAll(inBuffer.toString());
					inBuffer = new StringBuffer();
				} else if((int)b != 0) {
					inBuffer.append(Character.toChars(b));
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private boolean checkEnd(StringBuffer buffer) {
		if(buffer.length() > 3) {
			if(buffer.substring(buffer.length()-4, buffer.length()).equalsIgnoreCase("\r\n\r\n")) {
				return false;
			}
		}
		return true;
	}
	
	public byte[] createResponseHandshake(Map<Keys, String> requestMap, byte[] key3) {
		byte[] resKey = getResponseKey(
				getPart(requestMap.get(Keys.KEY1)), 
				getPart(requestMap.get(Keys.KEY2)),
				key3);
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
		builder.append(Keys.SEC_LOCATION.getKey()+": " + "ws://" + requestMap.get(Keys.HOST) + "/" + "\r\n");
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
	
	public byte[] getResponseKey(byte[] key1, byte[] key2, byte[] key3) {
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
	
	public void event(String s) {
		try {
			OutputStream out = socket.getOutputStream();
			out.write(0x00);
			out.write(s.getBytes());
			out.write(0xff);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package dk.tb.server.request.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;

import dk.tb.server.request.Keys;
import dk.tb.server.request.RequestObject;
import dk.tb.server.request.RequestStrategy;

@Default
@RequestScoped
class RequestObjectImpl implements RequestObject {
	
	private Map<Keys, String> headerMap;
	private byte[] requesAsBytes;
	private String requestAsString;
	private Socket socket;

	@Override
	public void setUpObject(Socket socket) throws IOException {
		this.socket = socket;
		InputStream input = socket.getInputStream();
		StringBuffer stringBuffer = new StringBuffer();
		List<Byte> byteBuffer = new ArrayList<Byte>();

		while(checkEnd(stringBuffer)) {
			byte b = (byte)input.read();
			byteBuffer.add(new Byte(b));
			try {
				stringBuffer.append(Character.toChars(b));
			} catch(IllegalArgumentException e) {
				//We just don't add the character - TODO fix problem with illegal char! 
			}
		}
		if(stringBuffer.toString().contains("WebSocket")) {
			for (int i = 0; i < 8; i++) {
				byte b = (byte)input.read();
				byteBuffer.add(new Byte(b));
			}
		}
		requestAsString = stringBuffer.toString();
		requesAsBytes = new byte[byteBuffer.size()];
		for (int i = 0; i < requesAsBytes.length; i++) {
			requesAsBytes[i] = byteBuffer.get(i);
		}
		headerMap = extractHeader(requestAsString);
	}
	
	public RequestObjectImpl() {}
	
	@Override
	public Map<Keys, String> getHeaderMap() {
		return headerMap;
	}

	@Override
	public byte[] getRequesAsBytes() {
		return requesAsBytes;
	}

	@Override
	public String getRequestAsString() {
		return requestAsString;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}
	
	@Override
	public void closeSocket() throws IOException {
		socket.close();
	}

	private boolean checkEnd(StringBuffer buffer) {
		if(buffer.length() > 3) {
			if(buffer.substring(buffer.length()-4, buffer.length()).equalsIgnoreCase("\r\n\r\n")) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public RequestStrategy.RequestType getRequestType() {
		if(requestAsString.length() < 1) {
			return RequestStrategy.RequestType.FAULT;
		} else {
			String path = headerMap.get(Keys.PATH);
			if(path != null && path.contains(".")) {
				return RequestStrategy.RequestType.RESOURCE;
			} else {
				return RequestStrategy.RequestType.CONNECT_REQUEST;
			}
		}
	}
	
	@Override
	public String getServletUri() {
		String path = getHeaderMap().get(Keys.PATH);
		String[] splitPath = path.split("/");
		return splitPath[splitPath.length-1];
	}
	
	public Map<Keys, String> extractHeader(String header) {
	    String[] lines = header.split("\r?\n|\r");
	    Map<Keys, String> result = new HashMap<Keys, String>();
	    //Add path
	    int start = lines[0].indexOf("GET")+4;
	    int end = lines[0].indexOf("HTTP")-1;
	    result.put(Keys.PATH, lines[0].substring(start, end).trim());
	    //Add all : separated values
	    int i = 1;
	    while(i < lines.length) {
	    	int index = lines[i].indexOf(":");
	    	if(index != -1) {
	    		String[] lineSplit = new String[] {
	    				lines[i].substring(0, index), 
	    				lines[i].substring(index+1, lines[i].length())};
    			for (Keys key : Keys.values()) {
    				if(key.getKey().equals(lineSplit[0])) {
    					result.put(key,lineSplit[1].trim());
    				}
    			}
	    	}
	    	i++;
	    }
	    return result;
	}

}

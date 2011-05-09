package dk.tb.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import dk.tb.server.util.RequestType;
@Default
@RequestScoped
public class RequestObjectDTO implements RequestObject {
	
	private Map<Keys, String> headerMap;
	private byte[] requesAsBytes;
	private String requestAsString;
	private Socket socket;

	@Inject private RequestHeaderMap requestHeaderMap;
	
	@Override
	public void setUpObject(Socket socket) throws IOException {
		this.socket = socket;
		InputStream input = socket.getInputStream();
		StringBuffer stringBuffer = new StringBuffer();
		List<Byte> byteBuffer = new ArrayList<Byte>();

		while(checkEnd(stringBuffer)) {
			byte b = (byte)input.read();
			byteBuffer.add(new Byte(b));
			stringBuffer.append(Character.toChars(b));
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
		headerMap = requestHeaderMap.extractHeader(requestAsString);
	}
	
	public RequestObjectDTO() {}
	
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
	public RequestType getRequestType() {
		if(requestAsString.length() < 1) {
			return RequestType.FAULT;
		} else {
			String path = headerMap.get(Keys.PATH);
			if(path != null && path.contains(".")) {
				return RequestType.RESOURCE;
			} else {
				return RequestType.CONNECT_REQUEST;
			}
		}
	}
	
	@Override
	public String getServletUri() {
		String path = getHeaderMap().get(Keys.PATH);
		String[] splitPath = path.split("/");
		return splitPath[splitPath.length-1];
	}
	

}

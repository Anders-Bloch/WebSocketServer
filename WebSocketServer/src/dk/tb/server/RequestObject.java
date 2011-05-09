package dk.tb.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import dk.tb.server.util.RequestType;

public interface RequestObject {

	public void setUpObject(Socket socket) throws IOException;

	public Map<Keys, String> getHeaderMap();

	public byte[] getRequesAsBytes();

	public String getRequestAsString();

	public InputStream getInputStream() throws IOException;

	public OutputStream getOutputStream() throws IOException;

	public void closeSocket() throws IOException;

	public RequestType getRequestType();

	public String getServletUri();

}
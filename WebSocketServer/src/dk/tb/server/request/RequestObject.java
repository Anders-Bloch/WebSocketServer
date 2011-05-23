package dk.tb.server.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;


public interface RequestObject {

	public void setUpObject(Socket socket) throws IOException;

	public Map<Keys, String> getHeaderMap();

	public byte[] getRequesAsBytes();

	public String getRequestAsString();

	public InputStream getInputStream() throws IOException;

	public OutputStream getOutputStream() throws IOException;

	public void closeSocket() throws IOException;

	public RequestStrategy.RequestType getRequestType();

	public String getServletUri();

}
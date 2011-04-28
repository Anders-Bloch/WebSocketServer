package dk.tb.servlets;

import java.io.IOException;
import java.io.OutputStream;

public interface WebSocketServlet {
	
	public void initServlet(OutputStream out, String id) throws IOException ;

	public void socketEvent(String event) throws IOException ;
	
	public void messageEvent(String event) throws IOException ;
	

}

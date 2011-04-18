package dk.tb.servlets;

public interface WebSocketServlet {
	
	public void socketEvent(byte[] bytes);
	
	public void messageEvent(byte[] bytes);

}

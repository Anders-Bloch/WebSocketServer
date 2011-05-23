package dk.tb.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

public interface WebSocketServlet {
	
	public void initServlet(OutputStream out, String id) throws IOException ;

	public void socketEvent(String event) throws IOException ;
	
	public void messageEvent(String event) throws IOException ;
	
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
	public @interface WebSocket {
		String uri();
	}
}

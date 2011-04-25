package dk.tb.server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.servlets.WebSocket;
import dk.tb.servlets.WebSocketServlet;

@Named
public class ClientImpl implements Client {
	
  	@Inject @Default private RequestObject requestObject;
	@Inject @Any Instance<WebSocketServlet> servletInstances;
  	
  	private WebSocketServlet webSocketServlet;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void run() throws IOException {
		//Find webSocketServlet.
		for (WebSocketServlet servlet : servletInstances) {
			Annotation[] annotations = servlet.getClass().getAnnotations();
			for (Annotation annotation : annotations) {
				if(annotation instanceof WebSocket && 
				   ((WebSocket)annotation).uri().equalsIgnoreCase(requestObject.getServletUri())) {
					webSocketServlet = servlet;
				}
			}
		}
		webSocketServlet.initServlet(requestObject.getOutputStream());
		
		logger.info("New Client started: " + this.hashCode());
		//Read input and call input on servlet
		try {
			InputStream in = requestObject.getInputStream();
			StringBuffer inBuilder = new StringBuffer();
			while(true) {
				byte b = (byte)in.read();
				if((int)b == -1) {
					if(inBuilder.length() == 0) break;
					webSocketServlet.socketEvent(inBuilder.toString());
					inBuilder = new StringBuffer();
				} else if((int)b != 0) {
					try {
						logger.info(b+"");
						inBuilder.append((char)b);
					} catch(Exception e) {
						logger.warn("byte skiped:" + b);
					}
				}
			}
		} finally {
			requestObject.closeSocket();
		}
	}
	
	public void event(String message) throws IOException {
		//logger.info("message received - updating client:"+this.hashCode() +"message:"+message);
		webSocketServlet.messageEvent(message);
	}

}

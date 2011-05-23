package dk.tb.server.request.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.server.request.Client;
import dk.tb.server.request.RequestObject;
import dk.tb.servlets.WebSocketServlet;

class ClientImpl implements Client {
	
  	@Inject @Default private RequestObject requestObject;
	@Inject @Any Instance<WebSocketServlet> servletInstances;
  	
  	private WebSocketServlet webSocketServlet;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void run() throws IOException {
		//Find webSocketServlet.
		for (WebSocketServlet servlet : servletInstances) {
			WebSocketServlet.WebSocket webSocket = servlet.getClass().getAnnotation(WebSocketServlet.WebSocket.class);
			if(webSocket.uri().equalsIgnoreCase(requestObject.getServletUri())) {
				webSocketServlet = servlet;
			}
		}
		webSocketServlet.initServlet(requestObject.getOutputStream(),this.hashCode()+"");
		
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
		webSocketServlet.messageEvent(message);
	}

}

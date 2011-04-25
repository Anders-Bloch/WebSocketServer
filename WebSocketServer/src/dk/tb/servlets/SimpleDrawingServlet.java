package dk.tb.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import dk.tb.pools.ClientPool;

@WebSocket(uri="SimpleDrawing")
public class SimpleDrawingServlet implements WebSocketServlet {

	@Inject private ClientPool clientPool;
	private OutputStream out;
	
	@Override
	public void socketEvent(String event) throws IOException {
		clientPool.callClients(event, "SimpleDrawing");
	}

	@Override
	public void messageEvent(String event) throws IOException {
		out.write(0x00);
		out.write(event.getBytes());
		out.write(0xff);
		out.flush();
	}

	@Override
	public void initServlet(OutputStream out) throws IOException {
		this.out = out;
	}

}

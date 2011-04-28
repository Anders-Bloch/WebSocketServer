package dk.tb.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import dk.tb.pools.ClientPool;

@WebSocket(uri="YellowStickers")
public class YellowStickers implements WebSocketServlet {

	@Inject private ClientPool clientPool;
	private OutputStream out;
	private String id;
	
	@Override
	public void socketEvent(String event) throws IOException {
		System.out.println(event);
		clientPool.callClients(event, "YellowStickers",id);
	}

	@Override
	public void messageEvent(String event) throws IOException {
		out.write(0x00);
		out.write(event.getBytes());
		out.write(0xff);
		out.flush();
	}

	@Override
	public void initServlet(OutputStream out, String id) throws IOException {
		this.out = out;
		this.id = id;
	}

}

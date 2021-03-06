package dk.tb.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dk.tb.server.ClientEvent;

@WebSocketServlet.WebSocket(uri="YellowStickers")
public class YellowStickers implements WebSocketServlet {
	
	private static Map<String, Task> tasks = Collections.synchronizedMap(new HashMap<String, Task>());

	@Inject private ClientEvent clientEvent;
	private OutputStream out;
	private String id;
	
	@Override
	public void socketEvent(String event) throws IOException {
		persistEvent(event);
		clientEvent.callClients(event, "YellowStickers",id);
	}

	private void persistEvent(String event) {
		String[] eventSplit = event.split(":");
		String func = eventSplit[0];
		if("new".equalsIgnoreCase(func)) {
			tasks.put(eventSplit[1], new Task(eventSplit[1], "new"));
		} else if("move".equalsIgnoreCase(func)) {
			String id = eventSplit[1];
			tasks.get(id).state = eventSplit[2];
		} else if("delete".equalsIgnoreCase(func)) {
			String id = eventSplit[1];
			tasks.remove(id);
		} else if("header".equalsIgnoreCase(func)) {
			String id = eventSplit[1].substring(6, eventSplit[1].length());
			if(eventSplit.length > 2)
				tasks.get(id).header = eventSplit[2];
		} else if("text".equalsIgnoreCase(func)) {
			String id = eventSplit[1].substring(4, eventSplit[1].length());
			if(eventSplit.length > 2)
				tasks.get(id).text = eventSplit[2];
		}
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
		if(tasks.size() > 0) {
			StringBuffer buffer = new StringBuffer();
			for (Task task : tasks.values()) {
				buffer.append(task.toString());
				buffer.append(";");
			}
			System.out.println(buffer.toString());
			out.write(0x00);
			out.write(buffer.toString().getBytes());
			out.write(0xff);
			out.flush();
		}
	}
	
	class Task {
		public String id;
		public String state;
		public String header;
		public String text;
		
		public Task(String id, String state) {
			this.id = id;
			this.state = state;
			header = "";
			text = "";
		}
		
		public String toString() {
			return "id:" + id + ":state:" + state + ":header:" + header + ":text:" + text;
		}
	}
}

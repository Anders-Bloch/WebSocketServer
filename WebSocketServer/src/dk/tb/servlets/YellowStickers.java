package dk.tb.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dk.tb.pools.ClientPool;

@WebSocket(uri="YellowStickers")
public class YellowStickers implements WebSocketServlet {
	
	private static Map<String, Task> tasks = Collections.synchronizedMap(new HashMap<String, Task>());

	@Inject private ClientPool clientPool;
	private OutputStream out;
	private String id;
	
	@Override
	public void socketEvent(String event) throws IOException {
		System.out.println(event);
		persistEvent(event);
		for (Task task : tasks.values()) {
			System.out.println(task);
		}
		clientPool.callClients(event, "YellowStickers",id);
	}

	private void persistEvent(String event) {
		String[] eventSplit = event.split(":");
		String func = eventSplit[0];
		if("new".equalsIgnoreCase(func)) {
			tasks.put(eventSplit[1], new Task(eventSplit[1], "new"));
		} else if("move".equalsIgnoreCase(func)) {
			String id = eventSplit[1];
			id = (Integer.parseInt(id)+1)+"";
			tasks.get(id).state = eventSplit[2];
		} else if("delete".equalsIgnoreCase(func)) {
			String id = eventSplit[1];
			id = (Integer.parseInt(id)+1)+"";
			tasks.remove(id);
		} else if("header".equalsIgnoreCase(func)) {
			String id = eventSplit[1];
			id = (Integer.parseInt(id.substring(6, id.length()))+1)+"";
			tasks.get(id).header = eventSplit[2];
		} else if("text".equalsIgnoreCase(func)) {
			String id = eventSplit[1];
			id = (Integer.parseInt(id.substring(4, id.length()))+1)+"";
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
			//buffer.append("state:");
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
			super();
			this.id = id;
			this.state = state;
		}
		
		public String toString() {
			
			return "id:" + (Integer.parseInt(id)-1) + ":state:" + state + ":header:" + header + ":text:" + text;
		}
	}
}

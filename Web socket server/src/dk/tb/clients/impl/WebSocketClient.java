package dk.tb.clients.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.Client;
import dk.tb.pools.ClientPool;


public class WebSocketClient implements Runnable, Client {
    
	private final ClientPool pool;
	
	private final OutputStream out;
	private final BufferedReader in;
	
	private final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);
	
	public WebSocketClient(ClientPool pool, Socket socket) throws IOException {
        super();
        logger.info("Initilazing new Client");
        this.pool = pool;
        this.out = socket.getOutputStream();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
	
    public void run() {
    	logger.info("Running client");
		try {
			StringBuilder builder = new StringBuilder();
			List<Integer> input = new ArrayList<Integer>();
			
			while(true) {
				int b = in.read();
				//logger.info("Input: " + b);
				//if(b == 0xff) {
				//if(b == 711) {
				if(b == 48) {
					builder = new StringBuilder();
					for (Integer i : input) {
						builder.append(Character.toChars(i));
					}
					pool.callAll(builder.toString());
					input.clear();
				} else {
					if(b != 0x00) {
						input.add(b);
					}
				}
			}
		} catch (IOException e) {
			logger.error("In client run: ",e);
		} 
		/*catch (InterruptedException e) {
			logger.error(e.getMessage());
		}*/
    }
    
    public void event(String event) throws IOException {
    	logger.info("Writing to stream, object:" + out.toString());
		out.flush();
		out.write(0x00);
		out.write(event.getBytes());
		out.write(0xFF);
		out.flush();
	}
}

package dk.tb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends Thread {
    
	//private final Socket client;
	private final ClientPool pool;
	
	private final OutputStream out;
	private final BufferedReader in;
	
	private final Logger logger = LoggerFactory.getLogger(Client.class);
	
	public Client(ClientPool pool, OutputStream out, BufferedReader in) {
        super();
        logger.info("Initilazing new Client");
        this.pool = pool;
        this.out = out;
        this.in = in;
    }
	
    public void run() {
    	logger.info("Running client");
		try {
			StringBuilder builder = new StringBuilder();
			List<Integer> input = new ArrayList<Integer>();
			
			while(true) {
				if(in.ready()) {
					int b = in.read();
					logger.info("Input: " + b);
					if(b == 711) {
						builder = new StringBuilder();
						for (Integer i : input) {
							builder.append(Character.toChars(i));
						}
						pool.callAll(builder.toString());
						input.clear();
					} else {
						input.add(b);
					}
				}
			}
		} catch (IOException e) {
			logger.error("In client run: ",e);
		}
    }
    
	public void event(String event) {
		logger.info("To client (out): "+event);
		try {
			out.flush();
			out.write(0x00);
			out.write(event.getBytes());
			out.write(0xFF);
			out.flush();
		} catch (IOException e) {
			logger.warn("Output write failed! Removing client from client pool.");
			pool.removeClient(this);
		}
	}
}

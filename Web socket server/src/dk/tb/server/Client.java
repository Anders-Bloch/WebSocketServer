package dk.tb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.server.handshakes.Handshake76;
import dk.tb.server.util.RequestHeaderMap;

class Client extends Thread {
    
	private final Socket client;
	private final ClientPool pool;
	private OutputStream out = null;
	
	private final Logger logger = LoggerFactory.getLogger(Client.class);
	
	public Client(Socket clientSocket, ClientPool pool) {
        super();
        logger.info("Initilazing new Client");
        client = clientSocket;
        this.pool = pool;
    }
	
    public void run() {
    	logger.info("Running client");
		try {
			out = client.getOutputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			StringBuilder builder = new StringBuilder();
			while(in.ready()) {
				builder.append(Character.toChars(in.read()));
			}
			logger.info(builder.toString());
			Handshake handshake = new Handshake76();
			byte[] bytes = handshake.createResponseHandshake(new RequestHeaderMap().extractHeader(builder.toString()));
			
			for (int i = 0; i < bytes.length; i++) {
				out.write(bytes[i]);
			}
			
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

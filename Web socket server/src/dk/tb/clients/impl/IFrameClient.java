package dk.tb.clients.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.clients.Client;

public class IFrameClient implements Client {
	
	private final Logger logger = LoggerFactory.getLogger(IFrameClient.class);
	private final OutputStream out;

	public IFrameClient(OutputStream out) {
		this.out = out;
	}
	
	public void event(String event) throws IOException {
		logger.info("Writing to stream, object:" + out.toString());
		String message = "<script type='text/javascript'>window.parent.addInput('" + event + "')</script>";
		String size = Integer.toHexString(message.getBytes().length);
		out.write(size.getBytes());
		out.write("\r\n".getBytes());
		out.write(message.getBytes());
		out.write("\r\n".getBytes());
		out.write("!".getBytes()); //JMeter
		out.flush();
		logger.info("Finished writing to outputstream!");
	}
}

package dk.tb.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.pools.ClientPool;

@Named
public class ClientImpl implements Client {
	
	private Socket socket;
  	@Inject private ClientPool clientPool; 
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void setFields(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		logger.info("New Client started: " + this.hashCode());
		try {
			InputStream in = socket.getInputStream();
			StringBuffer inBuilder = new StringBuffer();
			while(true) {
				byte b = (byte)in.read();
				if((int)b == -1) {
					if(inBuilder.length() == 0) break;
					clientPool.callClients(inBuilder.toString());
					inBuilder = new StringBuffer();
				} else if((int)b != 0) {
					inBuilder.append(Character.toChars(b));
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public void event(String message) throws IOException {
		OutputStream out = socket.getOutputStream();
		out.write(0x00);
		out.write(message.getBytes());
		out.write(0xff);
		out.flush();
	}

}

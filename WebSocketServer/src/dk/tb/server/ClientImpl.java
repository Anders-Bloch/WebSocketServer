package dk.tb.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.pools.ClientPool;

@Named
public class ClientImpl implements Client {
	
  	@Inject private ClientPool clientPool;
  	@Inject @Default private RequestObject requestObject;
  	private OutputStream out;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void run() {
		try {
			out = requestObject.getOutputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		logger.info("New Client started: " + this.hashCode());
		try {
			InputStream in = requestObject.getInputStream();
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
				requestObject.closeSocket();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public void event(String message) throws IOException {
		//logger.info("message received - updating client:"+this.hashCode() +"message:"+message);
		out.flush();
		out.write(0x00);
		out.write(message.getBytes());
		out.write(0xff);
		out.flush();
	}

}

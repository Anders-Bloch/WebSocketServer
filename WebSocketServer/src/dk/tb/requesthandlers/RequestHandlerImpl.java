package dk.tb.requesthandlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.tb.factories.util.ServerThreadPool;
import dk.tb.handshake.Handshake;
import dk.tb.pools.ClientPool;
import dk.tb.server.Client;
import dk.tb.server.Keys;
import dk.tb.server.RequestHeaderMap;

@Named
public class RequestHandlerImpl implements Handler {
	
	private Socket socket;

	@Inject private RequestHeaderMap requestHeaderMap;
	@Inject private ResourceRequestHandler resourceRequestHandler;
	@Inject private Handshake handshake;
	@Inject private ServerThreadPool threadPool;
	@Inject private Instance<Client> clientInstance;
	@Inject private ClientPool clientPool;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void handleRequest(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			StringBuffer buffer = new StringBuffer();
			
			byte[] key3 = new byte[8];

			while(checkEnd(buffer)) {
				buffer.append(Character.toChars(in.read()));
			}
			if(buffer.toString().contains("WebSocket")) {
				for (int i = 0; i < 8; i++) {
					key3[i] = (byte)in.read();
				}
			}
			String request = buffer.toString();
			if(request.length() < 1) {
				out.write("HTTP/1.1 400 OK\r\nExpires: -1\r\nCache-Control: private, max-age=0\r\nContent-Type: text/html; charset=UTF-8".getBytes());
				out.flush();
				socket.close();
			} else {
				Map<Keys, String> header = requestHeaderMap.extractHeader(request);
				String path = header.get(Keys.PATH);
				if(path != null && resourceRequestHandler.isResourceRequest(path)) {
					String output = resourceRequestHandler.resolveResource(path);
					out.write(output.getBytes());
					out.flush();
					socket.close();
				} else {
					logger.info("Request:\r\n"+buffer.toString()+String.valueOf(key3));
					writeOutput(out, header, key3);
					startClient();
				}
			}
		} catch(IOException e) {
			logger.error(e.getMessage());
		}
	}

	private void writeOutput(OutputStream out, Map<Keys, String> header, byte[] key3) throws IOException {
		byte[] response = handshake.createResponseHandshake(header, key3);
		logger.info("Response:\r\n"+new String(response));
		out.write(response);
		out.flush();
	}

	private void startClient() throws IOException {
		Client client = clientInstance.get();
		client.setFields(socket);
		clientPool.addClient(client);
		threadPool.runTask(client);
	}
	
	private boolean checkEnd(StringBuffer buffer) {
		if(buffer.length() > 3) {
			if(buffer.substring(buffer.length()-4, buffer.length()).equalsIgnoreCase("\r\n\r\n")) {
				return false;
			}
		}
		return true;
	}

}

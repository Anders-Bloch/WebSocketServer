package dk.tb.server.request.strategy;

import java.io.IOException;
import java.io.OutputStream;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import dk.tb.handshake.Handshake;
import dk.tb.pools.ClientPool;
import dk.tb.server.Client;
import dk.tb.server.RequestObject;
import dk.tb.server.util.RequestType;

@RequestStrategyQualifier
public class NewConnectionStrategy implements RequestStrategy {

	@Inject @Default RequestObject requestObject;
	@Inject private Handshake handshake;
	@Inject private Instance<Client> clientInstance;
	@Inject private ClientPool clientPool;
	
	@Override
	public RequestType getType() {
		return RequestType.CONNECT_REQUEST;
	}
	
	@Override
	public void request() throws IOException {
		writeOutput();
		startClient();
	}
	
	private void writeOutput() throws IOException {
		OutputStream out = requestObject.getOutputStream();
		byte[] response = handshake.createResponseHandshake();
		//printResponse(response);
		out.write(response);
		out.flush();
	}


	private void startClient() throws IOException {
		Client client = clientInstance.get();
		clientPool.addClient(client, requestObject.getServletUri());
		client.run();
	}
	
	//Debugging
	private void printResponse(byte[] response) {
		for (byte b : response) {
			System.out.print(Character.toChars(b));
		}
	}

}

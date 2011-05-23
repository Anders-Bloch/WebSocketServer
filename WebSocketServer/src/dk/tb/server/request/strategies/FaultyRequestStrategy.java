package dk.tb.server.request.strategies;

import java.io.IOException;
import java.io.OutputStream;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import dk.tb.server.request.RequestObject;
import dk.tb.server.request.RequestStrategy;
import dk.tb.server.request.RequestStrategy.RequestStrategyQualifier;
@RequestStrategyQualifier
public class FaultyRequestStrategy implements RequestStrategy {

	@Inject @Default RequestObject requestObject;
	
	@Override
	public void request() throws IOException {
		OutputStream out = requestObject.getOutputStream();
		out.write("HTTP/1.1 400 OK\r\nExpires: -1\r\nCache-Control: private, max-age=0\r\nContent-Type: text/html; charset=UTF-8".getBytes());
		out.flush();
		requestObject.closeSocket();
	}

	@Override
	public RequestType getType() {
		return RequestType.FAULT;
	}

}

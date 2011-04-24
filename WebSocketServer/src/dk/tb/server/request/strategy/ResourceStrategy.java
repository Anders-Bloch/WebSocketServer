package dk.tb.server.request.strategy;

import java.io.IOException;
import java.io.OutputStream;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import dk.tb.requesthandlers.ResourceRequestHandler;
import dk.tb.server.Keys;
import dk.tb.server.RequestObject;
import dk.tb.server.util.RequestType;

@RequestStrategyQ
public class ResourceStrategy implements RequestStrategy {

	@Inject private ResourceRequestHandler resourceRequestHandler;
	@Inject @Default RequestObject requestObject;
	
	@Override
	public void request() throws IOException {
		String path = requestObject.getHeaderMap().get(Keys.PATH);
		OutputStream out = requestObject.getOutputStream();
		String output = resourceRequestHandler.resolveResource(path);
		out.write(output.getBytes());
		out.flush();
		requestObject.closeSocket();
	}
	
	@Override
	public RequestType getType() {
		return RequestType.RESOURCE;
	}

}

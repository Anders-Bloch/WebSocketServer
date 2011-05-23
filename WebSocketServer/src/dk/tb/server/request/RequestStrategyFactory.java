package dk.tb.server.request;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import dk.tb.server.request.RequestStrategy.RequestStrategyQualifier;


@RequestScoped
public class RequestStrategyFactory {
	
	@Inject @Default RequestObject requestObject;
	@Inject @RequestStrategyQualifier Instance<RequestStrategy> instance;
	
	@Produces @Default @RequestScoped
	public RequestStrategy getRequestStrategy() {
		for (RequestStrategy req : instance) {
			if(req.getType() == requestObject.getRequestType()) {
				return req;
			}
		}
		return null;
	}
}

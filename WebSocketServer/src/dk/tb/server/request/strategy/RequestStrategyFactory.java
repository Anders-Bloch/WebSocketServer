package dk.tb.server.request.strategy;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import dk.tb.server.RequestObject;

@RequestScoped
public class RequestStrategyFactory {
	
	@Inject @Default RequestObject requestObject;
	@Inject @RequestStrategyQ Instance<RequestStrategy> instance;
	
	@Produces @RequestScoped
	public RequestStrategy getRequestStrategy() {
		for (RequestStrategy req : instance) {
			System.out.println(req.getType());
			if(req.getType() == requestObject.getRequestType()) {
				return req;
			}
		}
		return null;
	}
}

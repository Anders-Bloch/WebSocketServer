package dk.tb.server.request;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

public interface RequestStrategy {
	
	public RequestType getType();
	public void request() throws IOException;
	
	public enum RequestType {
		RESOURCE("Request for a static server resource ex. html page or javascript page"),
		FAULT("Request failed - ex. wrong path"),
		CONNECT_REQUEST("New Web Socket Client requesting connection");
		
		private String description;
		
		RequestType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}
	
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
	public @interface RequestStrategyQualifier {}
}

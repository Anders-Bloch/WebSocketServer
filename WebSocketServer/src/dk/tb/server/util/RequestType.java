package dk.tb.server.util;

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

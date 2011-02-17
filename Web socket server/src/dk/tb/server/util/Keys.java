package dk.tb.server.util;

public enum Keys {
	PATH("empty"),
	CONN("Connection"),
	HOST("Host"),
	ORIGIN("Origin"),
	UPGRADE("Upgrade"),
	LOCATION("Location"),
	KEY1("Sec-WebSocket-Key1"), 
	KEY2("Sec-WebSocket-Key2"), 
	KEY3("empty"),
	SEC_ORIGIN("Sec-WebSocket-Origin"),
	SEC_LOCATION("Sec-WebSocket-Location");
	
	private String key;
	
	Keys(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}

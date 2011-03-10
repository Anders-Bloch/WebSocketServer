package dk.tb.factories;

import dk.tb.factories.impl.WebSocketFactory;

public class ServerFactoryInit {
	
	public enum ServerInitParam {
		WebSocket,
		SimplePoll;
		
		public static ServerInitParam getParam(String initParam) {
			if(initParam.equalsIgnoreCase("WebSocket")) {
				return WebSocket;
			} else {
				return null;
			}
		}
	}
	
	public static ServerFactory getFactory(ServerInitParam param) {
		if(param == ServerInitParam.WebSocket) {
			return new WebSocketFactory();
		} else if(param == ServerInitParam.SimplePoll){
			return null;
		} else {
			return null;
		}
	}
}

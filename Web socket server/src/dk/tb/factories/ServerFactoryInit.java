package dk.tb.factories;

import dk.tb.factories.impl.IFrameFactory;
import dk.tb.factories.impl.WebSocketFactory;
import dk.tb.factories.impl.XHRPollFactory;

public class ServerFactoryInit {
	
	public enum ServerInitParam {
		WebSocket,
		IFrame,
		XHRPoll;
		
		public static ServerInitParam getParam(String initParam) {
			if(initParam.equalsIgnoreCase("WebSocket")) {
				return WebSocket;
			} else if(initParam.equalsIgnoreCase("IFrame")) {
				return IFrame;
			} else if(initParam.equalsIgnoreCase("XHRPoll")) {
				return XHRPoll;
			} else {
				return null;
			}
		}
	}
	
	public static ServerFactory getFactory(ServerInitParam param) {
		if(param == ServerInitParam.WebSocket) {
			return new WebSocketFactory();
		} else if(param == ServerInitParam.XHRPoll){
			return new XHRPollFactory();
		} else if(param == ServerInitParam.IFrame){
			return new IFrameFactory();
		} else {
			return null;
		}
	}
}

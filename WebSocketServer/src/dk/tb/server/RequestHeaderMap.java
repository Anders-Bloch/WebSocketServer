package dk.tb.server;

import java.util.Map;

public interface RequestHeaderMap {

	public Map<Keys, String> extractHeader(String header);

}
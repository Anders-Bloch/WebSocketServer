package dk.tb.handlers.util;

import java.io.FileReader;
import java.io.IOException;

public class ResourceRequestHandler {
	
	public boolean isResourceRequest(String path) {
		return path.contains(".");
	}
	
	public String resolveResource(String path) throws IOException {
		path = path.substring(1);
		StringBuilder result = new StringBuilder();
		result.append("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nCache-Control: no-cache\r\nExpires: Fri, 01 Jan 1990 00:00:00 GMT\r\n");
		FileReader reader = new FileReader(path);
		StringBuilder builder = new StringBuilder();
		while(reader.ready()) {
			builder.append(Character.toChars(reader.read()));
		}
		result.append("Content-Length: " + builder.length() + "\r\n\r\n");
		result.append(builder.toString());
		reader.close();
		return result.toString();
	}
}

package dk.tb.server;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaderMapImpl implements RequestHeaderMap {
	
	@Override
	public Map<Keys, String> extractHeader(String header) {
	    String[] lines = header.split("\r?\n|\r");
	    Map<Keys, String> result = new HashMap<Keys, String>();
	    //Add path
	    int start = lines[0].indexOf("GET")+4;
	    int end = lines[0].indexOf("HTTP")-1;
	    result.put(Keys.PATH, lines[0].substring(start, end).trim());
	    //Add all : separated values
	    int i = 1;
	    while(i < lines.length) {
	    	int index = lines[i].indexOf(":");
	    	if(index != -1) {
	    		String[] lineSplit = new String[] {
	    				lines[i].substring(0, index), 
	    				lines[i].substring(index+1, lines[i].length())};
    			for (Keys key : Keys.values()) {
    				if(key.getKey().equals(lineSplit[0])) {
    					result.put(key,lineSplit[1].trim());
    				}
    			}
	    	}
	    	i++;
	    }
	    return result;
	}

}

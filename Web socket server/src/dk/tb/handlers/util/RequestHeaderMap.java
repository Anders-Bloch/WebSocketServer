package dk.tb.handlers.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHeaderMap {
	
	private final Logger logger = LoggerFactory.getLogger(RequestHeaderMap.class);
	
	public Map<Keys, String> extractHeader(String header) {
	    String[] lines = header.split("\r?\n|\r");
	    Map<Keys, String> result = new HashMap<Keys, String>();
	    //Add path
	    int start = lines[0].indexOf("GET")+4;
	    int end = lines[0].indexOf("HTTP")-1;
	    logger.info("Lines in header: " + lines.length);
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
	    result.put(Keys.KEY3, lines[lines.length-1].trim());
	    return result;
	}

}

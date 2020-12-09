package Webserver;

import java.util.HashMap;
import java.util.Map;

public class Request {
	
	public String method;
	public String URI;
	public String HTTPVersion;
	public Map<String, String> headers;
	public String body;
	
	// https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5
	public Request(String rawHTTP) throws Exception {
		if(rawHTTP.trim().length() == 0) {
			throw new Exception("Empty request");
		}
		
		// Line counter
		int i = 0;
		String[] lines = rawHTTP.split("\n");
		
		// Ignore empty leading lines
		while(i < lines.length && lines[i].trim().length() == 0) {
			++i;
		}
		
		// Webserver.Request-Line
		String[] requestLine = lines[i++].split(" ");
		if(requestLine.length != 3) {
			throw new Exception(String.format("Invalid Webserver.Request-Line:\n%s\n", lines[i - 1]));
		}
		else {
			this.method = requestLine[0];
			this.URI = requestLine[1];
			this.HTTPVersion = requestLine[2];
		}
		
		// general-header
		// request-header
		// CRLF
		this.headers = new HashMap<String, String>();
		while(i < lines.length && lines[i].trim().length() != 0) {
			String[] header = lines[i].split(":", 2);
			if(header.length != 2 || header[0].trim().length() == 0) {
				// Invalid header
				continue;
			}
			
			headers.put(header[0].trim(), header[1].trim());
			
			++i;
		}
		
		// lines[i] should be a CRLF here. Next line is the beginning of request's body
		++i;
		
		StringBuilder bodyBuilder = new StringBuilder();
		while(i < lines.length) {
			bodyBuilder.append(lines[i++]);
			bodyBuilder.append("\r\n");
		}
		this.body = bodyBuilder.toString();
		
	}
	
}

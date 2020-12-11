package Webserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request {
	
	public String method;
	public String URI;
	public String HTTPVersion;
	public Map<String, String> headers;
	public String body;
	
	// For test purposes
	public Request() {
		method = "";
		URI = "";
		HTTPVersion = "";
		headers = new HashMap<String, String>();
		body = "";
	}
	
	// https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5
	public Request(String rawHTTP) throws Exception {
		if(rawHTTP.strip().length() == 0) {
			throw new Exception("Empty request");
		}
		
		// Line counter
		int i = 0;
		String[] lines = rawHTTP.split("\n");
		
		// Ignore empty leading lines
		while(i < lines.length && lines[i].strip().length() == 0) {
			++i;
		}
		
		// Request-Line
		String[] requestLine = lines[i++].split(" ");
		if(requestLine.length != 3) {
			throw new Exception(String.format("Invalid Request-Line:\n%s\n", lines[i - 1]));
		}
		else {
			this.method = requestLine[0].strip();
			this.URI = requestLine[1].strip();
			this.HTTPVersion = requestLine[2].strip();
		}
		
		// general-header
		// request-header
		// CRLF
		this.headers = new HashMap<String, String>();
		while(i < lines.length && lines[i].strip().length() != 0) {
			String[] header = lines[i].split(":", 2);
			if(header.length != 2 || header[0].strip().length() == 0) {
				// Invalid header
				continue;
			}
			
			headers.put(header[0].strip(), header[1].strip());
			
			++i;
		}
		
		// lines[i] should be a CRLF here. Next line is the beginning of request's body
		++i;
		
		StringBuilder bodyBuilder = new StringBuilder();
		while(i < lines.length) {
			bodyBuilder.append(lines[i++].strip());
			bodyBuilder.append("\r\n");
		}
		this.body = bodyBuilder.toString().strip();
		
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Request request = (Request) o;
		return Objects.equals(method, request.method) &&
			Objects.equals(URI, request.URI) &&
			Objects.equals(HTTPVersion, request.HTTPVersion) &&
			Objects.equals(headers, request.headers) &&
			Objects.equals(body, request.body);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(method, URI, HTTPVersion, headers, body);
	}
}

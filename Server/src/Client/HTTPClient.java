package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HTTPClient {
	
	public interface HTTPResponseHandler {
		void onResponse(String responseBody);
	}
	
	public static void send(String URI, String Body, HTTPResponseHandler handler) {
		new Thread(() -> {
			try {
				handleMessage(URI, Body, handler);
			}
			catch(IOException e) {
				System.out.printf("Error while sending a message:\n%s\n", e);
			}
		}).start();
	}
	
	private static void handleMessage(String URI, String Body, HTTPResponseHandler handler) throws IOException {
		Socket serverSocket = new Socket(InetAddress.getLoopbackAddress(), 1234);
		
		OutputStream requestStream = serverSocket.getOutputStream();
		
		StringBuilder request = new StringBuilder();
		request.append("GET ");
		request.append(URI);
		request.append(" HTTP/1.1\r\n");
		request.append("\r\n");
		for(String i : Body.split("\n")) {
			request.append(i.strip());
			request.append("\r\n");
		}
		
		requestStream.write(request.toString().getBytes(StandardCharsets.UTF_8));
		
		BufferedReader input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		
		// Sometimes a client establishes connection before sending any data
		// Maximum wait time in seconds
		final double waitTime = 10;
		// Retries to read data after [d] seconds
		double waitInterval = 0.02;
		// Current wait time in seconds
		double waitedFor = 0;
		while(waitedFor <= waitTime && !input.ready()) {
			try {
				Thread.sleep((long) (waitInterval * 1000));
				waitedFor += waitInterval;
				waitInterval *= 1.5;
			}
			catch(InterruptedException e) {
				return;
			}
		}
		
		if(!input.ready()) {
			// No data after [waitTime] seconds. Close the connection.
			serverSocket.close();
			return;
		}
		
		StringBuilder data = new StringBuilder();
		while(input.ready()) {
			data.append(input.readLine());
			data.append("\n");
		}
		
		serverSocket.close();
		handler.onResponse(data.toString());
	}
	
}

package common;

import Webserver.Request;
import Webserver.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HTTPClient {
    
    public static final Map<String, String> defaultHeaders = new HashMap<String, String> ();
    private static String address = "127.0.0.1";
    private static int port = 1234;
    
    public interface HTTPResponseHandler {
        void onResponse (Response res);
    }
    
    public static void setServerAddress (String address) {
        HTTPClient.address = address;
    }
    
    public static void setServerPort (int port) {
        HTTPClient.port = port;
    }
    
    public static void send (String URI, String body, HTTPResponseHandler handler) {
        Request req = new Request ();
        req.method = "GET";
        req.URI = URI;
        req.body = body;
        
        send (req, handler);
    }
    
    public static void send (Request req, HTTPResponseHandler handler) {
        new Thread (() -> {
            try {
                handleMessage (req, handler);
            } catch (IOException e) {
                System.out.printf ("Error while sending a message:\n%s\n", e);
            }
        }).start ();
    }
    
    private static void handleMessage (Request req, HTTPResponseHandler handler) throws IOException {
        // Add default headers
        for (Map.Entry<String, String> i : defaultHeaders.entrySet ()) {
            req.headers.put (i.getKey (), i.getValue ());
        }
        
        Socket serverSocket = new Socket (InetAddress.getByName (address), port);
        
        OutputStream requestStream = serverSocket.getOutputStream ();
        requestStream.write (req.toByteArray ());
        serverSocket.shutdownOutput ();
        
        BufferedReader input = new BufferedReader (new InputStreamReader (serverSocket.getInputStream (), StandardCharsets.UTF_8));
        // Sometimes a client establishes connection before sending any data
        // Maximum wait time in seconds
        final double waitTime = 10;
        // Retries to read data after [d] seconds
        double waitInterval = 0.02;
        // Current wait time in seconds
        double waitedFor = 0;
        while (waitedFor <= waitTime && !input.ready ()) {
            try {
                Thread.sleep ((long) (waitInterval * 1000));
                waitedFor += waitInterval;
                waitInterval *= 1.5;
            } catch (InterruptedException e) {
                return;
            }
        }
        
        if (!input.ready ()) {
            // No data after [waitTime] seconds. Close the connection.
            serverSocket.close ();
            return;
        }
        
        StringBuilder data = new StringBuilder ();
        while (input.ready ()) {
            data.append (input.readLine ());
            data.append ("\n");
        }
        
        Response res;
        try {
            res = new Response (data.toString ());
        } catch (Exception e) {
            System.out.printf ("Error while parsing a response: \n%s\n", e);
            return;
        }
        
        serverSocket.close ();
        handler.onResponse (res);
    }
    
}

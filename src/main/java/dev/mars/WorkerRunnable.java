package dev.mars;

import java.io.*;
import java.net.Socket;

public class WorkerRunnable implements Runnable{
    protected Socket clientSocket = null;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            // Create reader and writer
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream output = clientSocket.getOutputStream();

            // Read the HTTP request (but we don't need to process it for this simple example)
            String line;
            StringBuilder requestBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line).append("\r\n");
            }

            // Create a proper HTTP response
            String responseBody = "Hello, World! This is a simple HTTP server.";

            // Write HTTP response with proper CRLF line endings
            String response = 
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Length: " + responseBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseBody;

            // Send the response
            output.write(response.getBytes());
            output.flush();

            // Close resources
            output.close();
            reader.close();
            clientSocket.close();

            System.out.println("Response sent to client: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

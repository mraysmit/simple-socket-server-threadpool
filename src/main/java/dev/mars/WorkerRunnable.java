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

            // Read the HTTP request and process it
            String line;
            StringBuilder requestBuilder = new StringBuilder();
            String requestLine = null;

            // Read the first line (request line) separately
            if ((line = reader.readLine()) != null) {
                requestLine = line;
                requestBuilder.append(line).append("\r\n");

                // Read the rest of the headers
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    requestBuilder.append(line).append("\r\n");
                }
            }

            // Parse the request line to determine the HTTP verb and handle accordingly
            String name = parseRequestLine(requestLine);

            // Create a proper HTTP response with the name parameter
            String responseBody = "Hello, " + name + "!";

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

    /**
     * Parses the request line to determine the HTTP verb and calls the appropriate handler.
     *
     * @param requestLine The HTTP request line
     * @return The value to be used in the response
     */
    private String parseRequestLine(String requestLine) {
        if (requestLine == null) {
            return "World";
        }

        // Extract the HTTP verb
        String[] parts = requestLine.split(" ");
        if (parts.length < 1) {
            return "World";
        }

        String httpVerb = parts[0];

        // Call the appropriate handler based on the HTTP verb
        switch (httpVerb) {
            case "GET":
                return getWithParam(requestLine);
            // Add cases for other HTTP verbs as needed (POST, PUT, DELETE, etc.)
            default:
                return "World";
        }
    }

    /**
     * Extracts the value of the 'name' parameter from a GET request.
     *
     * @param requestLine The HTTP request line
     * @return The value of the 'name' parameter, or "World" if not found
     */
    private String getWithParam(String requestLine) {
        String name = "World";
        if (requestLine != null && requestLine.startsWith("GET")) {
            // Extract the URL part
            String[] parts = requestLine.split(" ");
            if (parts.length >= 2) {
                String url = parts[1];
                // Check if there are query parameters
                if (url.contains("?")) {
                    String queryString = url.substring(url.indexOf("?") + 1);
                    String[] params = queryString.split("&");
                    for (String param : params) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2 && keyValue[0].equals("name")) {
                            name = keyValue[1];
                            break;
                        }
                    }
                }
            }
        }
        return name;
    }
}

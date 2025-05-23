package dev.mars;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    private static final int PORT = 9000;
    private ThreadPooledServer server;
    private ExecutorService executorService;

    @BeforeEach
    public void setUp() {
        // Create and start the server in a separate thread
        server = new ThreadPooledServer(PORT, 5);
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(server);

        // Give the server a moment to start up
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        // Stop the server
        if (server != null) {
            server.stop();
        }

        // Shutdown the executor service
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    @Test
    public void testHttpResponse() throws IOException {
        // Create a socket connection to the server
        Socket socket = new Socket("localhost", PORT);

        // Send an HTTP GET request
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("GET / HTTP/1.1");
        out.println("Host: localhost:" + PORT);
        out.println("Connection: close");
        out.println(); // Empty line to indicate end of headers

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read status line
        String statusLine = in.readLine();
        System.out.println("[DEBUG_LOG] Status line: " + statusLine);
        assertTrue(statusLine.contains("200 OK"), "Status line should contain 200 OK");

        // Read headers
        String line;
        boolean foundContentType = false;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println("[DEBUG_LOG] Header: " + line);
            if (line.toLowerCase().startsWith("content-type:")) {
                foundContentType = true;
                assertTrue(line.toLowerCase().contains("text/plain"), 
                        "Content-Type header should specify text/plain");
            }
        }
        assertTrue(foundContentType, "Response should include Content-Type header");

        // Read body
        StringBuilder responseBody = new StringBuilder();
        while ((line = in.readLine()) != null) {
            responseBody.append(line);
        }

        // Close resources
        in.close();
        out.close();
        socket.close();

        // Verify the response body
        assertEquals("Hello, World!", responseBody.toString());

        System.out.println("HTTP test passed successfully!");
    }

    @Test
    public void testHttpResponseWithNameParameter() throws IOException {
        // Create a socket connection to the server
        Socket socket = new Socket("localhost", PORT);

        // Send an HTTP GET request with name parameter
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("GET /?name=John HTTP/1.1");
        out.println("Host: localhost:" + PORT);
        out.println("Connection: close");
        out.println(); // Empty line to indicate end of headers

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read status line
        String statusLine = in.readLine();
        System.out.println("[DEBUG_LOG] Status line: " + statusLine);
        assertTrue(statusLine.contains("200 OK"), "Status line should contain 200 OK");

        // Read headers
        String line;
        boolean foundContentType = false;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println("[DEBUG_LOG] Header: " + line);
            if (line.toLowerCase().startsWith("content-type:")) {
                foundContentType = true;
                assertTrue(line.toLowerCase().contains("text/plain"), 
                        "Content-Type header should specify text/plain");
            }
        }
        assertTrue(foundContentType, "Response should include Content-Type header");

        // Read body
        StringBuilder responseBody = new StringBuilder();
        while ((line = in.readLine()) != null) {
            responseBody.append(line);
        }

        // Close resources
        in.close();
        out.close();
        socket.close();

        // Verify the response body contains the name parameter
        assertEquals("Hello, John!", responseBody.toString());

        System.out.println("HTTP test with name parameter passed successfully!");
    }
}

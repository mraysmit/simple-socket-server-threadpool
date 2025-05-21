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
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);


            writer.print("HTTP/1.1 200 OK \r\n\r\n");
          //  writer.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Length: 0\r\n\r\n");

            // Write a proper HTTP response
//            String responseBody = "ResponseBody";
//            writer.println("HTTP/1.1 200 OK");
//            writer.println("Content-Type: text/plain; charset=UTF-8");
//            writer.println("Content-Length: " + responseBody.length());
//            writer.println();
//            writer.println(responseBody);

            writer.close();
            reader.close();


        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}

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

            writer.print("HTTP/1.1 200 OK\n\nWorkerRunnable: hello".getBytes());
            writer.close();
            reader.close();

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}

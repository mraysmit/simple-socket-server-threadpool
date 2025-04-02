package dev.mars;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

public class WorkerRunnable implements Runnable{
    protected Socket clientSocket = null;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            output.write("HTTP/1.1 200 OK\n\nWorkerRunnable: hello".getBytes());
            output.close();
            input.close();

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}

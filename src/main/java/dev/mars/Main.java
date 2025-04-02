package dev.mars;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        var server = new ThreadPooledServer(9000, 10);
        System.out.println("Starting Server");
        new Thread(server).start();

        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("Stopping Server");
        server.stop();

    }

}

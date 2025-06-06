package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int PORT;

    public static void main(String[] args) {
        if (args.length < 1) return;
        Server.PORT = Integer.parseInt(args[0]);


        try (ServerSocket serverSocket = new ServerSocket(Server.PORT)) {
            System.out.println("Server is listening on port " + Server.PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                new ServerThread(socket).start();
            }
        }
        catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
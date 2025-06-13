package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class Server {
    private static int PORT;

    public static void main(String[] args) {
        if (args.length < 1) return;
        Server.PORT = Integer.parseInt(args[0]);


        try (ServerSocket serverSocket = new ServerSocket(Server.PORT)) {
            System.out.println("Server is listening on port " + Server.PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getLocalAddress().getHostName() + ": New client connected");

                ServerThread thread = new ServerThread(socket);
                thread.setName(thread.getName() + "\t" + socket.getLocalAddress().getHostName());
                thread.start();
                printActiveServices();
            }
        }
        catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void printActiveServices() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        System.out.printf("%-15s \t %-15s \t %-15s \t %-15s \t %s\n", "Name", "IP", "State", "Priority", "isDaemon");
        threads.stream().filter(thread -> thread.getName().startsWith("Thread-")).forEach(thread ->
                System.out.printf("%-15s \t %-15s \t %-15s \t %-15d \t %s\n",
                thread.getName().split("\t")[0], thread.getName().split("\t")[1],
                thread.getState(), thread.getPriority(), thread.isDaemon()));
    }
}
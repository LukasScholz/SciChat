package org.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

public class Server {
    private static int PORT;

    public static void main(String[] args) {
        if (args.length < 1) return;
        Server.PORT = Integer.parseInt(args[0]);


        try (ServerSocket serverSocket = new ServerSocket(Server.PORT)) {
            System.out.println("Server is listening on port " + Server.PORT);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println(socket.getLocalAddress().getHostName() + ": New client connected");

                    // Get Public Key from Client
                    BufferedReader keyReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String input = keyReader.readLine();
                    RSAPublicKey publicKey = getPublicKeyfromString(input);
                    ServerThread thread = new ServerThread(socket, publicKey, null);
                    thread.setName(thread.getName() + "\t" + socket.getLocalAddress().getHostName());
                    thread.start();
                    printActiveServices();
                }
                catch (Exception E) {E.printStackTrace();}
            }
        }
        catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static RSAPublicKey getPublicKeyfromString(String publicKeyB64) throws NoSuchAlgorithmException, InvalidKeySpecException {
            byte[] decoded = Base64.getDecoder().decode(publicKeyB64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
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
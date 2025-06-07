package org.server;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ServerThread extends Thread {
    private final Socket SOCKET;
    private final BufferedReader READER;
    private final PrintWriter WRITER;

    public ServerThread(Socket socket) throws IOException {
        this.SOCKET = socket;
        InputStream input = socket.getInputStream();
        this.READER = new BufferedReader(new InputStreamReader(input));
        OutputStream output = socket.getOutputStream();
        this.WRITER = new PrintWriter(output, true);
    }

    public void run() {
        try {
            String message;
            while (true){
                 message = READER.readLine();
                 if(isTermination(message)) break;
                 parseMessage(message);
            }
            SOCKET.close();
            System.out.println("Client disconnected");

        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void parseMessage(String message) {
        WRITER.println("Server: " + message.toUpperCase()
        );
    }

    private static boolean isTermination(String message) {
        return message.equals("bye");
    }
}
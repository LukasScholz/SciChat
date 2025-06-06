package org.server;


import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        InputStream input = socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(input));
        OutputStream output = socket.getOutputStream();
        this.writer = new PrintWriter(output, true);
    }



    public void run() {
        try {
            String message;
            while (true){
                 message = reader.readLine();
                 if(!isTermination(message)) break;
                 parseMessage(message);
            }
            socket.close();

        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void parseMessage(String message) {
        writer.println("Server: " + message);
    }

    private static boolean isTermination(String message) {
        return message.equals("bye");
    }
}
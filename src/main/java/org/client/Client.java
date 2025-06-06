package org.client;

import java.io.*;
import java.net.Socket;

public class Client {
    
    private final String server;
    private final int port;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Socket socket;


    public Client(String[] args) throws IOException{
        if (args.length < 2) throw new IllegalArgumentException();

        server = args[0];
        port = Integer.parseInt(args[1]);

        socket = new Socket(server, port);

        OutputStream output = socket.getOutputStream();

        writer = new PrintWriter(output, true);

        InputStream input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));
    }

    private static boolean isTermination(String message) {
        return message.equals("bye");
    }
    public static void main(String[] args) {
        try {
            Client client = new Client(args);

            Console console = System.console();
            String text;
            String message;

            do {
                text = console.readLine("Enter text: ");

                client.writer.println(text);
                message = client.reader.readLine();

                System.out.println(message);

            } while (!isTermination(message));
            
            client.socket.close();
        }
        catch (Exception ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}

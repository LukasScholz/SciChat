package org.client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Client {
    
    private final int KEYSIZE = 4096;
    private final String server;
    public final KeyPair KEYS;
    private final int PORT;
    private final BufferedReader READER;
    private final PrintWriter WRITER;
    private final Socket SOCKET;
    private final BufferedReader CONSOLEREADER;


    public Client(String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length < 2) throw new IllegalArgumentException();

        server = args[0];
        PORT = Integer.parseInt(args[1]);

        SOCKET = new Socket(server, PORT);

        OutputStream output = SOCKET.getOutputStream();

        WRITER = new PrintWriter(output, true);

        InputStream input = SOCKET.getInputStream();
        READER = new BufferedReader(new InputStreamReader(input));

        CONSOLEREADER = new BufferedReader(new InputStreamReader(System.in));
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEYSIZE);
        KEYS = generator.generateKeyPair();
    }

    public String encrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, KEYS.getPublic());
        byte[] secretMessageBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public String decrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedMessageBytes = Base64.getDecoder().decode(message);
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, KEYS.getPrivate());
        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    private static boolean isTermination(String message) {
        return message.equals("bye");
    }
    private String read(String message) throws IOException {System.out.println(message); return CONSOLEREADER.readLine();}
    public static void main(String[] args) {
        try {
            Client client = new Client(args);
            // send public Key to server
            String b64key = Base64.getEncoder().encodeToString(client.KEYS.getPublic().getEncoded());
            client.WRITER.println(b64key);
            String message;

            do {
                String text = client.read("Enter text: ");

                client.WRITER.println(text);
                message = client.READER.readLine();

                System.out.println((message));
                System.out.println(client.decrypt(message));

            } while (!isTermination(message));
            
            client.SOCKET.close();
        }
        catch (Exception ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}

package org.server;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class ServerThread extends Thread {
    private final Socket SOCKET;
    private final BufferedReader READER;
    private final PrintWriter WRITER;
    public final PublicKey CLIENTKEY;
    public final PrivateKey SERVERKEY;

    public ServerThread(Socket socket, PublicKey publicKey, PrivateKey privateKey) throws IOException {
        this.SOCKET = socket;
        InputStream input = socket.getInputStream();
        this.READER = new BufferedReader(new InputStreamReader(input));
        OutputStream output = socket.getOutputStream();
        this.WRITER = new PrintWriter(output, true);
        this.CLIENTKEY = publicKey;
        this.SERVERKEY = privateKey;
    }

    public void run() {
        try {
            String message;
            while (true){
                 message = READER.readLine();
                 if(isTermination(message)) break;
                 parseMessage(message);
            }
            System.out.println(SOCKET.getLocalAddress().getHostName() + ": Client disconnected");
            SOCKET.close();

        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String encrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, CLIENTKEY);
        byte[] secretMessageBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public String decrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedMessageBytes = Base64.getDecoder().decode(message);
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, SERVERKEY);
        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    private void parseMessage(String message) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        WRITER.println(this.encrypt("Server: " + message.toUpperCase()));
    }

    private static boolean isTermination(String message) {
        return message.equals("bye");
    }

}
package Model;

import Model.ChatServer;
import Model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ObjectInputStream clientIn;
    private final ObjectOutputStream clientOut;
    private String username;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
        clientIn = new ObjectInputStream(clientSocket.getInputStream());
        // Read the username sent by the client
        try {
            username = (String) clientIn.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        try {

            // Read messages from the client and forward them to the appropriate recipient
            while (true) {
                Message message = (Message) clientIn.readObject();
                if (message == null) {
                    break;
                }
                // Forward the message to the recipient:
                ChatServer chatServer = ChatServer.getInstance();
                chatServer.sendMessage(message);
            }

            // Client Disconnected:
            clientSocket.close();
            ChatServer.getInstance().removeClient(username);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Send the message to the client:
    public synchronized void sendMessage(Message message) throws IOException {
        clientOut.writeObject(message);
        clientOut.flush();
    }

    public String getUsername() {
        return username;
    }
}

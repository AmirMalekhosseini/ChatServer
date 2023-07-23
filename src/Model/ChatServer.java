package Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 12345;
    private static final int MAX_THREADS = 10;

    private static ChatServer instance;
    private  Map<String, ClientHandler> clientsMap = new ConcurrentHashMap<>();

    private ChatServer() {

    }

    public static synchronized ChatServer getInstance() {
        if (instance == null) {
            instance = new ChatServer();
        }
        return instance;
    }

    public void start() {
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            // ToDo: Change While True.
            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Create a new thread to handle the client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientsMap.put(clientHandler.getUsername(), clientHandler);
                threadPool.submit(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(Message message) throws IOException {
        // Send the message to the client
        ClientHandler recipientHandler = clientsMap.get(message.getReceiver());
        if (recipientHandler != null) {
            recipientHandler.sendMessage(message);
        }
    }

    public void removeClient(String username) {
        clientsMap.remove(username);
    }
}

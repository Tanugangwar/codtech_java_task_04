import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 1234;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server started...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler handler = new ClientHandler(clientSocket, clientHandlers);
                clientHandlers.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Set<ClientHandler> handlers;

    public ClientHandler(Socket socket, Set<ClientHandler> handlers) {
        this.socket = socket;
        this.handlers = handlers;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                broadcastMessage(message);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler handler : handlers) {
            if (handler != this) {
                handler.out.println(message);
            }
        }
    }

    private void closeEverything() {
        try {
            in.close();
            out.close();
            socket.close();
            handlers.remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            Thread receiveThread = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println("Received: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            receiveThread.start();

            System.out.println("Enter your name:");
            String name = scanner.nextLine();
            out.println(name + " has joined the chat.");

            while (true) {
                String msg = scanner.nextLine();
                out.println(name + ": " + msg);
            }

        } catch (IOException e) {
            System.out.println("Unable to connect to server.");
        }
    }
}

// Author: Rahulkumar Arya
// Date: 20/07/2025
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static int PORT = 3456;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter port number for server: ");
        try {
            PORT = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default port 3456.");
        }
        scanner.close();
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clientHandlers.add(handler);
                new Thread(handler).start();
            }
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ignored) {}
        }
    }

    static void broadcast(String message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler handler : clientHandlers) {
                if (handler != sender) {
                    handler.sendMessage(message);
                }
            }
        }
    }

    static void removeHandler(ClientHandler handler) {
        clientHandlers.remove(handler);
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Enter your name:");
                name = in.readLine();
                out.println("Welcome, " + name + "! You can start chatting.");
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.equalsIgnoreCase("exit")) break;
                    Server.broadcast(name + ": " + msg, this);
                }
            } catch (IOException e) {
                System.out.println("Connection error: " + e.getMessage());
            } finally {
                Server.removeHandler(this);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        void sendMessage(String message) {
            out.println(message);
        }
    }
}

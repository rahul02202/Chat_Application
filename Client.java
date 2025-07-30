// Author: Rahulkumar Arya
// Date: 20/07/2025
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static int SERVER_PORT = 3456;

    public static void main(String[] args) throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter port number to connect to server: ");
        try {
            SERVER_PORT = Integer.parseInt(userInput.readLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default port 3456.");
        }
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Thread receiveThread = new Thread(() -> {
            String msg;
            try {
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException ignored) {}
        });
        receiveThread.start();

        String input;
        while ((input = userInput.readLine()) != null) {
            out.println(input);
            if (input.equalsIgnoreCase("exit")) break;
        }
        socket.close();
    }
}

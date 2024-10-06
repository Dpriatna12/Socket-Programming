import java.io.*;
import java.net.*;

public class BlockingClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message;

            do {
                System.out.print("Enter message (type 'bye' to exit): ");
                message = consoleReader.readLine();
                writer.println(message); // Kirim pesan ke server

                String response = reader.readLine(); // Terima respons dari server
                System.out.println("Server response: " + response);

            } while (!message.equalsIgnoreCase("bye"));

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}

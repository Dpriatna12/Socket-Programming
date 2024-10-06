import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingServer {
    private static final int PORT = 8080;
    private static ExecutorService pool = Executors.newFixedThreadPool(10); // Thread pool untuk menangani klien

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept(); // Menerima koneksi dari klien
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                // Menangani setiap klien dalam thread terpisah
                pool.execute(new ClientHandler(socket));
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}

// Thread untuk menangani komunikasi dengan klien
class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String clientAddress = socket.getInetAddress().getHostAddress();
            String message;

            while ((message = reader.readLine()) != null) {
                System.out.println("Received from " + clientAddress + ": " + message);
                writer.println("Echo from server to " + clientAddress + ": " + message);
            }

            System.out.println("Client disconnected: " + clientAddress);
        } catch (IOException ex) {
            System.out.println("Client connection error: " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

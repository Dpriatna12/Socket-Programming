import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NonBlockingClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
        clientChannel.configureBlocking(false); // Set non-blocking mode

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            System.out.print("Enter message (type 'bye' to exit): ");
            String message = scanner.nextLine();

            buffer.clear();
            buffer.put(message.getBytes());
            buffer.flip();
            clientChannel.write(buffer); // Kirim pesan ke server

            // Baca respons dari server
            buffer.clear();
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                System.out.println("Server response: " + new String(buffer.array()).trim());
            }

            if (message.equalsIgnoreCase("bye")) {
                break; // Keluar dari loop
            }
        }

        clientChannel.close();
    }
}

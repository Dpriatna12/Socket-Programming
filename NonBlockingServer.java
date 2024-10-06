import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NonBlockingServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false); // Set non-blocking mode
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // Daftarkan server untuk menerima koneksi

        System.out.println("Non-blocking server is listening on port " + PORT);

        while (true) {
            selector.select(); // Tunggu hingga ada event
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    // Koneksi klien baru diterima
                    SocketChannel clientChannel = serverSocketChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ); // Daftarkan untuk membaca
                    System.out.println("New client connected: " + clientChannel.getRemoteAddress());
                }

                if (key.isReadable()) {
                    // Membaca data dari klien
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    int bytesRead = clientChannel.read(buffer);

                    if (bytesRead == -1) {
                        System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
                        clientChannel.close();
                    } else {
                        buffer.flip();
                        String message = new String(buffer.array()).trim();
                        System.out.println("Received from " + clientChannel.getRemoteAddress() + ": " + message);

                        // Mengirim respons kembali ke klien
                        buffer.flip();
                        clientChannel.write(buffer);
                    }
                }

                keyIterator.remove(); // Hapus kunci yang telah diproses
            }
        }
    }
}

package handWritingNetty.HttpExample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class BasicNIOServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public BasicNIOServer(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port: " + port);
    }

    public void start() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    register();
                } else if (key.isReadable()) {
                    respond(key);
                }
                iter.remove();
            }
        }
    }

    private void register() throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("Accepted new connection from client: " + client);
    }

    private void respond(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int bytesRead = client.read(buffer);
        if (bytesRead == -1) {
            client.close();
            System.out.println("Disconnected from client: " + client);
            return;
        }
        buffer.flip();
        String receivedData = new String(buffer.array(), 0, buffer.limit());
        System.out.println("Received data: " + receivedData);
        buffer.clear();

        // 组装HTTP响应
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + "Response from server".length() + "\r\n" +
                "\r\n" +
                "Response from server";
        buffer.put(httpResponse.getBytes());
        buffer.flip();
        client.write(buffer);
    }

    public static void main(String[] args) {
        try {
            BasicNIOServer server = new BasicNIOServer(8080);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

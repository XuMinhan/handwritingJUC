package wendyNetty.processors.httpProcessor.utils.HttpExample;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class HttpGetRequestExample {
    public static void main(String[] args) throws Exception {
        String hostname = "localhost";
        int port = 8080;
        String path = "/";
        String request = "GET " + path + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n" +
                "Connection: close\r\n\r\n";

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(hostname, port));

        ByteBuffer writeBuffer = ByteBuffer.wrap(request.getBytes(StandardCharsets.UTF_8));
        while (writeBuffer.hasRemaining()) {
            socketChannel.write(writeBuffer);
        }

        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(readBuffer) > 0) {
            readBuffer.flip();
            while (readBuffer.hasRemaining()) {
                System.out.print((char) readBuffer.get());
            }
            readBuffer.clear();
        }

        socketChannel.close();
    }
}

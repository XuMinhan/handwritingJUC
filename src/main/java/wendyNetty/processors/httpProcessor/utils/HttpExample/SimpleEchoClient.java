package wendyNetty.processors.httpProcessor.utils.HttpExample;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SimpleEchoClient {
    public static void main(String[] args) {
        try {
            // 打开SocketChannel
            SocketChannel socketChannel = SocketChannel.open();

            // 连接到服务器
            socketChannel.connect(new InetSocketAddress("localhost", 8080));

            // 发送消息到服务器
            String message = "Hello, this is the client!";
            ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes());
            while (writeBuffer.hasRemaining()) {
                socketChannel.write(writeBuffer);
            }

            // 读取服务器回应的消息
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(readBuffer);
            String response = new String(readBuffer.array()).trim();
            System.out.println("Response from server: " + response);

            // 关闭连接
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package wendyNetty;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SimpleClient {
    public static void main(String[] args) {
        try {
            // 连接到服务器
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8080));

            // 发送一个包含命令ID的消息
            // 命令ID为1，不带数据
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putInt(1); // 命令ID为1
            buffer.flip();
            socketChannel.write(buffer);

            // 接收服务器响应
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            while (socketChannel.read(readBuffer) > 0) {
                readBuffer.flip();
                while (readBuffer.hasRemaining()) {
                    System.out.println(2);
                    System.out.print((char) readBuffer.get());
                }
                readBuffer.clear();
                System.out.println(111);
            }
            System.out.println(111);

            // 关闭连接
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

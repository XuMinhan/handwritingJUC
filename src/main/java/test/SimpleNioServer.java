package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SimpleNioServer {
    public static void main(String[] args) {
        try {
            // 打开服务器套接字通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            // 配置为非阻塞
            serverSocketChannel.configureBlocking(false);

            // 绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(8080));

            // 打开选择器
            Selector selector = Selector.open();

            // 将服务器套接字通道注册到选择器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started on port 8080...");

            while (true) {
                // 检查是否有事件
                selector.select();

                // 获取事件
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    // 处理事件
                    if (key.isAcceptable()) {
                        register(selector, serverSocketChannel);
                    }

                    if (key.isReadable()) {
                        answerWithEcho(key);
                    }

                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("Client connected: " + client);
    }

    private static void answerWithEcho(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            clientChannel.close();
            System.out.println("Connection closed by client.");
            return;
        }
        System.out.println(" 输出1次");

        buffer.flip();
        clientChannel.write(buffer);
        buffer.clear();
    }
}

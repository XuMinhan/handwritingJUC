package IO.wendyNetty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WendyEventLoop {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ExecutorService pool;

    private Controller controller;
    private int threadPoolSize;

    public WendyEventLoop(int port, int threadPoolSize) throws IOException {
        // 初始化选择器、服务器套接字通道和线程池
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        pool = Executors.newFixedThreadPool(threadPoolSize);
        controller = new Controller();
    }

    public WendyEventLoop(int port) throws IOException {
        // 初始化选择器、服务器套接字通道和线程池
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        pool = Executors.newFixedThreadPool(10);
        controller = new Controller();
    }

    Map<SelectionKey, Boolean> keyProcessingStatus = new ConcurrentHashMap<>();

    public void start() {
        try {
            System.out.println("Server started on port " + serverSocketChannel.socket().getLocalPort());
            // 假设有一个全局的状态跟踪Map

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();


                    Boolean isProcessing = keyProcessingStatus.getOrDefault(key, Boolean.FALSE);

                    if (!isProcessing) {
                        if (key.isAcceptable()) {
                            register(selector, serverSocketChannel);
                        } else if (key.isReadable()) {
                            // 标记为正在处理
                            keyProcessingStatus.put(key, Boolean.TRUE);
                            // 将读取操作提交到线程池
                            pool.submit(() -> {
                                try {
                                    answerWithEcho(key);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    // 处理完成，更新状态
                                    keyProcessingStatus.remove(key);
                                }
                            });
                        }
                    }
                    iter.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (selector != null) {
                    selector.close();
                }
                if (serverSocketChannel != null) {
                    serverSocketChannel.close();
                }
                if (pool != null) {
                    pool.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        if (client != null) {
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            System.out.println("Client connected: " + client);
        }
    }

    private void answerWithEcho(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            System.out.println("Connection closed by client: " + clientChannel);
            clientChannel.close();
        } else {
            buffer.flip(); // 准备读取buffer中的数据
            // 假设命令ID总是消息的前4个字节
            controller.process(clientChannel, buffer);
            buffer.clear();
        }
    }


}

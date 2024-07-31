package handWritingNetty;

import handWritingNetty.Inter.HandlerChain;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoopWorker implements Runnable {


    private final HandlerChain chain;
    private final Selector selector;

    private final Boolean isRunning;


    public LoopWorker(HandlerChain chain) throws IOException {
        this.chain = chain;
        this.selector = Selector.open();
        isRunning = false;
    }


    public void transferChannel(ServerSocketChannel socketChannel) throws Exception {
        SocketChannel clientChannel = socketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        if (!isRunning) {
            run();
        }
    }
    private static final int INITIAL_BUFFER_SIZE = 1024; // 初始缓冲区大小
    private static final int MAX_BUFFER_SIZE = 65536; // 最大缓冲区大小
    public void proceedData(SocketChannel client) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
        int read = client.read(buffer);


        if (!buffer.hasRemaining()) {
            if (buffer.capacity() < MAX_BUFFER_SIZE) {
                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                buffer.flip();
                newBuffer.put(buffer);
                buffer = newBuffer;
            } else {
                System.out.println("Buffer size exceeded maximum limit");
                client.close();
                return;
            }
        }

        if (read == -1) {
            client.close();
            System.out.println("Disconnected from client: " + client);
            return;
        }

        buffer.flip();
        if (chain != null) {
            HashMap<String, Object> context = new LinkedHashMap<>();
            chain.proceed(client, buffer, context);
        }

    }

    @Override
    public void run() {
        try {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isReadable()) {
                    proceedData((SocketChannel) key.channel());
                }
                iter.remove();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


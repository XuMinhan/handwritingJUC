package wendySpring.springConsist.wendyNetty.processors.forwardingHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ForwardingProcessor {

    public ForwardingProcessor() {
    }

    public void process(SocketChannel clientChannel, ByteBuffer requestData, String serverAddress, int serverPort) {
        try (Selector selector = Selector.open();
             SocketChannel serverChannel = SocketChannel.open()) {

            serverChannel.configureBlocking(false);
            serverChannel.connect(new InetSocketAddress(serverAddress, serverPort));
            serverChannel.register(selector, SelectionKey.OP_CONNECT);

            while (!Thread.currentThread().isInterrupted()) {
                selector.select(1000); // Wait for an event
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) continue;

                    if (key.isConnectable()) {
                        if (serverChannel.finishConnect()) {
                            // Connection is established, switch to writing mode
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                    } else if (key.isWritable()) {
                        serverChannel.write(requestData);
                        if (!requestData.hasRemaining()) {
                            // Switch to reading mode once the request is fully sent
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {
                        ByteBuffer responseBuffer = ByteBuffer.allocate(4096);
                        while (serverChannel.read(responseBuffer) > 0) {
                            responseBuffer.flip();
                            clientChannel.write(responseBuffer); // Write the response back to the client
                            responseBuffer.compact();
                        }
                        // Once done, we can close the connection. However, in a real-world scenario, you might want to keep it open for further communication.
                        serverChannel.close();
                        break; // Exit the loop after processing the response
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package wendySpring.springConsist.wendyNetty.processors.gatewayHandler;

import wendySpring.springConsist.wendyNetty.AddressAndPort;
import wendySpring.springConsist.wendyNetty.OnlyString;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.HttpPostRequest;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.MethodAndHandler;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.utils.httpRequestParser.HttpRequestParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

public class GateWayProcessor {
    public String getFirstPathComponent(String path) {
        if (path == null || path.isEmpty()) {
            return null; // 或者抛出一个异常，根据你的需要
        }

        // 分割路径字符串
        String[] parts = path.split("/");

        // 遍历分割后的字符串数组，找到第一个非空的部分
        for (String part : parts) {
            if (!part.isEmpty()) {
                return part;
            }
        }

        return null; // 如果路径仅由分隔符组成，返回 null 或抛出异常
    }
    public void process(AddressAndPort nacosAddressAndPort,SocketChannel clientChannel, ByteBuffer requestData) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(requestData.array());
        HttpRequestParser.HttpRequest parse = HttpRequestParser.parse(inputStream);
        String requestPath = parse.getPath();
        String requestMethod = parse.getMethod().toUpperCase();
        Map<String, String> requestParams = parse.getParameters();


        String firstPathComponent = getFirstPathComponent(requestPath);
//        OnlyString onlyString = new OnlyString(firstPathComponent);
//        AddressAndPort transFormAddressAndPort = (AddressAndPort) HttpPostRequest.sendPostRequest(nacosAddressAndPort, "/getRandomAddressAndPortByPost", onlyString,AddressAndPort.class);
        AddressAndPort transFormAddressAndPort = (AddressAndPort) HttpPostRequest.sendGetRequest(nacosAddressAndPort, "/getRandomAddressAndPortByGet",AddressAndPort.class,"serverId",firstPathComponent );
        System.out.println(transFormAddressAndPort);

        try (Selector selector = Selector.open();
             SocketChannel serverChannel = SocketChannel.open()) {

            serverChannel.configureBlocking(false);
            serverChannel.connect(new InetSocketAddress(transFormAddressAndPort.getServerAddress(), transFormAddressAndPort.getPort()));
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


        // 假设handler是已经定义好的处理实例


    }
}

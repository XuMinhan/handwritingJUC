package wendySpring;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8081; // HTTP默认端口
        String path = "/test1"; // 请求路径
        String params = "param1=value1&param2=value2"; // 查询参数
        String request = "GET " + path + "?" + params + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n" +
                "Connection: Close\r\n\r\n";

        try (SocketChannel serverChannel = SocketChannel.open()) {
            serverChannel.configureBlocking(false); // 配置为非阻塞模式
            serverChannel.connect(new InetSocketAddress(hostname, port));

            // 等待连接完成
            while (!serverChannel.finishConnect()) {
                // 这里可以添加连接等待时的操作，比如检查超时
            }

            // 将HTTP请求写入ByteBuffer
            ByteBuffer buffer = ByteBuffer.wrap(request.getBytes(StandardCharsets.UTF_8));

            // 发送请求
            while (buffer.hasRemaining()) {
                serverChannel.write(buffer);
            }

            // 读取响应
            ByteBuffer responseBuffer = ByteBuffer.allocate(4096);
            while (serverChannel.read(responseBuffer) > 0) {
                responseBuffer.flip(); // 切换到读模式
                System.out.print(StandardCharsets.UTF_8.decode(responseBuffer));
                responseBuffer.clear(); // 清空缓冲区，准备再次读取
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

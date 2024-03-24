package wendyNetty;

import java.io.IOException;

public class NettyStarter {
    public static void main(String[] args) throws IOException {
        int port = 8080; // 示例端口
        new NioEventLoopWithThreadPool(port).start();
    }
}

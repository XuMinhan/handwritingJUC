package spring.sprintConsist.wendyNetty;

import java.io.IOException;

public class WendyEventLoopStarter {
    public static void main(String[] args) throws IOException {
        int port = 8080; // 示例端口
        new WendyEventLoop(port).start();
    }
}

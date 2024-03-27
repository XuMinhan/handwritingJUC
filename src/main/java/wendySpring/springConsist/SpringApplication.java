package wendySpring.springConsist;

import wendySpring.springConsist.wendyNetty.WendyEventLoop;

public class SpringApplication {
    public static void run(int port) {
        new WendyEventLoop(port).start();
    }
    public static void run() {
        new WendyEventLoop(8080).start();
    }
}

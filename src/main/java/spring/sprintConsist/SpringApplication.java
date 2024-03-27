package spring.sprintConsist;

import spring.sprintConsist.wendyNetty.WendyEventLoop;

import java.io.IOException;

public class SpringApplication {
    public static void run(int port) {
        new WendyEventLoop(port).start();
    }
    public static void run() {
        new WendyEventLoop(8080).start();
    }
}

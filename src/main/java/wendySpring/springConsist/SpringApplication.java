package wendySpring.springConsist;

import wendySpring.HttpControllerRegister;
import wendySpring.springConsist.wendyNetty.WendyEventLoop;

public class SpringApplication {
    public static void run(int port,Class<?> controllerRegister) {
        new WendyEventLoop(port,controllerRegister).start();
    }
    public static void run(Class<?> controllerRegister) {
        new WendyEventLoop(8080,controllerRegister).start();
    }
}

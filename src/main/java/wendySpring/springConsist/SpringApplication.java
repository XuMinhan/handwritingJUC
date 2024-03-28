package wendySpring.springConsist;

import wendySpring.HttpControllerRegister;
import wendySpring.springConsist.wendyNetty.AddressAndPort;
import wendySpring.springConsist.wendyNetty.WendyEventLoop;

public class SpringApplication {
    public static void run(int port,Class<?> controllerRegister) {
        new WendyEventLoop(port,controllerRegister).start();
    }
    public static void run(int port,Class<?> controllerRegister, AddressAndPort addressAndPort) {
        new WendyEventLoop(port,controllerRegister,addressAndPort).start();
    }
    public static void run(Class<?> controllerRegister) {
        new WendyEventLoop(8080,controllerRegister).start();
    }
    public static void run(Class<?> controllerRegister, AddressAndPort addressAndPort) {
        new WendyEventLoop(8080,controllerRegister,addressAndPort).start();
    }

}

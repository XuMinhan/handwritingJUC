package wendySpring.springConsist;

import wendySpring.springConsist.wendyNetty.AddressAndPort;
import wendySpring.springConsist.wendyNetty.WendyEventLoop;

public class SpringApplication {
    public static void run(int port,Class<?> controllerRegister) throws Exception {
        new WendyEventLoop(port,controllerRegister).start();
    }
    public static void run(int port,AddressAndPort addressAndPort) {
        new WendyEventLoop(port,addressAndPort).start();
    }

    //注册wencos的地址
    public static void run(String serviceId,int port,Class<?> controllerRegister, AddressAndPort addressAndPort) throws Exception {
        new WendyEventLoop(serviceId,port,controllerRegister,addressAndPort).start();
    }




}

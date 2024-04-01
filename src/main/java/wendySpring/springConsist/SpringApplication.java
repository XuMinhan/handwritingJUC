package wendySpring.springConsist;

import wendySpring.springConsist.wendyNetty.AddressAndPort;
import wendySpring.springConsist.wendyNetty.WendyEventLoop;

import static wendySpring.springConsist.wendyNetty.WendyEventLoop.NACOS;
import static wendySpring.springConsist.wendyNetty.WendyEventLoop.GATEWAY;
import static wendySpring.springConsist.wendyNetty.WendyEventLoop.ONESPRINGCLOUD;

public class SpringApplication {


    //NACOS/普通
    public static void run(int port,Class<?> application) throws Exception {
        new WendyEventLoop(null,port,application,null,NACOS).start();
    }
    //网关GATEWAY
    public static void run(int port,AddressAndPort addressAndPort) throws Exception {
        new WendyEventLoop(null,port,null,addressAndPort,GATEWAY).start();
    }

    //注册wencos的地址
    //OneSpringCloud
    public static void run(String serviceId,int port,Class<?> application, AddressAndPort addressAndPort) throws Exception {
        new WendyEventLoop(serviceId,port,application,addressAndPort,ONESPRINGCLOUD).start();
    }
}

package springCloudDEMO.cloudWendyOneService;

import wendySpring.springConsist.SpringApplication;
import wendySpring.springConsist.wendyNetty.AddressAndPort;

public class WendyServiceApplication {


    //服务名+端口+nacos端口 = 分布式启动器
    public static void main(String[] args) throws Exception {
        AddressAndPort addressAndPort = new AddressAndPort("localhost",8858);
        SpringApplication.run("firstService",8081, HttpControllerRegister.class,addressAndPort);
    }

}
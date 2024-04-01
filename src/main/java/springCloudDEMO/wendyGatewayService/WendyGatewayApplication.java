package springCloudDEMO.wendyGatewayService;

import wendySpring.springConsist.SpringApplication;
import wendySpring.springConsist.wendyNetty.AddressAndPort;

public class WendyGatewayApplication {



    //端口+nacos地址 = 网关启动器
    public static void main(String[] args) throws Exception {
        AddressAndPort remoteAddressAndPort = new AddressAndPort("localhost", 8858);
        SpringApplication.run(8080, remoteAddressAndPort);
    }

}
package wendySpring.soManyApplications.cloudWendyOneService;

import wendySpring.springConsist.SpringApplication;
import wendySpring.springConsist.wendyNetty.AddressAndPort;

public class WendyServiceApplication {
//    public static void main(String[] args) {SpringApplication.run(HttpControllerRegister.class);}
//    public static void main(String[] args) {SpringApplication.run(8080,HttpControllerRegister.class);}

//    public static void main(String[] args) {
//        AddressAndPort remoteAddressAndPort = new AddressAndPort("localhost", 8081);
//        SpringApplication.run(HttpControllerRegister.class, remoteAddressAndPort);
//    }


    public static void main(String[] args) throws Exception {
        AddressAndPort addressAndPort = new AddressAndPort("localhost",8858);
        SpringApplication.run("firstService",8081, HttpControllerRegister.class,addressAndPort);
    }

}
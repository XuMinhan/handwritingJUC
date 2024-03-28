package wendySpring.soManyApplications.wendyForward;

import wendySpring.soManyApplications.wendyOneService.HttpControllerRegister;
import wendySpring.springConsist.SpringApplication;
import wendySpring.springConsist.wendyNetty.AddressAndPort;

public class WendyForwardApplication {
//    public static void main(String[] args) {SpringApplication.run(HttpControllerRegister.class);}
//    public static void main(String[] args) {SpringApplication.run(8080,HttpControllerRegister.class);}

//    public static void main(String[] args) {
//        AddressAndPort remoteAddressAndPort = new AddressAndPort("localhost", 8081);
//        SpringApplication.run(HttpControllerRegister.class, remoteAddressAndPort);
//    }
    public static void main(String[] args) {
        AddressAndPort remoteAddressAndPort = new AddressAndPort("localhost", 8081);
        SpringApplication.run(8080, remoteAddressAndPort);
    }

}
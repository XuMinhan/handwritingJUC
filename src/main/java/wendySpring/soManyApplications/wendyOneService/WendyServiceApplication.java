package wendySpring.soManyApplications.wendyOneService;

import wendySpring.springConsist.SpringApplication;

public class WendyServiceApplication {
//    public static void main(String[] args) {SpringApplication.run(HttpControllerRegister.class);}
//    public static void main(String[] args) {SpringApplication.run(8080,HttpControllerRegister.class);}

//    public static void main(String[] args) {
//        AddressAndPort remoteAddressAndPort = new AddressAndPort("localhost", 8081);
//        SpringApplication.run(HttpControllerRegister.class, remoteAddressAndPort);
//    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(8081, HttpControllerRegister.class);
    }

}
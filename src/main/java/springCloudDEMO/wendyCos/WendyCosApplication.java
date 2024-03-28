package springCloudDEMO.wendyCos;

import wendySpring.soManyApplications.wendyCos.HttpControllerRegisterForNacos;
import wendySpring.springConsist.SpringApplication;

public class WendyCosApplication {
    //端口+nacos注册类 = nacos启动器 / 普通单点应用启动器
    public static void main(String[] args) throws Exception {
        SpringApplication.run(8858, HttpControllerRegisterForNacos.class);
    }
}

package wendySpring.springConsist.wendyNetty.processors.httpProcessor.utils;

import java.net.InetAddress;

public class GetLocalIP {
    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("内网IP地址: " + ip.getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

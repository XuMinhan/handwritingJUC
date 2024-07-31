package handWritingNetty.Test;

import java.io.OutputStream;
import java.net.Socket;

public class SimpleClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8080;

        try (Socket socket = new Socket(hostname, port)) {
            OutputStream out = socket.getOutputStream();
            out.write('a');  // 发送字节 'a'
            out.flush();  // 确保数据被发送到服务器
            System.out.println("Sent a byte to " + hostname + " on port " + port);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

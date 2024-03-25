package wendyNetty.processors.httpProcessor.utils.HttpExample.getRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RawSocketGetExample {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8080; // HTTP默认端口
        String path = "/test1"; // 请求路径
        String params = "param1=value1&param2=value2"; // 查询参数

        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 构造GET请求
            out.println("GET " + path + "?" + params + " HTTP/1.1");
            out.println("Host: " + hostname);
            out.println("Connection: Close");
            out.println(""); // HTTP请求头和请求体之间必须有一个空行

            // 读取响应
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package Wenty;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleClient {
    private String serverHost;
    private int serverPort;
    private String localServiceHost;
    private int localServicePort;

    public SimpleClient(String serverHost, int serverPort, String localServiceHost, int localServicePort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.localServiceHost = localServiceHost;
        this.localServicePort = localServicePort;
    }

    public void start() {
        try (Socket serverSocket = new Socket(serverHost, serverPort);
             Socket localServiceSocket = new Socket(localServiceHost, localServicePort)) {
            System.out.println("Connected to server and local service. Forwarding data...");
            // 同时转发数据：服务端 -> 本地服务，本地服务 -> 服务端
            Thread serverToLocal = new Thread(() -> forwardData(serverSocket, localServiceSocket));
            Thread localToServer = new Thread(() -> forwardData(localServiceSocket, serverSocket));
            serverToLocal.start();
            localToServer.start();

            serverToLocal.join();
            localToServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void forwardData(Socket inputSocket, Socket outputSocket) {
        try (InputStream inputStream = inputSocket.getInputStream();
             OutputStream outputStream = outputSocket.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 配置服务端地址和端口，以及本地服务地址和端口
        String serverHost = "localhost";
        int serverPort = 8080;
        String localServiceHost = "localhost";
        int localServicePort = 80; // 假定本地服务监听在80端口
        SimpleClient client = new SimpleClient(serverHost, serverPort, localServiceHost, localServicePort);
        client.start();
    }
}

package Wenty;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EnhancedPortForwarder {
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private final Map<Socket, Socket> connectionMap = new ConcurrentHashMap<>();
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final int MAX_CONNECTIONS = 100; // 假设最大并发连接数为100

    public EnhancedPortForwarder(int localPort, String remoteHost, int remotePort) {
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(localPort)) {
            System.out.println("Port forwarder listening on port " + localPort);
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                // 背压控制：如果活跃连接数超过限制，则拒绝新连接
                if (activeConnections.incrementAndGet() > MAX_CONNECTIONS) {
                    System.out.println("Max connections reached. Dropping connection.");
                    activeConnections.decrementAndGet();
                    clientSocket.close();
                    continue;
                }

                Socket remoteSocket = new Socket(remoteHost, remotePort);
                connectionMap.put(clientSocket, remoteSocket); // 添加到映射中

                // 启动数据转发线程
                startForwardingThread(clientSocket, remoteSocket);
                startForwardingThread(remoteSocket, clientSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startForwardingThread(Socket inputSocket, Socket outputSocket) {
        Thread thread = new Thread(() -> {
            forwardData(inputSocket, outputSocket);
            connectionMap.remove(inputSocket);
            connectionMap.remove(outputSocket);
            activeConnections.decrementAndGet();
            try {
                inputSocket.close();
                outputSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
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
        int localPort = 8080;
        String remoteHost = "localhost";
        int remotePort = 80;
        EnhancedPortForwarder forwarder = new EnhancedPortForwarder(localPort, remoteHost, remotePort);
        forwarder.start();
    }
}

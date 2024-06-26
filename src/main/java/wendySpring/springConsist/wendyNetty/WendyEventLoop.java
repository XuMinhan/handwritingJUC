package wendySpring.springConsist.wendyNetty;

import wendySpring.springConsist.springBean.Result;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.HttpPostRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.net.InetAddress;

public class WendyEventLoop {

    public static int SPRING = 0;
    public static int NACOS = 1;
    public static int GATEWAY = 2;
    public static int ONESPRINGCLOUD = 3;
    public int BUMP_EVERY_SECONDS = 30;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ExecutorService pool;

    private EventLoopRegister eventLoopRegister;

    private AddressAndPort addressAndPort;

    private Class<?> applicationClass;

    private int port;
    //0纯spring，1 nacos，2 网关，3 单个springCloud
    private int function;



    // 私有初始化方法，被所有构造函数调用
    public  WendyEventLoop(String serviceId, int port, Class<?> applicationClass, AddressAndPort addressAndPort, int function) throws Exception {




        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            this.port = port;
            this.function = function;
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            pool = Executors.newFixedThreadPool(10);
            this.applicationClass = applicationClass;

            if (function == NACOS || function == ONESPRINGCLOUD) {
                eventLoopRegister = new EventLoopRegister(applicationClass);
            } else if (function == GATEWAY) {
                eventLoopRegister = new EventLoopRegister();
            }
            this.addressAndPort = addressAndPort;

            if (function == ONESPRINGCLOUD) {
                InetAddress ip = InetAddress.getLocalHost();
                ServiceIdAndAddressPort serviceIdAndAddressPort = new ServiceIdAndAddressPort(serviceId, ip.getHostAddress(), this.port);
                System.out.println(serviceIdAndAddressPort);
                System.out.println(addressAndPort);
                Result ret = (Result) HttpPostRequest.sendPostRequest(addressAndPort, "/postUpload", serviceIdAndAddressPort, Result.class);
                if (ret==null) {
                    System.out.println("注册失败");
                }
                if (ret.getResult() == 1) {
                    System.out.println("注册成功");
                    //开始心跳
                    startBumping(addressAndPort, serviceIdAndAddressPort);
                } else {
                    System.out.println("注册失败");
                }
            }
        } catch (IOException e) {
            System.out.println("IO错误: " + e.getMessage());
        }
    }

    Map<SelectionKey, Boolean> keyProcessingStatus = new ConcurrentHashMap<>();

    public void start() {
        try {
            System.out.println("Server started on port " + serverSocketChannel.socket().getLocalPort());
            // 假设有一个全局的状态跟踪Map

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    Boolean isProcessing = keyProcessingStatus.getOrDefault(key, Boolean.FALSE);

                    if (!isProcessing) {
                        if (key.isAcceptable()) {
                            register(selector, serverSocketChannel);
                        } else if (key.isReadable()) {
                            // 标记为正在处理
                            keyProcessingStatus.put(key, Boolean.TRUE);
                            // 将读取操作提交到线程池
                            pool.submit(() -> {
                                try {
                                    answerWithEcho(key);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                } finally {
                                    // 处理完成，更新状态
                                    keyProcessingStatus.remove(key);
                                }
                            });
                        }
                    }
                    iter.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (selector != null) {
                    selector.close();
                }
                if (serverSocketChannel != null) {
                    serverSocketChannel.close();
                }
                if (pool != null) {
                    pool.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        if (client != null) {
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        }
    }

    private void answerWithEcho(SelectionKey key) throws Exception {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 用于累积数据

        while (true) {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) { // 检查客户端是否关闭了连接
                System.out.println("Connection closed by client: " + clientChannel);
                clientChannel.close();
                break;
            } else if (bytesRead == 0) { // 没有更多数据可读
                break;
            }
            buffer.flip(); // 准备从buffer读取数据
            while (buffer.hasRemaining()) {
                baos.write(buffer.get()); // 将数据写入ByteArrayOutputStream
            }
            buffer.clear(); // 清空buffer，准备下一次读取
        }

        // 将累积的数据转换为ByteBuffer，交给controller处理

        //写全代表注册在nacos上,并且启动http服务器,需要增加心跳和注册功能


        ByteBuffer dataBuffer = ByteBuffer.wrap(baos.toByteArray());

        if (this.function==SPRING) {

        }else if (this.function==NACOS){
            eventLoopRegister.httpProcess(clientChannel, dataBuffer);
        }else if (this.function==GATEWAY){
            eventLoopRegister.gateWayProcess(addressAndPort, clientChannel, dataBuffer);
        }else if (this.function==ONESPRINGCLOUD){
            eventLoopRegister.httpProcess(clientChannel, dataBuffer);
        }

//        if (addressAndPort == null && applicationClass == null) {
//        } else if (addressAndPort == null) {//nacos
//            eventLoopRegister.httpProcess(clientChannel, dataBuffer);
//        } else if (applicationClass == null) {//gateway
////            eventLoopRegister.forwardingProcess(clientChannel, dataBuffer, addressAndPort.getServerAddress(), addressAndPort.getPort());
//            eventLoopRegister.gateWayProcess(addressAndPort, clientChannel, dataBuffer);
//        } else {
//            eventLoopRegister.httpProcess(clientChannel, dataBuffer);
//        }
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public void startBumping(AddressAndPort addressAndPort, ServiceIdAndAddressPort serviceIdAndAddressPort) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("跳动一次");
                heartBump(addressAndPort, serviceIdAndAddressPort);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }, 0, BUMP_EVERY_SECONDS, TimeUnit.SECONDS); // 例如，每分钟检查一次
    }

    private void heartBump(AddressAndPort addressAndPort, ServiceIdAndAddressPort serviceIdAndAddressPort) throws Exception {
        HttpPostRequest.sendPostRequest(addressAndPort, "/heartBeat", serviceIdAndAddressPort, null);
    }
}

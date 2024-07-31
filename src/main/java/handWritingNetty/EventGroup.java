package handWritingNetty;


import handWritingNetty.Inter.HandlerChain;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EventGroup {

    private ChannelInitializer initializer;

    private HandlerChain chain;

    private final int port;

    private final int loopWorkerNum;

    private int tmpPos;

    List<LoopWorker> loopWorkers = new ArrayList<>();


    private void createAndStartLoopWorkers() throws Exception {
        if (initializer != null) {
            ChannelPipeline channelPipeline = new ChannelPipeline();
            initializer.initChannel(channelPipeline);
            chain = channelPipeline.createChain();
        }
        for (int i = 0; i < loopWorkerNum; i++) {
            LoopWorker loopWorker = new LoopWorker(chain);
            loopWorkers.add(loopWorker);
        }
    }


    public void start() throws Exception {

        createAndStartLoopWorkers();


        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port: " + port);
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    LoopWorker nextLoopWorker = getNextLoopWorker();
                    nextLoopWorker.transferChannel(serverSocket);
                }
                iter.remove();
            }
        }
    }

    private LoopWorker getNextLoopWorker() {
        int i = (tmpPos + 1) % loopWorkerNum;
        tmpPos = i;
        return loopWorkers.get(i);
    }

    public EventGroup childHandler(ChannelInitializer initializer) {
        this.initializer = initializer;
        return this;
    }

    public EventGroup(int port) {
        this.port = port;
        loopWorkerNum = 10;
    }


}

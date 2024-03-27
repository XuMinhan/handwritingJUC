package spring.sprintConsist.wendyNetty;

import spring.sprintConsist.wendyNetty.processors.httpProcessor.HttpProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EventLoopRegister {
    private HttpProcessor httpProcessor;

    public EventLoopRegister() {
        httpProcessor = new HttpProcessor();
    }

    public void process(SocketChannel clientChannel, ByteBuffer requestData) throws IOException {
        httpProcessor.process(clientChannel, requestData);
    }

}

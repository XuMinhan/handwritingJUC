package wendySpring.springConsist.wendyNetty;

import wendySpring.springConsist.wendyNetty.processors.httpProcessor.HttpProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EventLoopRegister {
    private HttpProcessor httpProcessor;

    public EventLoopRegister(Class<?> controllerRegister) {
        httpProcessor = new HttpProcessor(controllerRegister);
    }

    public void process(SocketChannel clientChannel, ByteBuffer requestData) throws IOException {
        httpProcessor.process(clientChannel, requestData);
    }



}

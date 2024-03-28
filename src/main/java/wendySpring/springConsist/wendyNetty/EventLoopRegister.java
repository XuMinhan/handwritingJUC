package wendySpring.springConsist.wendyNetty;

import wendySpring.springConsist.wendyNetty.processors.forwardingHandler.ForwardingProcessor;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.HttpProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EventLoopRegister {
    private HttpProcessor httpProcessor;

    private ForwardingProcessor forwardingProcessor;



    public EventLoopRegister(Class<?> controllerRegister) {
        httpProcessor = new HttpProcessor(controllerRegister);//纯本地
    }
    public EventLoopRegister() {
        forwardingProcessor = new ForwardingProcessor();//纯转发
    }

    public void httpProcess(SocketChannel clientChannel, ByteBuffer requestData) throws IOException {
        httpProcessor.process(clientChannel, requestData);
    }

    public void forwardingProcess(SocketChannel clientChannel, ByteBuffer requestData,String serverAddress, int port){

        forwardingProcessor.process(clientChannel,requestData,serverAddress,port);
    }


}

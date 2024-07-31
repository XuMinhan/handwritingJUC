package handWritingNetty.handler;

import handWritingNetty.Inter.ReceiveHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class HttpRequestLoggerHandler implements ReceiveHandler {
    @Override
    public void handle(SocketChannel channel, ByteBuffer data, HashMap<String, Object> context) {
        System.out.println(context);
    }
}

package handWritingNetty.handler;

import handWritingNetty.Inter.ReceiveHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class LoggingReceiveHandler implements ReceiveHandler {
    @Override
    public void handle(SocketChannel channel, ByteBuffer data, HashMap<String,Object> context) {
        System.out.println("Data received: " + new String(data.array()).trim());
    }
}
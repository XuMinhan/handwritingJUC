package handWritingNetty.handler;


import handWritingNetty.Inter.SendHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class EchoSendHandler implements SendHandler {
    @Override
    public void handle(SocketChannel channel, ByteBuffer data, HashMap<String,Object> hashMap) {
        try {
            channel.write(data);
            System.out.println("Data sent: " + new String(data.array()).trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

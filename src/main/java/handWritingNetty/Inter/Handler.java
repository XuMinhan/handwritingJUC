package handWritingNetty.Inter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public interface Handler {
    void handle(SocketChannel channel, ByteBuffer data, HashMap<String,Object> context);
}

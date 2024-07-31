package handWritingNetty.Inter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public interface HandlerChain {
    void proceed(SocketChannel channel, ByteBuffer data, HashMap<String,Object> context);
}

package handWritingNetty;


import handWritingNetty.Inter.Handler;
import handWritingNetty.Inter.HandlerChain;
import handWritingNetty.Inter.ReceiveHandler;
import handWritingNetty.Inter.SendHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;

class DefaultHandlerChain implements HandlerChain {

    private final List<Handler> handlers;

    public DefaultHandlerChain(List<Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void proceed(SocketChannel channel, ByteBuffer data, HashMap<String,Object> context) {
        for (Handler handler : handlers) {
            if (handler instanceof ReceiveHandler) {
                handler.handle(channel, data,context);
            }
        }
        for (Handler handler : handlers) {
            if (handler instanceof SendHandler) {
                handler.handle(channel, data,context);
            }
        }
    }
}
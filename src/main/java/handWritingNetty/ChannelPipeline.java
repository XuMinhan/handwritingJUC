package handWritingNetty;


import handWritingNetty.Inter.Handler;
import handWritingNetty.Inter.HandlerChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ChannelPipeline 用于管理一组处理器
public class ChannelPipeline {
    private List<Handler> handlers = new ArrayList<>();

    public void addLast(Handler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
    }
    public HandlerChain createChain() {
        return new DefaultHandlerChain(handlers);
    }
}
package webFrame;

import handWritingNetty.ChannelInitializer;
import handWritingNetty.ChannelPipeline;
import handWritingNetty.EventGroup;
import webFrame.handlerForWeb.HttpRequestHandler;
import webFrame.handlerForWeb.HttpResponseHandler;

public class WebServer {
    EventGroup eventGroup;
    public WebServer(int port,Class<?> controllerRegister )  {
        this.eventGroup = new EventGroup(port)
                .childHandler(new ChannelInitializer() { // 设置一个新的 Channel
                    @Override
                    public void initChannel(ChannelPipeline channelPipeline) throws Exception {
                        channelPipeline.addLast(
                                new HttpRequestHandler(controllerRegister),
                                new HttpResponseHandler()
                        );
                    }
                });
    }
    public void start() throws Exception {
        eventGroup.start();
    }
}

package handWritingNetty.Test;


import handWritingNetty.ChannelInitializer;
import handWritingNetty.ChannelPipeline;
import handWritingNetty.EventGroup;
import webFrame.handlerForWeb.HttpResponseHandler;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        EventGroup eventGroup = new EventGroup(8080)
                .childHandler(new ChannelInitializer() { // 设置一个新的 Channel
                    @Override
                    public void initChannel(ChannelPipeline channelPipeline) throws Exception {
                        channelPipeline.addLast(
//                                new LoggingReceiveHandler(),
//                                new HttpRequestHandler(),
//                                new HttpRequestLoggerHandler(),
                                new HttpResponseHandler()
                        );
                    }
                });
        eventGroup.start();

    }
}

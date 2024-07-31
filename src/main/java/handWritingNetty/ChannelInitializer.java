package handWritingNetty;


// ChannelInitializer 用于初始化通道
public abstract class ChannelInitializer {
    public abstract void initChannel(ChannelPipeline ch) throws Exception;
}
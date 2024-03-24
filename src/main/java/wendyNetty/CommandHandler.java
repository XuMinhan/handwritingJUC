package wendyNetty;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CommandHandler {

    @Command(1)
    public byte[] handleCommand1(SocketChannel clientChannel, ByteBuffer requestData) {
        // 处理命令1的逻辑
        System.out.println("Handling command 1");
        // 实现发送响应到clientChannel等逻辑
        return "Handling command 1".getBytes();
    }

    @Command(2)
    public byte[] handleCommand2(SocketChannel clientChannel, ByteBuffer requestData) {
        // 处理命令1的逻辑
        System.out.println("Handling command 1");
        // 实现发送响应到clientChannel等逻辑
        return "Handling command 2".getBytes();
    }

    // 更多命令处理方法...
}

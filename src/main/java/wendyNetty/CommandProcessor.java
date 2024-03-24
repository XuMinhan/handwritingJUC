package wendyNetty;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

//解决连接的释放报错问题
//写一个json的生成器和解析器
//将所有的工具类进行替换

//event->controller->不同的处理器（http/其他协议）->不同的方法


public class CommandProcessor {
    private final HashMap<Integer, Method> commandMap = new HashMap<>();
    private final Object handler;

    public CommandProcessor() {
        this.handler = new CommandHandler();
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                commandMap.put(command.value(), method);
            }
        }
    }

    public void process(int commandId, SocketChannel clientChannel, ByteBuffer requestData) {
        // 根据commandId找到对应的处理逻辑，并执行
        // 这里仅作为示例，您需要根据实际逻辑填充
        Method method = commandMap.get(commandId);
        if (method != null) {
            try {
                // 假设处理方法返回的是需要发送回客户端的数据
                byte[] responseData = (byte[]) method.invoke(handler, new Object[]{clientChannel, requestData});
                ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
                clientChannel.write(responseBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

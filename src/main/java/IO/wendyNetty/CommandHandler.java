package IO.wendyNetty;

import IO.netUtils.json.test.Person;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CommandHandler {

    @Url(method = "GET", url = "/test1")
    public Person handleCommand1(@Param("name") String name , @Param("age") int age) {
        // 处理命令1的逻辑
        // 实现发送响应到clientChannel等逻辑
        return new Person(name, age);
    }

    @Url(method = "post", url = "/test2")
    public Person handleCommand2(ByteBuffer requestData) {
        // 处理命令1的逻辑
        System.out.println("Handling command 1");
        // 实现发送响应到clientChannel等逻辑
        return new Person("xmh", 18);
    }

    // 更多命令处理方法...
}

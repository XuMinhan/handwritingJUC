package wendyNetty;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Controller {
    private CommandProcessor commandProcessor;

    public Controller() {
        commandProcessor = new CommandProcessor();
    }

    public void process(SocketChannel clientChannel, ByteBuffer requestData) {
        int commandId = 1;
        commandProcessor.process(commandId, clientChannel, requestData);
    }

}

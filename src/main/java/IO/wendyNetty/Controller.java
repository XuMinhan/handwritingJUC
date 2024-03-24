package IO.wendyNetty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Controller {
    private HttpProcessor httpProcessor;

    public Controller() {
        httpProcessor = new HttpProcessor();
    }

    public void process(SocketChannel clientChannel, ByteBuffer requestData) throws IOException {
        httpProcessor.process(clientChannel, requestData);
    }

}

package webFrame.handlerForWeb;

import handWritingNetty.Inter.SendHandler;
import webFrame.utils.httpResponseUtil.HttpResponseUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class HttpResponseHandler implements SendHandler {

    @Override
    public void handle(SocketChannel channel, ByteBuffer data, HashMap<String, Object> context) {
//        context.forEach((key, value) -> System.out.println(key + ": " + value));
        // 构建并发送 HTTP 响应
        sendHttpResponse(channel, context);
    }

    private void sendHttpResponse(SocketChannel channel, HashMap<String, Object> context) {
        Object result = context.get("result");
        if (result instanceof Error) {
            Error error = (Error) result;
            try {
                ByteBuffer errorMessage = ByteBuffer.wrap(error.getErrorMessage().getBytes());
                channel.write(errorMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String commonResult = HttpResponseUtils.buildHttpResponse(result);
        ByteBuffer commonResultByteBuffer = ByteBuffer.wrap(commonResult.getBytes());
        try {
            channel.write(commonResultByteBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

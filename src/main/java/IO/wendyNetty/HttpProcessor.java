package IO.wendyNetty;

import IO.netUtils.httpRequestParser.HttpRequestParser;
import IO.netUtils.httpResponseUtil.HttpResponseUtils;
import IO.netUtils.json.test.Person;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

//解决连接的释放报错问题
//写一个json的生成器和解析器
//将所有的工具类进行替换

//event->controller->不同的处理器（http/其他协议）->不同的方法


public class HttpProcessor {
    private final HashMap<String, Method> commandMap = new HashMap<>();
    private final Object handler;

    public HttpProcessor() {
        this.handler = new CommandHandler();
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Url.class)) {
                Url url = method.getAnnotation(Url.class);
                commandMap.put(url.url(), method);
            }
        }
    }

    public void process(SocketChannel clientChannel, ByteBuffer requestData) throws IOException {
        // 根据commandId找到对应的处理逻辑，并执行
        // 这里仅作为示例，您需要根据实际逻辑填充


        Method method = commandMap.get("/test1");
        InputStream inputStream = new ByteArrayInputStream(requestData.array());
        HttpRequestParser.HttpRequest parse = HttpRequestParser.parse(inputStream);

        System.out.println(parse.getPath());
        System.out.println(parse.getParameters());
        System.out.println(parse.getHeaders());
        System.out.println(parse.getBody());

        if (method != null) {
            try {
                // 假设处理方法返回的是需要发送回客户端的数据
//                byte[] responseData = (byte[]) method.invoke(handler, new Object[]{clientChannel, requestData});

                Person xmh = new Person("xmh", 12);

                String responseData = HttpResponseUtils.buildHttpResponse(xmh);

                ByteBuffer responseBuffer = ByteBuffer.wrap(responseData.getBytes());


                clientChannel.write(responseBuffer);
                clientChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

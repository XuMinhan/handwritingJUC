package wendyNetty.processors.httpProcessor;

import wendyNetty.processors.httpProcessor.utils.httpRequestParser.HttpRequestParser;
import wendyNetty.processors.httpProcessor.utils.httpResponseUtil.HttpResponseUtils;
import wendyNetty.processors.httpProcessor.utils.json.WendyJsonUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.lang.reflect.Parameter;
import java.util.Map;

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


        InputStream inputStream = new ByteArrayInputStream(requestData.array());
        HttpRequestParser.HttpRequest parse = HttpRequestParser.parse(inputStream);
        Method method = commandMap.get(parse.getPath());

//        System.out.println(parse.getPath());
//        System.out.println(parse.getMethod());
//        System.out.println(parse.getParameters());
//        System.out.println(parse.getHeaders());
//        System.out.println(parse.getBody());

        String requestMethod = parse.getMethod();
        String body = parse.getBody();
        Map<String, String> requestParams = parse.getParameters();
        if (method != null) {
            // 检查这个Method是否支持当前的请求方法
            Url urlAnnotation = method.getAnnotation(Url.class);
            if (urlAnnotation != null && urlAnnotation.method().equalsIgnoreCase(requestMethod)) {
                // 如果请求方法匹配，执行方法
                if (requestMethod.equals("GET")) {
                    try {
                        Parameter[] parameters = method.getParameters();
                        Object[] args = new Object[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            RequestParam requestParamAnnotation = parameters[i].getAnnotation(RequestParam.class);
                            if (requestParamAnnotation != null) {
                                // 从请求参数中获取值，并将其转换为正确的类型
                                String paramName = requestParamAnnotation.value();
                                String paramValue = requestParams.get(paramName);
                                Class<?> paramType = parameters[i].getType();
                                Object argValue = convertStringToType(paramValue, paramType);
                                args[i] = argValue;
                            }
                        }

                        Object resp = method.invoke(handler, args);
                        String responseData = HttpResponseUtils.buildHttpResponse(resp);
                        ByteBuffer responseBuffer = ByteBuffer.wrap(responseData.getBytes());
                        clientChannel.write(responseBuffer);
                        clientChannel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }if (requestMethod.equals("POST")) {
                    try {
                        // 直接获取方法的唯一参数，假设这个参数使用了@RequestBody注解
                        Parameter parameter = method.getParameters()[0]; // 直接获取第一个（也是唯一一个）参数
                        // 反序列化JSON字符串为Java对象
                        Class<?> paramType = parameter.getType();
                        Object argValue = WendyJsonUtils.deserialize(body, paramType);

                        // 调用处理方法
                        Object resp = method.invoke(handler, new Object[]{argValue});

                        // 序列化响应对象为JSON字符串
                        String responseData = HttpResponseUtils.buildHttpResponse(resp);
                        ByteBuffer responseBuffer = ByteBuffer.wrap(responseData.getBytes());

                        // 发送响应
                        clientChannel.write(responseBuffer);
                        clientChannel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // 如果找到了URL对应的方法，但是请求方法不匹配，返回405错误
                String methodNotAllowedResponse = "HTTP/1.1 405 Method Not Allowed\r\nContent-Length: 0\r\n\r\n";
                ByteBuffer responseBuffer = ByteBuffer.wrap(methodNotAllowedResponse.getBytes());
                try {
                    clientChannel.write(responseBuffer);
                    clientChannel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // 如果没有找到URL对应的方法，返回404错误
            String notFoundResponse = "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n";
            ByteBuffer responseBuffer = ByteBuffer.wrap(notFoundResponse.getBytes());
            try {
                clientChannel.write(responseBuffer);
                clientChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object convertStringToType(String value, Class<?> type) {
        if (Integer.class == type || int.class == type) {
            return Integer.parseInt(value);
        }
        // 添加更多类型的支持...
        return value; // 默认情况下，返回字符串本身
    }

}

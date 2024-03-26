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
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping url = method.getAnnotation(GetMapping.class);
                commandMap.put(url.value(), method);
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping url = method.getAnnotation(PostMapping.class);
                commandMap.put(url.value(), method);
            }
        }
    }

    public void process(SocketChannel clientChannel, ByteBuffer requestData) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(requestData.array());
        HttpRequestParser.HttpRequest parse = HttpRequestParser.parse(inputStream);
        String requestPath = parse.getPath();
        String requestMethod = parse.getMethod().toUpperCase();
        Map<String, String> requestParams = parse.getParameters();

        // 假设handler是已经定义好的处理实例

        Method method = commandMap.get(requestPath);

        if (method != null) {
            try {
                boolean methodMatch = false;
                Object response = null;
                if ("GET".equals(requestMethod) && method.isAnnotationPresent(GetMapping.class)) {

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
                    // 确保请求方法和路径与GetMapping注解匹配
                    methodMatch = true;
                    response = method.invoke(handler, args);

                } else if ("POST".equals(requestMethod) && method.isAnnotationPresent(PostMapping.class)) {
                    // 确保请求方法和路径与PostMapping注解匹配
                    methodMatch = true;
                    Parameter parameter = method.getParameters()[0]; // 假设POST方法只有一个参数
                    Class<?> paramType = parameter.getType();
                    Object argValue = WendyJsonUtils.deserialize(parse.getBody(), paramType);
                    response = method.invoke(handler, argValue);
                }

                if (!methodMatch) {
                    sendResponse(clientChannel, "HTTP/1.1 405 Method Not Allowed\r\nContent-Length: 0\r\n\r\n");
                    return;
                }

                // 处理成功，发送响应
                String responseData = HttpResponseUtils.buildHttpResponse(response);
                sendResponse(clientChannel, responseData);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(clientChannel, "HTTP/1.1 500 Internal Server Error\r\nContent-Length: 0\r\n\r\n");
            }
        } else {
            // 没有找到匹配的处理方法，发送404响应
            sendResponse(clientChannel, "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n");
        }
    }

    private void sendResponse(SocketChannel clientChannel, String response) throws IOException {
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
        clientChannel.write(responseBuffer);
        clientChannel.close();
    }

    public Object convertStringToType(String value, Class<?> type) {
        if (Integer.class == type || int.class == type) {
            return Integer.parseInt(value);
        }
        // 添加更多类型的支持...
        return value; // 默认情况下，返回字符串本身
    }

}

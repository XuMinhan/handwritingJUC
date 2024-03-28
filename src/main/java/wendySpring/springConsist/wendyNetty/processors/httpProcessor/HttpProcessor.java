package wendySpring.springConsist.wendyNetty.processors.httpProcessor;

import wendySpring.HttpControllerRegister;
import wendySpring.springConsist.springBean.Resource;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.utils.httpRequestParser.HttpRequestParser;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.utils.json.WendyJsonUtils;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.utils.httpResponseUtil.HttpResponseUtils;
import wendySpring.springConsist.springBean.SimpleDIContainer;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
    private final HashMap<String, MethodAndHandler> commandMap = new HashMap<>();

    public HttpProcessor(Class<?> controllerRegister) {

        // 通过反射遍历HttpControllerRegister中所有字段
        for (Field field : controllerRegister.getDeclaredFields()) {
            // 检查字段是否使用了@Resource注解
            if (field.isAnnotationPresent(Resource.class)) {
                try {
                    // 确保可以访问私有字段
                    field.setAccessible(true);
                    // 使用SimpleDIContainer获取字段实例
                    Object handler = SimpleDIContainer.getInstance(field.getType());

                    // 遍历handler中所有方法，检查是否使用了@GetMapping或@PostMapping注解
                    for (Method method : handler.getClass().getDeclaredMethods()) {
                        MethodAndHandler methodAndHandler = new MethodAndHandler(method, handler);
                        if (method.isAnnotationPresent(GetMapping.class)) {
                            GetMapping getMapping = method.getAnnotation(GetMapping.class);
                            commandMap.put(getMapping.value(), methodAndHandler);
                        } else if (method.isAnnotationPresent(PostMapping.class)) {
                            PostMapping postMapping = method.getAnnotation(PostMapping.class);
                            commandMap.put(postMapping.value(), methodAndHandler);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        MethodAndHandler methodAndHandler = commandMap.get(requestPath);
        Method method = methodAndHandler.getMethod();
        Object handler = methodAndHandler.getHandler();

        if (method != null) {
            try {
                boolean methodMatch = false;
                Object response = null;
                if ("GET".equals(requestMethod) && method.isAnnotationPresent(GetMapping.class)) {
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        if (HttpRequestParser.HttpRequest.class.isAssignableFrom(parameters[i].getType())) {
                            args[i] = parse;
                        } else {
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
                    }
                    // 确保请求方法和路径与GetMapping注解匹配
                    methodMatch = true;
                    response = method.invoke(handler, args);

                } else if ("POST".equals(requestMethod) && method.isAnnotationPresent(PostMapping.class)) {
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        if (HttpRequestParser.HttpRequest.class.isAssignableFrom(parameters[i].getType())) {
                            args[i] = parse;
                        } else {
                            RequestBody RequestBodyAnnotation = parameters[i].getAnnotation(RequestBody.class);
                            if (RequestBodyAnnotation != null) {
                                // 确保请求方法和路径与PostMapping注解匹配
                                methodMatch = true;
                                Parameter parameter = method.getParameters()[i]; // 假设POST方法只有一个参数
                                Class<?> paramType = parameter.getType();
                                Object argValue = WendyJsonUtils.deserialize(parse.getBody(), paramType);
                                args[i] = argValue;
                            }
                        }
                    }

                    response = method.invoke(handler, args);

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
        System.out.println("已经关闭");
    }

    public Object convertStringToType(String value, Class<?> type) {
        if (Integer.class == type || int.class == type) {
            return Integer.parseInt(value);
        }
        // 添加更多类型的支持...
        return value; // 默认情况下，返回字符串本身
    }

}

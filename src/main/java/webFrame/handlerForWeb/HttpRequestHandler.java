package webFrame.handlerForWeb;

import handWritingNetty.Inter.ReceiveHandler;
import webFrame.Scan.ControllerScanner;
import webFrame.Scan.MethodAndHandler;
import webFrame.Scan.annotation.GetMapping;
import webFrame.Scan.annotation.PostMapping;
import webFrame.Scan.annotation.RequestBody;
import webFrame.Scan.annotation.RequestParam;
import webFrame.utils.httpRequestParser.HttpRequestParser;
import webFrame.utils.json.JsonUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class HttpRequestHandler implements ReceiveHandler {

    Map<String, MethodAndHandler> commandMap;

    public HttpRequestHandler(Class<?> controllerRegister) {
        ControllerScanner controllerScanner = new ControllerScanner();
        commandMap = controllerScanner.doScan(controllerRegister);
    }

    @Override
    public void handle(SocketChannel channel, ByteBuffer data, HashMap<String, Object> context) {
        String httpRequest = new String(data.array(), StandardCharsets.UTF_8).trim();

        // 解析 HTTP 请求
        HttpRequestParser.HttpRequest parse = parseHttpRequest(httpRequest, context);
        try {
            Object processResult = process(parse);
            context.put("result", processResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object process(HttpRequestParser.HttpRequest parse) throws IOException {

        String requestPath = parse.getPath();
        String requestMethod = parse.getMethod().toUpperCase();
        Map<String, String> requestParams = parse.getParameters();

        // 假设handler是已经定义好的处理实例
        MethodAndHandler methodAndHandler = commandMap.get(requestPath);

        if (methodAndHandler != null) {


            Method method = methodAndHandler.getMethod();
            Object handler = methodAndHandler.getHandler();


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
                                Object argValue = JsonUtils.deserialize(parse.getBody(), paramType);
                                args[i] = argValue;
                            }
                        }
                    }

                    response = method.invoke(handler, args);

                }

                if (!methodMatch) {
                    return new Error("405", "HTTP/1.1 405 Method Not Allowed\r\nContent-Length: 0\r\n\r\n");
                }
                return response;
                // 处理成功，发送响应
            } catch (Exception e) {
                e.printStackTrace();
                return new Error("500", "HTTP/1.1 500 Internal Server Error\r\nContent-Length: 0\r\n\r\n");
            }
        } else {
            return new Error("404", "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n");
            // 没有找到匹配的处理方法，发送404响应
        }
    }

    public Object convertStringToType(String value, Class<?> type) {
        if (Integer.class == type || int.class == type) {
            return Integer.parseInt(value);
        }
        // 添加更多类型的支持...
        return value; // 默认情况下，返回字符串本身
    }

    private HttpRequestParser.HttpRequest parseHttpRequest(String strRequest, HashMap<String, Object> context) {
        InputStream inputStream = new ByteArrayInputStream(strRequest.getBytes());
        try {
            HttpRequestParser.HttpRequest request = HttpRequestParser.parse(inputStream);
            context.put("httpRequest", request);
            return request;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

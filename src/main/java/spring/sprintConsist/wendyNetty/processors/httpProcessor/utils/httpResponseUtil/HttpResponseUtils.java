package spring.sprintConsist.wendyNetty.processors.httpProcessor.utils.httpResponseUtil;

import spring.sprintConsist.wendyNetty.processors.httpProcessor.utils.json.WendyJsonUtils;

public class HttpResponseUtils {

    // 构建HTTP响应字符串
    public static String buildHttpResponse(Object responseObject) {
        String responseBody = WendyJsonUtils.serialize(responseObject);
        StringBuilder responseBuilder = new StringBuilder();

        // 响应行
        responseBuilder.append("HTTP/1.1 200 OK\r\n");

        // 响应头
        responseBuilder.append("Content-Type: application/json\r\n");
        responseBuilder.append("Content-Length: ").append(responseBody.getBytes().length).append("\r\n");
        responseBuilder.append("Connection: close\r\n");
        responseBuilder.append("\r\n");

        // 响应体
        responseBuilder.append(responseBody);

        return responseBuilder.toString();
    }
}

package spring.sprintConsist.wendyNetty.processors.httpProcessor.utils.httpRequestParser;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

public class HttpRequestParser {

    public static class HttpRequest {
        private String method;
        private String path;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> parameters = new HashMap<>();
        private String body; // 用于存储原始请求体数据

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
// Getter and Setter
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest();

        // 解析请求行
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Request line is missing");
        }

        String[] requestLine = line.split(" ");
        request.method = requestLine[0];
        if (requestLine[1].contains("?")) {
            String[] pathAndParameters = requestLine[1].split("\\?");
            request.path = pathAndParameters[0];
            parseParameters(pathAndParameters[1], request.parameters);
        } else {
            request.path = requestLine[1];
        }

        // 解析请求头
        line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            String[] header = line.split(": ");
            request.headers.put(header[0], header[1]);
            line = reader.readLine();
        }

        // 解析POST请求体
        if ("POST".equalsIgnoreCase(request.method)) {
            StringBuilder bodyBuilder = new StringBuilder();
            while (reader.ready()) {
                bodyBuilder.append((char) reader.read());
            }
            request.body = bodyBuilder.toString();

            // 检查内容类型并相应地解析请求体
            String contentType = request.headers.get("Content-Type");
            if ("application/x-www-form-urlencoded".equals(contentType)) {
                parseParameters(request.body, request.parameters);
            } else if ("application/json".equals(contentType)) {
                // 对于JSON，我们保留原始体，不进行参数解析
                // 你可以在后续处理中将body解析为JSON对象
            }
        }

        return request;
    }

    private static void parseParameters(String data, Map<String, String> parameters) throws UnsupportedEncodingException {
        String[] pairs = data.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
            String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
            parameters.put(key, value);
        }
    }

    // 测试用例和main方法保持不变
}

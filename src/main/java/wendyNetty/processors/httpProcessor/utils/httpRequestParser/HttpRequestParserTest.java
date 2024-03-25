package wendyNetty.processors.httpProcessor.utils.httpRequestParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpRequestParserTest {

    public static void main(String[] args) throws IOException {
        testGETRequest();
        testPOSTRequestUrlencoded();
        testPOSTRequestJson();
    }

    private static void testGETRequest() throws IOException {
        System.out.println("Testing GET Request...");
        String getRequest = "GET /test?param1=value1&param2=value2 HTTP/1.1\r\nHost: localhost\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(getRequest.getBytes());
        HttpRequestParser.HttpRequest request = HttpRequestParser.parse(inputStream);
        System.out.println("Method: " + request.getMethod());
        System.out.println("Path: " + request.getPath());
        request.getParameters().forEach((key, value) -> System.out.println("Parameter: " + key + " = " + value));
        System.out.println();
    }

    private static void testPOSTRequestUrlencoded() throws IOException {
        System.out.println("Testing POST Request (application/x-www-form-urlencoded)...");
        String postRequestUrlencoded = "POST /test HTTP/1.1\r\nHost: localhost\r\nContent-Type: application/x-www-form-urlencoded\r\nContent-Length: 27\r\n\r\nparam1=value1&param2=value2";
        InputStream inputStream = new ByteArrayInputStream(postRequestUrlencoded.getBytes());
        HttpRequestParser.HttpRequest request = HttpRequestParser.parse(inputStream);
        System.out.println("Method: " + request.getMethod());
        System.out.println("Path: " + request.getPath());
        System.out.println("Body: " + request.getBody());
        request.getParameters().forEach((key, value) -> System.out.println("Parameter: " + key + " = " + value));
        System.out.println();
    }

    private static void testPOSTRequestJson() throws IOException {
        System.out.println("Testing POST Request (application/json)...");
        String postRequestJson = "POST /test HTTP/1.1\r\nHost: localhost\r\nContent-Type: application/json\r\nContent-Length: 34\r\n\r\n{\"param1\":\"value1\",\"param2\":\"value2\"}";
        InputStream inputStream = new ByteArrayInputStream(postRequestJson.getBytes());
        HttpRequestParser.HttpRequest request = HttpRequestParser.parse(inputStream);
        System.out.println("Method: " + request.getMethod());
        System.out.println("Path: " + request.getPath());
        System.out.println("Body: " + request.getBody());
        System.out.println();
    }
}

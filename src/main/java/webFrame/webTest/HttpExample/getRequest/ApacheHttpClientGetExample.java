package webFrame.webTest.HttpExample.getRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ApacheHttpClientGetExample {
    public static void main(String[] args) {
        // 构造请求URL，包括参数
//        String urlWithParams = "http://localhost:8080/testGet?age=18&name=lhk";
        String urlWithParams = "http://localhost:8080/testGetReturn1?age=18&name=lhk";

        // 使用try-with-resources确保资源正确关闭
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(urlWithParams);
            String response = httpClient.execute(request, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity()));

            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

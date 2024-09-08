package webFrame.webTestSwapCalculator.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SwapGet {
    public static void main(String[] args) {
        // 构造请求URL，包括参数
//        String urlWithParams = "http://localhost:8080/testGet?age=18&name=lhk";
        String urlWithParams = "http://localhost:8080/swap";

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

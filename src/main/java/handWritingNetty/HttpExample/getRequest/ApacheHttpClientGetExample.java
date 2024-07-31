package handWritingNetty.HttpExample.getRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ApacheHttpClientGetExample {
    public static void main(String[] args) {
        String urlWithParams = "http://localhost:8080";
//        String urlWithParams = "http://localhost:8080/test1?age=18&name=lhk";
        System.out.println("Sending request to URL: " + urlWithParams);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(urlWithParams);
            System.out.println("Executing request...");
            String response = httpClient.execute(request, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity()));

            System.out.println("Response received: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package webFrame.webTest.HttpExample.postRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.fasterxml.jackson.databind.ObjectMapper; // 需要添加Jackson依赖
import org.apache.http.util.EntityUtils;
import webFrame.utils.json.test.Person;

public class HttpClientExample {

    public static void main(String[] args) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost request = new HttpPost("http://localhost:8080/testPostPerson"); // 替换为你的目标URL

            // 构造Person对象并转换成JSON
            Person person = new Person("xmh", 1222);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(person);

            // 设置请求体
            StringEntity entity = new StringEntity(json);
            request.setEntity(entity);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            // 发送请求
            HttpResponse response = httpClient.execute(request);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            System.out.println(EntityUtils.toString(response.getEntity()));
        } finally {
            httpClient.close();
        }
    }
}

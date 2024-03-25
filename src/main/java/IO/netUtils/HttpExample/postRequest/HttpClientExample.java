package IO.netUtils.HttpExample.postRequest;

import IO.netUtils.json.test.Person;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.fasterxml.jackson.databind.ObjectMapper; // 需要添加Jackson依赖
import org.apache.http.util.EntityUtils;

public class HttpClientExample {

    public static void main(String[] args) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost request = new HttpPost("http://localhost:8080/test2"); // 替换为你的目标URL

            // 构造Person对象并转换成JSON
            Person person = new Person("xmh", 18);
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

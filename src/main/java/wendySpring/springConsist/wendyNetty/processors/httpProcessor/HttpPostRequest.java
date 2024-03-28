package wendySpring.springConsist.wendyNetty.processors.httpProcessor;

import wendySpring.springConsist.wendyNetty.AddressAndPort;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.utils.json.WendyJsonUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPostRequest {

    public static Object sendPostRequest(AddressAndPort addressAndPort, String endpoint,Object body, Class<?> clazz) throws Exception {
        // 将对象序列化成JSON
        int port = addressAndPort.getPort();
        String address = addressAndPort.getServerAddress();


        String jsonInputString = WendyJsonUtils.serialize(body);

        URL url = new URL("http", address, port, endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        // 发送JSON数据
        try(DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(jsonInputString);
            wr.flush();
        }

        int status = con.getResponseCode();
        StringBuilder response = new StringBuilder();
        if (status > 299) {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } else {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        }

        // 将响应JSON字符串反序列化为对象
        // 如果响应体非空，则尝试反序列化为对象
        if (response.length() > 0) {
            return WendyJsonUtils.deserialize(response.toString(), clazz);
        }

        // 如果响应体为空，返回null或进行其他合适的处理
        return null;
    }
}

package IO.netUtils.HttpExample.postRequest;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleHttpPostClient {

    public static void main(String[] args) {
        try {
            // URL和JSON数据
            String url = "http://localhost:8080/test2"; // 替换为你的目标URL
            String jsonInputString = "{\"name\": \"xmh\", \"age\": 18}";

            // 创建URL和HttpURLConnection
            URL urlObj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            // 设置请求方法和请求头
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            // 发送JSON数据
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应码
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package handWritingNetty.HttpExample.getRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpClientGet100 {
    public static void main(String[] args) {
        // 构造请求URL，包括参数
        String urlWithParams = "http://localhost:8080/test1?age=18&name=lhk";

        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 创建一个 CountDownLatch 实例，初始值为 100
        CountDownLatch latch = new CountDownLatch(100);

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 提交100个任务
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    // 等待所有线程准备好
                    latch.await();
                    // 发送 GET 请求
                    sendGetRequest(urlWithParams);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            // 减少计数
            latch.countDown();
        }

        // 关闭线程池
        executorService.shutdown();

        // 记录结束时间并计算总耗时
        while (!executorService.isTerminated()) {
            // 等待所有任务完成
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime - startTime) + "ms");
    }

    private static void sendGetRequest(String urlWithParams) {
        // 使用 try-with-resources 确保资源正确关闭
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(urlWithParams);
            String response = httpClient.execute(request, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity()));

//            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

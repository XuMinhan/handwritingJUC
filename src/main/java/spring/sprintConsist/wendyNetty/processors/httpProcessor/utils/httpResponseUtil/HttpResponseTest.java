package spring.sprintConsist.wendyNetty.processors.httpProcessor.utils.httpResponseUtil;

import spring.sprintConsist.wendyNetty.processors.httpProcessor.utils.json.test.Person;

public class HttpResponseTest {

    public static void main(String[] args) {
        Person person = new Person("John Doe", 30);

        // 使用HttpResponseUtils生成HTTP响应
        String httpResponse = HttpResponseUtils.buildHttpResponse(person);

        // 打印HTTP响应
        System.out.println(httpResponse);
    }
}

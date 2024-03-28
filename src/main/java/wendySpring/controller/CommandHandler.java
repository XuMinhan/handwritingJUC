package wendySpring.controller;

import org.apache.http.HttpRequest;
import wendySpring.service.MyService;
import wendySpring.springConsist.springBean.Resource;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.*;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.utils.json.test.Person;

@Controller
public class CommandHandler {

    @Resource
    MyService myService;

    @GetMapping("/test1")
    public Person handleCommand1(HttpRequest httpRequest, @RequestParam("name") String name , @RequestParam("age") int age) {
        // 处理命令1的逻辑
        // 实现发送响应到clientChannel等逻辑
        myService.serviceMethod();
        return new Person(name, age);
    }

    @PostMapping("/test2")
    public Person handleCommand2(HttpRequest httpRequest,@RequestBody Person person) {
        // 处理命令1的逻辑
        System.out.println(person);
        // 实现发送响应到clientChannel等逻辑
        return person;
    }

    // 更多命令处理方法...
}

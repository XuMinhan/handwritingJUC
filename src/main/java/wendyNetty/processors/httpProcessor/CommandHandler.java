package wendyNetty.processors.httpProcessor;

import wendyNetty.processors.httpProcessor.utils.json.test.Person;

public class CommandHandler {

    @GetMapping("/test1")
    public Person handleCommand1(@RequestParam("name") String name , @RequestParam("age") int age) {
        // 处理命令1的逻辑
        // 实现发送响应到clientChannel等逻辑
        return new Person(name, age);
    }

    @PostMapping("/test2")
    public Person handleCommand2(@RequestBody Person person) {
        // 处理命令1的逻辑
        System.out.println(person);
        // 实现发送响应到clientChannel等逻辑
        return new Person("xmh", 18);
    }

    // 更多命令处理方法...
}

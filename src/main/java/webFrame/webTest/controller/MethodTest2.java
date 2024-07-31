package webFrame.webTest.controller;

import webFrame.Scan.annotation.*;
import webFrame.utils.json.test.Person;

@Controller
public class MethodTest2 {
    @GetMapping("/testGet")
    public Person testGet(@RequestParam("name") String name, @RequestParam("age") Integer age) {
        return new Person(name,age);
    }
    @GetMapping("/testGetReturn1")
    public Integer testGetReturn1(@RequestParam("name") String name, @RequestParam("age") Integer age) {
        return 1;
    }

    @PostMapping("/testPostPerson")
    public Integer testPost(@RequestBody Person person) {
        return 1;
    }
}

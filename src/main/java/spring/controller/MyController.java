package spring.controller;

import spring.springBean.Component;
import spring.springBean.Resource;
import spring.service.MyService;
@Component
public class MyController {
    @Resource
    private MyService myService;

    public void controllerMethod() {
        myService.serviceMethod();
    }
}
package spring;

import spring.controller.MyController;
import spring.springBean.SimpleDIContainer;

public class Main {
    public static void main(String[] args) {

        MyController myController = SimpleDIContainer.getInstance(MyController.class);
        myController.controllerMethod(); // 应该输出 "Service method called"
    }
}
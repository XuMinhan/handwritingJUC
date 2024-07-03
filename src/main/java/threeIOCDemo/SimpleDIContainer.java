package threeIOCDemo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SimpleDIContainer {

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();        // 一级缓存
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();  // 二级缓存
    private final Map<String, Supplier<Object>> singletonFactories = new ConcurrentHashMap<>(); // 三级缓存

    // 获取Bean的方法
    public Object getBean(String beanName) {
        Object bean = singletonObjects.get(beanName);
        if (bean != null) {
            return bean;
        }

        bean = earlySingletonObjects.get(beanName);
        if (bean == null) {
            Supplier<Object> beanFactory = singletonFactories.get(beanName);
            if (beanFactory != null) {
                bean = beanFactory.get();
                earlySingletonObjects.put(beanName, bean);
                singletonObjects.put(beanName, bean);
                singletonFactories.remove(beanName);
            }
        }
        return bean;
    }

    // 添加Bean工厂
    public void addBeanFactory(String beanName, Supplier<Object> beanFactory) {
        singletonFactories.put(beanName, beanFactory);
    }
}

class A {
    private B b;

    public A(B b) {
        this.b = b;
    }

    public void doSomething() {
        System.out.println("A instance, with B: " + b);
    }
}

class B {
    private A a;

    public B() {
    }

    public void setA(A a) {
        this.a = a;
    }

    public void doSomething() {
        System.out.println("B instance, with A: " + a);
    }
}



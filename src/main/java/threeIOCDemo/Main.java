package threeIOCDemo;

public class Main {
    public static void main(String[] args) {
        SimpleDIContainer container = new SimpleDIContainer();

        // B needs A, but since A also needs B, we need to handle this via the container
        container.addBeanFactory("A", () -> new A((B) container.getBean("B")));
        container.addBeanFactory("B", () -> {
            B b = new B();
            b.setA((A) container.getBean("A"));
            return b;
        });

        A a = (A) container.getBean("A");
        B b = (B) container.getBean("B");

        a.doSomething();
        b.doSomething();
    }
}
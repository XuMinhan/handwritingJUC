package QuasarExample;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;

public class QuasarExample {
    public static void main(String[] args) throws Exception {
        Fiber<Void> fiber = new Fiber<Void>() {
            @Override
            protected Void run() throws SuspendExecution, InterruptedException {
                System.out.println("Hello from Fiber!");
                Fiber.sleep(1000);  // 类似于 Thread.sleep，但不会阻塞底层线程
                System.out.println("Fiber wakes up!");
                return null;
            }
        };
        fiber.start();
        fiber.join();  // 等待纤程结束
    }
}

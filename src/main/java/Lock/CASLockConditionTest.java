package Lock;

import java.util.LinkedList;
import java.util.List;

public class CASLockConditionTest {
    private static final CASLock lock = new CASLock();
    private static final ConditionObject notFull = lock.newCondition("notFull");
    private static final ConditionObject notEmpty = lock.newCondition("notEmpty");
    private static final List<Integer> buffer = new LinkedList<>(); // 使用List作为缓冲区
    private static final int MAX_BUFFER_SIZE = 100; // 定义缓冲区的最大容量

    public static void main(String[] args) {
        // 生产者线程，无限循环生产
        new Thread(() -> {
            int produceItem = 0;
            while (true) { // 无限循环
                lock.lock();
                try {
                    while (buffer.size() == MAX_BUFFER_SIZE) {
                        notFull.await(); // 如果缓冲区满了，就等待
                    }
                    produceItem++; // 生产新元素
                    buffer.add(produceItem); // 将新元素放入缓冲区
                    System.out.println("Produced: " + produceItem);
                    notEmpty.signal(); // 通知至少一个等待的消费者
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        }).start();

        // 多个消费者线程，无限循环消费
        final int CONSUMER_COUNT = 1; // 消费者数量
        for (int j = 0; j < CONSUMER_COUNT; j++) {
            new Thread(() -> {
                while (true) { // 无限循环
                    lock.lock();
                    try {
                        while (buffer.isEmpty()) {
                            notEmpty.await(); // 如果缓冲区空了，就等待
                        }
                        int item = buffer.remove(0); // 从缓冲区消费一个元素
                        System.out.println("Consumed by " + Thread.currentThread().getId() + ": " + item);
                        notFull.signal(); // 通知至少一个等待的生产者
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }
                }
            }).start();
        }
    }
}

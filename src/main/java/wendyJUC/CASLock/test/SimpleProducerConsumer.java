package wendyJUC.CASLock.test;

import wendyJUC.CASLock.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleProducerConsumer {

    public static void main(String[] args) {
        // 参数检查和解析

        int numProducers = 10;
        int numConsumers = 10;

        final Queue<Integer> buffer = new LinkedList<>();
        final int MAX_CAPACITY = 10;
        final CASLockV2 lock = new CASLockV2();
        final ConditionObject notEmpty = lock.newCondition("notEmpty");
        final ConditionObject notFull = lock.newCondition("notFull");

        // 时间戳记录
        final AtomicLong lastProducerActivity = new AtomicLong(System.currentTimeMillis());
        final AtomicLong lastConsumerActivity = new AtomicLong(System.currentTimeMillis());

        // 创建并启动生产者线程
        for (int i = 0; i < numProducers; i++) {
            Thread producer = new Thread(() -> {
                int value = 0;
                while (true) {
                    try {
                        lock.lock();
                        while (buffer.size() == MAX_CAPACITY) {
                            notFull.await();
                        }
                        buffer.add(value);
                        value++;
                        if (value % 1000 == 0) {
                            System.out.println("Produced: " + value);
                        }
                        notEmpty.signal();
                        lastProducerActivity.set(System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }
                }
            });
            producer.start();
        }

        // 创建并启动消费者线程
        for (int i = 0; i < numConsumers; i++) {
            Thread consumer = new Thread(() -> {
                while (true) {
                    try {
                        lock.lock();
                        while (buffer.isEmpty()) {
                            notEmpty.await();
                        }
                        int item = buffer.poll();
                        notFull.signal();
                        lastConsumerActivity.set(System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }
                }
            });
            consumer.start();
        }

        // 监控线程
        Thread monitor = new Thread(() -> {
            final long TIMEOUT = 5000; // 设置超时时间为5000毫秒
            try {
                while (true) {
                    Thread.sleep(TIMEOUT); // 每5秒检查一次
                    if (System.currentTimeMillis() - lastProducerActivity.get() > TIMEOUT &&
                            System.currentTimeMillis() - lastConsumerActivity.get() > TIMEOUT) {
                        System.out.println("Both threads are inactive for more than " + TIMEOUT + " ms");
                        // 这里可以实施一些恢复措施，如重启线程等
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        monitor.start();
    }
}

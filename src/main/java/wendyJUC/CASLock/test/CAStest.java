package wendyJUC.CASLock.test;


import wendyJUC.CASLock.CASLockV2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CAStest {

    // 共享资源
    private static Integer counter = 0;

    public static void main(String[] args) throws InterruptedException {
        final CASLockV2 lock = new CASLockV2();
        final int numberOfThreads = 100; // 线程数量
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(numberOfThreads);

        // 创建并准备启动线程
        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(() -> {
                try {
                    startSignal.await(); // 等待主线程信号

                    for (int j = 0; j < 10000; j++) {
//                    while (true){

                        lock.lock();
//                        System.out.println(Thread.currentThread().getName() + "抢到锁");
                        try {
                            // 模拟一些需要同步的工作
////                        System.out.println("Thread " + Thread.currentThread().getId() + " acquired the lock.");
//                            System.out.println("add");
                            counter++;
//                            System.out.println(counter);
                            if (counter % 100000 == 0) {
                                System.out.println("Produced: " + counter);
//
                            }

                            // 为了清晰看到效果，让线程睡眠一会儿
//                        Thread.sleep(100); // 100毫秒
//                        System.out.println("Thread " + Thread.currentThread().getId() + " released the lock.");
                        } finally {

                            lock.unlock();
//                            System.out.println(Thread.currentThread().getName() + "释放锁");

                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown(); // 完成任务，计数减一
                }
            });
        }

        Thread.sleep(100); // 短暂延迟，确保所有线程都在await
        startSignal.countDown(); // 释放所有线程
        doneSignal.await(); // 等待所有线程完成

        // 关闭ExecutorService
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // 打印最终的计数器值
        System.out.println("Final counter value: " + counter);
    }
}

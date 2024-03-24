package lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CAStest {

    // 共享资源
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        final CASLock lock = new CASLock();
        final int numberOfThreads = 2000; // 线程数量
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(numberOfThreads);

        // 创建并准备启动线程
        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(() -> {
                try {
                    startSignal.await(); // 等待主线程信号
                    lock.lock();
                    try {
                        // 模拟一些需要同步的工作
//                        System.out.println("Thread " + Thread.currentThread().getId() + " acquired the lock.");
                       for (int j = 0;j<1000 ;j++){
                            counter++;
                        }
                        // 为了清晰看到效果，让线程睡眠一会儿
//                        Thread.sleep(100); // 100毫秒
//                        System.out.println("Thread " + Thread.currentThread().getId() + " released the lock.");
                    } finally {
                        lock.unlock();
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

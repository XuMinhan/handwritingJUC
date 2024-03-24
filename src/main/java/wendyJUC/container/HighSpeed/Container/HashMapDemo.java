package wendyJUC.container.HighSpeed.Container;


import wendyJUC.container.HighSpeed.MyMap;

import java.util.Random;
import java.util.concurrent.*;

//1.0不使用任何同步机制，线程多会爆，在resize加上锁后不爆，但是长度不对，且数据错了
//于是对判断是否需要扩容加上锁。长度对了，不会过度扩容，但是数据仍然不对，发现及其有趣的现象
//String key = "The" + threadNum + "-K" +j; // 使用 j % 5 来强制哈希冲突
//resize的时候若不暂停插入，则导致同步数据缺失。
//若不对单个节点进行加锁，进行hash碰撞暴力测试，进入死锁
//对单个节点进行加锁，CAS锁使用了监视器，用synchronized对这个监视器做限制，使用这个监视器进行等待/通知
//直接自旋，因为觉得使用synchronized太蠢了
//发现效率不如直接使用synchronized
//加上对每个index的大小进行单独控制，以保证size的高并发，并且将size改成模糊size，理解了提起放大的原因
//因为他通过判断大小，确实较大，并且尝试获取锁，如果他在判断大于临界值到获取锁的过程中，size超过最大值，则会崩溃


public class HashMapDemo {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    ConcurrentHashMap concurrentHashMap;

    public static void main(String[] args) throws InterruptedException {
        int mapSize = 16;
        int threadTotalNum = 200;
        int runNum = 20;
        final MyMap<String, Integer> myMap = new WendyHashMap<>(mapSize);
//        final MyHashMapEasyVersion<String, Integer> myHashMapEasyVersion = new MyHashMapEasyVersion<>();
//        ConcurrentHashMap<String, Integer> stringIntegerConcurrentHashMap = new ConcurrentHashMap<>();


        ExecutorService executor = Executors.newFixedThreadPool(threadTotalNum);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threadTotalNum);

        for (int i = 0; i < threadTotalNum; i++) {
            final int threadNum = i;
            executor.execute(() -> {
                try {
                    startSignal.await(); // 等待主线程的信号

//                    for (int j = 0; j < runNum; j++) {
//                        String key = "The" + threadNum + "-K" +j; // 使用 j % 5 来强制哈希冲突
//                        myMap.put(key, j);
//                    }

                    for (int j = 0; j < runNum; j++) {
                        String key = generateRandomString(10); // 使用 j % 5 来强制哈希冲突
                        myMap.put(key,j);
//                        stringIntegerConcurrentHashMap.put(key,j);
//                        myHashMapEasyVersion.put(key, j);
                    }

//                    for (int j = 0; j < runNum; j++) {
//                        String key = "sameString"; // 测试hash碰撞，
//                        myMap.put(key, j);
//                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown(); // 完成一个任务，计数减一
                }
            });
        }

        Thread.sleep(100); // 短暂延迟，确保所有线程都已启动并在await

        long startTime = System.nanoTime(); // 所有线程开始执行的时间戳


        startSignal.countDown(); // 释放所有等待线程
        doneSignal.await(); // 等待所有线程完成任务

        long endTime = System.nanoTime(); // 所有线程执行完成的时间戳
        long duration = endTime - startTime; // 计算总持续时间


        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // 打印最终的 MyHashMap 内容
        System.out.println(myMap);
        System.out.println(myMap.size());

//        System.out.println(myHashMapEasyVersion);
//        System.out.println(myHashMapEasyVersion.size());
//
//        System.out.println(stringIntegerConcurrentHashMap);
//        System.out.println(stringIntegerConcurrentHashMap.size());

        System.out.println("Total execution time: " + duration + " nanoseconds");

    }


//    public static void main(String[] args) throws InterruptedException {
//        final MyHashMapEasyVersion<String, Integer> myHashMapEasyVersion = new MyHashMapEasyVersion<>();
//        long startTime = System.nanoTime(); // 所有线程开始执行的时间戳
//
//        for (int j = 0; j < 2000 * 2000; j++) {
//            String key = generateRandomString(10); // 使用 j % 5 来强制哈希冲突
//            myHashMapEasyVersion.put(key, j);
//        }
//        long endTime = System.nanoTime(); // 所有线程执行完成的时间戳
//        long duration = endTime - startTime; // 计算总持续时间
//        System.out.println(duration);
//
//
//    }
}

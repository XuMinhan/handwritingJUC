package thread;

public class ThreadPoolDemo {
    public static void main(String[] args) throws InterruptedException {
        AdvancedThreadPool threadPool = new AdvancedThreadPool(2, 4, 5);

        // 提交5个任务到线程池
        for (int i = 0; i < 20; i++) {
            int taskNo = i;
            threadPool.execute(() -> {
                String threadName = Thread.currentThread().getName();
//                System.out.println("Executing task " + taskNo + " by " + threadName);
                try {
                    // 模拟任务执行耗时
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    System.out.println("Task " + taskNo + " was interrupted.");
                }
//                System.out.println("Task " + taskNo + " finished by " + threadName);
            });
        }

        // 等待一段时间让任务执行
        Thread.sleep(500);

        // 关闭线程池，并等待已提交的任务完成执行
        System.out.println("Shutting down the thread pool.");
        threadPool.shutdown();
//        threadPool.awaitTermination(10);//纳秒
        Thread.sleep(50);

        threadPool.awaitTermination();


        if (threadPool.isTerminated()) {
            System.out.println("All tasks have completed execution and the thread pool is terminated.");
        } else {
            System.out.println("The thread pool was not terminated properly.");
        }
        if (threadPool.isShutdown()) {
            System.out.println("isShutdown");
        } else {
            System.out.println("isShutdown");
        }
    }
}

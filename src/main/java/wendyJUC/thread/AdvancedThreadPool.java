package wendyJUC.thread;

import wendyJUC.CASLock.*;
import wendyJUC.container.HighSpeed.BaseType.WendyBoolean;
import wendyJUC.container.HighSpeed.BaseType.WendyInteger;
import wendyJUC.container.HighSpeed.Container.WendyBlockingQueue;
import wendyJUC.container.LowSpeed.LeonArrayList;


public class AdvancedThreadPool {
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final WendyBlockingQueue<Runnable> workQueue;
    private final LeonArrayList<Thread> workers;
    private final WendyBoolean isShutdown = new WendyBoolean(false);
    private final WendyBoolean isTerminated = new WendyBoolean(false);

    private final WendyInteger activeThreads = new WendyInteger(0);

    private final CASLockV2 ThreadPoolLock = new CASLockV2();
    private final ConditionObject termination = ThreadPoolLock.newCondition("terminal");

    public AdvancedThreadPool(int corePoolSize, int maximumPoolSize, int queueSize) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = new WendyBlockingQueue<>(queueSize);
        this.workers = new LeonArrayList<>();

        initCoreWorkers();
    }

    private void initCoreWorkers() {
        for (int i = 0; i < corePoolSize; i++) {
            createAndStartWorker();
        }
    }

    public void execute(Runnable task) {
        if (isShutdown.get()) {
            throw new IllegalStateException("ThreadPool is shutdown.");
        }
        // 尝试将任务添加到队列
        if (!workQueue.offer(task)) {

            ThreadPoolLock.lock();
            try {
                if (workers.size() < maximumPoolSize) {
                    // 如果活跃线程数小于最大线程数，创建新线程执行任务
                    createAndStartWorker(task);
                } else {
                    rejectTask();
                }
            } finally {
                ThreadPoolLock.unlock();

            }
        }
    }

    private void createAndStartWorker(Runnable firstTask) {
        Thread worker = new Thread(() -> {
            if (firstTask != null) {
                firstTask.run();
            }
            runTask();
        });
        workers.add(worker);
        worker.start();
    }

    private void createAndStartWorker() {
        createAndStartWorker(null);
    }

    private void runTask() {

        while (!isShutdown.get() || !workQueue.isEmpty()) {
            Runnable task;
            try {
                task = workQueue.take(); // 可能会抛出InterruptedException
                activeThreads.incrementAndGet(); // 获取任务即将执行时增加
            } catch (InterruptedException e) {
                // 如果线程池正在关闭且队列为空，结束此线程
                if (isShutdown.get() && workQueue.isEmpty()) {
                    break;
                }

                continue;
            }
            try {
                task.run();
            } finally {
                activeThreads.decrementAndGet();
                checkTermination(); // 检查是否应该标记线程池为终止
            }
        }

    }


    public void shutdown() {
        isShutdown.set(true);
        // 唤醒所有可能因为workQueue.take()而阻塞的工作线程
        System.out.println(workers.size());
        for (Thread worker : workers) {
            System.out.println("干掉");
            worker.interrupt();

        }
        checkTermination();
    }


    public void shutdownNow() {
        isShutdown.set(true);
        workQueue.clear(); // 清空队列
        for (Thread worker : workers) {
            worker.interrupt(); // 中断所有工作线程
        }
        checkTermination();
    }

    private void checkTermination() {
        ThreadPoolLock.lock();
        try {
            if (isShutdown.get() && activeThreads.get() == 0 && workQueue.isEmpty()) {
                if (!isTerminated.get()) {
                    isTerminated.set(true);
                    termination.signalAll(); // 唤醒所有等待线程池终止的线程
                }
            }

        } finally {
            ThreadPoolLock.unlock();
        }
    }

    public boolean awaitTermination() throws InterruptedException {
        ThreadPoolLock.lock();
        try {
            while (!isTerminated.get()) {
                termination.await(); // 等待被唤醒或超时
            }
        } finally {
            ThreadPoolLock.unlock();
        }
        return true;
    }


    private void rejectTask() {
        System.out.println("任务拒绝");
//        throw new RuntimeException("Task rejected due to shutdown or max capacity reached.");
    }

    public boolean isTerminated() {
        return isTerminated.get();
    }

    public boolean isShutdown() {
        return isShutdown.get();
    }
}
//public boolean awaitTermination(long timeout) throws InterruptedException {
//    ThreadPoolLock.lock();
//    try {
//        while (!isTerminated.get()) {
//            if (timeout <= 0L) return false; // 超时返回
////                termination.awaitNanos(timeout); // 等待被唤醒或超时
//        }
//    } finally {
//        ThreadPoolLock.unlock();
//    }
//    return true;
//}


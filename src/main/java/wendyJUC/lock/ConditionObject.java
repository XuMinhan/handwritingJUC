package wendyJUC.lock;


import wendyJUC.container.LowSpeed.LeonBlockingQueue;

import java.util.concurrent.locks.LockSupport;

public class ConditionObject {
    private final CASLock externalLock; // 重命名传入的锁
    private final CASLock queueLock = new CASLock(); // 新的锁用于同步等待队列
    private final int DEFAULT_CONDITION_WAITINGTHREAD_LENGTH = 10000;

    private final LeonBlockingQueue<Thread> waitingThreads;

    public ConditionObject(CASLock lock) {
        this.externalLock = lock;
        this.waitingThreads = new LeonBlockingQueue<>(DEFAULT_CONDITION_WAITINGTHREAD_LENGTH);
    }

    public ConditionObject(CASLock lock, int waitingThreadLength) {
        this.externalLock = lock;
        this.waitingThreads = new LeonBlockingQueue<>(waitingThreadLength);
    }

    public void await() throws InterruptedException {
        Thread currentThread = Thread.currentThread();
        queueLock.lock(); // 使用新的CASLock来保护等待队列
        try {
            waitingThreads.put(currentThread);
            queueLock.unlock();
            externalLock.unlock(); // 使用外部锁
            LockSupport.park();

        } finally {
            externalLock.lock(); // 重新获取外部锁
        }
    }
//    public void await() throws InterruptedException {
//        Thread currentThread = Thread.currentThread();
//        queueLock.lock(); // 使用新的CASLock来保护等待队列
//        try {
//            waitingThreads.put(currentThread);
//        } finally {
//            queueLock.unlock();
//        }
//
//        boolean wasInterrupted = false;
//        while (waitingThreads.contains(currentThread)) {
//            externalLock.unlock(); // 使用外部锁
//            try {
//                // 这里使用Thread.yield()仅仅是为了简化
//                // 实际中可以使用LockSupport.park()来阻塞线程
////                Thread.yield();
//                SimpleLockSupport.park();
//                if (Thread.interrupted()) { // 检查并清除中断状态
//                    throw new InterruptedException("Thread was interrupted.");
//                }
//            } finally {
//                externalLock.lock(); // 重新获取外部锁
//            }
//        }
//
//        if (wasInterrupted) {
//            // 如果在等待时线程被中断，重新设置中断状态
//            currentThread.interrupt();
//        }
//    }

    public void signal() {
        queueLock.lock(); // 使用新的CASLock来保护等待队列
        try {
            Thread t = waitingThreads.poll();
            LockSupport.unpark(t);
        } finally {
            queueLock.unlock();
        }
    }

    public void signalAll() {
        queueLock.lock(); // 使用新的CASLock来保护等待队列
        try {
            while (!waitingThreads.isEmpty()) {
                Thread t = waitingThreads.poll();
                LockSupport.unpark(t);

            }
        } finally {
            queueLock.unlock();
        }
    }


}
//
//public void awaitNanos(long nanosTimeout) throws InterruptedException {
//    if (Thread.interrupted()) {
//        throw new InterruptedException("Thread was interrupted before awaitNanos");
//    }
//
//    final long deadline = System.nanoTime() + nanosTimeout; // 计算截止时间
//    final Thread currentThread = Thread.currentThread();
//    boolean wasInterrupted = false;
//
//    queueLock.lock(); // 加锁保护等待队列
//    try {
//        waitingThreads.put(currentThread); // 将当前线程加入等待队列
//    } finally {
//        queueLock.unlock();
//    }
//
//    try {
//        while (waitingThreads.contains(currentThread)) {
//            long remainingNanos = deadline - System.nanoTime(); // 计算剩余时间
//            if (remainingNanos <= 0L) {
//                break; // 如果已经超时，退出等待循环
//            }
//
//            long waitTimeMillis = remainingNanos / 1_000_000L; // 将纳秒转换为毫秒
//            int additionalNanos = (int) (remainingNanos % 1_000_000L); // 获取剩余的纳秒
//
//            externalLock.unlock(); // 释放外部锁，允许其他线程操作
//            try {
//                synchronized (this) {
//                    this.wait(waitTimeMillis, additionalNanos); // 使用对象的wait方法等待
//                }
//            } catch (InterruptedException e) {
//                wasInterrupted = true; // 标记中断状态，稍后处理
//                // 但如果不再等待队列中了，需要立即处理中断
//                if (!waitingThreads.contains(currentThread)) {
//                    break;
//                }
//            } finally {
//                externalLock.lock(); // 重新获得外部锁
//            }
//        }
//    } finally {
//        queueLock.lock();
//        try {
//            waitingThreads.remove(currentThread); // 确保线程从等待队列中移除
//        } finally {
//            queueLock.unlock();
//        }
//
//        if (wasInterrupted) {
//            currentThread.interrupt(); // 恢复中断状态
//        }
//    }
//}
package wendyJUC.CASLock;

import sun.misc.Unsafe;

import java.util.concurrent.ConcurrentHashMap;

public class ConditionObject {
    private final CASLockV2 externalLock;

    protected Unsafe unsafe;

    protected WaitQueue waitQueue;


    public ConditionObject(CASLockV2 externalLock, ConcurrentHashMap<Thread, Boolean> threadIsSet) {
        this.externalLock = externalLock;
        waitQueue = new WaitQueue(threadIsSet);
        unsafe = CASLockV2.unsafe;
    }

    public void await() throws InterruptedException {
        Thread currentThread = Thread.currentThread();
        waitQueue.enqueue(currentThread);
        externalLock.unlock();
        externalLock.lock();
    }

    public void signal() {
        Thread thread = waitQueue.dequeue();
        if (thread != null) {
            externalLock.waitQueue.enqueue(thread);
        }
    }

    public void signalAll() {
        Thread thread;
        while ((thread = waitQueue.dequeue()) != null) {
            externalLock.waitQueue.enqueue(thread);
        }
    }
}

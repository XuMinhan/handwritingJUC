package wendyJUC.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;


public class CASLockV1 {
    private final LeonBlockingQueue<Thread> waitingThreads;
    private static final Unsafe unsafe;
    private static final long valueOffset;
    private volatile int state = 0;

    static {
        try {
            Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            unsafe = (Unsafe) theUnsafeInstance.get(null);

            valueOffset = unsafe.objectFieldOffset(CASLockV1.class.getDeclaredField("state"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public CASLockV1() {
        waitingThreads = new LeonBlockingQueue<>(2000);
    }

    public CASLockV1(Integer capacity) {
        waitingThreads = new LeonBlockingQueue<>(capacity);
    }

    public void lock() {
        while (!tryLock()) {

            // 将线程加入到等待队列
            Thread currentThread = Thread.currentThread();
            waitingThreads.put(currentThread);
            // 线程停止执行，等待被唤醒
//            SimpleLockSupport.park();
            unsafe.park(false,0);
        }
    }

    public boolean tryLock() {
        return unsafe.compareAndSwapInt(this, valueOffset, 0, 1);
    }

    public void unlock() {
        // 尝试从等待队列中唤醒一个线程
        state = 0;  // 正常解锁

        Thread threadToUnpark = waitingThreads.poll();
        if (threadToUnpark != null) {

            unsafe.unpark(threadToUnpark);
        }
    }
}
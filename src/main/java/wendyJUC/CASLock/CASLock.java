package wendyJUC.CASLock;

import sun.misc.Unsafe;
import wendyJUC.container.LowSpeed.LeonBlockingQueue;
import wendyJUC.container.LowSpeed.LeonHashMap;

import java.lang.reflect.Field;

public class CASLock {

    private final LeonBlockingQueue<Thread> waitingThreads;

    private static final Unsafe unsafe;
    private static final long valueOffset;
    private volatile int state = 0;

    static {
        try {
            Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            unsafe = (Unsafe) theUnsafeInstance.get(Unsafe.class);

            valueOffset = unsafe.objectFieldOffset(CASLock.class.getDeclaredField("state"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public int getState() {
        return state;
    }

    public CASLock() {
        waitingThreads = new LeonBlockingQueue<>(1000);
    }
    public CASLock(Integer capacity) {
        waitingThreads = new LeonBlockingQueue<>(capacity);
    }

    public void lock() {
        while (!tryLock()) {
            // 自旋等待
            Thread.yield();
        }
    }

    public boolean tryLock() {
        return unsafe.compareAndSwapInt(this, valueOffset, 0, 1);
    }

    public void unlock() {
        state = 0; // 这里没有使用CAS来解锁，是因为只有持有锁的线程才会调用unlock
    }

}

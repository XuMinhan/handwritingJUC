package wendyJUC.CASLock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleBlockingQueue<T> {
    private Queue<T> queue = new LinkedList<>();
    private volatile int lockState = 0;
    private static final Unsafe unsafe;
    private static final long lockStateOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            lockStateOffset = unsafe.objectFieldOffset(SimpleBlockingQueue.class.getDeclaredField("lockState"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public SimpleBlockingQueue(int capacity) {
        // Optionally handle the capacity if needed
    }

    public void put(T item) {
        while (!unsafe.compareAndSwapInt(this, lockStateOffset, 0, 1)) {
            Thread.yield();  // Use yield to prevent excessive CPU usage
        }
        try {
            Thread tmp = (Thread) item;
            System.out.println(tmp.getName());
            queue.add(item);
        } finally {
            lockState = 0;
        }
    }

    @Override
    public String toString() {
        return queue.toString();
    }

    public T poll() {
        while (!unsafe.compareAndSwapInt(this, lockStateOffset, 0, 1)) {
            Thread.yield();  // Use yield to prevent excessive CPU usage
        }
        try {
            return queue.poll();
        } finally {
            lockState = 0;
        }
    }
}

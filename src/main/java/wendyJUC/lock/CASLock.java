package wendyJUC.lock;

import wendyJUC.container.LowSpeed.LeonHashMap;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class CASLock {
    private LeonHashMap<String, ConditionObject> conditionMap = new LeonHashMap<>();

    public ConditionObject newCondition(String conditionName) {
        // Lazy initialization of ConditionObject
        ConditionObject condition = conditionMap.get(conditionName);
        if (condition == null) {
            synchronized (conditionMap) {
                condition = conditionMap.get(conditionName); // Double-check locking
                if (condition == null) {
                    condition = new ConditionObject(this);
                    conditionMap.put(conditionName, condition);
                }
            }
        }
        return condition;
    }
    private static final Unsafe unsafe;
    private static final long valueOffset;
    private volatile int state = 0;

    static {
        try {
            Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            unsafe = (Unsafe) theUnsafeInstance.get(Unsafe.class);

            valueOffset = unsafe.objectFieldOffset(CASLock.class.getDeclaredField("state"));
        } catch (Exception ex) { throw new Error(ex); }
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

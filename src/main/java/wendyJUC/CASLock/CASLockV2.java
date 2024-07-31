package wendyJUC.CASLock;


import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class CASLockV2 {

    protected static final Unsafe unsafe;
    private static final long valueOffset;
    private volatile int state = 0;
    protected WaitQueue waitQueue;
    private final ConcurrentHashMap<Thread, Boolean> threadIsSet = new ConcurrentHashMap<>();

    static {
        try {
            Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            unsafe = (Unsafe) theUnsafeInstance.get(null);
            valueOffset = unsafe.objectFieldOffset(CASLockV2.class.getDeclaredField("state"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public CASLockV2() {
        waitQueue = new WaitQueue(threadIsSet);
    }

    public void lock() {
        threadIsSet.put(Thread.currentThread(), false);
        boolean b = tryLock();
        while (!b) {
            Thread currentThread = Thread.currentThread();
            waitQueue.enqueue(currentThread);
            unsafe.park(false, 0);
            b = tryLock();
        }
        threadIsSet.put(Thread.currentThread(), true);
    }

    public boolean tryLock() {
        return unsafe.compareAndSwapInt(this, valueOffset, 0, 1);
    }


    public void unlock() {
        Thread nextThread = waitQueue.dequeue();
        if (nextThread == null) {
            //可能是正在处理，也可能真的没有，也可能正在退出，且准备重新加入的线程，这是由conditionObject的逻辑导致的
            //如果判断没有处理完，则循环弹出
            //两种可能弹出 判断均已处理完成/得到线程

            while (nextThread == null && !judgeIsAllSet()) {
                nextThread = waitQueue.dequeue();
            }
            //如果处理完成，则得到处理完的线程
            if (nextThread == null) {

                nextThread = waitQueue.dequeue();
            }


        }
        state = 0;
        threadIsSet.remove(Thread.currentThread());

        if (nextThread != null) {

            unsafe.unpark(nextThread);
        }


    }

    private final HashMap<String, ConditionObject> conditionObjectHashMap = new HashMap<>();


    public HashMap<String, ConditionObject> getConditionObjectHashMap() {
        return conditionObjectHashMap;
    }

    public ConditionObject newCondition(String conditionName) {

        ConditionObject conditionObject = new ConditionObject(this, threadIsSet);
        conditionObjectHashMap.put(conditionName, conditionObject);
        return conditionObject;
    }

    private boolean judgeIsAllSet() {
        for (ConcurrentHashMap.Entry<Thread, Boolean> entry : threadIsSet.entrySet()) {
            if (!entry.getValue()) {
                return false;
            }
        }
        return true;
    }

}

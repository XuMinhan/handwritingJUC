package wendyJUC.container.HighSpeed.BaseType;

import wendyJUC.lock.CASLock;

public class WendyBoolean {
    private volatile boolean value;
    private final CASLock lock = new CASLock();

    public WendyBoolean(boolean initialValue) {
        value = initialValue;
    }


    // 获取当前值
    public boolean get() {
        return value; // 直接读取volatile变量
    }

    // 安全地设置新值
    public void set(boolean newValue) {
        try {
            lock.lock(); // 尝试获取锁
            value = newValue;
        } finally {
            lock.unlock(); // 释放锁
        }
    }
}

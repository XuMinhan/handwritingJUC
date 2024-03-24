package container.HighSpeed.BaseType;

import lock.CASLock;

public class WendyInteger {
    private volatile int value;
    private final CASLock lock = new CASLock();

    public WendyInteger(int initialValue) {
        value = initialValue;
    }

    // 获取当前值
    public int get() {
        return value; // 直接读取volatile变量
    }

    // 安全地设置新值
    public void set(int newValue) {
        try {
            lock.lock(); // 尝试获取锁
            value = newValue;
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    // 安全地递增并返回新值
    public int incrementAndGet() {
        try {
            lock.lock(); // 尝试获取锁
            return ++value;
        } finally {
            lock.unlock(); // 释放锁
        }
    }
    public int getAndIncrement() {
        try {
            lock.lock(); // 尝试获取锁
            return value++; // 返回当前值，然后递增
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    // 安全地递减并返回新值
    public int decrementAndGet() {
        try {
            lock.lock(); // 尝试获取锁
            return --value;
        } finally {
            lock.unlock(); // 释放锁
        }
    }
}

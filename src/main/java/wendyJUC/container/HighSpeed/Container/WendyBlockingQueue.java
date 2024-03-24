package wendyJUC.container.HighSpeed.Container;

import wendyJUC.lock.CASLock;
import wendyJUC.lock.ConditionObject;
import wendyJUC.container.LowSpeed.LeonLinkedList;
public class WendyBlockingQueue<T> {
    private final LeonLinkedList<T> queue = new LeonLinkedList<>();
    private final int capacity;
    private final CASLock lock = new CASLock();
    private final ConditionObject notEmpty = lock.newCondition("notEmpty");
    private final ConditionObject notFull = lock.newCondition("notFull");

    public WendyBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(T element) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await(); // 等待队列非满
            }
            queue.addLast(element);
//            System.out.println("Producer " + " put: " + element.toString());
            notEmpty.signal(); // 通知队列非空
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException{
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await(); // 等待队列非空
            }
            T element = queue.removeFirst();
//            System.out.println("Consumer "  + " took: " + element.toString());
            notFull.signal(); // 通知队列非满

            return element;
        } finally {
            lock.unlock();
        }
    }


    public boolean offer(T element) {
        lock.lock();
        try {
            if (queue.size() == capacity) {
                return false; // 队列已满，无法添加
            }
            queue.addLast(element);
            notEmpty.signal(); // 可能有线程在等待队列非空
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
    public T poll() {
        lock.lock();
        try {
            if (queue.isEmpty()) {
                return null; // 队列为空，立即返回null
            }
            T element = queue.removeFirst();
            notFull.signal(); // 可能有线程在等待队列非满
            return element;
        } finally {
            lock.unlock();
        }
    }
    public boolean contains(T element) {
        lock.lock();
        try {
            // 假设LeonLinkedList有contains方法可直接调用
            // 如果没有，需要遍历queue来手动检查每个元素
            return queue.contains(element);
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            queue.clear(); // 清空队列
            notFull.signal(); // 清空队列后，队列非满，通知所有等待的生产者线程
        } finally {
            lock.unlock();
        }
    }

}

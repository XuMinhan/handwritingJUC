package wendyJUC.container.LowSpeed;

import java.util.NoSuchElementException;

public class LeonBlockingQueue<T> {
    private final LeonLinkedList<T> queue = new LeonLinkedList<>();
    private final int capacity;

    public LeonBlockingQueue(int capacity) {
        this.capacity = capacity;
    }
    public boolean remove(T element) {
        if (queue.isEmpty()) {
            return false; // 队列为空，无法移除元素
        }
        return queue.remove(element);
    }
    public void put(T element) {
        if (queue.size() >= capacity) {
            throw new IllegalStateException("Queue full");
        }
        queue.addLast(element);
    }

    public T take() {
        if (queue.isEmpty()) {
            throw new NoSuchElementException("Queue empty");
        }
        return queue.removeFirst();
    }

    public boolean offer(T element) {
        if (queue.size() >= capacity) {
            return false; // 队列已满，无法添加
        }
        queue.addLast(element);
        return true;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public T poll() {
        if (queue.isEmpty()) {
            return null; // 队列为空，立即返回null
        }
        return queue.removeFirst();
    }

    public boolean contains(T element) {
        return queue.contains(element);
    }

    public void clear() {
        queue.clear();
    }
}

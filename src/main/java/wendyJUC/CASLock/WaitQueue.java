package wendyJUC.CASLock;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WaitQueue {
    //noi
    private Node head;
    private Node tail;
    private final CASLock linkCASLock;
    private final ConcurrentHashMap<Thread, Boolean> threadIsSet;

    @Override
    public String toString() {
        return "WaitQueue{" +
                "head=" + head +
                '}';
    }

    private static class Node {
        Thread thread;
        Node next;

        @Override
        public String toString() {
            return "Node{" +
                    "thread=" + thread +
                    ", next=" + next +
                    '}';
        }
    }

    public WaitQueue(ConcurrentHashMap<Thread, Boolean> threadIsSet) {
        this.threadIsSet = threadIsSet;
        head = new Node(); // Dummy head node
        tail = new Node(); // Initial dummy tail node
        head.next = tail;
        linkCASLock = new CASLock();
    }

    public void enqueue(Thread thread) {
        linkCASLock.lock();
        try {
            Node newNode = new Node();
            Node tmp = tail;
            tail.thread = thread;
            tail = newNode;
            tmp.next = tail;
            threadIsSet.put(thread, true);
        } finally {
            linkCASLock.unlock();
        }
    }

    public Thread dequeue() {
        if (head.next == tail) {
            if (linkCASLock.getState() == 0) {
                return null;
            } else {
                while (linkCASLock.getState() == 1) {
                    if (head.next != tail) {
                        break;
                    }
                }
            }
        }
        linkCASLock.lock();
        try {
            Node first = head.next;
            if (first == tail) {
                return null; // Queue is empty
            }
            head.next = first.next;
            threadIsSet.put(first.thread, false);
            return first.thread;
        } finally {
            linkCASLock.unlock();
        }
    }

    public boolean isEmpty() {
        return (head.next == tail && linkCASLock.getState() == 0);
    }

}

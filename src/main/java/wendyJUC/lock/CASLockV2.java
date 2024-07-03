package wendyJUC.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;


public class CASLockV2 {

    private static class Node {
        Thread thread;
        Node next;

        public Node(Thread thread) {
            this.thread = thread;
        }

        public Node() {
        }

        @Override
        public String toString() {
            return "Node{" +
                    "thread=" + thread +
                    ", next=" + next +
                    '}';
        }
    }

    private static final Unsafe unsafe;
    private static final long valueOffset;
    private static final long valueLinkStateOffset;
    private volatile int state = 0;
    private volatile int linkState = 0;

    private Node head;
    private Node tail;

    static {
        try {
            Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            unsafe = (Unsafe) theUnsafeInstance.get(null);

            valueOffset = unsafe.objectFieldOffset(CASLockV2.class.getDeclaredField("state"));

            valueLinkStateOffset = unsafe.objectFieldOffset(CASLockV2.class.getDeclaredField("linkState"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public CASLockV2() {
        head = new Node();
        tail = new Node();
        head.next = tail;
    }


    public void lock() {
        while (!tryLock()) {
            // 将线程加入到等待队列
            Thread currentThread = Thread.currentThread();

            while (!unsafe.compareAndSwapInt(this, valueLinkStateOffset, 0, 1)) {
                Thread.yield();
            }
            try {
//                System.out.println("before Lock" + head);
                Node tmpNode = new Node();
                tail.thread = currentThread;
                tail.next = tmpNode;
                tail = tmpNode;
//                System.out.println("after Lock" + head);
                // 线程停止执行，等待被唤醒
            } finally {
                linkState = 0;
                unsafe.park(false, 0);
            }
        }
    }

    public boolean tryLock() {
        return unsafe.compareAndSwapInt(this, valueOffset, 0, 1);
    }

    public void unlock() {
        state = 0;


        while (!unsafe.compareAndSwapInt(this, valueLinkStateOffset, 0, 1)) {
            Thread.yield();
        }
        if (head.next == tail) {
//            System.out.println("节点无内容");
            linkState = 0;
            return;
        }


//        System.out.println("before Unlock" + head);

        Node tmpNode = head.next;
        head.next = tmpNode.next;
//        System.out.println("after Unlock" + head);

        linkState = 0;
        // 线程停止执行，等待被唤醒
        if (tmpNode.thread != null) {
            unsafe.unpark(tmpNode.thread);
        }
    }
}
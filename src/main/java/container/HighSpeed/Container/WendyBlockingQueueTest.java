package container.HighSpeed.Container;


public class WendyBlockingQueueTest {

    private static final int CAPACITY = 1; // 队列容量
    private static final int NUMBER_OF_PRODUCERS = 10; // 生产者数量
    private static final int NUMBER_OF_CONSUMERS = 10; // 消费者数量
    private static final int ITEMS_PER_PRODUCER = 5; // 每个生产者要插入的元素数量
    private static final int ITEMS_PER_CONSUMER = 5; // 每个消费者要读取的元素数量

    public static void main(String[] args) {
        final WendyBlockingQueue<Integer> queue = new WendyBlockingQueue<>(CAPACITY);

        // 创建并启动生产者线程
        for (int i = 0; i < NUMBER_OF_PRODUCERS; i++) {
            final int producerId = i;
            new Thread(() -> {
                for (int j = 0; j < ITEMS_PER_PRODUCER; j++) {
                    try {
                        queue.put(j);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }

        // 创建并启动消费者线程
        for (int i = 0; i < NUMBER_OF_CONSUMERS; i++) {
            final int consumerId = i;
            new Thread(() -> {
                for (int j = 0; j < ITEMS_PER_CONSUMER; j++) {
                    try {
                        Integer value = queue.take();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }
}

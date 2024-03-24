package container.HighSpeed.Container;

import lock.CASLock;
import container.HighSpeed.MyMap;

public class WendyHashMap<K, V> implements MyMap<K, V> {
    private static int DEFAULT_SIZE = 16;
    Entry[] table = null;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;


    private int threshold;

    private int fuzzySize;

    private CASLock[] locks;

    private CASLock fuzzyLock;

    private int[] singleSizes;

    int size = 0;

    public WendyHashMap(int mapSize) {
        fuzzyLock = new CASLock();
        table = new Entry[mapSize];
        singleSizes = new int[mapSize];
        threshold = (int) (table.length * DEFAULT_LOAD_FACTOR);
        locks = new CASLock[table.length];
        for (int i = 0; i < table.length; i++) {
            singleSizes[i] = 0;
            locks[i] = new CASLock();

        }

    }
    public WendyHashMap() {
        int mapSize =DEFAULT_SIZE;
        fuzzyLock = new CASLock();
        table = new Entry[mapSize];
        singleSizes = new int[mapSize];
        threshold = (int) (table.length * DEFAULT_LOAD_FACTOR);
        locks = new CASLock[table.length];
        for (int i = 0; i < table.length; i++) {
            singleSizes[i] = 0;
            locks[i] = new CASLock();

        }

    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < table.length; i++) {
            Entry<K, V> entry = table[i];
            // 为每个索引添加标签
            sb.append("index").append(i).append("-");
            if (entry != null) {
                sb.append("{");
                // 遍历链表
                while (entry != null) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                    entry = entry.next;
                    if (entry != null) {
                        sb.append("}->{"); // 用于分隔同一索引下的不同节点
                    }
                }
                sb.append("}"); // 链表结束
            } else {
                sb.append("null"); // 该索引下没有节点
            }
            if (i < table.length - 1) {
                sb.append(",\n"); // 为了清晰显示，每个索引后换行
            }
        }

        return sb.toString();
    }

    public String toStringV0() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean firstEntry = true; // 用于控制逗号的添加，第一个条目前不加逗号
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> entry = table[i];
            while (entry != null) {
                if (!firstEntry) {
                    sb.append(", ");
                } else {
                    firstEntry = false;
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                entry = entry.next;
            }
        }

        sb.append("}");
        return sb.toString();
    }


    @Override
    public int size() {
        for (int i = 0; i < table.length; i++) {
            size = size + singleSizes[i];
        }
        return size;
    }


    /**
     * 算出hashcode, 取模得到下标，然后通过下标拿到对象。
     * 如果为空那么可以直接赋值。
     * 如果不为空，那么使用链表进行存储
     *
     * @param k
     * @param v
     * @return
     */
    @Override
    public V put(K k, V v) {
        fuzzyLock.lock();
        try {
            if (fuzzySize >= threshold) { // threshold 是扩容的阈值，需要你定义
                resize();
            }
        } finally {
            fuzzyLock.unlock();
        }
//        synchronized (resizeLock) {
//            if (fuzzySize >= threshold) {
//                resize();
//            }
//        }
//        synchronized (this) {
//            if (fuzzySize >= threshold) { // threshold 是扩容的阈值，需要你定义
//                resize();
//            }
//        }


        int index = hash(k, table.length);
        locks[index].lock();
        int hash = k.hashCode();

//        System.out.println(index + "得到了锁, Thread ID: " + Thread.currentThread().getId());
        try {
            Entry<K, V> entry = table[index];
            if (null == entry) {
                table[index] = new Entry<>(k, v, null, hash);
                singleSizes[index]++;
                fuzzySize++;
            } else {
                Entry<K, V> previous = null;
                Entry<K, V> current = entry;
                while (current != null) {
//                    以下可以注释掉，以测试hash碰撞
                    if ((k == current.getKey() || k.equals(current.getKey())) && hash == current.hash) {
                        V oldValue = current.getValue();
                        current.v = v; // 更新值
                        return oldValue;
                    }
                    previous = current;
                    current = current.next;
                }
                previous.next = new Entry<>(k, v, null, hash); // 添加到链表末尾
                singleSizes[index]++;
                fuzzySize++;
            }
        } finally {
            locks[index].unlock();
//            System.out.println(index + "释放了锁, Thread ID: " + Thread.currentThread().getId());

        }

        return null;
    }

    private int hash(K k, int length) {
//        System.out.println(k.toString() + "的hash为" + k.hashCode());
        //-1458027206 -1429398055 -1400768904 -1486656357 -1515285508
        return (k.hashCode() & 0x7FFFFFFF) % length;
    }

    private void resize() {


        for (CASLock lock : locks) {
            lock.lock();
        }
        CASLock[] newLocks = new CASLock[table.length * 2];
        for (int i = 0; i < table.length * 2; i++) {
            newLocks[i] = new CASLock();
        }

        try {
            Entry[] newTable = new Entry[table.length * 2];
            int[] newSingleSizes = new int[table.length * 2];
            int newfuzzySize = 0;


//            以下为不重散列版本
//             重新散列所有现有的条目到新的数组中
//            for (int i = 0; i < table.length; i++) {
//                newTable[i] = table[i];
//                newfuzzySize = singleSizes[i] + newfuzzySize;
//                newSingleSizes[i] = singleSizes[i];
//
//            }


            for (int i = 0; i < table.length; i++) {
                Entry<K, V> entry = table[i];
                while (entry != null) {
                    Entry<K, V> next = entry.next; // 保存下一个条目的引用
                    int index = hash(entry.getKey(), newTable.length); // 在新表中的位置
                    entry.next = newTable[index]; // 插入到新表
                    newTable[index] = entry;
                    newSingleSizes[index]++;
                    newfuzzySize++;
                    entry = next; // 处理链表中的下一个条目
                }
            }


            table = newTable;
            fuzzySize = newfuzzySize;
            singleSizes = newSingleSizes;
            threshold = (int) (table.length * DEFAULT_LOAD_FACTOR);

        } finally {
            for (CASLock lock : locks) {
                lock.unlock();
            }
            locks = newLocks;
        }
    }


    /**
     * 1. 通过key 进行hash 计算得到index
     * 2. 根据index  判断是否为空，如果为空就直接返回null。
     * 3. 如果不为空，有查询的key与当前key 进行比较,如果当前节点的next是否为空，那么就返回当前节点，如果为不为空那么就取遍历next,直到相等为止。
     * 4. 如果相等就直接返回数据。
     *
     * @param k
     * @return
     */
    @Override
    public V get(K k) {
        int index = hash(k, table.length);
        int hash = k.hashCode();
        Entry<K, V> entry = table[index];
        if (null == entry) {
            return null;

        } else {
            if (entry.getKey() == k && hash == entry.hash) {
                return entry.getValue();
            }
            Entry<K, V> next = entry.next;
            while (null != next) {
                if (next.getKey() == k && hash == next.hash) {
                    return next.getValue();
                }
                next = next.next;
            }
        }
        return null;
    }

    @Override
    public V remove(K k) {
        int index = hash(k, table.length);
        locks[index].lock(); // 根据index锁定对应段
        try {
            Entry<K, V> entry = table[index];
            if (entry == null) {
                return null;
            } else {
                if ((k == entry.getKey() || k.equals(entry.getKey())) && entry.hash == k.hashCode()) {
                    // 直接移除该元素
                    V oldValue = entry.getValue();
                    table[index] = entry.next; // 将头节点指向下一个节点
                    singleSizes[index]--;
                    fuzzySize--; // 更新总大小
                    return oldValue;
                }
                Entry<K, V> previous = null;
                while (entry != null) {
                    if ((k == entry.getKey() || k.equals(entry.getKey())) && entry.hash == k.hashCode()) {
                        if (previous != null) {
                            previous.next = entry.next; // 移除当前节点
                        }
                        singleSizes[index]--;
                        fuzzySize--; // 更新总大小
                        return entry.getValue();
                    }
                    previous = entry;
                    entry = entry.next;
                }
            }
        } finally {
            locks[index].unlock(); // 释放锁
        }
        return null;
    }

     V getOrDefault(K key, V defaultValue) {
        V v;
        return (((v = get(key)) != null))
                ? v
                : defaultValue;
    }


    class Entry<K, V> implements MyMap.Entry<K, V> {

        int hash;
        K k;
        V v;
        Entry<K, V> next;


        public Entry(K k, V v, Entry<K, V> next, int hash) {
            this.k = k;
            this.v = v;
            this.hash = hash;
            this.next = next;
        }

        @Override
        public K getKey() {
            return k;
        }

        @Override
        public V getValue() {
            return v;
        }


    }
}

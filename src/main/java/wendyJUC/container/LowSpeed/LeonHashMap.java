package wendyJUC.container.LowSpeed;

import wendyJUC.container.HighSpeed.MyMap;

public class LeonHashMap<K, V> implements MyMap<K, V> {

    Entry[] table = null;
    private static final float DEFAULT_LOAD_FACTOR = 0.9f;
    private int threshold;

    int size = 0;

    public LeonHashMap() {
        table = new Entry[16];
        threshold = (int) (table.length * DEFAULT_LOAD_FACTOR);

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

        if (size >= threshold) { // threshold 是扩容的阈值，需要你定义
            resize();
        }


        int index = hash(k, table.length);
        int hash = k.hashCode();

        Entry<K, V> entry = table[index];
        if (null == entry) {
            table[index] = new Entry<>(k, v, null, hash);
            size++;
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
            size++;
        }


        return null;
    }

    private int hash(K k, int length) {
//        System.out.println(k.toString() + "的hash为" + k.hashCode());
        //-1458027206 -1429398055 -1400768904 -1486656357 -1515285508
        return (k.hashCode() & 0x7FFFFFFF) % length;
    }

    private void resize() {

        Entry[] newTable = new Entry[table.length * 2];
        // 重新散列所有现有的条目到新的数组中
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> entry = table[i];
            while (entry != null) {
                Entry<K, V> next = entry.next; // 保存下一个条目的引用
                int index = hash(entry.getKey(), newTable.length); // 在新表中的位置
                entry.next = newTable[index]; // 插入到新表
                newTable[index] = entry;
                entry = next; // 处理链表中的下一个条目
            }
        }
        table = newTable;

        threshold = (int) (table.length * DEFAULT_LOAD_FACTOR);

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
        int hash = k.hashCode();
        Entry<K, V> entry = table[index];
        if (null == entry) {
            return null;
        } else {
            if (entry.getKey() == k && entry.hash == hash) {
                // 直接移除该元素
                entry = entry.next;
                table[index] = entry;
                return (V) table[index];
            }
            if (null != entry.next) {
                // 链表
                Entry<K, V> head = entry;
                Entry<K, V> p = head;
                Entry<K, V> next = head.next;
                do {
                    if (next.getKey() == k && next.hash == hash) {
                        // 删除该节点, 前一个节点的next 指向该节点的next
                        p.next = next.next;
                        break;
                    }
                    p = next;
                    next = next.next;
                } while (next != null);
            } else {
                // 数组
                table[index] = null;
            }
            return table[index] == null ? null : (V) table[index].getValue();
        }

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

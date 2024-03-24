package wendyJUC.container.LowSpeed;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LeonArrayList<T> implements Iterable<T> {
    private Object[] elements;
    private int size = 0;

    public LeonArrayList() {
        elements = new Object[16]; // 初始容量设为10
    }

    // 确保容量
    private void ensureCapacity() {
        if (size == elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
    }

    // 添加元素
    public void add(T element) {
        ensureCapacity();
        elements[size++] = element;
    }

    // 获取元素
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T)elements[index];
    }

    // 删除元素
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index+1, elements, index, numMoved);
        }
        elements[--size] = null; // 清除末尾的引用
    }

    // 返回迭代器实现
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) elements[currentIndex++];
            }
        };
    }

    // 获取大小
    public int size() {
        return size;
    }
}

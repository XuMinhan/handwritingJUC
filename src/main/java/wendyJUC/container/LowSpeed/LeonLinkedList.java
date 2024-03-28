package wendyJUC.container.LowSpeed;

import java.util.NoSuchElementException;

public class LeonLinkedList<T> {
    private Node<T> head; // 链表头节点
    private int size = 0; // 链表大小

    private static class Node<T> {
        T item;
        Node<T> next;

        Node(T element, Node<T> next) {
            this.item = element;
            this.next = next;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("Cannot remove from an empty list.");
        }
        T element = head.item;
        head = head.next;
        size--;
        return element;
    }

    public void addLast(T element) {
        if (head == null) {
            head = new Node<>(element, null);
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = new Node<>(element, null);
        }
        size++;
    }

    public void add(T element) {
        this.add(element);
    }

    public T get(int index) {
        checkElementIndex(index);
        Node<T> current = node(index);
        return current.item;
    }

    public boolean remove(T element) {
        if (head == null) return false; // 如果列表为空，则直接返回false

        Node<T> prev = null;
        Node<T> current = head;

        // 检查头节点是否是要删除的节点
        if (element.equals(current.item)) {
            head = head.next;
            size--;
            return true;
        }

        // 遍历查找元素
        while (current != null && !element.equals(current.item)) {
            prev = current;
            current = current.next;
        }

        // 如果元素未找到
        if (current == null) {
            return false;
        }

        // 如果元素找到，从链表中移除它
        prev.next = current.next;
        size--;
        return true;
    }

    public boolean contains(T element) {
        Node<T> current = head;
        while (current != null) {
            if (element == null) {
                if (current.item == null) {
                    return true;
                }
            } else if (element.equals(current.item)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }


    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
    public void clear() {
        head = null; // 将头节点设置为null，从而清空链表
        size = 0; // 将链表大小设置为0
    }

    private Node<T> node(int index) {
        Node<T> x = head;
        for (int i = 0; i < index; i++) {
            x = x.next;
        }
        return x;
    }

    public int size() {
        return size;
    }

    // 示例：打印链表中的所有元素
    public void printList() {
        Node<T> current = head;
        while (current != null) {
            System.out.print(current.item + " ");
            current = current.next;
        }
    }

}

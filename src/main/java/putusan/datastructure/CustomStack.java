package id.umm.putusan.datastructure;

/**
 * Struktur data sekunder untuk audit log.
 */
public class CustomStack<T> implements StrukturData<T> {
    private static class Node<E> {
        E value;
        Node<E> next;
        Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    @Override
    public void add(T value) {
        push(value);
    }

    public void push(T value) {
        top = new Node<>(value, top);
        size++;
        validateInvariant();
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        Node<T> current = top;
        for (int i = 0; i < index; i++) current = current.next;
        return current.value;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        if (index == 0) return pop();

        Node<T> current = top;
        for (int i = 0; i < index - 1; i++) current = current.next;
        T result = current.next.value;
        current.next = current.next.next;
        size--;
        validateInvariant();
        return result;
    }

    public T pop() {
        if (isEmpty()) throw new IllegalStateException("Stack kosong.");
        T result = top.value;
        top = top.next;
        size--;
        validateInvariant();
        return result;
    }

    public T peek() {
        if (isEmpty()) throw new IllegalStateException("Stack kosong.");
        return top.value;
    }

    public int search(T target) {
        Node<T> current = top;
        int index = 0;
        while (current != null) {
            if (target == null ? current.value == null : target.equals(current.value)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    @Override
    public boolean contains(T target) {
        return search(target) >= 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        top = null;
        size = 0;
        validateInvariant();
    }

    @Override
    public void validateInvariant() {
        int count = 0;
        Node<T> slow = top;
        Node<T> fast = top;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                throw new IllegalStateException("Invariant rusak: cycle terdeteksi.");
            }
        }
        Node<T> current = top;
        while (current != null) {
            count++;
            current = current.next;
        }
        if (count != size) {
            throw new IllegalStateException("Invariant rusak: size tidak cocok.");
        }
    }
}

package id.umm.putusan.datastructure;

/**
 * Dynamic Array generik buatan sendiri.
 * Tidak menggunakan java.util collection.
 */
public class DynamicArray<T> implements StrukturData<T> {
    private Object[] data;
    private int size;

    public DynamicArray() {
        this(10);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Kapasitas awal minimal 1.");
        }
        data = new Object[initialCapacity];
    }

    @Override
    public void add(T value) {
        ensureCapacity(size + 1);
        data[size++] = value;
        validateInvariant();
    }

    public void insert(int index, T value) {
        checkPositionForInsert(index);
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = value;
        size++;
        validateInvariant();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    public void set(int index, T value) {
        checkIndex(index);
        data[index] = value;
        validateInvariant();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = null;
        validateInvariant();
        return removed;
    }

    public int search(T target) {
        for (int i = 0; i < size; i++) {
            if (target == null ? data[i] == null : target.equals(data[i])) {
                return i;
            }
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
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
        validateInvariant();
    }

    public int capacity() {
        return data.length;
    }

    private void ensureCapacity(int required) {
        if (required <= data.length) return;
        int newCapacity = data.length * 2;
        while (newCapacity < required) newCapacity *= 2;
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }

    private void checkPositionForInsert(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index insert: " + index + ", size: " + size);
        }
    }

    @Override
    public void validateInvariant() {
        if (size < 0 || size > data.length) {
            throw new IllegalStateException("Invariant rusak: size di luar kapasitas.");
        }
        for (int i = size; i < data.length; i++) {
            if (data[i] != null) {
                throw new IllegalStateException("Invariant rusak: slot setelah size harus null.");
            }
        }
    }
}

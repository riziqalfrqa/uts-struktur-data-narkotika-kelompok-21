package id.umm.putusan.datastructure;

public interface StrukturData<T> {
    void add(T data);
    T get(int index);
    T remove(int index);
    int size();
    boolean isEmpty();
    boolean contains(T data);
    void clear();
    void validateInvariant();
}

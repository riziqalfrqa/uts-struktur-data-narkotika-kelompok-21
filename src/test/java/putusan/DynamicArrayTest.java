package putusan;

import putusan.datastructure.DynamicArray;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {
    @Test
    void kapasitasDanUkuran() {
        DynamicArray<Integer> a = new DynamicArray<>(2);
        for (int i = 0; i < 50; i++) a.add(i);
        assertEquals(50, a.size());
        assertEquals(0, a.get(0));
        assertEquals(49, a.get(49));
        a.validateInvariant();
    }

    @Test
    void boundaryDanManipulasi() {
        DynamicArray<String> a = new DynamicArray<>();
        a.add("A"); a.add("B"); a.add("C");
        assertEquals("A", a.remove(0));
        assertEquals("C", a.remove(a.size() - 1));
        a.insert(1, "X");
        assertEquals("X", a.get(1));
        a.validateInvariant();
    }

    @Test
    void tambahKembaliSetelahHapus() {
        DynamicArray<Integer> a = new DynamicArray<>();
        for (int i = 0; i < 50; i++) a.add(i);
        a.remove(0);
        a.remove(24);
        a.remove(a.size() - 1);
        a.add(100); a.add(101); a.add(102);
        assertEquals(50, a.size());
        a.validateInvariant();
    }

    @Test
    void errorIndex() {
        DynamicArray<Integer> a = new DynamicArray<>();
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(0));
    }
}

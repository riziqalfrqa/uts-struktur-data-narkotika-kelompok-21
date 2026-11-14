package putusan.service;

import putusan.datastructure.DynamicArray;
import putusan.model.Putusan;

public class PutusanRepository {
    private final DynamicArray<Putusan> data = new DynamicArray<>(16);

    public void add(Putusan p) { data.add(p); }

    public void insert(int index, Putusan p) { data.insert(index, p); }

    public Putusan get(int index) { return data.get(index); }

    public Putusan remove(int index) { return data.remove(index); }

    public int size() { return data.size(); }

    public boolean isEmpty() { return data.isEmpty(); }

    public void clear() { data.clear(); }

    public DynamicArray<Putusan> raw() { return data; }

    public SearchResult searchByCaseId(String key) {
        int steps = 0;
        for (int i = 0; i < data.size(); i++) {
            steps++;
            if (data.get(i).getCaseId().equalsIgnoreCase(key)) {
                return new SearchResult(data.get(i), steps);
            }
        }
        return new SearchResult(null, steps);
    }

    public SearchResult searchByNomorPerkara(String key) {
        int steps = 0;
        for (int i = 0; i < data.size(); i++) {
            steps++;
            if (data.get(i).getNomorPerkara().equalsIgnoreCase(key)) {
                return new SearchResult(data.get(i), steps);
            }
        }
        return new SearchResult(null, steps);
    }

    public record SearchResult(Putusan result, int steps) {}
}

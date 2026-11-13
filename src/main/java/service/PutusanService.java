package id.umm.putusan.service;

import id.umm.putusan.datastructure.DynamicArray;
import id.umm.putusan.model.Putusan;

public class PutusanService {
    private final PutusanRepository repository;

    public PutusanService(PutusanRepository repository) {
        this.repository = repository;
    }

    public DynamicArray<Putusan> filter(String jenis, double minBerat, double maxBerat,
                                        String pasal, int minVonis, int maxVonis) {
        DynamicArray<Putusan> result = new DynamicArray<>();
        for (int i = 0; i < repository.size(); i++) {
            Putusan p = repository.get(i);
            boolean okJenis = jenis == null || jenis.isBlank() ||
                    (p.getJenisNarkotika() != null && p.getJenisNarkotika().equalsIgnoreCase(jenis));
            boolean okBerat = p.getBeratBarangBuktiGram() >= minBerat &&
                    p.getBeratBarangBuktiGram() <= maxBerat;
            boolean okPasal = pasal == null || pasal.isBlank() ||
                    (p.getPasalTerbukti() != null && p.getPasalTerbukti().equalsIgnoreCase(pasal));
            boolean okVonis = p.getVonisBulan() >= minVonis && p.getVonisBulan() <= maxVonis;

            if (okJenis && okBerat && okPasal && okVonis) result.add(p);
        }
        return result;
    }

    public Stats statistics() {
        long totalDenda = 0;
        long totalVonis = 0;
        int count = repository.size();

        String[] names = new String[Math.max(1, count)];
        int[] frequencies = new int[Math.max(1, count)];
        int unique = 0;

        for (int i = 0; i < count; i++) {
            Putusan p = repository.get(i);
            totalDenda += p.getDendaRupiah();
            totalVonis += p.getVonisBulan();

            String jenis = p.getJenisNarkotika();
            if (jenis == null || jenis.isBlank()) continue;

            int found = -1;
            for (int j = 0; j < unique; j++) {
                if (names[j].equalsIgnoreCase(jenis)) {
                    found = j;
                    break;
                }
            }
            if (found >= 0) frequencies[found]++;
            else {
                names[unique] = jenis;
                frequencies[unique] = 1;
                unique++;
            }
        }

        return new Stats(count, count == 0 ? 0 : (double) totalVonis / count,
                totalDenda, names, frequencies, unique);
    }

    public DynamicArray<Putusan> sortByVonis(boolean ascending) {
        DynamicArray<Putusan> result = copyData();
        mergeSort(result, 0, result.size() - 1, ascending);
        return result;
    }

    private DynamicArray<Putusan> copyData() {
        DynamicArray<Putusan> copy = new DynamicArray<>(Math.max(1, repository.size()));
        for (int i = 0; i < repository.size(); i++) copy.add(repository.get(i));
        return copy;
    }

    private void mergeSort(DynamicArray<Putusan> a, int left, int right, boolean asc) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(a, left, mid, asc);
        mergeSort(a, mid + 1, right, asc);
        merge(a, left, mid, right, asc);
    }

    private void merge(DynamicArray<Putusan> a, int left, int mid, int right, boolean asc) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        Putusan[] l = new Putusan[n1];
        Putusan[] r = new Putusan[n2];

        for (int i = 0; i < n1; i++) l[i] = a.get(left + i);
        for (int j = 0; j < n2; j++) r[j] = a.get(mid + 1 + j);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            boolean takeLeft = asc
                    ? l[i].getVonisBulan() <= r[j].getVonisBulan()
                    : l[i].getVonisBulan() >= r[j].getVonisBulan();
            a.set(k++, takeLeft ? l[i++] : r[j++]);
        }
        while (i < n1) a.set(k++, l[i++]);
        while (j < n2) a.set(k++, r[j++]);
    }

    public record Stats(int totalPerkara, double rataRataVonis,
                        long totalDenda, String[] jenis, int[] frequency, int unique) {}
}

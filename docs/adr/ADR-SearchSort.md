ADR-002: Strategi Search dan Sort

 Search
Pencarian `caseId` dan `nomorPerkara` menggunakan linear search. Setiap iterasi dihitung dan ditampilkan agar efisiensi operasi dapat diamati.

 Sort
Data diurutkan berdasarkan `vonisBulan` menggunakan Merge Sort. Hasil sort ditempatkan pada Dynamic Array salinan sehingga urutan data utama tidak berubah.

 Kompleksitas
- Linear Search: O(n)
- Merge Sort: O(n log n)
- Dynamic Array insert/remove tengah: O(n)

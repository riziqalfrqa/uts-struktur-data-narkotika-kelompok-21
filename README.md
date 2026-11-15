Kelompok 21 Pengadilan Negeri Tegal
Anggota : 
  1. Habib Riziq Alfaruq (202510370110117)
  2. Surya Firdaus Putra (202510370110145)

Struktur yang dipilih : 
  DynamicArray<T> sebagai struktur penyimpanan utama
  CustomStack<T> sebagai struktur untuk audit log

CARA MENGGUNAKAN 
Pastikan File CSV harus berada di dalam folder /data
Menjalankan Program
- mvn -Dmaven.test.skip=true package
- java -cp target/classes putusan.app.Main


Fitur yang Tersedia 
1. Impor CSV
2. Tampil data
3. Tambah/Hapus Data
4. Search data
5. Statistik
6. Ekspor CSV

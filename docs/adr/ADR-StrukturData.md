 ADR-001: Pemilihan Struktur Data

 Keputusan
Struktur utama menggunakan Dynamic Array kustom generik `DynamicArray<T>`.
Struktur sekunder menggunakan Stack kustom `CustomStack<T>` untuk audit log.

 Alasan
Dynamic Array sesuai dengan kebutuhan PRD karena mendukung resize, add, insert, get, set, remove, dan search.
Stack cocok untuk audit log karena operasi terbaru dapat ditempatkan di posisi teratas sehingga riwayat terbaru mudah ditampilkan terlebih dahulu.

 Konsekuensi
Dynamic Array membutuhkan proses shifting saat insert/remove di tengah, sedangkan Stack memiliki akses indeks yang harus ditelusuri dari top. Keduanya tidak menggunakan collection bawaan Java sebagai penyimpanan inti.

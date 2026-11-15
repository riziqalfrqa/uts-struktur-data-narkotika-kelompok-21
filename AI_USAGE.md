Penggunaan AI sebagai alat bantu dalam proses pengembangan program dan menyelesaikan tugas UTS

Alfa
Tanggal Penggunaan 12 November 2026 
1. Untuk implementasi struktur data DynamicArray
  prompt yang digunakan
  "buat implmentasi struktur data dynamic array menggunakan java tanpa menggunakan arraylist atau linkedlist. gunakan generic<T> dan tambahkan juga add, get, remove, size, isEmpty, contains, clear dan validateInvariant."
  output  yang diberikan : contoh implementasi `StrukturData<T> dan DynamicArray<T>`
  penggunaan dalam project :
  - StrukturData.java
  - DynamicArray.java

2. Membuat Custom Stack
   prompt yang digunakan
     "buatkan custom stcak generic di java menggunaakn struktur data sendiri dan tanpa menggunakan java.util.stack."
   output yang diberikan : memberikan implementasi Stack dengan operasi push, pop, peek, size, dan isEmpty.
   penggunaan dalam project :
   - CustomStack<T>

3. Penggunaan AI utnuk model putusan
   prompt yang digunakan
   "buatkan class java putusan untuk menyimpan data putusan pengadilan narkotika berdasarkan atribut yang sudah ditntukan"
   Output yang diberikan : memberikan struktur class dengan atribut sesuai yang di modul seperti nomor perkara, pengadilan, tanggal putusan, jenis narkotika, berat barang bukti, pasal, tuntunan, vonis, denda, dan data lainnya"
   Penggunaan dalam project :
   - Putusan.java

4. Untuk CSV converter
   prompt yang digunakan :
   "buatkan class java untuk membaca data putusan dari file CSV dan mengubah setiap baris menjadi object putusan, kemudian masukkan ke repository"
   outpur yang diberikan : memberikan contoh alur pembacaan file csv, parsing setiap kolom, pembuatan object putusan, dan penyimpanan data ke repository"
   penggunaan dalam project :
   - CSVImporter.java


5. Penggunaan AI untuk Filter, Statistik, dan Sorting
  Prompt:
"Buatkan service Java untuk melakukan filter data putusan berdasarkan jenis narkotika, berat barang bukti, pasal terbukti, dan rentang vonis. Tambahkan statistik dan sorting berdasarkan lama vonis tanpa mengubah urutan data asli."

  Output:
AI memberikan contoh implementasi beberapa operasi pengolahan data, seperti filter, perhitungan rata-rata vonis, total denda, frekuensi jenis narkotika, dan       sorting.
Penggunaan dalam Project
Konsep tersebut digunakan sebagai referensi untuk implementasi fitur pada PutusanService.java.

Memeriksa Implementasi lagi agar sorting tidak mengubah data utama dan tetap menggunakan struktur data yang telah dibuat.

6. Penggunaan AI untuk Audit Log dan Export
  Prompt
  "Buatkan fitur Java untuk mencatat aktivitas pengguna menggunakan CustomStack dan melakukan export data putusan ke file CSV atau laporan."

  Output
  AI memberikan contoh implementasi pencatatan aktivitas dan penulisan data ke file menggunakan Java I/O.
  Penggunaan dalam Project
  Konsep tersebut digunakan dalam:
  - AuditLogger.java
  - ReportExporter.java
  Pada proses pengerjaan, AI juga digunakan untuk membantu mencari dan memperbaiki error pada kode ReportExporter.java.

 7. Penggunaan AI untuk Debugging Maven
  Prompt
  "Maven mengalami compile error pada ReportExporter.java. Jelaskan penyebab error dan berikan versi kode yang benar."

  Output
  AI membantu mengidentifikasi masalah pada penulisan string dan escaping karakter pada kode Java.
  
   
note : setiap yang diberikan oleh AI/LLM yang digunakan, sudah saya cek dan periksa kembali tanpa diterima mentah' ke dalam project khususnya yang sudah di push ke github
LLM yang digunakan Gemini dan ChatGPT

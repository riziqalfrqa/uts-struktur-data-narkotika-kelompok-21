package putusan.app;

import putusan.datastructure.DynamicArray;
import putusan.model.Putusan;
import putusan.service.*;

import java.nio.file.Path;
import java.util.Scanner;
import javax.swing.SwingUtilities;

public class Main {
    private final PutusanRepository repository = new PutusanRepository();
    private final PutusanService service = new PutusanService(repository);
    private final AuditLogger audit = new AuditLogger();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0 && "--cli".equals(args[0])) {
            new Main().run();
            return;
        }
        PutusanFrame.applyLookAndFeel();
        SwingUtilities.invokeLater(() -> new PutusanFrame().setVisible(true));
    }

    private void run() {
        System.out.println("=== SISTEM MANAJEMEN DAN ANALISIS PUTUSAN NARKOTIKA ===");
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> importData();
                    case "2" -> tampilData(repository.raw());
                    case "3" -> tambahData();
                    case "4" -> hapusData();
                    case "5" -> cariData();
                    case "6" -> filterData();
                    case "7" -> statistik();
                    case "8" -> sorting();
                    case "9" -> audit.printHistory();
                    case "10" -> export();
                    case "0" -> running = false;
                    default -> System.out.println("Menu tidak tersedia.");
                }
            } catch (Exception e) {
                System.out.println("Operasi gagal: " + e.getMessage());
            }
        }
        System.out.println("Program selesai.");
    }

    private void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Impor CSV");
        System.out.println("2. Tampilkan semua data");
        System.out.println("3. Tambah data");
        System.out.println("4. Hapus data");
        System.out.println("5. Cari caseId / nomor perkara");
        System.out.println("6. Filter");
        System.out.println("7. Statistik");
        System.out.println("8. Sort berdasarkan vonis");
        System.out.println("9. Audit log");
        System.out.println("10. Ekspor CSV");
        System.out.println("0. Keluar");
        System.out.print("Pilih: ");
    }

    private void importData() {
        System.out.print("Path CSV [data/putusan-50.csv]: ");
        String input = scanner.nextLine().trim();
        Path path = Path.of(input.isBlank() ? "data/putusan-50.csv" : input);

        repository.clear();
        try {
            int accepted = new CsvImporter().importFile(path, repository);
            audit.log("IMPORT CSV: " + path + " | diterima=" + accepted);
            System.out.println("Record berhasil dimuat: " + accepted);
            if (accepted != 50) {
                System.out.println("PERINGATAN: PRD mensyaratkan tepat 50 record valid.");
            }
        } catch (Exception e) {
            repository.clear();
            throw new IllegalStateException("CSV gagal dimuat: " + e.getMessage(), e);
        }
    }

    private void tampilData(DynamicArray<Putusan> data) {
        if (data.isEmpty()) {
            System.out.println("Tidak ada data.");
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, data.get(i));
        }
    }

    private void tambahData() {
        Putusan p = inputPutusan();
        repository.add(p);
        audit.log("ADD " + p.getCaseId());
        System.out.println("Data ditambahkan.");
    }

    private Putusan inputPutusan() {
        String caseId = ask("caseId");
        String nomor = ask("nomorPerkara");
        String pengadilan = ask("pengadilan");
        String tanggal = ask("tanggalPutusan YYYY-MM-DD");
        String terdakwa = askOptional("terdakwaKode");
        String jenis = askOptional("jenisNarkotika");
        double berat = Double.parseDouble(ask("beratBarangBuktiGram"));
        String dakwaan = askOptional("pasalDakwaan");
        String terbukti = askOptional("pasalTerbukti");
        int tuntutan = Integer.parseInt(ask("tuntutanBulan"));
        int vonis = Integer.parseInt(ask("vonisBulan"));
        long denda = Long.parseLong(ask("dendaRupiah"));
        String barang = askOptional("barangBukti");
        String memberatkan = askOptional("keadaanMemberatkan");
        String meringankan = askOptional("keadaanMeringankan");
        String source = askOptional("sourcePages");

        return new Putusan(caseId, nomor, pengadilan, java.time.LocalDate.parse(tanggal),
                terdakwa, jenis, berat, dakwaan, terbukti, tuntutan, vonis, denda,
                barang, memberatkan, meringankan, source);
    }

    private void hapusData() {
        if (repository.isEmpty()) {
            System.out.println("Data kosong.");
            return;
        }
        int index = Integer.parseInt(ask("index yang dihapus (1-" + repository.size() + ")")) - 1;
        Putusan removed = repository.remove(index);
        audit.log("REMOVE index=" + index + " caseId=" + removed.getCaseId());
        System.out.println("Dihapus: " + removed);
    }

    private void cariData() {
        System.out.print("Cari berdasarkan 1=caseId, 2=nomorPerkara: ");
        String mode = scanner.nextLine().trim();
        String key = ask("kata kunci");
        PutusanRepository.SearchResult result =
                mode.equals("1") ? repository.searchByCaseId(key) : repository.searchByNomorPerkara(key);

        if (result.result() == null) System.out.println("Data tidak ditemukan.");
        else System.out.println("Ditemukan: " + result.result());
        System.out.println("Jumlah langkah/iterasi: " + result.steps());
        audit.log("SEARCH mode=" + mode + " key=" + key + " steps=" + result.steps());
    }

    private void filterData() {
        String jenis = askOptional("jenisNarkotika (kosong = semua)");
        double minBerat = Double.parseDouble(ask("min berat gram"));
        double maxBerat = Double.parseDouble(ask("max berat gram"));
        String pasal = askOptional("pasalTerbukti (kosong = semua)");
        int minVonis = Integer.parseInt(ask("min vonis bulan"));
        int maxVonis = Integer.parseInt(ask("max vonis bulan"));

        DynamicArray<Putusan> result =
                service.filter(jenis, minBerat, maxBerat, pasal, minVonis, maxVonis);
        tampilData(result);
        audit.log("FILTER result=" + result.size());
    }

    private void statistik() {
        PutusanService.Stats s = service.statistics();
        System.out.println("\n=== STATISTIK ===");
        System.out.println("Total perkara aktif : " + s.totalPerkara());
        System.out.printf("Rata-rata vonis     : %.2f bulan%n", s.rataRataVonis());
        System.out.println("Total akumulasi denda: Rp" + s.totalDenda());
        System.out.println("Distribusi jenis narkotika:");
        for (int i = 0; i < s.unique(); i++) {
            System.out.println("- " + s.jenis()[i] + ": " + s.frequency()[i]);
        }
        audit.log("STATISTICS");
    }

    private void sorting() {
        System.out.print("1=ascending, 2=descending: ");
        boolean asc = scanner.nextLine().trim().equals("1");
        DynamicArray<Putusan> sorted = service.sortByVonis(asc);
        tampilData(sorted);
        audit.log("SORT vonis " + (asc ? "ASC" : "DESC"));
    }

    private void export() {
        System.out.print("Path output [docs/demo/hasil.csv]: ");
        String input = scanner.nextLine().trim();
        Path path = Path.of(input.isBlank() ? "docs/demo/hasil.csv" : input);
        try {
            ReportExporter.exportPutusan(path, repository.raw());
            audit.log("EXPORT " + path);
            System.out.println("Ekspor berhasil: " + path);
        } catch (Exception e) {
            throw new IllegalStateException("Ekspor gagal: " + e.getMessage(), e);
        }
    }

    private String ask(String label) {
        System.out.print(label + ": ");
        String s = scanner.nextLine().trim();
        if (s.isBlank()) throw new IllegalArgumentException(label + " wajib diisi.");
        return s;
    }

    private String askOptional(String label) {
        System.out.print(label + ": ");
        String s = scanner.nextLine().trim();
        return s.isBlank() ? null : s;
    }
}

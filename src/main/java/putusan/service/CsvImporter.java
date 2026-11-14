package putusan.service;

import putusan.model.Putusan;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class CsvImporter {

    public int importFile(Path path, PutusanRepository repository) throws IOException {
        int accepted = 0;
        int lineNumber = 1;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String header = reader.readLine();
            lineNumber++;
            if (header == null) throw new IOException("CSV kosong.");

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    lineNumber++;
                    continue;
                }
                try {
                    Putusan p = parse(line);
                    repository.add(p);
                    accepted++;
                } catch (RuntimeException ex) {
                    System.err.println("Record ditolak pada baris " + lineNumber + ": " + ex.getMessage());
                }
                lineNumber++;
            }
        }
        return accepted;
    }

    private Putusan parse(String line) {
        String[] f = CsvUtil.parseLine(line);
        if (f.length != 16) {
            throw new IllegalArgumentException("jumlah kolom harus 16, ditemukan " + f.length);
        }

        return new Putusan(
            text(f[0]), text(f[1]), text(f[2]),
            date(f[3]), nullable(f[4]), nullable(f[5]),
            decimal(f[6]), nullable(f[7]), nullable(f[8]),
            integer(f[9]), integer(f[10]), money(f[11]),
            nullable(f[12]), nullable(f[13]), nullable(f[14]), nullable(f[15])
        );
    }

    private String text(String s) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException("field wajib tidak boleh kosong");
        return s.trim();
    }

    private String nullable(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private LocalDate date(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s.trim());
    }

    private double decimal(String s) {
        if (s == null || s.isBlank()) return 0;
        return Double.parseDouble(s.trim());
    }

    private int integer(String s) {
        if (s == null || s.isBlank()) return 0;
        return Integer.parseInt(s.trim());
    }

    private long money(String s) {
        if (s == null || s.isBlank()) return 0L;
        return Long.parseLong(s.trim());
    }
}

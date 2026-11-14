package putusan.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilitas CSV sederhana. Koleksi hanya dipakai sementara untuk parsing,
 * bukan sebagai struktur penyimpanan utama/sekunder.
 */
public final class CsvUtil {
    private CsvUtil() {}

    public static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}

package id.umm.putusan.service;

import id.umm.putusan.datastructure.DynamicArray;
import id.umm.putusan.model.Putusan;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ReportExporter {
    private ReportExporter() {}

    public static void exportPutusan(Path path, DynamicArray<Putusan> data) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            w.write("caseId,nomorPerkara,pengadilan,jenisNarkotika,beratGram,vonisBulan,dendaRupiah");
            w.newLine();
            for (int i = 0; i < data.size(); i++) {
                Putusan p = data.get(i);
                w.write(csv(p.getCaseId()) + "," + csv(p.getNomorPerkara()) + "," +
                        csv(p.getPengadilan()) + "," + csv(p.getJenisNarkotika()) + "," +
                        p.getBeratBarangBuktiGram() + "," + p.getVonisBulan() + "," +
                        p.getDendaRupiah());
                w.newLine();
            }
        }
    }

    private static String csv(String s) {
        if (s == null) return "";
        return """ + s.replace(""", """") + """;
    }
}

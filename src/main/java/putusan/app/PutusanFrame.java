package putusan.app;

import putusan.datastructure.DynamicArray;
import putusan.model.Putusan;
import putusan.service.AuditLogger;
import putusan.service.CsvImporter;
import putusan.service.PutusanRepository;
import putusan.service.PutusanService;
import putusan.service.ReportExporter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class PutusanFrame extends JFrame {
    private static final Color NAVY = new Color(24, 39, 67);
    private static final Color BLUE = new Color(43, 102, 210);
    private static final Color BACKGROUND = new Color(245, 247, 251);

    private final PutusanRepository repository = new PutusanRepository();
    private final PutusanService service = new PutusanService(repository);
    private final AuditLogger audit = new AuditLogger();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Case ID", "Nomor Perkara", "Pengadilan", "Tanggal", "Jenis Narkotika",
                    "Berat (g)", "Pasal Terbukti", "Vonis (bln)", "Denda (Rp)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 5 -> Double.class;
                    case 7 -> Integer.class;
                    case 8 -> Long.class;
                    default -> String.class;
                };
            }
    };
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
    private final JTextField searchField = new JTextField(16);
    private final JTextField quickFilterField = new JTextField(16);
    private final JComboBox<String> searchMode =
            new JComboBox<>(new String[]{"Case ID", "Nomor perkara"});
    private final List<Putusan> displayedData = new ArrayList<>();
    private final JLabel countValue = new JLabel("0");
    private final JLabel resultLabel = new JLabel("Semua data");

    public PutusanFrame() {
        super("Data Putusan Narkotika");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 620));
        setSize(1180, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        buildUi();
        loadDefaultData();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 28));
        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createContent(), BorderLayout.CENTER);
        setContentPane(root);

        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(235, 240, 248));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setRowSorter(sorter);
        table.setSelectionBackground(new Color(222, 234, 255));
        table.setSelectionForeground(NAVY);
        sorter.setSortsOnUpdates(true);

        searchField.setToolTipText("Cari case ID atau nomor perkara secara tepat");
        quickFilterField.setToolTipText("Saring tabel berdasarkan teks di semua kolom");
        quickFilterField.putClientProperty("JTextField.placeholderText", "Saring tabel...");
        quickFilterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyQuickFilter(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyQuickFilter(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyQuickFilter(); }
        });
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 18));
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Manajemen Putusan Narkotika");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(NAVY);
        JLabel subtitle = new JLabel("Kelola, cari, dan tinjau data putusan dengan lebih mudah");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(105, 117, 139));
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);
        titlePanel.add(text, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.NORTH);

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cards.setOpaque(false);
        cards.add(createCard("TOTAL PERKARA", countValue));
        header.add(cards, BorderLayout.CENTER);
        return header;
    }

    private JPanel createCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 241)),
                BorderFactory.createEmptyBorder(14, 17, 14, 17)));
        card.setPreferredSize(new Dimension(250, 78));
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(113, 125, 146));
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(NAVY);
        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout(0, 9));
        toolbar.setOpaque(false);
        JPanel actions = new JPanel(new GridLayout(1, 5, 8, 0));
        actions.setOpaque(false);
        actions.add(button("Impor CSV", BLUE, e -> importData()));
        actions.add(button("Tambah", new Color(37, 145, 105), e -> addData()));
        actions.add(button("Hapus", new Color(194, 70, 70), e -> removeData()));
        actions.add(button("Ekspor", NAVY, e -> exportData()));
        actions.add(button("Statistik", new Color(111, 84, 177), e -> showStatistics()));
        JPanel analysisActions = new JPanel(new GridLayout(1, 5, 8, 0));
        analysisActions.setOpaque(false);
        analysisActions.add(button("Filter", new Color(111, 84, 177), e -> filterData()));
        analysisActions.add(button("Urut vonis", new Color(65, 116, 164), e -> sortBySentence()));
        analysisActions.add(button("Tampilkan semua", new Color(83, 96, 117), e -> showAllData()));
        analysisActions.add(button("Detail", new Color(65, 116, 164), e -> showSelectedDetails()));
        analysisActions.add(button("Audit log", NAVY, e -> showAuditLog()));

        JPanel search = new JPanel(new GridLayout(1, 2, 16, 0));
        search.setOpaque(false);
        JPanel exactSearch = new JPanel(new BorderLayout(7, 0));
        exactSearch.setOpaque(false);
        exactSearch.add(searchMode, BorderLayout.WEST);
        exactSearch.add(searchField, BorderLayout.CENTER);
        exactSearch.add(button("Cari", BLUE, e -> searchData()), BorderLayout.EAST);
        JPanel quickSearch = new JPanel(new BorderLayout(7, 0));
        quickSearch.setOpaque(false);
        quickSearch.add(new JLabel("Saring tabel"), BorderLayout.WEST);
        quickSearch.add(quickFilterField, BorderLayout.CENTER);
        search.add(exactSearch);
        search.add(quickSearch);
        JPanel actionRows = new JPanel(new GridLayout(2, 1, 0, 8));
        actionRows.setOpaque(false);
        actionRows.add(actions);
        actionRows.add(analysisActions);
        toolbar.add(actionRows, BorderLayout.NORTH);
        toolbar.add(search, BorderLayout.SOUTH);
        content.add(toolbar, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 231, 239)));
        content.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        resultLabel.setForeground(new Color(113, 125, 146));
        JLabel tip = new JLabel("Klik judul kolom untuk mengurutkan");
        tip.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tip.setForeground(new Color(113, 125, 146));
        footer.add(resultLabel, BorderLayout.WEST);
        footer.add(tip, BorderLayout.EAST);
        content.add(footer, BorderLayout.SOUTH);
        return content;
    }

    private JButton button(String label, Color color, java.awt.event.ActionListener action) {
        JButton button = new JButton(label);
        button.setUI(new BasicButtonUI());
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(90, 38));
        button.addActionListener(action);
        return button;
    }

    private void loadDefaultData() {
        Path defaultPath = Path.of("data/putusan-50.csv");
        if (!Files.isRegularFile(defaultPath)) return;
        try {
            int loaded = importFile(defaultPath);
            audit.log("IMPORT CSV: " + defaultPath + " | diterima=" + loaded);
            refreshTable();
        } catch (Exception e) {
            showError("Data awal gagal dimuat: " + e.getMessage());
        }
    }

    private void importData() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih file CSV putusan");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("File CSV (*.csv)", "csv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        try {
            int loaded = importFile(path);
            audit.log("IMPORT CSV: " + path + " | diterima=" + loaded);
            refreshTable();
            JOptionPane.showMessageDialog(this, loaded + " data berhasil dimuat.",
                    "Impor berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError("Impor gagal: " + e.getMessage());
        }
    }

    private int importFile(Path path) throws Exception {
        repository.clear();
        try {
            return new CsvImporter().importFile(path, repository);
        } catch (Exception e) {
            repository.clear();
            refreshTable();
            throw e;
        }
    }

    private void addData() {
        JTextField caseId = new JTextField();
        JTextField nomor = new JTextField();
        JTextField pengadilan = new JTextField();
        JTextField tanggal = new JTextField(LocalDate.now().toString());
        JTextField terdakwa = new JTextField();
        JTextField jenis = new JTextField();
        JTextField berat = new JTextField("0");
        JTextField dakwaan = new JTextField();
        JTextField pasal = new JTextField();
        JTextField tuntutan = new JTextField("0");
        JTextField vonis = new JTextField("0");
        JTextField denda = new JTextField("0");
        JTextField barangBukti = new JTextField();
        JTextField memberatkan = new JTextField();
        JTextField meringankan = new JTextField();
        JTextField sourcePages = new JTextField();

        Object[][] fields = {
                {"Case ID *", caseId}, {"Nomor perkara *", nomor}, {"Pengadilan *", pengadilan},
                {"Tanggal (YYYY-MM-DD, opsional)", tanggal}, {"Kode terdakwa", terdakwa},
                {"Jenis narkotika", jenis}, {"Berat barang bukti (gram)", berat},
                {"Pasal dakwaan", dakwaan}, {"Pasal terbukti", pasal}, {"Tuntutan (bulan)", tuntutan},
                {"Vonis (bulan)", vonis}, {"Denda (rupiah)", denda},
                {"Barang bukti", barangBukti}, {"Keadaan memberatkan", memberatkan},
                {"Keadaan meringankan", meringankan}, {"Halaman sumber", sourcePages}
        };
        JPanel form = new JPanel(new GridLayout(fields.length, 2, 10, 8));
        for (Object[] field : fields) {
            form.add(new JLabel((String) field[0]));
            form.add((JTextField) field[1]);
        }
        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setPreferredSize(new Dimension(560, 440));
        if (JOptionPane.showConfirmDialog(this, formScroll, "Tambah data putusan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            Putusan p = new Putusan(caseId.getText(), nomor.getText(), pengadilan.getText(),
                    tanggal.getText().isBlank() ? null : LocalDate.parse(tanggal.getText().trim()),
                    emptyToNull(terdakwa.getText()), emptyToNull(jenis.getText()),
                    Double.parseDouble(berat.getText().trim()), emptyToNull(dakwaan.getText()),
                    emptyToNull(pasal.getText()),
                    Integer.parseInt(tuntutan.getText().trim()), Integer.parseInt(vonis.getText().trim()),
                    Long.parseLong(denda.getText().trim()), emptyToNull(barangBukti.getText()),
                    emptyToNull(memberatkan.getText()), emptyToNull(meringankan.getText()),
                    emptyToNull(sourcePages.getText()));
            repository.add(p);
            audit.log("ADD " + p.getCaseId());
            showAllData();
        } catch (RuntimeException e) {
            showError("Data tidak valid. Periksa tanggal dan nilai angka.\n" + e.getMessage());
        }
    }

    private void removeData() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu.",
                    "Belum ada pilihan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Hapus data yang dipilih?",
                "Konfirmasi hapus", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        int modelIndex = table.convertRowIndexToModel(selected);
        Putusan selectedData = displayedData.get(modelIndex);
        int repositoryIndex = -1;
        for (int i = 0; i < repository.size(); i++) {
            if (repository.get(i) == selectedData) {
                repositoryIndex = i;
                break;
            }
        }
        if (repositoryIndex < 0) {
            showError("Data yang dipilih tidak lagi tersedia.");
            return;
        }
        Putusan removed = repository.remove(repositoryIndex);
        audit.log("REMOVE index=" + repositoryIndex + " caseId=" + removed.getCaseId());
        showAllData();
    }

    private void exportData() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan data putusan");
        chooser.setSelectedFile(Path.of("hasil-putusan.csv").toFile());
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("File CSV (*.csv)", "csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        if (!path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            path = path.resolveSibling(path.getFileName() + ".csv");
        }
        try {
            ReportExporter.exportPutusan(path, repository.raw());
            audit.log("EXPORT " + path);
            JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke:\n" + path,
                    "Ekspor berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError("Ekspor gagal: " + e.getMessage());
        }
    }

    private void showStatistics() {
        PutusanService.Stats stats = service.statistics();
        StringBuilder message = new StringBuilder(String.format(Locale.getDefault(),
                "Total perkara aktif: %d%nRata-rata vonis: %.2f bulan%nTotal akumulasi denda: Rp%s%n%nDistribusi jenis narkotika:%n",
                stats.totalPerkara(), stats.rataRataVonis(), NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
                        .format(stats.totalDenda())));
        for (int i = 0; i < stats.unique(); i++) {
            message.append("- ").append(stats.jenis()[i]).append(": ")
                    .append(stats.frequency()[i]).append('\n');
        }
        audit.log("STATISTICS");
        JOptionPane.showMessageDialog(this, message.toString(), "Statistik putusan",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAuditLog() {
        if (audit.raw().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Audit log masih kosong.",
                    "Audit log", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder history = new StringBuilder();
        for (int i = 0; i < audit.raw().size(); i++) {
            history.append(i + 1).append(". ").append(audit.raw().get(i)).append('\n');
        }
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(history.toString(), 18, 60);
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Audit log (terbaru ke terlama)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchData() {
        String key = searchField.getText().trim();
        if (key.isEmpty()) {
            showError("Masukkan case ID atau nomor perkara yang ingin dicari.");
            return;
        }
        boolean byCaseId = searchMode.getSelectedIndex() == 0;
        PutusanRepository.SearchResult result = byCaseId
                ? repository.searchByCaseId(key)
                : repository.searchByNomorPerkara(key);
        DynamicArray<Putusan> matches = new DynamicArray<>();
        if (result.result() != null) matches.add(result.result());
        quickFilterField.setText("");
        sorter.setSortKeys(null);
        showData(matches, "Hasil pencarian: " + result.steps() + " langkah");
        audit.log("SEARCH mode=" + (byCaseId ? "caseId" : "nomorPerkara")
                + " key=" + key + " steps=" + result.steps());
        if (result.result() == null) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan.\nJumlah langkah: " + result.steps(),
                    "Hasil pencarian", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterData() {
        JTextField jenis = new JTextField();
        JTextField minBerat = new JTextField("0");
        JTextField maxBerat = new JTextField("1000000");
        JTextField pasal = new JTextField();
        JTextField minVonis = new JTextField("0");
        JTextField maxVonis = new JTextField("1200");
        Object[][] fields = {
                {"Jenis narkotika (kosong = semua)", jenis},
                {"Berat minimum (gram)", minBerat},
                {"Berat maksimum (gram)", maxBerat},
                {"Pasal terbukti (kosong = semua)", pasal},
                {"Vonis minimum (bulan)", minVonis},
                {"Vonis maksimum (bulan)", maxVonis}
        };
        JPanel form = new JPanel(new GridLayout(fields.length, 2, 10, 8));
        for (Object[] field : fields) {
            form.add(new JLabel((String) field[0]));
            form.add((JTextField) field[1]);
        }
        if (JOptionPane.showConfirmDialog(this, form, "Filter data putusan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            double minWeight = Double.parseDouble(minBerat.getText().trim());
            double maxWeight = Double.parseDouble(maxBerat.getText().trim());
            int minimumSentence = Integer.parseInt(minVonis.getText().trim());
            int maximumSentence = Integer.parseInt(maxVonis.getText().trim());
            if (minWeight > maxWeight || minimumSentence > maximumSentence) {
                throw new IllegalArgumentException("Nilai minimum tidak boleh melebihi nilai maksimum.");
            }
            DynamicArray<Putusan> filtered = service.filter(emptyToNull(jenis.getText()),
                    minWeight, maxWeight, emptyToNull(pasal.getText()),
                    minimumSentence, maximumSentence);
            quickFilterField.setText("");
            sorter.setSortKeys(null);
            showData(filtered, "Hasil filter: " + filtered.size() + " perkara");
            audit.log("FILTER result=" + filtered.size());
        } catch (RuntimeException e) {
            showError("Filter tidak valid. Periksa kembali nilai angka.\n" + e.getMessage());
        }
    }

    private void sortBySentence() {
        Object[] options = {"Terkecil ke terbesar", "Terbesar ke terkecil"};
        int selected = JOptionPane.showOptionDialog(this, "Pilih urutan vonis:",
                "Urutkan berdasarkan vonis", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (selected < 0) return;
        DynamicArray<Putusan> sorted = service.sortByVonis(selected == 0);
        quickFilterField.setText("");
        sorter.setSortKeys(null);
        showData(sorted, "Diurutkan berdasarkan vonis "
                + (selected == 0 ? "terkecil ke terbesar" : "terbesar ke terkecil"));
        audit.log("SORT vonis " + (selected == 0 ? "ASC" : "DESC"));
    }

    private void showAllData() {
        searchField.setText("");
        quickFilterField.setText("");
        sorter.setRowFilter(null);
        sorter.setSortKeys(null);
        refreshTable();
    }

    private void showSelectedDetails() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih satu perkara untuk melihat detail.",
                    "Belum ada pilihan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Putusan p = displayedData.get(table.convertRowIndexToModel(selected));
        String details = "Case ID: " + p.getCaseId()
                + "\nNomor perkara: " + p.getNomorPerkara()
                + "\nPengadilan: " + p.getPengadilan()
                + "\nTanggal putusan: " + value(p.getTanggalPutusan())
                + "\nKode terdakwa: " + value(p.getTerdakwaKode())
                + "\nJenis narkotika: " + value(p.getJenisNarkotika())
                + "\nBerat barang bukti: " + p.getBeratBarangBuktiGram() + " gram"
                + "\nPasal dakwaan: " + value(p.getPasalDakwaan())
                + "\nPasal terbukti: " + value(p.getPasalTerbukti())
                + "\nTuntutan: " + p.getTuntutanBulan() + " bulan"
                + "\nVonis: " + p.getVonisBulan() + " bulan"
                + "\nDenda: Rp" + p.getDendaRupiah()
                + "\nBarang bukti: " + value(p.getBarangBukti())
                + "\nKeadaan memberatkan: " + value(p.getKeadaanMemberatkan())
                + "\nKeadaan meringankan: " + value(p.getKeadaanMeringankan())
                + "\nHalaman sumber: " + value(p.getSourcePages());
        JTextArea textArea = new JTextArea(details, 18, 54);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Detail putusan", JOptionPane.INFORMATION_MESSAGE);
    }

    private String value(Object value) {
        return value == null ? "-" : value.toString();
    }

    private void refreshTable() {
        showData(repository.raw(), "Semua data");
    }

    private void showData(DynamicArray<Putusan> data, String description) {
        tableModel.setRowCount(0);
        displayedData.clear();
        for (int i = 0; i < data.size(); i++) {
            Putusan p = data.get(i);
            displayedData.add(p);
            tableModel.addRow(new Object[]{p.getCaseId(), p.getNomorPerkara(), p.getPengadilan(),
                    p.getTanggalPutusan() == null ? "" : p.getTanggalPutusan().toString(),
                    p.getJenisNarkotika() == null ? "" : p.getJenisNarkotika(),
                    p.getBeratBarangBuktiGram(), p.getPasalTerbukti() == null ? "" : p.getPasalTerbukti(),
                    p.getVonisBulan(), p.getDendaRupiah()});
        }
        resultLabel.setText(description);
        updateSummary();
        applyQuickFilter();
    }

    private void updateSummary() {
        PutusanService.Stats stats = service.statistics();
        countValue.setText(Integer.toString(stats.totalPerkara()));
    }

    private void applyQuickFilter() {
        String query = quickFilterField.getText().trim();
        sorter.setRowFilter(query.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
    }

    private String emptyToNull(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Terjadi kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException e) {
            System.err.println("Tampilan sistem tidak tersedia: " + e.getMessage());
        }
    }
}

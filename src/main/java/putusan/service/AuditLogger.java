package putusan.service;

import putusan.datastructure.CustomStack;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {
    private final CustomStack<String> stack = new CustomStack<>();
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void log(String action) {
        stack.push(LocalDateTime.now().format(FORMAT) + " | " + action);
    }

    public void printHistory() {
        if (stack.isEmpty()) {
            System.out.println("Audit log kosong.");
            return;
        }
        System.out.println("\n=== AUDIT LOG (terbaru -> terlama) ===");
        for (int i = 0; i < stack.size(); i++) {
            System.out.println((i + 1) + ". " + stack.get(i));
        }
    }

    public CustomStack<String> raw() {
        return stack;
    }
}

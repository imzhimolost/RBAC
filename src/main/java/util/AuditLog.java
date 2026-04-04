package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AuditLog {
    private static final AuditLog instance = new AuditLog();
    public static AuditLog getInstance() { return instance; }

    public record AuditEntry(
            String timestamp,
            String action,
            String performer,
            String target,
            String details
    ) {}

    private final BlockingQueue<AuditEntry> logQueue = new LinkedBlockingQueue<>();
    private final List<AuditEntry> entries = Collections.synchronizedList(new ArrayList<>());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditLog() {
        Thread worker = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    AuditEntry entry = logQueue.take();
                    entries.add(entry);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        worker.setDaemon(true);
        worker.start();
    }
    public void log(String action, String performer, String target, String details){
        String datetime = LocalDateTime.now().format(FORMATTER);
        AuditEntry entry = new AuditEntry(datetime, action, performer, target, details);

        try {
            logQueue.put(entry);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Ошибка добавления лога в очередь: " + e.getMessage());
        }
    }

    public List<AuditEntry> getAll(){
        return entries;
    }

    public List<AuditEntry> getByPerformer(String performer){
        return entries.stream().filter(auditEntry -> auditEntry.performer.equals(performer)).toList();
    }

    public List<AuditEntry> getByAction(String action){
        return entries.stream().filter(auditEntry -> auditEntry.action.equals(action)).toList();
    }

    public void printLog() {
        entries.forEach(auditEntry -> System.out.printf("[%s] %s -- BY %s ON %s; DETAILS: %s\n",
                auditEntry.timestamp, auditEntry.action, auditEntry.performer, auditEntry.timestamp, auditEntry.details));
    }

    public void saveToFile(String filename){
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (AuditEntry auditEntry : entries) {
                writer.printf("[%s] %s -- BY %s ON %s; DETAILS: %s\n",
                        auditEntry.timestamp(), auditEntry.action(), auditEntry.performer(), auditEntry.target(), auditEntry.details());
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения лога: " + e.getMessage());
        }
    }
}

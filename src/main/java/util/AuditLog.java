package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    private final List<AuditEntry> entries = new ArrayList<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void log(String action, String performer, String target, String details){
        String datetime = LocalDateTime.now().format(FORMATTER);
        entries.add(new AuditEntry(datetime, action, performer, target, details));
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

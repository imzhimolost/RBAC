package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AssignmentMetadata(String assignedBy, String assignedAt, String reason) {
    public static AssignmentMetadata now(String assignedBy, String reason){
        if(assignedBy == null || assignedBy.isBlank()) throw new IllegalArgumentException("Error: assignedBy required");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String currenttime = LocalDateTime.now().format(formatter);
        return new AssignmentMetadata(assignedBy, currenttime, reason);
    }

    public String format(){
        return String.format("Assigned by: %s, assigned at: %s, reason: %s", assignedBy, assignedAt, reason);
    }
}

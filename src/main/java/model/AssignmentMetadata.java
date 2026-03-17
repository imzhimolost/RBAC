package model;

import util.DateUtils;

public record AssignmentMetadata(String assignedBy, String assignedAt, String reason) {
    public static AssignmentMetadata now(String assignedBy, String reason){
        if(assignedBy == null || assignedBy.isBlank()) throw new IllegalArgumentException("Error: assignedBy required");
        return new AssignmentMetadata(assignedBy, DateUtils.getCurrentDateTime(), reason);
    }

    public String format(){
        return String.format("Assigned by: %s, assigned at: %s, reason: %s", assignedBy, assignedAt, reason);
    }
}

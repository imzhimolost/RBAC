package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TemporaryAssignment extends AbstractRoleAssignment{
    String expiresAt;
    boolean autoRenew;

    public TemporaryAssignment(User username, Role rolename, AssignmentMetadata metadataname, String expirationdate){
        super(username, rolename, metadataname);
        expiresAt = expirationdate;
        autoRenew = false;
    }

    public void extend(String newExpirationDate){
        expiresAt = newExpirationDate;
    }

    public boolean isExpired(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime dateexpire = LocalDateTime.parse(expiresAt, formatter);
        LocalDateTime datenow = LocalDateTime.now();
        return datenow.isAfter(dateexpire);
    }

    @Override
    public String summary(){
        return String.format("[%s] %s assigned to %s by %s at %s \nReason: %s \nStatus: %s\nExpires at: %s",
                assignmentType(), role.name, user.username(), metadata.assignedBy(), metadata.assignedAt(),
                metadata.reason(), isActive()? "ACTIVE" : "EXPIRED", expiresAt);
    }

    @Override
    public boolean isActive(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime dateexpire = LocalDateTime.parse(expiresAt, formatter);
        LocalDateTime datenow = LocalDateTime.now();
        return !datenow.isAfter(dateexpire);
    }

    @Override
    public String assignmentType(){
        return "TEMPORARY";
    }

}

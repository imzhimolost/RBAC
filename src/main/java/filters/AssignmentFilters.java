package filters;

import model.RoleAssignment;
import model.TemporaryAssignment;
import model.User;
import model.Role;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AssignmentFilters {
    public AssignmentFilter byUser(User user){
        return assignment -> assignment.user().equals(user);
    }

    public AssignmentFilter byUsername(String username){
        return assignment -> assignment.user().username().equals(username);
    }

    public AssignmentFilter byRole(Role role){
        return assignment -> assignment.role().equals(role);
    }

    public AssignmentFilter byRoleName(String roleName){
        return assignment -> assignment.role().getName().equals(roleName);
    }

    public AssignmentFilter activeOnly(){
        return RoleAssignment::isActive;
    }

    public AssignmentFilter inactiveOnly(){
        return assignment -> !assignment.isActive();
    }

    public AssignmentFilter byType(String type){
        return assignment -> assignment.assignmentType().equals(type);
    }

    public AssignmentFilter assignedBy(String username){
        return assignment -> assignment.metadata().assignedBy().equals(username);
    }

    public AssignmentFilter assignedAfter(String date){
        return assignment -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            LocalDateTime dateFilter = LocalDateTime.parse(date, formatter);
            LocalDateTime dateAssigned = LocalDateTime.parse(assignment.metadata().assignedAt(), formatter);

            return dateAssigned.isAfter(dateFilter);
        };
    }

    public AssignmentFilter expiringBefore(String date){
        return assignment -> {
            if (!(assignment instanceof TemporaryAssignment)) {
                return false;
            }

            TemporaryAssignment tempAssignment = (TemporaryAssignment) assignment;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            LocalDateTime dateExpire = LocalDateTime.parse(tempAssignment.getExpiresAt(), formatter);
            LocalDateTime dateFilter = LocalDateTime.parse(date, formatter);
            return dateExpire.isBefore(dateFilter);
        };
    }
}

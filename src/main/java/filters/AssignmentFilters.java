package filters;

import model.RoleAssignment;
import model.User;
import model.Role;

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
        return assignment -> assignment.metadata().assignedAt().compareTo(date) > 0;
    }

    public AssignmentFilter expiringBefore(String date){
        return assignment -> assignment.assignmentType().equals("TEMPORARY") && !assignment.isActive();
    }
}

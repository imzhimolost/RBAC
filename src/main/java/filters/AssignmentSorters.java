package filters;

import model.RoleAssignment;
import java.util.Comparator;

public class AssignmentSorters {
    public Comparator<RoleAssignment> byUsername(){
        return Comparator.comparing(assignment -> assignment.user().username());
    }

    public Comparator<RoleAssignment> byRoleName(){
        return Comparator.comparing(assignment -> assignment.role().getName());
    }

    public Comparator<RoleAssignment> byAssignmentDate(){
        return Comparator.comparing(assignment -> assignment.metadata().assignedAt());
    }
}

package model;

import java.util.UUID;

public abstract class AbstractRoleAssignment implements RoleAssignment {
    String assignmentId;
    User user;
    Role role;
    AssignmentMetadata metadata;

    public AbstractRoleAssignment( User username, Role rolename, AssignmentMetadata metadataname){
        assignmentId = UUID.randomUUID().toString();
        user = username;
        role = rolename;
        metadata = metadataname;
    }

    @Override
    public String assignmentId(){
        return assignmentId;
    }

    @Override
    public User user(){
        return user;
    }

    @Override
    public Role role(){
        return role;
    }

    @Override
    public AssignmentMetadata metadata(){
        return metadata;
    }

    @Override
    public abstract boolean isActive();

    @Override
    public abstract String assignmentType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleAssignment that)) return false;
        return assignmentId().equals(that.assignmentId());
    }

    @Override
    public int hashCode(){
        return assignmentId.hashCode();
    }

    public String summary(){
        return String.format("[%s] %s assigned to %s by %s at %s \nReason: %s \nStatus: %s",
                assignmentType(), role.name, user.username(), metadata.assignedBy(), metadata.assignedAt(),
                metadata.reason(), isActive()? "ACTIVE" : "EXPIRED");
    }

}

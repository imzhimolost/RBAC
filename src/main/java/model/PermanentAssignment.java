package model;

public class PermanentAssignment extends AbstractRoleAssignment{
    boolean revoked;

    public PermanentAssignment(User username, Role rolename, AssignmentMetadata metadataname){
        super(username, rolename, metadataname);
        revoked = false;
    }

    public void revoke(){
        revoked = true;
    }

    public boolean isRevoked(){
        return revoked;
    }

    @Override
    public boolean isActive(){
        return !revoked;
    }

    @Override
    public String assignmentType(){
        return "PERMANENT";
    }
}

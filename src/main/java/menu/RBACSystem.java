package menu;

import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import model.*;

public class RBACSystem {
    UserManager userManager;
    RoleManager roleManager;
    AssignmentManager assignmentManager;
    String currentUser;

    public UserManager getUserManager() {
        return userManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public AssignmentManager getAssignmentManager() {
        return assignmentManager;
    }

    public void setCurrentUser(String username){
        currentUser = username;
    }

    public String getCurrentUser(){
        return currentUser;
    }

    public void initialize(){
        Permission permissionRead = new Permission("READ", "settings", "able to read settings");
        Permission permissionWrite = new Permission("WRITE", "reports", "able to write reports");
        Permission permissionDelete = new Permission("DELETE", "users", "able to delete users");

        Role roleAdmin = new Role("Admin", "admin role");
        roleManager.add(roleAdmin);
        roleManager.addPermissionToRole("Admin", permissionDelete);
        roleManager.addPermissionToRole("Admin", permissionWrite);
        roleManager.addPermissionToRole("Admin", permissionRead);
        Role roleMan = new Role("Manager", "manager role");
        roleManager.add(roleMan);
        roleManager.addPermissionToRole("Manager", permissionWrite);
        roleManager.addPermissionToRole("Manager", permissionRead);
        Role roleViewer = new Role("Viewer", "viewer role");
        roleManager.add(roleViewer);
        roleManager.addPermissionToRole("Viewer", permissionWrite);

        User admin = User.create("admin", "John Ivan Smith", "admin@example.com");
        userManager.add(admin);
        AssignmentMetadata assignmentMetadataAdmin = AssignmentMetadata.now("System", "system initialization");
        PermanentAssignment assignmentAdmin = new PermanentAssignment(admin, roleAdmin, assignmentMetadataAdmin);
        assignmentManager.add(assignmentAdmin);
    }

    public String generateStatistics(){
        return "STATS:\nNumber of users: " + userManager.count() + "\nNumber of roles:" + roleManager.count() + "\nNumber of assignments: " + assignmentManager.count() + "\n";
    }
}

package menu;

import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import model.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RBACSystem {
    UserManager userManager;
    RoleManager roleManager;
    AssignmentManager assignmentManager;
    String currentUser;
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public RBACSystem() {
        this.userManager = new UserManager();
        this.roleManager = new RoleManager();
        this.assignmentManager = new AssignmentManager();
    }

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
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must be not null");
        }
        currentUser = username;
    }

    public String getCurrentUser(){
        return currentUser;
    }

    public void executeAsyncTask(Runnable task){
        executor.submit(task);
    }

    public void shutdown(){
        executor.shutdown();
        try{
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }catch (InterruptedException e){
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public ExecutorService getExecutor() {
        return executor;
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
        return "STATS:\nNumber of users: " + userManager.count() + "\nNumber of roles: " + roleManager.count() + "\nNumber of assignments: " + assignmentManager.count() + "\n";
    }
}

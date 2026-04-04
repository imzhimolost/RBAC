package util;

import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import model.Permission;
import model.Role;
import model.RoleAssignment;
import model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportGenerator {
    public String generateUserReport(UserManager userManager, AssignmentManager assignmentManager) {
        return userManager.findAll().parallelStream()
                .map(user -> {
                    List<RoleAssignment> roles = assignmentManager.findByUser(user);
                    String roleNames = roles.isEmpty() ? "No roles" :
                            roles.stream().map(a -> a.role().getName()).sorted().collect(Collectors.joining(", "));

                    return String.format("USER: %s; EMAIL: %S\n   ROLE: %s\n---------------------------", user.username(), user.email(), roleNames);
                })
                .collect(Collectors.joining("\n", "--------USER REPORT--------\n", ""));
    }

    public String generateRoleReport(RoleManager roleManager, AssignmentManager assignmentManager){
        StringBuilder sb = new StringBuilder("--------ROLE REPORT--------\n");
        for (Role r : roleManager.findAll()) {
            long count = assignmentManager.findByRole(r).size();
            sb.append(String.format("ROLE: %s | NUMBER OF USERS: %d\n", r.getName(), count));
            sb.append("---------------------------");
        }
        return sb.toString();
    }

    public String generatePermissionMatrix(UserManager userManager, AssignmentManager assignmentManager){
        return userManager.findAll().parallelStream()
                .map(user -> {
                    Set<Permission> perms = assignmentManager.getUserPermissions(user);
                    String resources = perms.stream().map(Permission::resource).distinct().sorted().collect(Collectors.joining(", "));
                    return String.format("USER: %s | RESOURCES: %s\n----------------------", user.username(), resources.isEmpty() ? "NO RESOURCES" : resources);
                })
                .collect(Collectors.joining("\n", "--------MATRIX--------\n", ""));
    }

    public void exportToFile(String report, String filename){
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(report);
            System.out.println("Report saved: " + filename);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

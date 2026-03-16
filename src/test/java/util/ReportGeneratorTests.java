package util;

import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;

    @BeforeEach
    void setUp() {
        reportGenerator = new ReportGenerator();
        userManager = new UserManager();
        roleManager = new RoleManager();
        assignmentManager = new AssignmentManager();
    }

    @Test
    void testGenerateUserReport() {
        User user = User.create("ivan_ivanov", "Ivan Ivanov", "ivan@mail.com");
        Role adminRole = new Role("ADMIN", "System Administrator");
        Role userRole = new Role("USER", "Regular User");

        userManager.add(user);
        roleManager.add(adminRole);
        roleManager.add(userRole);

        AssignmentMetadata assignmentTestAdmin = AssignmentMetadata.now("ivan2008", "some reason");
        PermanentAssignment permanentTestAdmin = new PermanentAssignment(user, adminRole, assignmentTestAdmin);
        AssignmentMetadata assignmentTestUser = AssignmentMetadata.now("ivan2008", "some reason");
        PermanentAssignment permanentTestUser = new PermanentAssignment(user, userRole, assignmentTestUser);
        assignmentManager.add(permanentTestAdmin);
        assignmentManager.add(permanentTestUser);

        String report = reportGenerator.generateUserReport(userManager, assignmentManager);

        assertTrue(report.contains("USER: ivan_ivanov"));
        assertTrue(report.contains("ROLE: ADMIN, USER"));
    }

    @Test
    void testGenerateUserReportNoRoles() {
        User user = User.create("lonely_user", "No Name", "none@mail.com");
        userManager.add(user);

        String report = reportGenerator.generateUserReport(userManager, assignmentManager);

        assertTrue(report.contains("USER: lonely_user"));
        assertTrue(report.contains("ROLE: No roles"));
    }

    @Test
    void testGenerateRoleReport() {
        Role role = new Role("EDITOR", "Can edit news");
        User u1 = User.create("u1", "User 1", "u1@test.com");
        User u2 = User.create("u2", "User 2", "u2@test.com");
        AssignmentMetadata assignmentTest = AssignmentMetadata.now("ivan2008", "some reason");

        roleManager.add(role);
        userManager.add(u1);
        userManager.add(u2);

        assignmentManager.add(new PermanentAssignment(u1, role, assignmentTest));
        assignmentManager.add(new PermanentAssignment(u2, role, assignmentTest));

        String report = reportGenerator.generateRoleReport(roleManager, assignmentManager);

        assertTrue(report.contains("ROLE: EDITOR | NUMBER OF USERS: 2"));
    }

    @Test
    void testGeneratePermissionMatrix() {
        User user = User.create("dev", "Developer", "dev@code.com");
        Role role = new Role("DEV_ROLE", "Dev");

        Permission p1 = new Permission("READ", "DATABASE", "DESCRIPTION");
        Permission p2 = new Permission("WRITE", "DATABASE", "DESCRIPTION");
        Permission p3 = new Permission("EXECUTE", "SERVER", "DESCRIPTION");

        AssignmentMetadata assignmentTest = AssignmentMetadata.now("ivan2008", "some reason");

        role.addPermission(p1);
        role.addPermission(p2);
        role.addPermission(p3);

        userManager.add(user);
        roleManager.add(role);
        assignmentManager.add(new PermanentAssignment(user, role, assignmentTest));

        String report = reportGenerator.generatePermissionMatrix(userManager, assignmentManager);

        assertTrue(report.contains("USER: dev"));
        assertTrue(report.contains("RESOURCES: DATABASE, SERVER") || report.contains("RESOURCES: SERVER, DATABASE"));
    }

    @Test
    void testExportToFile(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("test_report.txt");
        String content = "Sample Report Content";

        reportGenerator.exportToFile(content, filePath.toString());

        assertTrue(Files.exists(filePath));
        assertEquals(content, Files.readString(filePath));
    }
}
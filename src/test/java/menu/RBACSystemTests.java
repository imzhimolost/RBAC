package menu;

import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RBACSystemTests {
    private RBACSystem testRBAC;

    @BeforeEach
    void setUp() {
        testRBAC = new RBACSystem();
        testRBAC.initialize();
    }

    @Test
    void testStatisticsAfterInitialize(){
        String expected = "STATS:\nNumber of users: 1\nNumber of roles: 3\nNumber of assignments: 1\n";
        assertEquals(expected, testRBAC.generateStatistics(), "Wrong stats string. \nExpected: " + expected + "\nGot: " + testRBAC.generateStatistics());
    }

    @Test
    void testSetCurrentUser(){
        testRBAC.setCurrentUser("user_test");
        String expected = "user_test";
        assertEquals(expected, testRBAC.getCurrentUser(), "Wrong username string.\nExpected: " + expected + "\nGot: " + testRBAC.getCurrentUser());
    }

    @Test
    void testSetCurrentUserBlank(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testRBAC.setCurrentUser("  ");
        });
    }

    @Test
    void testGetUserManager(){
        UserManager manager = testRBAC.getUserManager();

        assertTrue(manager.exists("admin"), "Admin role should exist");
        assertEquals(1, manager.count(), "Should have 1 user");
    }

    @Test
    void testGetRoleManager(){
        RoleManager manager = testRBAC.getRoleManager();

        assertTrue(manager.exists("Admin"), "Admin role should exist");
        assertTrue(manager.findByName("Admin").get().hasPermission("READ", "settings"));
        assertEquals(3, manager.count(), "Should have 3 roles");
    }

    @Test
    void testGetAssignmentManager(){
        AssignmentManager manager = testRBAC.getAssignmentManager();

        assertEquals(1, manager.count(), "Should have 1 assignment");
    }
}

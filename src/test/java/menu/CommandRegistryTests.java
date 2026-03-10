package menu;

import managers.AssignmentManager;
import managers.RoleManager;
import managers.UserManager;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommandRegistryTests {

    private CommandParser parser;
    private RBACSystem rbacSystem;
    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;

    private Map<String, Command> registeredCommands;

    @BeforeEach
    void setUp() {
        parser = mock(CommandParser.class);
        rbacSystem = mock(RBACSystem.class);
        userManager = mock(UserManager.class);
        roleManager = mock(RoleManager.class);
        assignmentManager = mock(AssignmentManager.class);

        when(rbacSystem.getUserManager()).thenReturn(userManager);
        when(rbacSystem.getRoleManager()).thenReturn(roleManager);
        when(rbacSystem.getAssignmentManager()).thenReturn(assignmentManager);
        when(rbacSystem.getCurrentUser()).thenReturn("admin-tester");

        registeredCommands = new HashMap<>();

        doAnswer(invocation -> {
            String name = invocation.getArgument(0);
            Command cmd = invocation.getArgument(2);
            registeredCommands.put(name, cmd);
            return null;
        }).when(parser).registerCommand(anyString(), anyString(), any(Command.class));

        CommandRegistry.register(parser);
    }

    @Test
    void testUserCreate() {
        String input = "user\nIvan User\nivan@example.com\n";
        Scanner scanner = new Scanner(input);

        registeredCommands.get("user-create").execute(scanner, rbacSystem);

        verify(userManager, times(1)).add(argThat(user ->
                user.username().equals("user") &&
                        user.fullName().equals("Ivan User") &&
                        user.email().equals("ivan@example.com")
        ));
    }

    @Test
    void testUserCreateInvalidData() {
        String input = "user\n \nivan@example.com\n";
        Scanner scanner = new Scanner(input);

        registeredCommands.get("user-create").execute(scanner, rbacSystem);

        verify(userManager, never()).add(any());
    }

    @Test
    void testRoleCreate() {
        String input = "SuperAdmin\nHas all permissions\n";
        Scanner scanner = new Scanner(input);

        registeredCommands.get("role-create").execute(scanner, rbacSystem);

        verify(roleManager).add(argThat(role ->
                role.getName().equals("SuperAdmin") &&
                        role.getDescription().equals("Has all permissions")
        ));
    }

    @Test
    void testAssignRole_Permanent() {
        User user = User.create("ivan", "Ivan User", "ivan@example.com");
        Role role = new Role("Editor", "Edit things");

        when(userManager.findByUsername("ivan")).thenReturn(Optional.of(user));
        when(roleManager.findByName("Editor")).thenReturn(Optional.of(role));

        String input = "ivan\nEditor\n1\nPromoted\n";
        Scanner scanner = new Scanner(input);

        registeredCommands.get("assign-role").execute(scanner, rbacSystem);

        verify(assignmentManager).add(any(PermanentAssignment.class));
    }

    @Test
    void testRevokeRole() {
        User user = User.create("ivan", "Ivan Uer", "ivan@example.com");
        Role role = new Role("Viewer", "");

        RoleAssignment assignment = mock(RoleAssignment.class);
        when(assignment.isActive()).thenReturn(true);
        when(assignment.assignmentId()).thenReturn("uuid-123");
        when(assignment.role()).thenReturn(role);

        when(userManager.findByUsername("ivan")).thenReturn(Optional.of(user));
        when(assignmentManager.findByUser(user)).thenReturn(List.of(assignment));

        String input = "ivan\n1\n";
        Scanner scanner = new Scanner(input);

        registeredCommands.get("revoke-role").execute(scanner, rbacSystem);

        verify(assignmentManager).revokeAssignment("uuid-123");
    }

    @Test
    void testRoleSearch_ByName() {
        String input = "1\nAdmin\n";
        Scanner scanner = new Scanner(input);

        registeredCommands.get("role-search").execute(scanner, rbacSystem);

        verify(roleManager).findAll(any(), any());
    }

    @Test
    void testPermissionsCheck() {
        User user = User.create("admin", "Admin", "admin@example.com");
        when(userManager.findByUsername("admin")).thenReturn(Optional.of(user));

        String input = "admin\nDELETE\nusers\n";
        Scanner scanner = new Scanner(input);

        registeredCommands.get("permissions-check").execute(scanner, rbacSystem);

        verify(assignmentManager).userHasPermission(user, "DELETE", "users");
    }

    @Test
    void testStatsCommand() {
        Scanner scanner = new Scanner("");
        registeredCommands.get("stats").execute(scanner, rbacSystem);

        verify(rbacSystem).generateStatistics();
    }
}
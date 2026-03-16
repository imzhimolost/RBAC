package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    private AuditLog auditLog;

    @BeforeEach
    void setUp() throws Exception {
        auditLog = AuditLog.getInstance();

        Field entriesField = AuditLog.class.getDeclaredField("entries");
        entriesField.setAccessible(true);
        List<?> entries = (List<?>) entriesField.get(auditLog);
        entries.clear();
    }

    @Test
    void testLogAddsEntry() {
        auditLog.log("LOGIN", "admin", "system", "Successful login");

        List<AuditLog.AuditEntry> entries = auditLog.getAll();

        assertEquals(1, entries.size());
        assertEquals("LOGIN", entries.getFirst().action());
        assertEquals("admin", entries.getFirst().performer());
        assertNotNull(entries.getFirst().timestamp());
    }

    @Test
    void testGetByPerformer() {
        auditLog.log("CREATE", "user1", "file1", "created file");
        auditLog.log("DELETE", "user2", "file2", "deleted file");
        auditLog.log("UPDATE", "user1", "file1", "updated file");

        List<AuditLog.AuditEntry> user1Actions = auditLog.getByPerformer("user1");

        assertEquals(2, user1Actions.size());
        assertTrue(user1Actions.stream().allMatch(e -> e.performer().equals("user1")));
    }

    @Test
    void testGetByAction() {
        auditLog.log("LOGIN", "user1", "app", "web");
        auditLog.log("LOGOUT", "user1", "app", "web");
        auditLog.log("LOGIN", "user2", "app", "mobile");

        List<AuditLog.AuditEntry> logins = auditLog.getByAction("LOGIN");

        assertEquals(2, logins.size());
        assertTrue(logins.stream().allMatch(e -> e.action().equals("LOGIN")));
    }

    @Test
    void testSaveToFile(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("audit.log");

        auditLog.log("ACTION", "USER", "TARGET", "DETAILS");
        auditLog.saveToFile(logFile.toString());

        assertTrue(Files.exists(logFile));

        List<String> lines = Files.readAllLines(logFile);
        assertEquals(1, lines.size());
        assertTrue(lines.getFirst().contains("ACTION -- BY USER ON TARGET; DETAILS: DETAILS"));
    }

    @Test
    void testGetAllReturnsCopyOrOriginal() {
        auditLog.log("TEST", "performer", "target", "details");
        List<AuditLog.AuditEntry> entries = auditLog.getAll();

        int initialSize = auditLog.getAll().size();
        entries.add(new AuditLog.AuditEntry("now", "HACK", "bad_guy", "db", "corrupted"));

        assertEquals(initialSize + 1, auditLog.getAll().size(),
                "Список внутри AuditLog изменился, так как getAll() вернул не копию");
    }
}
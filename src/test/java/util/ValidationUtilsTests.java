package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTests {
    @Test
    public void testUsernameValidation() {
        assertTrue(ValidationUtils.isValidUsername("ivan"));
        assertFalse(ValidationUtils.isValidUsername("i"));
        assertFalse(ValidationUtils.isValidUsername("ivan@test"));
    }

    @Test
    public void testEmailValidation() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertFalse(ValidationUtils.isValidEmail("test/example.com"));
    }

    @Test
    public void testDateValidation() {
        assertTrue(ValidationUtils.isValidDate("01.01.2026 15:00:00"));
        assertFalse(ValidationUtils.isValidDate("01-01-2026"));
    }

    @Test
    public void testNormalizeString() {
        assertEquals("Ivan Ivanov", ValidationUtils.normalizeString("   Ivan       Ivanov "));
    }

    @Test
    public void testRequireNonEmpty() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonEmpty("  ", "Name"));
        assertDoesNotThrow(() -> ValidationUtils.requireNonEmpty("Valid", "Name"));
    }
}

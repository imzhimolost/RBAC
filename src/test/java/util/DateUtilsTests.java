package util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTests {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Test
    void testGetCurrentDate() {
        String expected = LocalDate.now().format(DATE_FORMATTER);
        assertEquals(expected, DateUtils.getCurrentDate());
    }

    @Test
    void testGetCurrentDateTime() {
        String result = DateUtils.getCurrentDateTime();
        assertTrue(result.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void testIsBefore() {
        String earlier = "01-01-2026 10:00:00";
        String later = "01-01-2026 12:00:00";

        assertTrue(DateUtils.isBefore(earlier, later));
        assertFalse(DateUtils.isBefore(later, earlier));
    }

    @Test
    void testIsAfter() {
        String earlier = "10-10-2025 15:00:00";
        String later = "11-10-2025 15:00:00";

        assertTrue(DateUtils.isAfter(later, earlier));
        assertFalse(DateUtils.isAfter(earlier, later));
    }

    @Test
    void testAddDays() {
        String initialDate = "28-12-2025";
        String result = DateUtils.addDays(initialDate, 5);
        assertEquals("02-01-2026", result);

        assertEquals("27-12-2025", DateUtils.addDays(initialDate, -1));
    }

    @Test
    void testFormatRelativeTime() {
        LocalDate now = LocalDate.now();

        String todayStr = now.format(DATE_FORMATTER);
        assertEquals("today", DateUtils.formatRelativeTime(todayStr));

        String futureStr = now.plusDays(5).format(DATE_FORMATTER);
        assertEquals("in 5 days", DateUtils.formatRelativeTime(futureStr));

        String pastStr = now.minusDays(3).format(DATE_FORMATTER);
        assertEquals("3 days ago", DateUtils.formatRelativeTime(pastStr));
    }

    @Test
    void testInvalidDateFormat() {
        assertThrows(Exception.class, () -> {
            DateUtils.addDays("2026/01/01", 1);
        });

        assertThrows(Exception.class, () -> {
            DateUtils.isBefore("01-01-2026", "02-01-2026"); // Нет времени, а метод ждет HH:mm:ss
        });
    }
}
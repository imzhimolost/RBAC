package util;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FormatUtilsTests {

    @Test
    void testFormatTable() {
        String[] headers = {"ID", "Name"};
        List<String[]> rows = Arrays.asList(
                new String[]{"1", "Alice"},
                new String[]{"20", "Bob"}
        );

        String result = FormatUtils.formatTable(headers, rows);

        assertTrue(result.contains("ID"));
        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("20"));

        assertTrue(result.contains("+----+-------+"));

        String[] lines = result.split("\n");
        assertEquals(6, lines.length);
    }

    @Test
    void testFormatBox() {
        String text = "Hello";
        String result = FormatUtils.formatBox(text);

        String[] lines = result.split("\n");
        assertEquals("+-------+", lines[0]);
        assertEquals("| Hello |", lines[1]);
        assertEquals("+-------+", lines[2]);
    }

    @Test
    void testFormatHeader() {
        String result = FormatUtils.formatHeader("menu");
        assertEquals("\n>>> MENU <<<\n", result);
    }

    @Test
    void testTruncate() {
        assertEquals("Java", FormatUtils.truncate("Java", 10));

        assertEquals("Long...", FormatUtils.truncate("LongString", 7));

        assertNull(FormatUtils.truncate(null, 5));

        assertEquals("...", FormatUtils.truncate("Something", 2));
    }

    @Test
    void testPadRight() {
        assertEquals("Test  ", FormatUtils.padRight("Test", 6));
        assertEquals("LongerString", FormatUtils.padRight("LongerString", 5)); // Если текст длиннее, не режет
        assertEquals("    ", FormatUtils.padRight(null, 4)); // Null превращается в пустую строку
    }

    @Test
    void testPadLeft() {
        assertEquals("  Test", FormatUtils.padLeft("Test", 6));
        assertEquals("    ", FormatUtils.padLeft("", 4));
        assertEquals("    ", FormatUtils.padLeft(null, 4));
    }

    @Test
    void testFormatTableAlignment() {
        String[] headers = {"A"};
        List<String[]> rows = Arrays.asList(
                new String[]{"LongText"},
                new String[]{"S"}
        );
        String result = FormatUtils.formatTable(headers, rows);

        assertTrue(result.contains("+----------+"));
        assertTrue(result.contains("| S        |"));
    }
}
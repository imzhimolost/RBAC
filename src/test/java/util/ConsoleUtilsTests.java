package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleUtilsTests {

    private ConsoleUtils consoleUtils;

    @BeforeEach
    void setUp() {
        consoleUtils = new ConsoleUtils();
    }

    private Scanner provideInput(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    void testPromptString_ValidInput() {
        Scanner scanner = provideInput("Hello World\n");
        String result = ConsoleUtils.promptString(scanner, "Enter text", true);
        assertEquals("Hello World", result);
    }

    @Test
    void testPromptString_EmptyThenValidInput() {
        Scanner scanner = provideInput("\nValid Input\n");
        String result = ConsoleUtils.promptString(scanner, "Enter text", true);
        assertEquals("Valid Input", result);
    }

    @Test
    void testPromptInt_ValidInRange() {
        Scanner scanner = provideInput("5\n");
        int result = ConsoleUtils.promptInt(scanner, "Enter number", 1, 10);
        assertEquals(5, result);
    }

    @Test
    void testPromptInt_InvalidFormatThenValid() {
        Scanner scanner = provideInput("abc\n15\n7\n");
        int result = ConsoleUtils.promptInt(scanner, "Enter number", 1, 10);
        assertEquals(7, result);
    }

    @Test
    void testPromptYesNo_Yes() {
        Scanner scanner = provideInput("да\n");
        assertTrue(ConsoleUtils.promptYesNo(scanner, "Agree?"));
    }

    @Test
    void testPromptYesNo_No() {
        Scanner scanner = provideInput("нет\n");
        assertFalse(ConsoleUtils.promptYesNo(scanner, "Agree?"));
    }

    @Test
    void testPromptYesNo_InvalidThenValid() {
        Scanner scanner = provideInput("maybe\nнет\n");
        assertFalse(ConsoleUtils.promptYesNo(scanner, "Agree?"));
    }

    @Test
    void testPromptChoice_ValidSelection() {
        List<String> options = Arrays.asList("Option A", "Option B", "Option C");
        Scanner scanner = provideInput("2\n");

        String result = consoleUtils.promptChoice(scanner, "Pick one", options);

        assertEquals("Option B", result);
    }

    @Test
    void testPromptChoice_InvalidIndexThenValid() {
        List<String> options = Arrays.asList("Red", "Green");
        Scanner scanner = provideInput("5\n1\n");

        String result = consoleUtils.promptChoice(scanner, "Pick color", options);

        assertEquals("Red", result);
    }
}
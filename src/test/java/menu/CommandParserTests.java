package menu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommandParserTests {
    private CommandParser commandParser;

    // Моки для зависимостей
    @Mock private Command mockCommand;
    @Mock private RBACSystem mockSystem;

    // Для перехвата вывода в консоль
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        commandParser = new CommandParser();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testRegisterCommand() {
        commandParser.registerCommand("test", "Test description", mockCommand);

        assertNotNull(commandParser);
    }

    @Test
    void testExecuteCommand() {
        commandParser.registerCommand("create", "Create item", mockCommand);
        Scanner scanner = new Scanner("arg1 arg2");

        commandParser.executeCommand("create", scanner, mockSystem);

        verify(mockCommand, times(1)).execute(scanner, mockSystem);
    }

    @Test
    void testExecuteCommandUnknown() {
        commandParser.executeCommand("unknown", new Scanner(""), mockSystem);

        String output = outContent.toString();
        assertTrue(output.contains("Неизвестная команда! Введите help для вывода списка команд.\n"));

        verifyNoInteractions(mockCommand);
    }

    @Test
    void testPrintHelp() {
        commandParser.registerCommand("add", "Add new item", mockCommand);
        commandParser.registerCommand("del", "Delete item", mockCommand);

        commandParser.printHelp();

        String output = outContent.toString();
        assertTrue(output.contains("add"));
        assertTrue(output.contains("Add new item"));
        assertTrue(output.contains("del"));
        assertTrue(output.contains("Delete item"));
    }

    @Test
    void testPrintHelpEmpty() {
        commandParser.printHelp();
        assertTrue(outContent.toString().contains("Спискок команд пуст.\n"));
    }

    @Test
    void testParseAndExecute() {
        commandParser.registerCommand("run", "Run task", mockCommand);

        String input = "run task1 --force";

        commandParser.parseAndExecute(input, new Scanner(System.in), mockSystem);

        verify(mockCommand, times(1)).execute(any(Scanner.class), eq(mockSystem));
    }

    @Test
    void testParseAndExecuteEmpty() {
        commandParser.parseAndExecute("", new Scanner(System.in), mockSystem);
        commandParser.parseAndExecute("   ", new Scanner(System.in), mockSystem);
        commandParser.parseAndExecute(null, new Scanner(System.in), mockSystem);

        verifyNoInteractions(mockCommand);
    }
}

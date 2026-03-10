package menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandParser {
    Map<String, Command> commands = new HashMap<>();
    Map<String, String> commandDescriptions = new HashMap<>();

    public void registerCommand(String name, String description, Command command){
        if(commands.containsKey(name)){
            throw new IllegalArgumentException("Command must be unique");
        }
        if(commandDescriptions.containsKey(name)){
            throw new IllegalArgumentException("Command must be unique");
        }

        commands.put(name, command);
        commandDescriptions.put(name, description);
    }

    public void executeCommand(String commandName, Scanner scanner, RBACSystem system){
        if(commands.containsKey(commandName)){
            Command command = commands.get(commandName);
            command.execute(scanner, system);
        }
        else{
            System.out.println("Неизвестная команда! Введите help для вывода списка команд.\n");
        }
    }

    public void printHelp() {
        if(commandDescriptions.isEmpty()){
            System.out.println("Спискок команд пуст.\n");
        }
        for(Map.Entry<String, String> item : commandDescriptions.entrySet()){
            System.out.println("Команда: " + item.getKey() + "; Описание: " + item.getValue() + "\n");
        }
    }

    public void parseAndExecute(String input, Scanner scanner, RBACSystem system){
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        Scanner inputScanner = new Scanner(input);

        if (!inputScanner.hasNext()) {
            return;
        }

        String commandName = inputScanner.next();

        executeCommand(commandName, inputScanner, system);

        inputScanner.close();
    }
}

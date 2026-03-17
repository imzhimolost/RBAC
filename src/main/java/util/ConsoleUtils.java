package util;

import java.util.List;
import java.util.Scanner;

public class ConsoleUtils {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static String promptString(Scanner scanner, String message, boolean required) {
        while(true){
            System.out.print(YELLOW + message + ": " + RESET);
            String input = scanner.nextLine().trim();
            if(required && input.isEmpty()){
                System.out.print(RED + "Error: Field is required.");
                continue;
            }
            return input;
        }
    }

    public static int promptInt(Scanner scanner, String message, int min, int max) {
        while(true){
            System.out.print(YELLOW + message + " (В диапазоне от " + min + " до " + max + "): " + RESET);
            try{
                int value = Integer.parseInt(scanner.nextLine());
                if(value >= min && value <= max) return value;
                System.out.print(RED + "Error: Number out of range." + RESET);
            } catch (NumberFormatException e){
                System.out.print(RED + "Error: Wrong number format." + RESET);
            }
        }
    }

    public static boolean promptYesNo(Scanner scanner, String message) {
        while(true){
            System.out.print(YELLOW + message + " (да/нет): " + RESET);
            String input = scanner.nextLine().trim().toLowerCase();
            if(input.equals("да")) return true;
            if(input.equals("нет")) return false;
            System.out.print(RED + "Error: Answer must be yes or no.");
        }
    }

    public <T> T promptChoice(Scanner scanner, String message, List<T> options){
        System.out.println(YELLOW + message + RESET);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i).toString());
        }
        int choice = promptInt(scanner, "Выберите номер", 1, options.size());
        return options.get(choice - 1);
    }
}

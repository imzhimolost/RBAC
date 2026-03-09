package menu;

import filters.UserFilter;
import model.User;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CommandRegistry {

    public static void register(CommandParser parser) {

        //1. Команды управления пользователями
        parser.registerCommand("user-list", "Вывести список всех пользователей", (scanner, system) -> {
            List<User> users = system.getUserManager().findAll();
            if (users.isEmpty()) {
                System.out.println("Список пользователей пуст.");
            } else {
                printUserTable(users);
            }
        });

        parser.registerCommand("user-create", "Создать нового пользователя", (scanner, system) -> {
            try {
                System.out.print("Введите username: ");
                String username = scanner.next();

                System.out.print("Введите полное имя (fullName): ");
                scanner.nextLine();
                String fullName = scanner.nextLine();

                System.out.print("Введите email: ");
                String email = scanner.next();

                if (username.isBlank() || fullName.isBlank() || !email.contains("@")) {
                    System.out.println("Ошибка: Некорректные данные (пустые поля или неверный email).");
                    return;
                }

                User newUser = User.create(username, fullName, email);
                system.getUserManager().add(newUser);
                System.out.println("Пользователь успешно создан!");

            } catch (Exception e) {
                System.out.println("Ошибка при создании пользователя: " + e.getMessage());
            }
        });

        parser.registerCommand("user-view", "Просмотр информации о пользователе", (scanner, system) -> {
            System.out.print("Введите username: ");
            String username = scanner.next();

            Optional<User> userOptional = system.getUserManager().findByUsername(username);

            if(userOptional.isPresent()){
                User user = userOptional.get();
                System.out.println("Пользователь: " + user.username() + " (" + user.fullName() + "), " + user.email());
                System.out.println("Назначенные роли:");
                system.getAssignmentManager().findByUser(user).forEach(assignment ->
                        System.out.println(" - " + assignment.role().getName() + " [" + assignment.assignmentType() + "]")
                );
            } else {
                System.out.println("Пользователь не найден.");
            }
        });

        parser.registerCommand("user-update", "Обновить данные пользователя", (scanner, system) -> {
            System.out.print("Введите username: ");
            String username = scanner.nextLine();
            System.out.print("Новое полное имя: ");
            String fullName = scanner.nextLine();
            System.out.print("Новый email: ");
            String email = scanner.nextLine();

            try {
                system.getUserManager().update(username, fullName, email);
                System.out.println("Данные успешно обновлены.");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        parser.registerCommand("user-delete", "Удалить пользователя", (scanner, system) -> {
            System.out.print("Введите username: ");
            String username = scanner.next();

            Optional<User> userOptional = system.getUserManager().findByUsername(username);

            if(userOptional.isEmpty()){
                System.out.println("Пользователь не найден.");
                return;
            }

            System.out.print("Подтвердить удаление? (введите 'да'): ");
            if (scanner.nextLine().equalsIgnoreCase("да")) {
                system.getUserManager().remove(userOptional.get());
                System.out.println("Пользователь удален.");
            }
        });

        parser.registerCommand("user-search", "Поиск пользователей по фильтрам", (scanner, system) -> {
            System.out.println("\nВыберите фильтр:");
            System.out.println("1. По username (содержит)");
            System.out.println("2. По email (содержит)");
            System.out.println("3. По домену email");
            System.out.println("4. По полному имени (содержит)");
            System.out.print("Ваш выбор: ");

            String choice = scanner.next();
            System.out.print("Введите строку для поиска: ");
            String query = scanner.next().toLowerCase();

            UserFilter filter;
            switch (choice) {
                case "1" -> filter = u -> u.username().toLowerCase().contains(query);
                case "2" -> filter = u -> u.email().toLowerCase().contains(query);
                case "3" -> filter = u -> u.email().toLowerCase().endsWith("@" + query) || u.email().contains(query);
                case "4" -> filter = u -> u.fullName().toLowerCase().contains(query);
                default -> {
                    System.out.println("Неверный выбор.");
                    return;
                }
            };

            List<User> results = system.getUserManager().findByFilter(filter);
            if (results.isEmpty()) {
                System.out.println("Пользователи не найдены.");
            } else {
                printUserTable(results);
            }
        });
    }

    private static void printUserTable(List<User> users) {
        System.out.println("-".repeat(70));
        System.out.printf("| %-15s | %-25s | %-20s |\n", "Username", "Full Name", "Email");
        System.out.println("-".repeat(70));
        for (User user : users) {
            System.out.printf("| %-15s | %-25s | %-20s |\n",
                    user.username(),
                    user.fullName(),
                    user.email());
        }
        System.out.println("-".repeat(70));
    }
}
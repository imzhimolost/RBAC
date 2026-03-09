package menu;

import filters.RoleFilter;
import filters.RoleFilters;
import filters.UserFilter;
import model.Permission;
import model.Role;
import model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

        //2. Команды управления ролями
        parser.registerCommand("role-list", "Вывести список всех ролей", (scanner, system) -> {
            List<Role> roles = system.getRoleManager().findAll();
            if (roles.isEmpty()) {
                System.out.println("Список ролей пуст.");
            } else {
                printRoleTable(roles);
            }
        });

        parser.registerCommand("role-create", "Создать новую роль", (scanner, system) -> {
            System.out.print("Название роли: ");
            String name = scanner.nextLine();
            System.out.print("Описание роли: ");
            String description = scanner.nextLine();
            try {
                Role role = new Role(name, description);
                system.getRoleManager().add(role);
                System.out.println("Роль создана.");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        parser.registerCommand("role-view", "Просмотр роли", (scanner, system) -> {
            System.out.print("Введите название роли: ");
            String name = scanner.next();

            Optional<Role> roleOptional = system.getRoleManager().findByName(name);

            if(roleOptional.isPresent()){
                Role role = roleOptional.get();
                System.out.println(role.format());
            } else {
                System.out.println("Роль не найдена.");
            }
        });

        //parser.registerCommand("role-update", "Обновить роль", (scanner, system) -> {});

        parser.registerCommand("role-delete", "Удалить роль", (scanner, system) -> {
            System.out.print("Введите название роли: ");
            String name = scanner.next();

            Optional<Role> roleOptional = system.getRoleManager().findByName(name);

            if(roleOptional.isEmpty()){
                System.out.println("Роль не найдена.");
                return;
            }

            System.out.print("Подтвердить удаление? (введите 'да'): ");
            if (scanner.nextLine().equalsIgnoreCase("да")) {
                system.getRoleManager().remove(roleOptional.get());
                System.out.println("Роль удалена.");
            }
        });

        parser.registerCommand("role-add-permission", "Добавить право к роли", (scanner, system) -> {
            System.out.print("Имя роли: ");
            String rName = scanner.nextLine();
            System.out.print("Имя права: ");
            String pName = scanner.nextLine();
            System.out.print("Ресурс: ");
            String pRes = scanner.nextLine();

            try {
                system.getRoleManager().addPermissionToRole(rName, new Permission(pName, pRes, "Описание"));
                System.out.println("Право добавлено.");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        parser.registerCommand("role-remove-permission", "Удалить право из роли", (scanner, system) -> {
            System.out.print("Имя роли: ");
            String rName = scanner.nextLine();

            Optional<Role> rOpt = system.getRoleManager().findByName(rName);

            if (rOpt.isEmpty()) {
                System.out.println("Роль не найдена.");
                return;
            }

            List<Permission> perms = new ArrayList<>(rOpt.get().getPermissions());

            for (int i = 0; i < perms.size(); i++) {
                System.out.println((i + 1) + ". " + perms.get(i).name() + " on " + perms.get(i).resource());
            }

            System.out.print("Номер для удаления: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < perms.size()) {
                Permission p = perms.get(idx);
                system.getRoleManager().removePermissionFromRole(rName, p);
                System.out.println("Право удалено.");
            }
        });

        parser.registerCommand("role-search", "Поиск ролей по фильтрам", (scanner, system) -> {
            System.out.println("\nВыберите фильтр для ролей:");
            System.out.println("1. По имени (содержит)");
            System.out.println("2. По наличию конкретного права (Name + Resource)");
            System.out.println("3. По минимальному количеству прав");
            System.out.print("Ваш выбор: ");

            String choice = scanner.next();
            RoleFilters filterFactory = new RoleFilters();
            RoleFilter filter = null;

            switch (choice) {
                case "1" -> {
                    System.out.print("Введите часть названия роли: ");
                    String sub = scanner.next();
                    filter = filterFactory.byNameContains(sub);
                }
                case "2" -> {
                    System.out.print("Введите название права (напр. READ): ");
                    String pName = scanner.next();
                    System.out.print("Введите ресурс (напр. settings): ");
                    String resource = scanner.next();
                    filter = filterFactory.hasPermission(pName, resource);
                }
                case "3" -> {
                    System.out.print("Введите минимальное количество прав: ");
                    if (scanner.hasNextInt()) {
                        int n = scanner.nextInt();
                        filter = filterFactory.hasAtLeastNPermissions(n);
                    } else {
                        System.out.println("Ошибка: ожидалось число.");
                        scanner.next();
                        return;
                    }
                }
                default -> {
                    System.out.println("Неверный выбор.");
                    return;
                }
            }

            List<Role> roles = system.getRoleManager().findAll(filter, Comparator.comparing(Role::getName));

            if (roles.isEmpty()) {
                System.out.println("Роли не найдены.");
            } else {
                printRoleTable(roles);
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

    private static void printRoleTable(List<Role> roles) {
        System.out.println("-".repeat(70));
        System.out.printf("| %-25s | %-15s | %s |\n", "Название", "Кол-во прав", "ID");
        System.out.println("-".repeat(70));
        for (Role role : roles) {
            System.out.printf("| %-25s | %-15s | %s |\n",
                    role.getName(),
                    role.getPermissions().size(),
                    role.getId());
        }
        System.out.println("-".repeat(70));
    }
}
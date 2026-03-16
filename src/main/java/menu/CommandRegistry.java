package menu;

import filters.*;
import model.*;
import util.AuditLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CommandRegistry {

    private static void logging(String action, RBACSystem system, String target, String details){
        AuditLog.getInstance().log(action, system.getCurrentUser(), target, details);
    }

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
            String username = null;
            try {
                System.out.print("Введите username: ");
                username = scanner.next();

                System.out.print("Введите полное имя (fullName): ");
                scanner.nextLine();
                String fullName = scanner.nextLine();

                System.out.print("Введите email: ");
                String email = scanner.next();

                if (username.isBlank() || fullName.isBlank() || !email.contains("@")) {
                    logging("user-create", system, username, "[ERROR] Data error");
                    System.out.println("Ошибка: Некорректные данные (пустые поля или неверный email).");
                    return;
                }

                User newUser = User.create(username, fullName, email);
                system.getUserManager().add(newUser);
                logging("user-create", system, username, "[SUCCESS] User created");
                System.out.println("Пользователь успешно создан!");

            } catch (Exception e) {
                logging("user-create", system, username, "[ERROR] " + e.getMessage());
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
                logging("user-delete", system, username, "[ERROR] No such user");
                System.out.println("Пользователь не найден.");
                return;
            }

            System.out.print("Подтвердить удаление? (введите 'да'): ");
            if (scanner.nextLine().equalsIgnoreCase("да")) {
                system.getUserManager().remove(userOptional.get());
                logging("user-delete", system, username, "[SUCCESS] User deleted");
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
                logging("role-create", system, name, "[SUCCESS] Role created");
                System.out.println("Роль создана.");
            } catch (Exception e) {
                logging("role-create", system, name, "[ERROR] " + e.getMessage());
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
                logging("role-delete", system, name, "[ERROR] No such role");
                System.out.println("Роль не найдена.");
                return;
            }

            System.out.print("Подтвердить удаление? (введите 'да'): ");
            if (scanner.nextLine().equalsIgnoreCase("да")) {
                system.getRoleManager().remove(roleOptional.get());
                logging("role-delete", system, name, "[SUCCESS] Role deleted");
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
                logging("role-add-permission", system, rName, "[SUCCESS] Role added");
                System.out.println("Право добавлено.");
            } catch (Exception e) {
                logging("role-add-permission", system, rName, "[ERROR] " + e.getMessage());
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        parser.registerCommand("role-remove-permission", "Удалить право из роли", (scanner, system) -> {
            System.out.print("Имя роли: ");
            String rName = scanner.nextLine();

            Optional<Role> rOpt = system.getRoleManager().findByName(rName);

            if (rOpt.isEmpty()) {
                logging("role-remove-permission", system, rName, "[ERROR] No such role");
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
                logging("role-remove-permission", system, rName, "[SUCCESS] Role removed");
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

        //3. Команды управления назначениями
        parser.registerCommand("assign-role", "Назначить роль пользователю", (scanner, system) -> {
            System.out.print("Username: ");
            String uName = scanner.nextLine();
            System.out.print("Имя роли: ");
            String rName = scanner.nextLine();
            System.out.print("Тип (1-Постоянное, 2-Временное): ");
            String type = scanner.nextLine();
            System.out.print("Причина: ");
            String reason = scanner.nextLine();

            try {
                User user = system.getUserManager().findByUsername(uName)
                        .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
                Role role = system.getRoleManager().findByName(rName)
                        .orElseThrow(() -> new IllegalArgumentException("Роль не найдена"));

                AssignmentMetadata meta = AssignmentMetadata.now(system.getCurrentUser(), reason);
                RoleAssignment assignment;

                if (type.equals("2")) {
                    System.out.print("Дата истечения (YYYY-MM-DD): "); String expires = scanner.nextLine();
                    assignment = new TemporaryAssignment(user, role, meta, expires);
                } else {
                    assignment = new PermanentAssignment(user, role, meta);
                }

                system.getAssignmentManager().add(assignment);
                logging("assign-role", system, rName, "[SUCCESS] Role assigned");
                System.out.println("Успешно назначено!");
            } catch (Exception e) {
                logging("assign-role", system, rName, "[ERROR] " + e.getMessage());
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        parser.registerCommand("revoke-role", "Отозвать роль у пользователя", (scanner, system) -> {
            System.out.print("Username: ");
            String uName = scanner.nextLine();

            Optional<User> userOptional = system.getUserManager().findByUsername(uName);

            if(userOptional.isEmpty()) {
                logging("revoke-role", system, uName, "[ERROR] No such user");
                System.out.println("Пользователь не найден.");
                return;
            }

            List<RoleAssignment> active = system.getAssignmentManager().findByUser(userOptional.get())
                    .stream().filter(RoleAssignment::isActive).toList();

            if(active.isEmpty()) {
                logging("revoke-role", system, uName, "[ERROR] No active roles");
                System.out.println("Нет активных назначений.");
                return;
            }

            System.out.println("Активные назначения:");
            for(int i = 0; i < active.size(); i++) {
                System.out.println((i + 1) + ". " + active.get(i).role().getName() + " (ID: " + active.get(i).assignmentId() + ")");
            }

            System.out.print("Номер для отзыва: ");

            try {
                int idx = Integer.parseInt(scanner.nextLine()) - 1;
                String id = active.get(idx).assignmentId();
                system.getAssignmentManager().revokeAssignment(id);
                logging("revoke-role", system, uName, "[SUCCESS] Role revoked");
                System.out.println("Назначение отозвано.");
            } catch (Exception e) {
                logging("revoke-role", system, uName, "[ERROR] " + e.getMessage());
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        parser.registerCommand("assignment-list", "Список всех назначений", (scanner, system) -> {
            system.getAssignmentManager().findAll().forEach(a ->
                    System.out.printf("User: %s | Role: %s | Status: %s\n",
                            a.user().username(), a.role().getName(), (a.isActive() ? "ACTIVE" : "INACTIVE"))
            );
        });

        parser.registerCommand("assignment-list-user", "Назначения конкретного пользователя", (scanner, system) -> {
            System.out.print("Username: ");
            system.getUserManager().findByUsername(scanner.nextLine()).ifPresentOrElse(u ->
                            system.getAssignmentManager().findByUser(u).forEach(a ->
                                    System.out.println(a.role().getName() + " [" + (a.isActive() ? "ACTIVE" : "INACTIVE") + "]")),
                    () -> System.out.println("Пользователь не найден.")
            );
        });

        parser.registerCommand("assignment-list-role", "Пользователи с конкретной ролью", (scanner, system) -> {
            System.out.print("Имя роли: ");
            system.getRoleManager().findByName(scanner.nextLine()).ifPresentOrElse(r ->
                            system.getAssignmentManager().findByRole(r).forEach(a ->
                                    System.out.println(a.user().username() + " [" + (a.isActive() ? "ACTIVE" : "INACTIVE") + "]")),
                    () -> System.out.println("Роль не найдена.")
            );
        });

        parser.registerCommand("assignment-active", "Только активные назначения", (scanner, system) -> {
            system.getAssignmentManager().getActiveAssignments().forEach(a ->
                    System.out.println(a.user().username() + " -> " + a.role().getName())
            );
        });

        parser.registerCommand("assignment-expired", "Истёкшие назначения", (scanner, system) -> {
            system.getAssignmentManager().getExpiredAssignments().forEach(a ->
                    System.out.println(a.user().username() + " -> " + a.role().getName() + " (Expired)")
            );
        });

        parser.registerCommand("assignment-extend", "Продлить временное назначение", (scanner, system) -> {
            System.out.print("ID назначения: ");
            String id = scanner.nextLine();
            System.out.print("Новая дата (YYYY-MM-DD): ");
            String newDate = scanner.nextLine();

            try {
                system.getAssignmentManager().extendTemporaryAssignment(id, newDate);
                System.out.println("Продлено.");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        parser.registerCommand("assignment-search", "Поиск назначений по фильтрам", (scanner, system) -> {
            System.out.println("\nВыберите фильтр для назначений:");
            System.out.println("1. По пользователю");
            System.out.println("2. По роли");
            System.out.println("3. По типу (постоянное/временное)");
            System.out.println("4. По статусу (активное/неактивное)");
            System.out.println("5. Назначённые после даты");
            System.out.println("6. Истекающие до даты");
            System.out.print("Ваш выбор: ");

            String choice = scanner.next();
            AssignmentFilters filterFactory = new AssignmentFilters();
            AssignmentFilter filter = null;

            switch (choice) {
                case "1" -> {
                    System.out.print("Введите username: ");
                    String user = scanner.next();
                    filter = filterFactory.byUsername(user);
                }
                case "2" -> {
                    System.out.print("Введите название роли: ");
                    String rolename = scanner.next();
                    filter = filterFactory.byRoleName(rolename);
                }
                case "3" -> {
                    System.out.print("Введите тип назначения (1 - постоянное; 2 - временное): ");
                    if (scanner.hasNextInt()) {
                        int n = scanner.nextInt();
                        if(n == 1){
                            filter = filterFactory.byType("PERMANENT");
                        } else if (n == 2) {
                            filter = filterFactory.byType("TEMPORARY");
                        } else {
                            System.out.println("Ошибка: ожидалось 1 или 2.");
                            return;
                        }
                    } else {
                        System.out.println("Ошибка: ожидалось число.");
                        scanner.next();
                        return;
                    }
                }
                case "4" -> {
                    System.out.print("Введите тип статуса (1 - активные; 2 - неактивные): ");
                    if (scanner.hasNextInt()) {
                        int n = scanner.nextInt();
                        if(n == 1){
                            filter = filterFactory.activeOnly();
                        } else if (n == 2) {
                            filter = filterFactory.inactiveOnly();
                        } else {
                            System.out.println("Ошибка: ожидалось 1 или 2.");
                            return;
                        }
                    } else {
                        System.out.println("Ошибка: ожидалось число.");
                        scanner.next();
                        return;
                    }
                }
                case "5" -> {
                    System.out.print("Введите дату назначения: ");
                    String date = scanner.next();
                    filter = filterFactory.assignedAfter(date);
                }
                case "6" -> {
                    System.out.print("Введите дату истекания: ");
                    String date = scanner.next();
                    filter = filterFactory.expiringBefore(date);
                }
                default -> {
                    System.out.println("Неверный выбор.");
                    return;
                }
            }

            List<RoleAssignment> assignments = system.getAssignmentManager().findAll(filter, Comparator.comparing(RoleAssignment::assignmentId));

            if (assignments.isEmpty()) {
                System.out.println("Назначения не найдены.");
            } else {
                printAssignmentRole(assignments);
            }
        });

        //4. Команды просмотра прав
        parser.registerCommand("permissions-user", "Все права конкретного пользователя", (scanner, system) -> {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            Optional<User> userOptional = system.getUserManager().findByUsername(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                System.out.println("Права пользователя " + username + ":");
                system.getAssignmentManager().getUserPermissions(user).forEach(permission ->
                        System.out.println(" - " + permission.name() + " (Ресурс: " + permission.resource() + ")")
                );
            } else {
                System.out.println("Пользователь не найден.");
            }
        });

        parser.registerCommand("permissions-check", "Проверить право у пользователя", (scanner, system) -> {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Имя права (например, READ): ");
            String pName = scanner.nextLine();
            System.out.print("Ресурс: ");
            String pRes = scanner.nextLine();

            Optional<User> userOptional = system.getUserManager().findByUsername(username);

            if (userOptional.isPresent()) {
                boolean hasAccess = system.getAssignmentManager().userHasPermission(userOptional.get(), pName, pRes);
                System.out.println("Результат проверки: " + (hasAccess ? "РАЗРЕШЕНО" : "ЗАПРЕЩЕНО"));
            } else {
                System.out.println("Пользователь не найден.");
            }
        });

        //5. Служебные команды
        parser.registerCommand("help", "Справка по командам", (scanner, system) -> {
            parser.printHelp();
        });

        parser.registerCommand("stats", "Статистика системы", (scanner, system) -> {
            System.out.println(system.generateStatistics());
        });

        parser.registerCommand("clear", "Очистить экран", (scanner, system) -> {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            for(int i = 0; i < 20; i++) System.out.println();
        });

        parser.registerCommand("exit", "Выход из программы", (scanner, system) -> {
            System.out.print("Вы уверены, что хотите выйти? (да/нет): ");
            if (scanner.nextLine().equalsIgnoreCase("да")) {
                System.out.println("Завершение работы системы. До свидания!");
                System.exit(0);
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

    private static void printAssignmentRole(List<RoleAssignment> assignments){
        for (RoleAssignment assignment : assignments){
            System.out.printf("User: %s | Role: %s | Status: %s\n",
                    assignment.user().username(), assignment.role().getName(), (assignment.isActive() ? "ACTIVE" : "INACTIVE"));
        }
    }
}
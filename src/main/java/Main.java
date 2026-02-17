import model.User;
import model.Permission;
import model.Role;
import model.AssignmentMetadata;

public class Main {
    public static void main(String[] args) {
        System.out.println("-----Тест первого пункта-----\n");

        test("Верные данные", "ivan2008", "Иван Иванов Иванович","ivan2008@example.com" );

        System.out.println("Тест пустых значений");
        test("Пустой юзернейм", "", "Иван Иванов Иванович","ivan2008@example.com" );
        test("Пустой ФИО", "ivan2008", "","ivan2008@example.com" );
        test("Пустой имейл", "ivan2008", "Иван Иванов Иванович","" );

        System.out.println("Тест юзернейм");
        test("Слишком короткий юзернейм", "iv", "Иван Иванов Иванович","ivan2008@example.com" );
        test("Недопустимые символы в юзернейме", "иван", "Иван Иванов Иванович","ivan2008@example.com" );

        System.out.println("Тест имеил");
        test("Нет собачки", "ivan2008", "Иван Иванов Иванович","ivan2008example.com" );
        test("Нет точки", "ivan2008", "Иван Иванов Иванович","ivan2008@examplecom" );

        System.out.println("\n-----Тест второго пункта-----\n");

        Permission permission = new Permission("WRITE", "ivan2008", "write some data");
        System.out.println(permission.format());

        System.out.println("\n-----Тест третьего пункта-----\n");

        Role role = new Role("ivan2008", "description");
        Role role1 = new Role("stepan2021", "stepan brawlstars");
        Permission permission1 = new Permission("Read", "stepan2021", "read some books");
        role.addPermission(permission);
        role.addPermission(permission1);
        role1.addPermission(permission1);
        System.out.println(role.equals(role1));
        System.out.println(role.equals(role));
        System.out.println(role.toString());
        System.out.println(role.format());

        System.out.println("\n-----Тест четвертого пункта-----\n");

        AssignmentMetadata assignment = AssignmentMetadata.now("ivan2008", "some reason");
        System.out.println(assignment.format());
    }

    public static void test(String testname, String username, String fullName, String email){
        System.out.println(testname);
        try {
            User user = User.create(username, fullName, email);
            System.out.println("Успешное создание: " + user.format());
        } catch (IllegalArgumentException error){
            System.out.println("Ошибка: " + error.getMessage());
        }
    }
}


import model.*;

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

        System.out.println("\n-----Тест седьмого точка первого пункта-----\n");

        User user = User.create("assigmenttest", "Тест Тестович Тест", "test@example.com");
        Role roleAssigment = new Role("moderator", "test for assigment");
        AssignmentMetadata assignmenttest = AssignmentMetadata.now("ivan2008", "some reason");
        PermanentAssignment permanent = new PermanentAssignment(user, roleAssigment,assignmenttest);
        System.out.println(permanent.assignmentType());
        System.out.println(permanent.isActive());
        permanent.revoke();
        System.out.println(permanent.isActive());
        System.out.println(permanent.isRevoked());
        System.out.println(permanent.summary());

        System.out.println("\n-----Тест седьмого точка второго пункта-----\n");

        TemporaryAssignment temporaryAssignmentTest = new TemporaryAssignment(user, roleAssigment, assignmenttest, "29.03.2026 00:00:00");
        System.out.println(temporaryAssignmentTest.assignmentType());
        System.out.println(temporaryAssignmentTest.isActive());
        System.out.println(temporaryAssignmentTest.isExpired());
        System.out.println(temporaryAssignmentTest.summary());
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


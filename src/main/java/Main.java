import model.User;

public class Main {
    public static void main(String[] args) {
        System.out.println("Тест первого пункта");

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


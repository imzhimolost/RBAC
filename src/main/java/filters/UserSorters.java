package filters;

import model.User;
import java.util.Comparator;

public class UserSorters {
    public Comparator<User> byUsername(){
        return Comparator.comparing(User::username);
    }

    public Comparator<User> byFullName(){
        return Comparator.comparing(User::fullName);
    }

    public Comparator<User> byEmail(){
        return Comparator.comparing(User::email);
    }
}

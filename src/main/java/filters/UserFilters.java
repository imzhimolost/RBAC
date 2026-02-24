package filters;

import model.User;

public class UserFilters {
    public UserFilter byUsername(String username){
        return user -> user.username().equals(username);
    }

    public UserFilter byUsernameContains(String substring){
        return user -> user.username().toLowerCase().contains(substring.toLowerCase());
    }

    public UserFilter byEmail(String email){
        return user -> user.email().equals(email);
    }

    public UserFilter byEmailDomain(String domain){
        return user -> user.email().toLowerCase().contains(domain.toLowerCase());
    }

    public UserFilter byFullNameContains(String substring){
        return user -> user.fullName().toLowerCase().contains(substring.toLowerCase());
    }
}

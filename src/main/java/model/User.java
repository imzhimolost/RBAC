package model;

import java.util.regex.Pattern;

public record User(String username, String fullName, String email) {
    public static User create(String username, String fullName, String email) {
        if(username == null || username.isBlank()) throw new IllegalArgumentException("Error: username required");
        if(fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Error: fullName required");
        if(email == null || email.isBlank()) throw new IllegalArgumentException("Error: email required");
//        if(Pattern.matches(USERNAME_REGEX, username))
        if(!Pattern.matches("^[a-zA-z0-9_]{3,20}$", username)) throw new IllegalArgumentException("Error: username don`t match parameters(allows A-Z, 0-9 and underscore)");
        if(!Pattern.matches("^.+@.+\\..+$", email)) throw new IllegalArgumentException("Error: wrong email format");

        return new User(username, fullName, email);
    }

    public String format(){
        return String.format("%s (%s) <%s>", username, fullName, email);
    }
}

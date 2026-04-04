package managers;

import filters.UserFilter;
import model.User;
import util.ValidationUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager implements Repository<User>{
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void add(User user){
        ValidationUtils.requireNonEmpty(user.username(), "Username");
        ValidationUtils.requireNonEmpty(user.email(), "Email");

        if (!ValidationUtils.isValidUsername(user.username())) {
            throw new IllegalArgumentException("Wrong username format");
        }
        if (!ValidationUtils.isValidEmail(user.email())) {
            throw new IllegalArgumentException("Wrong email format");
        }

        if(users.putIfAbsent(user.username(), user) != null){
            throw new IllegalArgumentException("User already added");
        }
    }

    @Override
    public boolean remove(User user){
        return users.remove(user.username()) != null;
    }

    @Override
    public Optional<User> findById(String id){
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByUsername(String username){
        return findById(username);
    }

    public Optional<User> findByEmail(String email){
        return users.values().stream()
                .filter(user -> user.email().equalsIgnoreCase(email)).findFirst();
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(users.values());
    }

    public List<User> findByFilter(UserFilter filter){
        return users.values().stream().filter(filter::test).toList();
    }

    public List<User> findByFilterParallel(UserFilter filter) {
        return users.values().parallelStream().filter(filter::test).toList();
    }

    public List<User> findAll(UserFilter filter, Comparator<User> sorter){
        return users.values().stream().filter(filter::test)
                .sorted(sorter).toList();
    }

    public boolean exists(String username){
        return users.containsKey(username);
    }

    public void update(String username, String newFullName, String newEmail){
        ValidationUtils.requireNonEmpty(newFullName, "Full Name");
        if (!ValidationUtils.isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Wrong email format");
        }

        String normalizedFullName = ValidationUtils.normalizeString(newFullName);

        User update = User.create(username, normalizedFullName, newEmail);
        if (users.replace(username, update) == null) {
            throw new NoSuchElementException("No such user in data");
        }
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserManager userManager = (UserManager) o;
        return users.equals(userManager.users);
    }

    @Override
    public int hashCode(){
        return users.hashCode();
    }

    @Override
    public int count(){
        return users.size();
    }

    @Override
    public void clear(){
        users.clear();
    }
}

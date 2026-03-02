package managers;

import filters.UserFilter;
import model.Role;
import model.User;

import java.util.*;

public class UserManager implements Repository<User>{
    private final Map<String, User> users = new HashMap<>();

    @Override
    public void add(User user){
        if(users.containsKey(user.username())){
            throw new IllegalArgumentException("User already added");
        }
        users.put(user.username(), user);
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

    public List<User> findAll(UserFilter filter, Comparator<User> sorter){
        return users.values().stream().filter(filter::test)
                .sorted(sorter).toList();
    }

    public boolean exists(String username){
        return users.containsKey(username);
    }

    void update(String username, String newFullName, String newEmail){
        if (!exists(username)) {
            throw new NoSuchElementException("No such user in data");
        }

        User update = User.create(username, newFullName, newEmail);
        users.put(username, update);
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

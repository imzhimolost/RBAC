package model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Role {
    String id;
    String name;
    String description;
    Set<Permission> permissions;

    public Role(String username, String userdescription){
        id = "role_" + UUID.randomUUID().toString();
        name = username;
        description = userdescription;
        permissions = new HashSet<>();
    }

    public void addPermission(Permission permission){
        permissions.add(permission);
    }

    public void removePermission(Permission permission){
        permissions.remove(permission);
    }

    public boolean hasPermission(Permission permission){
        return permissions.contains(permission);
    }

    public boolean hasPermission(String permissionName, String resource){
        for(Permission item : permissions){
            if(item.matches(permissionName, resource)){
                return true;
            }
        }
        return false;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id.equals(role.id);
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    public String toString(){
        String result = "Role: {id: '" + id + "', name: '" + name + "', description: '" + description + "', permissions: \n";
        for (Permission item : permissions){
            result = result + " - " + item.format() + "\n";
        }
        return result;
    }

    public String format(){
        String result = "Role: " + name + " [ID: " + id + "] \nDescription: " + description + " \nPermissions (" + permissions.size() + "):\n";
        for(Permission item : permissions){
            result = result + " - " + item.format() + "\n";
        }
        return result;
    }
}

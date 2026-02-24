package filters;

import model.Role;
import model.Permission;

public class RoleFilters {
    public RoleFilter byName(String name){
        return role -> role.getName().equals(name);
    }

    public RoleFilter byNameContains(String substring){
        return role -> role.getName().toLowerCase().contains(substring.toLowerCase());
    }

    public RoleFilter hasPermission(Permission permission){
        return role -> role.hasPermission(permission);
    }

    public RoleFilter hasPermission(String permissionName, String resource){
        return role -> role.hasPermission(permissionName, resource);
    }

    public RoleFilter hasAtLeastNPermissions(int n){
        return role -> role.getPermissions().size() >= n;
    }
}

package managers;

import filters.RoleFilter;
import model.Permission;
import model.Role;

import java.util.*;

public class RoleManager implements Repository<Role>{
    private final Map<String, Role> rolesById = new HashMap<>();
    private final Map<String, Role> rolesByName = new HashMap<>();

    @Override
    public void add(Role role){
        if(rolesByName.containsKey(role.getName())){
            throw new IllegalArgumentException("Role name must be unique");
        }
        if(rolesById.containsKey(role.getId())){
            throw new IllegalArgumentException("Role ID must be unique");
        }

        rolesByName.put(role.getName(), role);
        rolesById.put(role.getId(), role);
    }

    @Override
    public boolean remove(Role role){
        Role removedRole = rolesById.remove(role.getId());

        if(removedRole != null){
            rolesByName.remove(removedRole.getName());
            return true;
        }

        return false;
    }

    @Override
    public Optional<Role> findById(String id){
        return Optional.ofNullable(rolesById.get(id));
    }

    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(rolesByName.get(name));
    }

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(rolesById.values());
    }

    public List<Role> findAll(RoleFilter filter, Comparator<Role> sorter) {
        return rolesById.values().stream()
                .filter(filter::test)
                .sorted(sorter)
                .toList();
    }

    public List<Role> findRolesWithPermission(String permissionName, String resource){
        String searchName = permissionName.toUpperCase().replace(" ", "_");
        String searchResource = resource.toLowerCase();

        return rolesById.values().stream()
                .filter(role -> role.hasPermission(searchName, searchResource))
                .toList();
    }

    public void addPermissionToRole(String roleName, Permission permission) {
        findByName(roleName).ifPresent(role -> role.addPermission(permission));
    }

    public void removePermissionFromRole(String roleName, Permission permission) {
        findByName(roleName).ifPresent(role -> role.removePermission(permission));
    }

    public boolean exists(String name) {
        return rolesByName.containsKey(name);
    }

    @Override public int count() {
        return rolesById.size();
    }

    @Override public void clear() {
        rolesById.clear();
        rolesByName.clear();
    }
}

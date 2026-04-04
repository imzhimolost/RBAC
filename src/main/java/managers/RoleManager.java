package managers;

import filters.AssignmentFilter;
import filters.RoleFilter;
import model.Permission;
import model.Role;
import model.RoleAssignment;
import util.ValidationUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RoleManager implements Repository<Role>{
    private final Map<String, Role> rolesById = new ConcurrentHashMap<>();
    private final Map<String, Role> rolesByName = new ConcurrentHashMap<>();

    @Override
    public void add(Role role){
        ValidationUtils.requireNonEmpty(role.getName(), "Имя роли");
        ValidationUtils.requireNonEmpty(role.getDescription(), "Описание роли");

        String normalizedName = ValidationUtils.normalizeString(role.getName());

        synchronized (this){
            if(rolesByName.containsKey(normalizedName) || rolesById.containsKey(role.getId())){
                throw new IllegalArgumentException("Role already exist");
            }
            rolesByName.put(normalizedName, role);
            rolesById.put(role.getId(), role);
        }
    }

    @Override
    public boolean remove(Role role){
        synchronized (this){
            Role removedRole = rolesById.remove(role.getId());
            if(removedRole != null){
                rolesByName.remove(ValidationUtils.normalizeString(removedRole.getName()));
                return true;
            }
            return false;
        }
    }

    @Override
    public Optional<Role> findById(String id){
        return Optional.ofNullable(rolesById.get(id));
    }

    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(rolesByName.get(name));
    }

    public List<Role> findByFilter(RoleFilter filter) {
        return rolesByName.values().stream().filter(filter::test).toList();
    }

    public List<Role> findByFilterParallel(RoleFilter filter) {
        return rolesByName.values().parallelStream().filter(filter::test).toList();
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
        ValidationUtils.requireNonEmpty(roleName, "Имя роли");
        synchronized (this){
            findByName(roleName).ifPresent(role -> role.addPermission(permission));
        }
    }

    public void removePermissionFromRole(String roleName, Permission permission) {
        ValidationUtils.requireNonEmpty(roleName, "Имя роли");
        synchronized (this){
            findByName(roleName).ifPresent(role -> role.removePermission(permission));
        }
    }

    public boolean exists(String name) {
        return rolesByName.containsKey(name);
    }

    @Override public int count() {
        return rolesById.size();
    }

    @Override public void clear() {
        synchronized (this) {
            rolesById.clear();
            rolesByName.clear();
        }
    }
}

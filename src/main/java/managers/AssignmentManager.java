package managers;

import filters.AssignmentFilter;
import model.Permission;
import model.Role;
import model.RoleAssignment;
import model.User;

import java.util.*;
import java.util.stream.Collectors;

public class AssignmentManager implements Repository<RoleAssignment>{
    private final Map<String, RoleAssignment> assignments = new HashMap<>();

    @Override
    public void add(RoleAssignment assignment) {
        boolean duplicate = assignments.values().stream()
                .filter(a -> a.user().equals(assignment.user()))
                .filter(a -> a.role().equals(assignment.role()))
                .anyMatch(RoleAssignment::isActive);

        if (duplicate) {
            throw new IllegalStateException("Role already assigned");
        }
        assignments.put(assignment.assignmentId(), assignment);
    }

    public Set<Permission> getUserPermissions(User user) {
        return assignments.values().stream()
                .filter(a -> a.user().equals(user))
                .filter(RoleAssignment::isActive)
                .flatMap(a -> a.role().getPermissions().stream())
                .collect(Collectors.toSet());
    }

    public boolean userHasRole(User user, Role role){
        return assignments.values().stream()
                .anyMatch(a -> a.user().equals(user)
                        && a.role().equals(role)
                        && a.isActive());
    }

    public boolean userHasPermission(User user, String permissionName, String resource) {
        return getUserPermissions(user).stream()
                .anyMatch(p -> p.name().equalsIgnoreCase(permissionName)
                        && p.resource().equalsIgnoreCase(resource));
    }

    public List<RoleAssignment> findByFilter(AssignmentFilter filter) {
        return assignments.values().stream().filter(filter::test).toList();
    }

    public List<RoleAssignment> findByUser(User user){
        return assignments.values().stream()
                .filter(roleAssignment -> roleAssignment.user().equals(user))
                .toList();
    }

    public List<RoleAssignment> findByRole(Role role){
        return assignments.values().stream()
                .filter(roleAssignment -> roleAssignment.role().equals(role))
                .toList();
    }

    public List<RoleAssignment> findAll(AssignmentFilter filter, Comparator<RoleAssignment> sorter){
        return assignments.values().stream().filter(filter::test).sorted(sorter).toList();
    }

    public List<RoleAssignment> getActiveAssignments() {
        return assignments.values().stream().filter(RoleAssignment::isActive).toList();
    }

    public List<RoleAssignment> getExpiredAssignments(){
        return assignments.values().stream().filter(roleAssignment -> !roleAssignment.isActive()).toList();
    }

    @Override public Optional<RoleAssignment> findById(String id) {
        return Optional.ofNullable(assignments.get(id));
    }

    @Override public List<RoleAssignment> findAll() {
        return new ArrayList<>(assignments.values());
    }

//    public void revokeAssignment(String assignmentId){
//        findById(assignmentId).ifPresent(roleAssignment -> roleAssignment.);
//    }
//
//    public void extendTemporaryAssignment(String assignmentId, String newExpirationDate){
//
//    }

    @Override public boolean remove(RoleAssignment a) {
        return assignments.remove(a.assignmentId()) != null;
    }

    @Override public int count() {
        return assignments.size();
    }

    @Override public void clear() {
        assignments.clear();
    }
}

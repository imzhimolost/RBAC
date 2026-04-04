package managers;

import filters.AssignmentFilter;
import model.*;
import util.ValidationUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AssignmentManager implements Repository<RoleAssignment>{
    private final Map<String, RoleAssignment> assignments = new ConcurrentHashMap<>();

    @Override
    public void add(RoleAssignment assignment) {
        ValidationUtils.requireNonEmpty(assignment.assignmentId(), "Assignment ID");
        synchronized (this) {
            boolean duplicate = assignments.values().stream()
                    .filter(a -> a.user().equals(assignment.user()))
                    .filter(a -> a.role().equals(assignment.role()))
                    .anyMatch(RoleAssignment::isActive);

            if (duplicate) {
                throw new IllegalStateException("Role already assigned");
            }
            assignments.put(assignment.assignmentId(), assignment);
        }
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

    public List<RoleAssignment> findByFilterParallel(AssignmentFilter filter) {
        return assignments.values().parallelStream().filter(filter::test).toList();
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

    public void revokeAssignment(String assignmentId){
        ValidationUtils.requireNonEmpty(assignmentId, "Assignment ID");
        synchronized (this){
            RoleAssignment assignment = assignments.get(assignmentId);
            if (assignment instanceof PermanentAssignment) {
                PermanentAssignment permAssigment = (PermanentAssignment) assignment;

                permAssigment.revoke();
            }
        }
    }

    public void extendTemporaryAssignment(String assignmentId, String newExpirationDate){
        ValidationUtils.requireNonEmpty(assignmentId, "Assignment ID");
        synchronized (this){
            RoleAssignment assignment = assignments.get(assignmentId);
            if (assignment instanceof TemporaryAssignment) {
                TemporaryAssignment tempAssognment = (TemporaryAssignment) assignment;

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                tempAssognment.extend(LocalDateTime.parse(newExpirationDate, formatter).toString());
            }
        }
    }

    public int cleanupExpiredAssignments() {
        int count = 0;
        for (RoleAssignment assignment : assignments.values()) {
            if (assignment instanceof TemporaryAssignment temp) {
                if (temp.isActive() && temp.isExpired()) {
                    synchronized (this) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override public boolean remove(RoleAssignment a) {
        return assignments.remove(a.assignmentId()) != null;
    }

    @Override public int count() {
        return assignments.size();
    }

    @Override public void clear() {
        synchronized (this) {
            assignments.clear();
        }
    }
}

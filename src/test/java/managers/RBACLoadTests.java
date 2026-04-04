package managers;

import model.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class RBACLoadTests {

    @Test
    void testConcurrentLoad() throws InterruptedException {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager assignmentManager = new AssignmentManager();

        int threadCount = 20;
        int opsPerThread = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < opsPerThread; j++) {
                        String uniqueId = threadId + "_" + j;

                        User user = User.create("user" + uniqueId, "Full Name", "user" + uniqueId + "@test.com");
                        userManager.add(user);

                        try {
                            Role role = new Role("Role_" + j, "Description");
                            roleManager.add(role);
                        } catch (IllegalArgumentException ignored) {}

                        // 3. Назначение роли
                        Role role = roleManager.findByName("Role_" + j).orElse(null);
                        if (role != null) {
                            AssignmentMetadata meta = AssignmentMetadata.now("LoadTester", "Heavy load");
                            assignmentManager.add(new PermanentAssignment(user, role, meta));
                        }

                        userManager.findAll();
                        userManager.findByEmail("user" + uniqueId + "@test.com");
                        assignmentManager.getActiveAssignments();
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка в потоке " + Thread.currentThread().getName() + ": " + e.getMessage());
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean finished = latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(finished);
        assertEquals(0, errorCount.get());

        assertEquals(threadCount * opsPerThread, userManager.count());
        assertEquals(threadCount * opsPerThread, assignmentManager.count());

        System.out.println("Нагрузочный тест успешно завершен");
        System.out.println("Всего пользователей: " + userManager.count());
        System.out.println("Всего назначений: " + assignmentManager.count());
    }
}
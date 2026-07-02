package Test;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */


import com.motorph.dao.UserAccountDAO;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.service.AuthService;
import com.motorph.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationTest {

    @Test
    void testValidLogin() throws Exception {
        UserAccount user = account(1, 10001, "admin", "password", Role.ADMIN, true);

        AuthService authService = new AuthService(fakeUserAccountDAO(user));

        UserAccount loggedIn = authService.login("admin", "password");

        assertNotNull(loggedIn);
        assertEquals("admin", loggedIn.getUsername());
        assertEquals(Role.ADMIN, loggedIn.getRole());
        assertTrue(loggedIn.isActive());
    }

    @Test
    void testInvalidLogin_incorrectPassword() throws Exception {
        UserAccount user = account(2, 10002, "employee", "correctPassword", Role.EMPLOYEE, true);

        AuthService authService = new AuthService(fakeUserAccountDAO(user));

        Exception exception = assertThrows(
                Exception.class,
                () -> authService.login("employee", "wrongPassword")
        );

        assertEquals("Invalid password.", exception.getMessage());
    }

    @Test
    void testInvalidLogin_unknownUser() {
        AuthService authService = new AuthService(fakeUserAccountDAO());

        Exception exception = assertThrows(
                Exception.class,
                () -> authService.login("unknown.user", "password")
        );

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void testRoleAssignmentOnLogin() throws Exception {
        UserAccount financeUser = account(3, 10003, "finance", "password", Role.FINANCE, true);

        AuthService authService = new AuthService(fakeUserAccountDAO(financeUser));

        UserAccount loggedIn = authService.login("finance", "password");

        assertEquals(Role.FINANCE, loggedIn.getRole());
        assertEquals("Finance", loggedIn.getRole().getRoleName());
    }

    @Test
    void testLockedAccountLogin() throws Exception {
        UserAccount lockedUser = account(4, 10004, "locked.employee", "password", Role.EMPLOYEE, false);

        AuthService authService = new AuthService(fakeUserAccountDAO(lockedUser));

        Exception exception = assertThrows(
                Exception.class,
                () -> authService.login("locked.employee", "password")
        );

        assertEquals("User account is deactivated.", exception.getMessage());
    }

    private static UserAccount account(
            int userId,
            int employeeId,
            String username,
            String plainPassword,
            Role role,
            boolean active
    ) throws Exception {
        String passwordHash = PasswordUtil.hashPassword(plainPassword);
        return new UserAccount(userId, employeeId, username, passwordHash, role, active);
    }

    private static UserAccountDAO fakeUserAccountDAO(UserAccount... users) {
        return new FakeUserAccountDAO(users);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class FakeUserAccountDAO implements UserAccountDAO {

        private final Map<Integer, UserAccount> byUserId = new HashMap<>();
        private final Map<Integer, UserAccount> byEmployeeId = new HashMap<>();
        private final Map<String, UserAccount> byUsername = new HashMap<>();

        FakeUserAccountDAO(UserAccount... users) {
            for (UserAccount user : users) {
                addOrUpdate(user);
            }
        }

        @Override
        public UserAccount findById(String userId) {
            return byUserId.get(Integer.parseInt(userId));
        }

        @Override
        public UserAccount findByEmployeeId(String employeeId) {
            return byEmployeeId.get(Integer.parseInt(employeeId));
        }

        @Override
        public UserAccount findByUsername(String username) {
            return byUsername.get(username);
        }

        @Override
        public List<UserAccount> findAll() {
            return new ArrayList<>(byUserId.values());
        }

        @Override
        public void save(UserAccount user) {
            addOrUpdate(user);
        }

        @Override
        public void update(UserAccount user) {
            addOrUpdate(user);
        }

        @Override
        public void delete(UserAccount user) {
            if (user == null) {
                return;
            }

            byUserId.remove(user.getUserId());
            byEmployeeId.remove(user.getEmployeeId());
            byUsername.remove(user.getUsername());
        }

        @Override
        public void changeRole(int userId, Role newRole) {
            UserAccount user = byUserId.get(userId);

            if (user != null) {
                user.setRole(newRole);
                addOrUpdate(user);
            }
        }

        @Override
        public void delete(String id) {
            UserAccount user = byUserId.get(Integer.parseInt(id));
            delete(user);
        }

        @Override
        public void save(Object entity) {
            addOrUpdate((UserAccount) entity);
        }

        @Override
        public void update(Object entity) {
            addOrUpdate((UserAccount) entity);
        }

        private void addOrUpdate(UserAccount user) {
            byUserId.put(user.getUserId(), user);
            byEmployeeId.put(user.getEmployeeId(), user);
            byUsername.put(user.getUsername(), user);
        }
    }
}
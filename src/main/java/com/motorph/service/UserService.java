package com.motorph.service;

import com.motorph.dao.UserAccountDAO;
import com.motorph.exception.UnauthorizedException;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.util.PasswordUtil;
import com.motorph.util.Session;

import java.util.List;

public class UserService {

    private final UserAccountDAO userDAO;

    public UserService(UserAccountDAO userAccountDAO) {
        this.userDAO = userAccountDAO;
    }

    // Only IT or Admin may manage user accounts.
    private void authorizeAdmin() {
        UserAccount current = Session.getCurrentUser();
        if (current == null) {
            throw new UnauthorizedException("No active session found.");
        }
        if (current.getRole() != Role.IT && current.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only IT or Admin can manage user accounts.");
        }
    }

    public void createUser(int employeeId, String username, String password, Role role) {
        authorizeAdmin();

        String hash = hashOrThrow(password);
        UserAccount newUser = new UserAccount(0, employeeId, username, hash, role, true);
        userDAO.save(newUser);
    }

    public void updateUser(UserAccount user) {
        userDAO.update(user);
    }

    // Resets to the default password ("password").
    public void resetPassword(int userId) {
        authorizeAdmin();
        UserAccount user = findByIdOrThrow(userId);
        user.setPasswordHash(hashOrThrow("password"));
        userDAO.update(user);
    }

    // Resets to a caller-supplied plain-text password.
    public void resetPassword(int userId, String newPassword) {
        UserAccount user = findByIdOrThrow(userId);
        user.setPasswordHash(hashOrThrow(newPassword));
        userDAO.update(user);
    }

    public void changeRole(int userId, Role newRole) {
        authorizeAdmin();
        UserAccount user = findByIdOrThrow(userId);
        user.setRole(newRole);
        userDAO.update(user);
    }

    public void deactivateUser(int userId) {
        authorizeAdmin();
        UserAccount user = findByIdOrThrow(userId);
        user.setActive(false);
        userDAO.update(user);
    }

    public void activateUser(int userId) {
        authorizeAdmin();
        UserAccount user = findByIdOrThrow(userId);
        user.setActive(true);
        userDAO.update(user);
    }

    public List<UserAccount> listUsers() {
        authorizeAdmin();
        return userDAO.findAll();
    }

    public UserAccount findByEmployeeId(int employeeId) {
        authorizeAdmin();
        return userDAO.findByEmployeeId(String.valueOf(employeeId));
    }

    public UserAccount findByUsername(String username) {
        authorizeAdmin();
        return userDAO.findByUsername(username);
    }

    public UserAccount findById(int userId) {
        authorizeAdmin();
        return findByIdOrThrow(userId);
    }

    private UserAccount findByIdOrThrow(int userId) {
        UserAccount user = userDAO.findById(String.valueOf(userId));
        if (user == null) {
            throw new RuntimeException("User not found: id=" + userId);
        }
        return user;
    }

    private String hashOrThrow(String plain) {
        try {
            return PasswordUtil.hashPassword(plain);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed.", e);
        }
    }
}

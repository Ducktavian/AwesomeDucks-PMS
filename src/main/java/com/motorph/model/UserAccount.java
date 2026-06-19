package com.motorph.model;

public class UserAccount {

    private int userId;             // user_account.user_account_id
    private int employeeId;         // user_account.employee_id  (was String employeeNumber)
    private String username;        // user_account.username
    private String passwordHash;    // user_account.password_hash
    private Role role;              // resolved from account_role + user_role
    private boolean active;         // user_account.is_active

    public UserAccount(int userId, int employeeId, String username,
                       String passwordHash, Role role, boolean active) {
        this.userId       = userId;
        this.employeeId   = employeeId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.active       = active;
    }

    
    // Getters
    public int getUserId() {
        return userId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    // Setters (only mutable fields)
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "UserAccount{userId=" + userId
                + ", employeeId=" + employeeId
                + ", username='" + username + '\''
                + ", role=" + role
                + ", active=" + active + '}';
    }
}
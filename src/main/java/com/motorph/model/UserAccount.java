package com.motorph.model;

public class UserAccount {

    private int userId;
    private int employeeId;
    private String username;
    private String passwordHash;
    private Role role;
    private boolean active;

    public UserAccount(int userId, int employeeId, String username,
                       String passwordHash, Role role, boolean active) {
        this.userId = userId;
        this.employeeId = employeeId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public int getUserId() { return userId; }
    public int getEmployeeId() { return employeeId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(Role role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }

    // NEW: needed so the DAO can write back the generated PK after an insert,
    // and so the Users screen can edit the username on update.
    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() {
        return "UserAccount{userId=" + userId + ", employeeId=" + employeeId
                + ", username='" + username + "', role=" + role + ", active=" + active + '}';
    }
}

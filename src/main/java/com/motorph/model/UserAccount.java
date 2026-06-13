package com.motorph.model;

/**
 *
 * @author Ducktavian
 */
public class UserAccount {
    
    private int userId;
    private String username;
    private String passwordHash;
    private Role role;
    private String employeeNumber;
    private boolean active;
    
    public UserAccount(int userId, String employeeNumber, String username, String passwordHash, Role role, boolean active) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.employeeNumber = employeeNumber;
        this.active = active;
    }
    
    // GETTERS  
    public int getUserId() {
        return userId;
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

    public String getEmployeeNumber() {
        return employeeNumber;
    }
    
    public boolean isActive() {
        return active;
    }
    
    // SETTERS

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}

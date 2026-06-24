package com.motorph.model;

public enum Role {

    ADMIN(1, "Admin"),
    HR(2, "HR"),
    IT(3, "IT"),
    FINANCE(4, "Finance"),
    EMPLOYEE(5, "Employee");

    private final int roleId;
    private final String roleName;

    Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public int getRoleId() { return roleId; }
    public String getRoleName() { return roleName; }

    public static Role fromId(int id) {
        for (Role r : values()) {
            if (r.roleId == id) return r;
        }
        return EMPLOYEE;
    }

    public static Role fromName(String name) {
        if (name == null) return EMPLOYEE;
        for (Role r : values()) {
            if (r.roleName.equalsIgnoreCase(name)) return r;
        }
        return EMPLOYEE;
    }

    @Override
    public String toString() {
        return roleName;
    }
}

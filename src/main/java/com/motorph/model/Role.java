package com.motorph.model;

/**
 * Maps to the user_role table (role_id 1-5).
 * The integer value matches the primary key in the database.
 */
public enum Role {

    SYSTEM_ADMINISTRATOR(1, "System Administrator"),
    HR_MANAGER          (2, "HR Manager"),
    PAYROLL_MANAGER     (3, "Payroll Manager"),
    DEPARTMENT_HEAD     (4, "Department Head"),
    EMPLOYEE            (5, "Employee");

    private final int    roleId;
    private final String roleName;

    Role(int roleId, String roleName) {
        this.roleId   = roleId;
        this.roleName = roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    /**
     * Look up a Role by its database primary key.
     * Returns EMPLOYEE as a safe default if the id is unrecognised.
     */
    public static Role fromId(int id) {
        for (Role r : values()) {
            if (r.roleId == id) return r;
        }
        return EMPLOYEE;
    }

    /**
     * Look up a Role by the role_name stored in user_role.role_name.
     * Comparison is case-insensitive.
     */
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
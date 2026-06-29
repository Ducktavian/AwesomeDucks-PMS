package com.motorph.ui.it;

import com.motorph.model.Employee;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;


public class UserAccountEntry {

    // Identity
    private int userId;        // 0 when creating a brand-new account
    private int employeeId;

    // Read-only employee details (sourced from the employee table)
    private String employeeNo;
    private String fullName;
    private String position;
    private String supervisor;
    private String employmentStatus;

    // Editable account fields
    private String username;
    private String newPassword; // null/blank = leave the existing password unchanged
    private Role role;
    private boolean active = true;

    public UserAccountEntry() {}

    // NEW: build a display entry from a persisted account + its employee record.
    public static UserAccountEntry fromAccount(UserAccount account, Employee employee) {
        UserAccountEntry entry = new UserAccountEntry();
        entry.userId = account.getUserId();
        entry.employeeId = account.getEmployeeId();
        entry.employeeNo = String.valueOf(account.getEmployeeId());
        entry.username = account.getUsername();
        entry.role = account.getRole();
        entry.active = account.isActive();

        if (employee != null) {
            entry.fullName = employee.getFullName();
            entry.position = employee.getPosition();
            entry.supervisor = employee.getImmediateSupervisor();
            entry.employmentStatus = employee.getStatus();
        }
        return entry;
    }

    // NEW: row for the Users table — matches the column order
    // { Employee No., Name, Status, Position, Immediate Supervisor, Role }.
    public String[] toTableRow() {
        return new String[] {
            employeeNo,
            fullName == null ? "" : fullName,
            active ? "Active" : "Inactive",
            position == null ? "" : position,
            supervisor == null ? "" : supervisor,
            role == null ? "" : role.getRoleName()
        };
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

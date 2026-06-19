package com.motorph.ui.IT;

/**
 * In-memory user account record for the IT Users screen.
 */
public class UserAccountEntry {

    private String employeeNo;
    private String firstName;
    private String lastName;
    private String username;
    private String supervisor;
    private String position;
    private String employmentStatus;
    private String accountStatus;
    private String role;

    public UserAccountEntry() {}

    public UserAccountEntry(String employeeNo, String firstName, String lastName,
            String username, String supervisor, String position,
            String employmentStatus, String accountStatus, String role) {
        this.employeeNo = employeeNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.supervisor = supervisor;
        this.position = position;
        this.employmentStatus = employmentStatus;
        this.accountStatus = accountStatus;
        this.role = role;
    }

    public static UserAccountEntry fromSampleRow(String[] row) {
        String[] nameParts = row[1].split(" ", 2);
        String first = nameParts.length > 0 ? nameParts[0] : row[1];
        String last  = nameParts.length > 1 ? nameParts[1] : "";

        String acctStatus = "Inactive".equalsIgnoreCase(row[2]) ? "Deactive" : "Active";
        String username = (first + "." + last.replace(" ", ""))
                .toLowerCase().replaceAll("[^a-z.]", "");

        return new UserAccountEntry(
            row[0], first, last, username,
            row[4], row[3], "Regular", acctStatus, row[5]
        );
    }

    public String[] toTableRow() {
        String displayStatus = "Deactive".equalsIgnoreCase(accountStatus) ? "Inactive" : "Active";
        return new String[] {
            employeeNo,
            firstName + " " + lastName,
            displayStatus,
            position,
            supervisor,
            role
        };
    }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

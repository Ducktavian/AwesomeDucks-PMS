package com.motorph.util;

import com.motorph.config.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkAccountGenerator {

    /*
     * Final role names:
     * Admin
     * IT
     * HR
     * Finance
     * Employee
     *
     * IMPORTANT:
     * These names must exist exactly in your user_role table.
     */

    private static final Map<String, String> POSITION_TO_ROLE = new HashMap<>();

    static {
        // Admin
        POSITION_TO_ROLE.put("Chief Executive Officer", "Admin");
        POSITION_TO_ROLE.put("Chief Finance Officer", "Admin");
        POSITION_TO_ROLE.put("Chief Operating Officer", "Admin");
        POSITION_TO_ROLE.put("Chief Marketing Officer", "Admin");

        // IT
        POSITION_TO_ROLE.put("IT Operations and Systems", "IT");

        // HR
        POSITION_TO_ROLE.put("HR Manager", "HR");
        POSITION_TO_ROLE.put("HR Team Leader", "HR");
        POSITION_TO_ROLE.put("HR Rank and File", "HR");

        // Finance
        POSITION_TO_ROLE.put("Payroll Manager", "Finance");
        POSITION_TO_ROLE.put("Payroll Team Leader", "Finance");
        POSITION_TO_ROLE.put("Payroll Rank and File", "Finance");
        POSITION_TO_ROLE.put("Accounting Head", "Finance");

        // Employee
        POSITION_TO_ROLE.put("Account Manager", "Employee");
        POSITION_TO_ROLE.put("Account Team Leader", "Employee");
        POSITION_TO_ROLE.put("Account Rank and File", "Employee");
        POSITION_TO_ROLE.put("Sales & Marketing", "Employee");
        POSITION_TO_ROLE.put("Supply Chain and Logistics", "Employee");
        POSITION_TO_ROLE.put("Customer Service and Relations", "Employee");

        // Default fallback
        POSITION_TO_ROLE.put("DEFAULT", "Employee");
    }

    public static void main(String[] args) {
        System.out.println("-- Bulk Account Generator for MotorPH");
        System.out.println("-- Generated: " + LocalDate.now());
        System.out.println("-- Roles: Admin, IT, HR, Finance, Employee");
        System.out.println("-- Password Format: employeeId + first name initial");
        System.out.println("-- Example: Manuel III Garcia = 10001M");
        System.out.println();

        try {
            verifyRequiredRolesExist();

            /*
             * OPTION 1:
             * Create SQL statements for employees without accounts.
             * Uncomment this only if you want to generate missing account SQL.
             */
            // generateFromDatabase();

            /*
             * OPTION 2:
             * Fix existing accounts.
             * This removes duplicate roles, assigns the correct role,
             * and resets all passwords.
             */
            fixExistingAccountRoles();
            // resetAllPasswordsByEmployeeInitial();

            /*
             * AFTER THIS WORKS:
             * Comment out resetAllPasswordsByEmployeeInitial()
             * so passwords do not reset every time you run this file.
             */

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================
    // Check Roles
    // =========================================================

    private static void verifyRequiredRolesExist() throws SQLException {
        System.out.println("-- Checking required roles in user_role table...");

        String[] requiredRoles = {
                "Admin",
                "IT",
                "HR",
                "Finance",
                "Employee"
        };

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (String roleName : requiredRoles) {
                if (!roleExists(conn, roleName)) {
                    throw new SQLException(
                            "Missing role in user_role table: " + roleName +
                            "\nPlease insert this role first before running BulkAccountGenerator."
                    );
                }
            }
        }

        System.out.println("-- All required roles exist.");
        System.out.println();
    }

    private static boolean roleExists(Connection conn, String roleName) throws SQLException {
        String sql = "SELECT role_id FROM user_role WHERE role_name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // =========================================================
    // Fix Existing Account Roles
    // =========================================================

    public static void fixExistingAccountRoles() throws SQLException {
        System.out.println("-- Fixing existing account roles...");
        System.out.println("-- This will remove all existing roles for each employee");
        System.out.println("-- and assign one role based on their position.");
        System.out.println();

        List<EmployeeData> employees = fetchAllEmployeesWithAccounts();

        if (employees.isEmpty()) {
            System.out.println("-- No employees with accounts found.");
            return;
        }

        System.out.println("-- Found " + employees.size() + " employees with accounts.");

        int fixed = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (EmployeeData emp : employees) {
                String roleName = getRoleForPosition(emp.position);
                int userId = getUserId(conn, emp.employeeId);

                if (userId == -1) {
                    continue;
                }

                deleteAllRoles(conn, userId);
                assignRole(conn, userId, roleName);

                fixed++;

                System.out.println(
                        "  Fixed employee " + emp.employeeId +
                        " (" + emp.firstName + " " + emp.lastName + ")" +
                        " | Position: " + emp.position +
                        " -> Role: " + roleName
                );
            }
        }

        System.out.println("-- Fixed " + fixed + " accounts.");
    }

    // =========================================================
    // Reset Existing Passwords
    // =========================================================

    public static void resetAllPasswordsByEmployeeInitial() throws SQLException {
        System.out.println();
        System.out.println("-- Resetting all account passwords...");
        System.out.println("-- Password format: employeeId + first name initial");
        System.out.println();

        String selectSql = """
                SELECT ua.user_account_id,
                       ua.employee_id,
                       ua.username,
                       e.first_name,
                       e.last_name
                FROM user_account ua
                JOIN employee e ON ua.employee_id = e.employee_id
                ORDER BY ua.employee_id
                """;

        String updateSql = """
                UPDATE user_account
                SET password_hash = ?
                WHERE user_account_id = ?
                """;

        int updated = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectPs = conn.prepareStatement(selectSql);
             ResultSet rs = selectPs.executeQuery();
             PreparedStatement updatePs = conn.prepareStatement(updateSql)) {

            while (rs.next()) {
                int userAccountId = rs.getInt("user_account_id");
                int employeeId = rs.getInt("employee_id");
                String username = rs.getString("username");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                String defaultPassword = generateDefaultPassword(employeeId, firstName);
                String hashedPassword;

                try {
                    hashedPassword = PasswordUtil.hashPassword(defaultPassword);
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Failed to hash password for employee " + employeeId,
                            e
                    );
                }

                updatePs.setString(1, hashedPassword);
                updatePs.setInt(2, userAccountId);
                updatePs.executeUpdate();

                updated++;

                System.out.println(
                        "  Reset password for employee " + employeeId +
                        " (" + firstName + " " + lastName + " / " + username + ")" +
                        " -> " + defaultPassword
                );
            }
        }

        System.out.println("-- Reset passwords for " + updated + " accounts.");
    }

    // =========================================================
    // Generate Missing Accounts
    // =========================================================

    public static void generateFromDatabase() throws SQLException {
        List<EmployeeData> employees = fetchEmployeesWithoutAccounts();

        if (employees.isEmpty()) {
            System.out.println("-- All employees already have accounts.");
            return;
        }

        System.out.println("-- Found " + employees.size() + " employees without accounts.");
        System.out.println();

        for (EmployeeData emp : employees) {
            generateUserAccountSQL(emp);
        }
    }

    private static void generateUserAccountSQL(EmployeeData emp) {
        String username = generateUsername(emp.firstName, emp.lastName);
        String plainPassword = generateDefaultPassword(emp.employeeId, emp.firstName);
        String roleName = getRoleForPosition(emp.position);

        String hashedPassword;

        try {
            hashedPassword = PasswordUtil.hashPassword(plainPassword);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to hash password for employee " + emp.employeeId,
                    ex
            );
        }

        System.out.println("-- Employee: " + emp.firstName + " " + emp.lastName + " (ID: " + emp.employeeId + ")");
        System.out.println("-- Username: " + username);
        System.out.println("-- Password: " + plainPassword);
        System.out.println("-- Position: " + emp.position);
        System.out.println("-- Role: " + roleName);

        System.out.println("INSERT INTO user_account (employee_id, username, password_hash, is_active, created_at)");
        System.out.println("VALUES (" + emp.employeeId + ", '" + username + "', '" + hashedPassword + "', 1, NOW());");

        System.out.println("SET @user_id = LAST_INSERT_ID();");
        System.out.println("SET @role_id = (SELECT role_id FROM user_role WHERE role_name = '" + roleName + "');");
        System.out.println("INSERT INTO account_role (user_account_id, role_id, created_at)");
        System.out.println("VALUES (@user_id, @role_id, NOW());");
        System.out.println();
    }

    // =========================================================
    // Fetch Employees
    // =========================================================

    private static List<EmployeeData> fetchAllEmployeesWithAccounts() throws SQLException {
        List<EmployeeData> list = new ArrayList<>();

        String sql = """
                SELECT e.employee_id,
                       e.first_name,
                       e.last_name,
                       p.position_name
                FROM employee e
                LEFT JOIN employee_position p ON e.position_id = p.position_id
                JOIN user_account ua ON e.employee_id = ua.employee_id
                ORDER BY e.employee_id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new EmployeeData(
                        rs.getInt("employee_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("position_name")
                ));
            }
        }

        return list;
    }

    private static List<EmployeeData> fetchEmployeesWithoutAccounts() throws SQLException {
        List<EmployeeData> list = new ArrayList<>();

        String sql = """
                SELECT e.employee_id,
                       e.first_name,
                       e.last_name,
                       p.position_name
                FROM employee e
                LEFT JOIN employee_position p ON e.position_id = p.position_id
                LEFT JOIN user_account ua ON e.employee_id = ua.employee_id
                WHERE ua.employee_id IS NULL
                ORDER BY e.employee_id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new EmployeeData(
                        rs.getInt("employee_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("position_name")
                ));
            }
        }

        return list;
    }

    // =========================================================
    // Username / Password Generation
    // =========================================================

    private static String generateUsername(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.toLowerCase().trim();
        String last = lastName == null ? "" : lastName.toLowerCase().trim();

        String base = first + "." + last;

        base = base.replaceAll("[^a-z.]", "");
        base = base.replaceAll("\\.+", ".");
        base = base.replaceAll("^\\.|\\.$", "");

        return base;
    }

    private static String generateDefaultPassword(int employeeId, String firstName) {
        String initial = "X";

        if (firstName != null && !firstName.isBlank()) {
            initial = firstName.trim().substring(0, 1).toUpperCase();
        }

        return employeeId + initial;
    }

    // =========================================================
    // Role Mapping
    // =========================================================

    private static String getRoleForPosition(String position) {
        if (position == null || position.isBlank()) {
            return POSITION_TO_ROLE.get("DEFAULT");
        }

        String cleanPosition = position.trim();

        for (Map.Entry<String, String> entry : POSITION_TO_ROLE.entrySet()) {
            if (entry.getKey().equals("DEFAULT")) {
                continue;
            }

            if (cleanPosition.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }

        for (Map.Entry<String, String> entry : POSITION_TO_ROLE.entrySet()) {
            if (entry.getKey().equals("DEFAULT")) {
                continue;
            }

            if (cleanPosition.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }

        return POSITION_TO_ROLE.get("DEFAULT");
    }

    // =========================================================
    // DB Helpers
    // =========================================================

    private static int getUserId(Connection conn, int employeeId) throws SQLException {
        String sql = "SELECT user_account_id FROM user_account WHERE employee_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_account_id");
                }
            }
        }

        return -1;
    }

    private static void deleteAllRoles(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM account_role WHERE user_account_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    private static void assignRole(Connection conn, int userId, String roleName) throws SQLException {
        String sql = """
                INSERT INTO account_role (user_account_id, role_id, created_at)
                SELECT ?, role_id, NOW()
                FROM user_role
                WHERE role_name = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, roleName);

            int inserted = ps.executeUpdate();

            if (inserted == 0) {
                throw new SQLException("Role not found in user_role table: " + roleName);
            }
        }
    }

    // =========================================================
    // Inner Data Class
    // =========================================================

    private static class EmployeeData {
        private final int employeeId;
        private final String firstName;
        private final String lastName;
        private final String position;

        private EmployeeData(int employeeId,
                             String firstName,
                             String lastName,
                             String position) {
            this.employeeId = employeeId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.position = position;
        }
    }
}
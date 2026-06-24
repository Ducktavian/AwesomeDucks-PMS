package com.motorph.util;

import com.motorph.config.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserAccountSqlSeedGenerator {

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

        POSITION_TO_ROLE.put("DEFAULT", "Employee");
    }

    public static void main(String[] args) {
        System.out.println("-- Generated user_account + account_role seed SQL");
        System.out.println("-- Generated on: " + LocalDate.now());
        System.out.println("-- Roles: Admin, IT, HR, Finance, Employee");
        System.out.println("-- Password format: employee_id + first name initial");
        System.out.println("-- Example: Manuel III Garcia = 10001M");
        System.out.println();

        try {
            printUserAccountInsertSql();
            printAccountRoleInsertSql();
            printLoginGuide();

        } catch (Exception e) {
            System.err.println("-- ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printUserAccountInsertSql() throws Exception {
        String sql = """
            SELECT e.employee_id,
                   e.first_name,
                   e.last_name,
                   p.position_name
            FROM employee e
            LEFT JOIN employee_position p ON e.position_id = p.position_id
            ORDER BY e.employee_id
            """;

        System.out.println("-- User Accounts");
        System.out.println("INSERT INTO `user_account` (`employee_id`, `username`, `password_hash`, `is_active`, `created_at`, `created_by`) VALUES");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean firstRow = true;

            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                String username = generateUsername(firstName, lastName);
                String plainPassword = generateDefaultPassword(employeeId, firstName);
                String hashedPassword = PasswordUtil.hashPassword(plainPassword);

                if (!firstRow) {
                    System.out.println(",");
                }

                System.out.print(
                        "(" + employeeId + ", " +
                        "'" + escapeSql(username) + "', " +
                        "'" + escapeSql(hashedPassword) + "', " +
                        "1, NOW(), NULL)"
                );

                firstRow = false;
            }

            System.out.println(";");
            System.out.println();
        }
    }

    private static void printAccountRoleInsertSql() throws SQLException {
        String sql = """
            SELECT DISTINCT p.position_name
            FROM employee e
            LEFT JOIN employee_position p ON e.position_id = p.position_id
            ORDER BY p.position_name
            """;

        System.out.println("-- Account Roles");
        System.out.println("INSERT INTO `account_role` (`user_account_id`, `role_id`, `created_at`, `created_by`)");
        System.out.println("SELECT ua.user_account_id, ur.role_id, NOW(), NULL");
        System.out.println("FROM `user_account` ua");
        System.out.println("JOIN `employee` e ON ua.employee_id = e.employee_id");
        System.out.println("LEFT JOIN `employee_position` p ON e.position_id = p.position_id");
        System.out.println("JOIN `user_role` ur ON ur.role_name = CASE");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String positionName = rs.getString("position_name");

                if (positionName == null || positionName.isBlank()) {
                    continue;
                }

                String roleName = getRoleForPosition(positionName);

                System.out.println(
                        "    WHEN p.position_name = '" + escapeSql(positionName) + "' THEN '" + escapeSql(roleName) + "'"
                );
            }
        }

        System.out.println("    ELSE 'Employee'");
        System.out.println("END;");
        System.out.println();
    }

    private static void printLoginGuide() throws SQLException {
        String sql = """
            SELECT e.employee_id,
                   e.first_name,
                   e.last_name,
                   p.position_name
            FROM employee e
            LEFT JOIN employee_position p ON e.position_id = p.position_id
            ORDER BY e.employee_id
            """;

        System.out.println("-- Login Guide");
        System.out.println("-- username | password | role");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String positionName = rs.getString("position_name");

                String username = generateUsername(firstName, lastName);
                String password = generateDefaultPassword(employeeId, firstName);
                String role = getRoleForPosition(positionName);

                System.out.println("-- " + username + " | " + password + " | " + role);
            }
        }
    }

    private static String generateUsername(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.toLowerCase().trim();
        String last = lastName == null ? "" : lastName.toLowerCase().trim();

        String username = first + "." + last;

        username = username.replaceAll("[^a-z.]", "");
        username = username.replaceAll("\\.+", ".");
        username = username.replaceAll("^\\.|\\.$", "");

        return username;
    }

    private static String generateDefaultPassword(int employeeId, String firstName) {
        String initial = "X";

        if (firstName != null && !firstName.isBlank()) {
            initial = firstName.trim().substring(0, 1).toUpperCase();
        }

        return employeeId + initial;
    }

    private static String getRoleForPosition(String position) {
        if (position == null || position.isBlank()) {
            return POSITION_TO_ROLE.get("DEFAULT");
        }

        String cleanPosition = position.trim();

        for (Map.Entry<String, String> entry : POSITION_TO_ROLE.entrySet()) {
            if ("DEFAULT".equals(entry.getKey())) {
                continue;
            }

            if (cleanPosition.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }

        return POSITION_TO_ROLE.get("DEFAULT");
    }

    private static String escapeSql(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("'", "''");
    }
}
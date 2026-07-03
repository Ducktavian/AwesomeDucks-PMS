/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.dao;

/**
 *
 * @author Rhynne Gracelle
 */

import com.motorph.config.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JdbcDashboardDAO {

    public int getTotalEmployees() {
        String sql = "SELECT COUNT(*) FROM employee";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public LeaveRequestCounts getLeaveRequestCounts() {
        String sql = """
            SELECT rs.request_status_type, COUNT(*) AS cnt
            FROM leave_request lr
            JOIN request_status rs ON lr.request_status_id = rs.request_status_id
            GROUP BY rs.request_status_type
        """;

        int pending = 0, approved = 0, rejected = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("request_status_type");
                int count = rs.getInt("cnt");

                switch (status) {
                    case "Pending" -> pending = count;
                    case "Approved" -> approved = count;
                    case "Rejected" -> rejected = count;
                    default -> { }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new LeaveRequestCounts(pending, approved, rejected);
    }

    public WorkTimeRequestCounts getPendingWorkTimeRequestCounts() {
        String sql = """
            SELECT wtr.request_type, COUNT(*) AS cnt
            FROM work_time_request wtr
            JOIN request_status rs ON wtr.request_status_id = rs.request_status_id
            WHERE rs.request_status_type = 'Pending'
            GROUP BY wtr.request_type
        """;

        int pendingOvertime = 0, pendingUndertime = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String type = rs.getString("request_type");
                int count = rs.getInt("cnt");

                if ("overtime".equals(type)) {
                    pendingOvertime = count;
                } else if ("undertime".equals(type)) {
                    pendingUndertime = count;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new WorkTimeRequestCounts(pendingOvertime, pendingUndertime);
    }

    public List<String[]> getEmployeesOnLeaveToday() {
        String sql = """
            SELECT CONCAT(e.first_name, ' ', e.last_name) AS emp_name,
                   d.department_name,
                   lt.leave_type_name
            FROM leave_request lr
            JOIN employee e ON lr.employee_id = e.employee_id
            JOIN employee_position p ON e.position_id = p.position_id
            JOIN department d ON p.department_id = d.department_id
            JOIN leave_type lt ON lr.leave_type_id = lt.leave_type_id
            JOIN request_status rs ON lr.request_status_id = rs.request_status_id
            WHERE rs.request_status_type = 'Approved'
              AND CURDATE() BETWEEN lr.start_date AND lr.end_date
            ORDER BY lr.start_date
        """;

        return queryThreeColumnRows(sql);
    }

    public List<String[]> getEmployeesOnOvertimeToday() {
        String sql = """
            SELECT CONCAT(e.first_name, ' ', e.last_name) AS emp_name,
                   d.department_name,
                   wtr.reason
            FROM work_time_request wtr
            JOIN employee e ON wtr.employee_id = e.employee_id
            JOIN employee_position p ON e.position_id = p.position_id
            JOIN department d ON p.department_id = d.department_id
            JOIN request_status rs ON wtr.request_status_id = rs.request_status_id
            WHERE rs.request_status_type = 'Approved'
              AND wtr.request_type = 'overtime'
              AND wtr.request_date = CURDATE()
            ORDER BY wtr.start_time
        """;

        return queryThreeColumnRows(sql);
    }

    private List<String[]> queryThreeColumnRows(String sql) {
        List<String[]> rows = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rows.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    public TicketCounts getTicketCounts() {
        String sql = """
            SELECT rs.request_status_type, COUNT(*) AS cnt
            FROM help_center_ticket t
            JOIN request_status rs ON t.request_status_id = rs.request_status_id
            WHERE rs.request_status_type IN ('Open', 'Resolved')
            GROUP BY rs.request_status_type
        """;

        int open = 0, resolved = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("request_status_type");
                int count = rs.getInt("cnt");

                if ("Open".equals(status)) {
                    open = count;
                } else if ("Resolved".equals(status)) {
                    resolved = count;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new TicketCounts(open, resolved);
    }

    public int getPendingPayrollCount() {
        String sql = """
            SELECT COUNT(*) FROM payroll p
            JOIN payroll_status ps ON p.payroll_status_id = ps.payroll_status_id
            WHERE ps.status_name IN ('Draft', 'Processing')
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public PayPeriodInfo getCurrentPayPeriod() {
        String sql = """
            SELECT period_start_date, period_end_date FROM pay_period
            WHERE CURDATE() BETWEEN period_start_date AND period_end_date
            ORDER BY period_start_date DESC LIMIT 1
        """;

        return queryPayPeriod(sql);
    }

    public PayPeriodInfo getUpcomingPayPeriod() {
        String sql = """
            SELECT period_start_date, period_end_date FROM pay_period
            WHERE period_start_date > CURDATE()
            ORDER BY period_start_date ASC LIMIT 1
        """;

        return queryPayPeriod(sql);
    }

    private PayPeriodInfo queryPayPeriod(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                LocalDate start = rs.getDate("period_start_date").toLocalDate();
                LocalDate end = rs.getDate("period_end_date").toLocalDate();
                return buildPayPeriodInfo(start, end);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private PayPeriodInfo buildPayPeriodInfo(LocalDate start, LocalDate end) {
        String monthLabel = start.format(DateTimeFormatter.ofPattern("MMMM"));

        String dateRange;
        if (start.getMonth() == end.getMonth() && start.getYear() == end.getYear()) {
            dateRange = start.getDayOfMonth() + "-" + end.getDayOfMonth() + ", " + end.getYear();
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d");
            dateRange = start.format(fmt) + " - " + end.format(fmt) + ", " + end.getYear();
        }

        return new PayPeriodInfo(monthLabel, dateRange);
    }

    public FinancialData getFinancialData() {
        List<String> labels = new ArrayList<>();
        List<Integer> revenue = new ArrayList<>();
        List<Integer> expenses = new ArrayList<>();

        String sql = """
            SELECT 
                DATE_FORMAT(pp.period_start_date, '%b %Y') AS period_label,
                SUM(p.gross_pay) AS revenue,
                SUM(p.total_benefits + p.total_deductions) AS expenses
            FROM payroll p
            JOIN pay_period pp ON p.pay_period_id = pp.pay_period_id
            GROUP BY YEAR(pp.period_start_date), MONTH(pp.period_start_date)
            ORDER BY pp.period_start_date
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                labels.add(rs.getString("period_label"));
                revenue.add(rs.getBigDecimal("revenue").intValue());
                expenses.add(rs.getBigDecimal("expenses").intValue());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new FinancialData(labels, revenue, expenses);
    }

    public static class FinancialData {
        public final List<String> labels;
        public final List<Integer> revenue;
        public final List<Integer> expenses;

        public FinancialData(List<String> labels, List<Integer> revenue, List<Integer> expenses) {
            this.labels = labels;
            this.revenue = revenue;
            this.expenses = expenses;
        }
    }

    public static class LeaveRequestCounts {
        public final int pending;
        public final int approved;
        public final int rejected;

        public LeaveRequestCounts(int pending, int approved, int rejected) {
            this.pending = pending;
            this.approved = approved;
            this.rejected = rejected;
        }
    }

    public static class WorkTimeRequestCounts {
        public final int pendingOvertime;
        public final int pendingUndertime;

        public WorkTimeRequestCounts(int pendingOvertime, int pendingUndertime) {
            this.pendingOvertime = pendingOvertime;
            this.pendingUndertime = pendingUndertime;
        }
    }

    public static class TicketCounts {
        public final int open;
        public final int resolved;

        public TicketCounts(int open, int resolved) {
            this.open = open;
            this.resolved = resolved;
        }
    }

    public static class PayPeriodInfo {
        public final String monthLabel;
        public final String dateRange;

        public PayPeriodInfo(String monthLabel, String dateRange) {
            this.monthLabel = monthLabel;
            this.dateRange = dateRange;
        }
    }
}
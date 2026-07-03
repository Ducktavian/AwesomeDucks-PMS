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
}
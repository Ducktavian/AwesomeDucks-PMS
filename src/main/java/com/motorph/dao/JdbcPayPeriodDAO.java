package com.motorph.dao;

import com.motorph.config.DatabaseConnection;
import com.motorph.model.PayPeriod;

import java.sql.*;
import java.time.LocalDate;

public class JdbcPayPeriodDAO implements PayPeriodDAO {

    // Draft (payroll_status_id = 1) - see seed data for `payroll_status`.
    private static final int DEFAULT_STATUS_DRAFT = 1;

    @Override
    public PayPeriod findOrCreate(LocalDate periodStart, LocalDate periodEnd, LocalDate payDate) {
        String selectSql = "SELECT pay_period_id, period_start_date, period_end_date, pay_date, payroll_status_id " +
                "FROM pay_period WHERE period_start_date = ? AND period_end_date = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement select = conn.prepareStatement(selectSql)) {
                select.setDate(1, Date.valueOf(periodStart));
                select.setDate(2, Date.valueOf(periodEnd));
                try (ResultSet rs = select.executeQuery()) {
                    if (rs.next()) {
                        return mapRow(rs);
                    }
                }
            }

            // Not found - create it.
            String insertSql = "INSERT INTO pay_period (period_start_date, period_end_date, pay_date, payroll_status_id) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement insert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insert.setDate(1, Date.valueOf(periodStart));
                insert.setDate(2, Date.valueOf(periodEnd));
                insert.setDate(3, Date.valueOf(payDate));
                insert.setInt(4, DEFAULT_STATUS_DRAFT);
                insert.executeUpdate();

                try (ResultSet keys = insert.getGeneratedKeys()) {
                    keys.next();
                    int newId = keys.getInt(1);
                    return new PayPeriod(newId, periodStart, periodEnd, payDate, DEFAULT_STATUS_DRAFT);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find or create pay period " + periodStart + " - " + periodEnd, e);
        }
    }

    @Override
    public PayPeriod findCurrent(LocalDate date) {
        String sql = "SELECT pay_period_id, period_start_date, period_end_date, pay_date, payroll_status_id " +
                "FROM pay_period WHERE ? BETWEEN period_start_date AND period_end_date " +
                "ORDER BY period_start_date DESC LIMIT 1";
        return findOne(sql, date);
    }

    @Override
    public PayPeriod findUpcoming(LocalDate date) {
        String sql = "SELECT pay_period_id, period_start_date, period_end_date, pay_date, payroll_status_id " +
                "FROM pay_period WHERE period_start_date > ? " +
                "ORDER BY period_start_date ASC LIMIT 1";
        return findOne(sql, date);
    }

    private PayPeriod findOne(String sql, LocalDate date) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to look up pay period for " + date, e);
        }
    }

    private PayPeriod mapRow(ResultSet rs) throws SQLException {
        return new PayPeriod(
                rs.getInt("pay_period_id"),
                rs.getDate("period_start_date").toLocalDate(),
                rs.getDate("period_end_date").toLocalDate(),
                rs.getDate("pay_date").toLocalDate(),
                rs.getInt("payroll_status_id"));
    }
}
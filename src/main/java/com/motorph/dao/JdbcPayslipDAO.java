package com.motorph.dao;

import com.motorph.config.DatabaseConnection;
import com.motorph.model.AllowanceBreakdown;
import com.motorph.model.DeductionBreakdown;
import com.motorph.model.Payslip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public class JdbcPayslipDAO implements PayslipDAO {

    private static final String SELECT_BASE =
            "SELECT payslip_id, payslip_number, generated_at, payroll_id, employee_id, " +
            "full_name, position_name, period_start_date, period_end_date, " +
            "hours_worked, hourly_rate, gross_pay, net_pay, " +
            "rice_subsidy, phone_allowance, clothing_allowance, " +
            "sss, philhealth, pagibig, withholding_tax " +
            "FROM v_payslip_detail";

    @Override
    public Payslip findById(String payslipId) {
        String sql = SELECT_BASE + " WHERE payslip_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payslipId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find payslip with id " + payslipId, e);
        }
    }

    @Override
    public List<Payslip> findByEmployeeId(String employeeId) {
        String sql = SELECT_BASE + " WHERE employee_id = ? ORDER BY period_start_date DESC";
        List<Payslip> payslips = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(employeeId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find payslips for employee " + employeeId, e);
        }
        return payslips;
    }

    @Override
    public List<Payslip> findAll() {
        String sql = SELECT_BASE + " ORDER BY period_start_date DESC, employee_id ASC";
        List<Payslip> payslips = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                payslips.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load payslips", e);
        }
        return payslips;
    }

    @Override
    public void save(Payslip payslip) {
        String sql = "INSERT INTO payslip (payroll_id, payslip_number, generated_at) VALUES (?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payslip.getPayrollId());
            stmt.setString(2, payslip.getPayslipId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save payslip " + payslip.getPayslipId(), e);
        }
    }

    @Override
    public void update(Payslip payslip) {
        // payroll_id is unique per payslip (uq_payslip_payroll) and never
        // changes once set, so it is the safest key to update by - unlike
        // payslip_number, which this call may itself be trying to change.
        String sql = "UPDATE payslip SET payslip_number = ?, generated_at = NOW() WHERE payroll_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payslip.getPayslipId());
            stmt.setInt(2, payslip.getPayrollId());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No payslip found for payroll_id " + payslip.getPayrollId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update payslip " + payslip.getPayslipId(), e);
        }
    }

    @Override
    public void delete(String payslipId) {
        String sql = "DELETE FROM payslip WHERE payslip_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payslipId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete payslip " + payslipId, e);
        }
    }

    @Override
    public void save(Object entity) {
        save((Payslip) entity);
    }

    @Override
    public void update(Object entity) {
        update((Payslip) entity);
    }

    private Payslip mapRow(ResultSet rs) throws SQLException {
        AllowanceBreakdown allowances = new AllowanceBreakdown(
                rs.getDouble("rice_subsidy"),
                rs.getDouble("phone_allowance"),
                rs.getDouble("clothing_allowance"));

        DeductionBreakdown deductions = new DeductionBreakdown(
                rs.getDouble("sss"),
                rs.getDouble("philhealth"),
                rs.getDouble("pagibig"),
                rs.getDouble("withholding_tax"));

        LocalDate periodStart = rs.getDate("period_start_date").toLocalDate();
        LocalDate periodEnd = rs.getDate("period_end_date").toLocalDate();

        return new Payslip(
                rs.getString("payslip_number"),
                rs.getInt("payroll_id"),
                String.valueOf(rs.getInt("employee_id")),
                rs.getString("full_name"),
                rs.getString("position_name"),
                periodStart,
                periodEnd,
                rs.getDouble("hours_worked"),
                rs.getDouble("hourly_rate"),
                rs.getDouble("gross_pay"),
                allowances,
                deductions,
                rs.getDouble("net_pay"));
    }
}
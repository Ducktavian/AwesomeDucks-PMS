package com.motorph.dao;

import com.motorph.config.DatabaseConnection;
import com.motorph.model.AllowanceBreakdown;
import com.motorph.model.DeductionBreakdown;
import com.motorph.model.FinancialSummary;
import com.motorph.model.Payroll;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public class JdbcPayrollDAO implements PayrollDAO {

    // benefit_type_id / deduction_type_id mapping - see seed data.
    private static final int BENEFIT_RICE = 1, BENEFIT_PHONE = 2, BENEFIT_CLOTHING = 3;
    private static final int DEDUCTION_SSS = 1, DEDUCTION_PHILHEALTH = 2, DEDUCTION_PAGIBIG = 3, DEDUCTION_TAX = 4;

    @Override
    public int save(Payroll payroll, AllowanceBreakdown allowances, DeductionBreakdown deductions) {
        String insertPayroll =
                "INSERT INTO payroll (employee_id, pay_period_id, payroll_status_id, basic_salary, " +
                "hourly_rate, hours_worked, basic_pay, overtime_pay, gross_pay, total_benefits, " +
                "total_deductions, net_pay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertBenefit =
                "INSERT INTO payroll_benefit (payroll_id, benefit_type_id, amount) VALUES (?, ?, ?)";
        String insertDeduction =
                "INSERT INTO payroll_deduction (payroll_id, deduction_type_id, amount) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int payrollId;
            try (PreparedStatement stmt = conn.prepareStatement(insertPayroll, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, payroll.getEmployeeId());
                stmt.setInt(2, payroll.getPayPeriodId());
                stmt.setInt(3, payroll.getPayrollStatusId());
                stmt.setDouble(4, payroll.getBasicSalary());
                stmt.setDouble(5, payroll.getHourlyRate());
                stmt.setDouble(6, payroll.getHoursWorked());
                stmt.setDouble(7, payroll.getBasicPay());
                stmt.setDouble(8, payroll.getOvertimePay());
                stmt.setDouble(9, payroll.getGrossPay());
                stmt.setDouble(10, payroll.getTotalBenefits());
                stmt.setDouble(11, payroll.getTotalDeductions());
                stmt.setDouble(12, payroll.getNetPay());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    payrollId = keys.getInt(1);
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertBenefit)) {
                insertBenefitRow(stmt, payrollId, BENEFIT_RICE, allowances.getRiceSubsidy());
                insertBenefitRow(stmt, payrollId, BENEFIT_PHONE, allowances.getPhoneAllowance());
                insertBenefitRow(stmt, payrollId, BENEFIT_CLOTHING, allowances.getClothingAllowance());
                stmt.executeBatch();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertDeduction)) {
                insertDeductionRow(stmt, payrollId, DEDUCTION_SSS, deductions.getSss());
                insertDeductionRow(stmt, payrollId, DEDUCTION_PHILHEALTH, deductions.getPhilHealth());
                insertDeductionRow(stmt, payrollId, DEDUCTION_PAGIBIG, deductions.getPagIbig());
                insertDeductionRow(stmt, payrollId, DEDUCTION_TAX, deductions.getWithholdingTax());
                stmt.executeBatch();
            }

            conn.commit();
            return payrollId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw new RuntimeException("Failed to save payroll for employee " + payroll.getEmployeeId(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    // Uses v_employee_payroll_summary for reading payroll history by employee.
    @Override
    public List<Payroll> findByEmployee(int employeeId) {
        String sql = "SELECT payroll_id, pay_period_id, payroll_status_id, basic_salary, " +
                     "hourly_rate, hours_worked, basic_pay, overtime_pay, gross_pay, " +
                     "total_benefits, total_deductions, net_pay " +
                     "FROM v_employee_payroll_summary WHERE employee_id = ? " +
                     "ORDER BY period_start_date DESC";
        List<Payroll> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payroll p = new Payroll(
                            employeeId,
                            rs.getInt("pay_period_id"),
                            rs.getInt("payroll_status_id"),
                            rs.getDouble("basic_salary"),
                            rs.getDouble("hourly_rate"),
                            rs.getDouble("hours_worked"),
                            rs.getDouble("basic_pay"),
                            rs.getDouble("overtime_pay"),
                            rs.getDouble("gross_pay"),
                            rs.getDouble("total_benefits"),
                            rs.getDouble("total_deductions"),
                            rs.getDouble("net_pay"));
                    p.setPayrollId(rs.getInt("payroll_id"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByEmployee failed for employeeId=" + employeeId, e);
        }
        return list;
    }

    @Override
    public int findPayrollId(int employeeId, int payPeriodId) {
        String sql = "SELECT payroll_id FROM payroll WHERE employee_id = ? AND pay_period_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setInt(2, payPeriodId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("payroll_id") : -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to look up payroll for employee " + employeeId, e);
        }
    }

    @Override
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM payroll p " +
                "JOIN payroll_status ps ON p.payroll_status_id = ps.payroll_status_id " +
                "WHERE ps.status_name IN ('Draft', 'Processing')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count pending payroll", e);
        }
    }

    @Override
    public FinancialSummary findFinancialSummary() {
        // GROUP BY the label expression itself (plus year/month) and order by the
        // earliest period date - satisfies MySQL's only_full_group_by mode.
        String sql = "SELECT DATE_FORMAT(pp.period_start_date, '%b %Y') AS period_label, " +
                "SUM(p.gross_pay) AS revenue, SUM(p.total_benefits + p.total_deductions) AS expenses " +
                "FROM payroll p " +
                "JOIN pay_period pp ON p.pay_period_id = pp.pay_period_id " +
                "GROUP BY period_label, YEAR(pp.period_start_date), MONTH(pp.period_start_date) " +
                "ORDER BY MIN(pp.period_start_date)";

        List<String> labels = new ArrayList<>();
        List<Integer> revenue = new ArrayList<>();
        List<Integer> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                labels.add(rs.getString("period_label"));
                revenue.add(rs.getBigDecimal("revenue").intValue());
                expenses.add(rs.getBigDecimal("expenses").intValue());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to compute financial summary", e);
        }

        return new FinancialSummary(labels, revenue, expenses);
    }

    private void insertBenefitRow(PreparedStatement stmt, int payrollId, int benefitTypeId, double amount) throws SQLException {
        stmt.setInt(1, payrollId);
        stmt.setInt(2, benefitTypeId);
        stmt.setDouble(3, amount);
        stmt.addBatch();
    }

    private void insertDeductionRow(PreparedStatement stmt, int payrollId, int deductionTypeId, double amount) throws SQLException {
        stmt.setInt(1, payrollId);
        stmt.setInt(2, deductionTypeId);
        stmt.setDouble(3, amount);
        stmt.addBatch();
    }
}
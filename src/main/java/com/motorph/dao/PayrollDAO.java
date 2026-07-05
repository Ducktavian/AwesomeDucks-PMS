package com.motorph.dao;

import com.motorph.model.AllowanceBreakdown;
import com.motorph.model.DeductionBreakdown;
import com.motorph.model.FinancialSummary;
import com.motorph.model.Payroll;

import java.util.List;

public interface PayrollDAO {
    int save(Payroll payroll, AllowanceBreakdown allowances, DeductionBreakdown deductions);
    int findPayrollId(int employeeId, int payPeriodId);

    // Uses v_employee_payroll_summary - returns all payroll runs for an employee.
    List<Payroll> findByEmployee(int employeeId);

    // Count of payroll runs not yet paid (Draft/Processing).
    int countPending();

    // Revenue (gross pay) vs. expenses (benefits + deductions), grouped by pay period.
    FinancialSummary findFinancialSummary();
}

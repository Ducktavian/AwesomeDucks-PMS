package com.motorph.dao;

import com.motorph.model.Payslip;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public interface PayslipDAO extends BaseDAO {
    Payslip findById(String payslipId);
    Payslip findByPayrollId(int payrollId);
    List<Payslip> findByEmployeeId(String employeeId);
    List<Payslip> findAll();
    void save(Payslip payslip);
    void update(Payslip payslip);
    void delete(String payslipId);
}

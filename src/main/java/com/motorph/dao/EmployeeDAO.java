package com.motorph.dao;

import com.motorph.model.Employee;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public interface EmployeeDAO extends BaseDAO {
    Employee findBy(String employeeId);
    List<Employee> findAll();
    void save(Employee employee);
    void update(Employee employee);
    void delete(String employeeId);
}

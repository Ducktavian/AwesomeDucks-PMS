package com.motorph.dao;

import com.motorph.model.Employee;
import java.util.List;
import java.util.Map;

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
    Map<String, Integer> findAllPositions();
    Map<String, Integer> findAllEmploymentStatuses();
    Map<String, Integer> findAllDepartments();
    Map<String, Integer> findPositionsByDepartment(int departmentId);
}

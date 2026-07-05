package com.motorph.service;

import com.motorph.dao.EmployeeDAO;
import com.motorph.exception.UnauthorizedException;
import com.motorph.model.Employee;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.util.Session;

import java.util.List;
import java.util.Map;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    private void authorizeHR() {
        UserAccount current = Session.getCurrentUser();
        if (current == null) {
            throw new UnauthorizedException("No active session found.");
        }
        Role role = current.getRole();
        if (role != Role.HR && role != Role.ADMIN) {
            throw new UnauthorizedException("Only HR or Admin can manage employees.");
        }
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.findAll();
    }

    public Employee findEmployee(String employeeId) {
        return employeeDAO.findBy(employeeId);
    }

    /**
     * Returns the next sequential employee ID (highest existing numeric id + 1)..
     */
    public String getNextEmployeeId() {
        int max = 0;
        for (Employee employee : employeeDAO.findAll()) {
            try {
                max = Math.max(max, Integer.parseInt(employee.getEmployeeId()));
            } catch (NumberFormatException ignored) {
                // Non-numeric ids can't seed the sequence; skip them.
            }
        }
        return String.valueOf(max < 10001 ? 10001 : max + 1);
    }

    public Map<String, Integer> getAvailablePositions() {
        return employeeDAO.findAllPositions();
    }

    public Map<String, Integer> getEmploymentStatuses() {
        return employeeDAO.findAllEmploymentStatuses();
    }

    public Map<String, Integer> getDepartments() {
        return employeeDAO.findAllDepartments();
    }

    public Map<String, Integer> getPositionsByDepartment(int departmentId) {
        return employeeDAO.findPositionsByDepartment(departmentId);
    }

    public void addEmployee(Employee employee) {
        authorizeHR();
        validateEmployee(employee);
        employeeDAO.save(employee);
    }

    public void updateEmployee(Employee employee) {
        authorizeHR();
        validateEmployee(employee);
        employeeDAO.update(employee);
    }

    public void deleteEmployee(String employeeId) {
        authorizeHR();
        employeeDAO.delete(employeeId);
    }
    

    private void validateEmployee(Employee employee) {
        if (employee.getEmployeeId() == null || employee.getEmployeeId().isBlank()) {
            throw new IllegalArgumentException("Employee ID is required.");
        }
        if (employee.getFirstName() == null || employee.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (employee.getLastName() == null || employee.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (employee.getBirthday() == null) {
            throw new IllegalArgumentException("Birthday is required.");
        } else {
            long age = java.time.temporal.ChronoUnit.YEARS.between(
                    employee.getBirthday(), java.time.LocalDate.now());
            if (age < 18) {
                throw new IllegalArgumentException("Employee must be at least 18 years old.");
            }
        }
        if (employee.getAddress() == null || employee.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required.");
        }
        if (employee.getPhoneNumber() == null || !employee.getPhoneNumber().matches("^\\d{9,11}$")) {
            throw new IllegalArgumentException("Phone number must be between 9 to 11 digits.");
        }
        String sssDigits = employee.getSSSNumber() == null
                ? ""
                : employee.getSSSNumber().replaceAll("\\D", "");
        if (sssDigits.length() != 10) {
            throw new IllegalArgumentException("SSS Number must contain exactly 10 numeric digits.");
        }
        // Government IDs may be entered with separators (e.g. 442-605-657-000);
        // validate on the digits only, so formatting hyphens don't count.
        String philhealthDigits = employee.getPhilhealthNumber() == null
                ? ""
                : employee.getPhilhealthNumber().replaceAll("\\D", "");
        if (philhealthDigits.length() != 12) {
            throw new IllegalArgumentException("PhilHealth Number must be 12 digits.");
        }
        String tinDigits = employee.getTIN() == null
                ? ""
                : employee.getTIN().replaceAll("\\D", "");
        if (tinDigits.length() != 12) {
            throw new IllegalArgumentException("TIN must be exactly 12 digits.");
        }
        String pagibigDigits = employee.getPagIbigNumber() == null
                ? ""
                : employee.getPagIbigNumber().replaceAll("\\D", "");
        if (pagibigDigits.length() != 12) {
            throw new IllegalArgumentException("Pag-IBIG Number must be exactly 12 digits.");
        }
        if (employee.getStatus() == null || employee.getStatus().isBlank()) {
            throw new IllegalArgumentException("Employment status is required.");
        }
        if (employee.getPosition() == null || employee.getPosition().isBlank()) {
            throw new IllegalArgumentException("Position is required.");
        }
        if (employee.getPositionId() == null) {
            throw new IllegalArgumentException("Please select a valid position before saving the employee.");
        }
        if (employee.getEmploymentStatusId() == null) {
            throw new IllegalArgumentException("Please select a valid employment status.");
        }
        if (employee.getImmediateSupervisor() == null || employee.getImmediateSupervisor().isBlank()) {
            throw new IllegalArgumentException("Immediate Supervisor is required.");
        }
        if (employee.getBasicSalary() <= 0) {
            throw new IllegalArgumentException("Basic Salary must be greater than 0.");
        }
        if (employee.getRiceSubsidy() < 0) {
            throw new IllegalArgumentException("Rice Subsidy must not be negative.");
        }
        if (employee.getPhoneAllowance() < 0) {
            throw new IllegalArgumentException("Phone Allowance must not be negative.");
        }
        if (employee.getClothingAllowance() < 0) {
            throw new IllegalArgumentException("Clothing Allowance must not be negative.");
        }
    }
}

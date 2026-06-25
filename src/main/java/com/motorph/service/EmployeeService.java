package com.motorph.service;

import com.motorph.dao.EmployeeDAO;
import com.motorph.exception.UnauthorizedException;
import com.motorph.model.Employee;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.util.Session;

import java.util.List;

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
        if (employee.getSSSNumber() == null || !employee.getSSSNumber().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("SSS Number must be exactly 10 digits.");
        }
        if (employee.getPhilhealthNumber() == null || !employee.getPhilhealthNumber().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("PhilHealth Number must be 12 digits.");
        }
        if (employee.getTIN() == null || !employee.getTIN().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("TIN must be exactly 12 digits.");
        }
        if (employee.getPagIbigNumber() == null || !employee.getPagIbigNumber().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Pag-IBIG Number must be exactly 12 digits.");
        }
        if (employee.getStatus() == null || employee.getStatus().isBlank()) {
            throw new IllegalArgumentException("Employment status is required.");
        }
        if (employee.getPosition() == null || employee.getPosition().isBlank()) {
            throw new IllegalArgumentException("Position is required.");
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

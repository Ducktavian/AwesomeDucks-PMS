package com.motorph.service;

import com.motorph.dao.EmployeeDAO;
import com.motorph.exception.UnauthorizedException;
import com.motorph.model.Employee;
import com.motorph.model.Finance;
import com.motorph.model.HR;
import com.motorph.model.IT;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.util.Session;
import java.util.List;

public class EmployeeService {

    private EmployeeDAO employeeDAO;
    
    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }
    
    
    // Authorize HR
    private void authorizeHR() {
        UserAccount current = Session.getCurrentUser();
        
        if (current == null) {
            throw new UnauthorizedException("No active session found.");
        }
        
        if (current.getRole() != Role.HR && current.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only HR or Admin can manage employees.");
        }
    }
    
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
    
    public Employee findEmployee(String employeeNumber) {
        return employeeDAO.findEmployee(employeeNumber);
    }
    
    public void addEmployee(Employee draft) {
        authorizeHR();
        validateEmployee(draft);
        Employee finalEmployee = convertToSpecificType(draft);
        employeeDAO.addEmployee(finalEmployee);
    }
    
    public void updateEmployee(Employee employee ){
        authorizeHR();
        validateEmployee(employee);
        employeeDAO.updateEmployee(employee);
    }
    
    public void deleteEmployee(String employeeNumber) {
        authorizeHR();
        employeeDAO.deleteEmployee(employeeNumber);
    }
    
    public String generateNextEmployeeNumber() {
        return employeeDAO.generateNextEmployeeNumber();
    }
    
    private void validateEmployee(Employee employee) {
        // Firstname
        if(employee.getFirstName() == null || employee.getFirstName().isBlank()){
            throw new IllegalArgumentException("First name is required.");
        }
        
        // LastName
        if(employee.getLastName() == null || employee.getLastName().isBlank()){
            throw new IllegalArgumentException("Last name is required.");
        }
        
        // Bday
        if (employee.getBirthday() == null) {
            throw new IllegalArgumentException("Birthday is required.");
        } else {
            long age = java.time.temporal.ChronoUnit.YEARS.between(employee.getBirthday(), java.time.LocalDate.now());
            if (age < 18) {
                throw new IllegalArgumentException("Employee must be at least 18 years old.");
            }
        }
        
        
        // Address
        if (employee.getAddress() == null || employee.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required.");
        }
        
        // PhoneNumber : 9 digits (478-355-427)
        if (employee.getPhoneNumber() == null || !employee.getPhoneNumber().matches("^\\d{9,11}$")) {
            throw new IllegalArgumentException("Phone number must be between 9 to 11 digits.");
        }
        // SSS #: 10 digits
        if (employee.getSSSNumber() == null || !employee.getSSSNumber().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("SSS Number must be exactly 10 digits.");
        }
        // Philhealth #: 12 digits
        if (employee.getPhilhealthNumber() == null || !employee.getPhilhealthNumber().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("PhilHealth Number must be 12 digits.");
        }
        
        // TIN #: 12 digits
        if (employee.getTIN() == null || !employee.getTIN().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("TIN must be exactly 12 digits.");
        }
        // Pag-ibig #: 12 digits
        if (employee.getPagIbigNumber() == null || !employee.getPagIbigNumber().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Pag-IBIG Number must be exactly 12 digits.");
        }
        
        // Status
        if (employee.getStatus() == null || employee.getStatus().isBlank()) {
            throw new IllegalArgumentException("Employment status is required.");
        }

        // Position
        if(employee.getPosition() == null || employee.getPosition().isBlank()){
            throw new IllegalArgumentException("Position is required.");
        }
        
        // Immediate Supervisor
        if (employee.getImmediateSupervisor() == null || employee.getImmediateSupervisor().isBlank()) {
            throw new IllegalArgumentException("Immediate Supervisor is required.");
        }


        // Basic Salary
        if(employee.getBasicSalary() <= 0){
            throw new IllegalArgumentException("Basic Salary must be greater than 0.");
        }
        
        // Rice Subisidy
        if (employee.getRiceSubsidy() <= 0) {
            throw new IllegalArgumentException("Rice Subisidy be greater than 0.");
        }
        // PhoneAllowance
        if (employee.getPhoneAllowance() <= 0) {
            throw new IllegalArgumentException("Phone Allowance must be greater than 0.");
            
        }
        // ClothingAllowance
        if (employee.getClothingAllowance() <= 0) {
            throw new IllegalArgumentException("Clothing Allowance must be greater than 0.");
        }
    }
    
    // Converts to EMPLOYEE type
    private Employee convertToSpecificType(Employee draft) {
        String pos = draft.getPosition().toLowerCase();

        if (pos.contains("hr")) {
            return new HR(draft.getEmployeeNumber(), draft.getLastName(), draft.getFirstName(), 
                          draft.getBirthday(), draft.getAddress(), draft.getPhoneNumber(), 
                          draft.getSSSNumber(), draft.getPhilhealthNumber(), draft.getTIN(), 
                          draft.getPagIbigNumber(), draft.getStatus(), draft.getPosition(), 
                          draft.getImmediateSupervisor(), draft.getBasicSalary(), 
                          draft.getRiceSubsidy(), draft.getPhoneAllowance(), draft.getClothingAllowance());
        } else if (pos.contains("it")) {
            return new IT(draft.getEmployeeNumber(), draft.getLastName(), draft.getFirstName(), 
                          draft.getBirthday(), draft.getAddress(), draft.getPhoneNumber(), 
                          draft.getSSSNumber(), draft.getPhilhealthNumber(), draft.getTIN(), 
                          draft.getPagIbigNumber(), draft.getStatus(), draft.getPosition(), 
                          draft.getImmediateSupervisor(), draft.getBasicSalary(), 
                          draft.getRiceSubsidy(), draft.getPhoneAllowance(), draft.getClothingAllowance());
        } else if (pos.contains("finance") || pos.contains("account") || pos.contains("payroll")) {
            return new Finance(draft.getEmployeeNumber(), draft.getLastName(), draft.getFirstName(), 
                          draft.getBirthday(), draft.getAddress(), draft.getPhoneNumber(), 
                          draft.getSSSNumber(), draft.getPhilhealthNumber(), draft.getTIN(), 
                          draft.getPagIbigNumber(), draft.getStatus(), draft.getPosition(), 
                          draft.getImmediateSupervisor(), draft.getBasicSalary(), 
                          draft.getRiceSubsidy(), draft.getPhoneAllowance(), draft.getClothingAllowance());
        } else {
            return draft; // default RegularEmployee
        }
    }
}

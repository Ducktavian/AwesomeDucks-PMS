package com.motorph.dao;

import com.motorph.model.Employee;
import java.util.List;
import com.motorph.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author Ducktavian
 */
public class JdbcEmployeeDAO implements EmployeeDAO {

    @Override
    public Employee findBy(String employeeId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "Select * FROM employee";
        /*
            1. Connection - DatabaseConnection.getConnection();
            2. PreparedStatement - This is your SQL Query.
            3. ResultSet - This would contain the result of your PreparedStatement.
        */
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
           )
        {
            /*
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("employeeId")
                )); 
            }
            */
        } 
        catch (SQLException e) 
        {
            e.getMessage();
        }
        
        return list;
    }

    @Override
    public void save(Employee employee) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Employee employee) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(String employeeId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object findById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void save(Object entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Object entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}

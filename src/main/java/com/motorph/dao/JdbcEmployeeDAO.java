package com.motorph.dao;

import com.motorph.model.Employee;
import java.util.List;
import com.motorph.config.DatabaseConnection;
import com.motorph.util.DateUtils;
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

    @Override // Get all Employees
    public List<Employee> findAll() {
        List<Employee> list = new ArrayList<>();
        String sql = """
                        SELECT
                            e.employee_id,
                            e.first_name,
                            e.last_name,
                            e.birthdate,
                            e.phone_number,
                            e.email,
                            ep.position_name,
                            d.department_name,
                            es.status_name            AS employment_status,
                            c.basic_salary,
                            c.rice_subsidy,
                            c.phone_allowance,
                            c.clothing_allowance,
                            c.gross_semi_monthly_rate,
                            c.hourly_rate,
                            c.effective_date          AS salary_effective_date,
                            a.full_address,
                            sup.first_name            AS supervisor_first_name,
                            sup.last_name             AS supervisor_last_name,
                            MAX(CASE WHEN git.id_type = 'SSS'       THEN egi.id_number END) AS sss_number,
                            MAX(CASE WHEN git.id_type = 'PhilHealth' THEN egi.id_number END) AS philhealth_number,
                            MAX(CASE WHEN git.id_type = 'TIN'        THEN egi.id_number END) AS tin,
                            MAX(CASE WHEN git.id_type = 'Pag-IBIG'   THEN egi.id_number END) AS pagibig_number
                        FROM employee e
                        JOIN employee_position   ep  ON e.position_id            = ep.position_id
                        JOIN department           d  ON ep.department_id          = d.department_id
                        JOIN employment_status   es  ON e.employment_status_id    = es.employment_status_id
                        LEFT JOIN compensation    c  ON e.employee_id             = c.employee_id
                                                    AND c.effective_date = (
                                                        SELECT MAX(c2.effective_date)
                                                        FROM compensation c2
                                                        WHERE c2.employee_id = e.employee_id
                                                    )
                        LEFT JOIN employee_address  ea  ON e.employee_id          = ea.employee_id
                        LEFT JOIN address             a  ON ea.address_id         = a.address_id
                        LEFT JOIN employee           sup ON e.immediate_supervisor_id = sup.employee_id
                        LEFT JOIN employee_government_id egi ON e.employee_id     = egi.employee_id
                        LEFT JOIN government_id_type  git ON egi.government_id_type_id = git.government_id_type_id
                        GROUP BY
                            e.employee_id, e.first_name, e.last_name, e.birthdate,
                            e.phone_number, e.email, ep.position_name, d.department_name,
                            es.status_name, c.basic_salary, c.rice_subsidy, c.phone_allowance,
                            c.clothing_allowance, c.gross_semi_monthly_rate, c.hourly_rate,
                            c.effective_date, a.full_address,
                            sup.first_name, sup.last_name
                        ORDER BY e.employee_id ASC
                    """;
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
            
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("employee_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getDate("birthdate").toLocalDate(),
                        rs.getString("full_address"),
                        rs.getString("phone_number"),
                        rs.getString("sss_number"),
                        rs.getString("philhealth_number"),
                        rs.getString("tin"),
                        rs.getString("pagibig_number"),
                        rs.getString("employment_status"),
                        rs.getString("position_name"),
                        rs.getString("supervisor_first_name") + " " + " " + rs.getString("supervisor_last_name"),
                        rs.getFloat("basic_salary"),
                        rs.getFloat("rice_subsidy"),
                        rs.getFloat("phone_allowance"),
                        rs.getFloat("clothing_allowance")
                        
                )); 
            }
            
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
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

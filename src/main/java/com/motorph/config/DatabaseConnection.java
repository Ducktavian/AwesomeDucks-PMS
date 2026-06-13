package com.motorph.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Ducktavian
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/payroll_system";
    
    private static final String USER = "root";
    private static final String PASSWORD = "salmonella17.";
    
    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

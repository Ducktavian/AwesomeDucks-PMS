package com.motorph.util;

import net.sf.jasperreports.engine .*;
import net.sf. jasperreports.view. JasperViewer;
import java.sql.Connection;
import java. util. HashMap;
import com.motorph.config. DatabaseConnection;

/**
 *
 * @author Ducktavian
 */
public class ReportGenerator {
    public void generateReprt(Connection conn) {
        try {
            
            /*
            1. Compile your JRXML to JasperFile. compileReport
            2. Establish a connection. ORM - SESSION, JDBC - Connection
            3. Fill the report. fillReport
            4. View the report. JasperViewer. viewReport.
            */
            
            // 1. Compile
            JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/JasperReport/EmployeePayslip.jrxml");
            
            // 2. Connection
            Connection connection = DatabaseConnection.getConnection();
            
            
            // 3. Fill Report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), connection);
            
            // 4. View Report
            JasperViewer.viewReport(jasperPrint, false);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

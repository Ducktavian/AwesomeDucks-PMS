package com.motorph.reporting;

import com.motorph.config.DatabaseConnection;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;


public class TimeCardReportGenerator {

    private static final String REPORT_PATH = "/JasperReport/EmployeeTimecard.jrxml";
    private static final String LOGO_PATH = "/com/motorph/img/MotorPH-icon.png";

    public static void view(int employeeId) throws Exception {
        try (InputStream reportStream = TimeCardReportGenerator.class.getResourceAsStream(REPORT_PATH);
             InputStream logoStream = TimeCardReportGenerator.class.getResourceAsStream(LOGO_PATH);
             Connection connection = DatabaseConnection.getConnection()) {

            if (reportStream == null) {
                throw new IllegalStateException("Report template not found: " + REPORT_PATH);
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> params = new HashMap<>();
            params.put("EMPLOYEE_ID", employeeId);
            params.put("LOGO_PATH", logoStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
            JasperViewer.viewReport(jasperPrint, false);
        }
    }
}

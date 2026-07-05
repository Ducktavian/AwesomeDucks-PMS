package com.motorph.reporting;

import com.motorph.config.DatabaseConnection;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;


public class PayslipReportGenerator {

    private static final String REPORT_PATH = "/JasperReport/EmployeePayslip.jrxml";
    private static final String LOGO_PATH = "/com/motorph/img/MotorPH-icon.png";

    private static JasperPrint fill(int payrollId) throws Exception {
        try (InputStream reportStream = PayslipReportGenerator.class.getResourceAsStream(REPORT_PATH);
             InputStream logoStream = PayslipReportGenerator.class.getResourceAsStream(LOGO_PATH);
             Connection connection = DatabaseConnection.getConnection()) {

            if (reportStream == null) {
                throw new IllegalStateException("Report template not found: " + REPORT_PATH);
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> params = new HashMap<>();
            params.put("PAYROLL_ID", payrollId);
            params.put("LOGO", logoStream);

            return JasperFillManager.fillReport(jasperReport, params, connection);
        }
    }

   
    public static void view(int payrollId) throws Exception {
        JasperPrint jasperPrint = fill(payrollId);
        JasperViewer.viewReport(jasperPrint, false);
    }

    public static void generateToPdf(int payrollId, String outputPath) throws Exception {
        JasperPrint jasperPrint = fill(payrollId);
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
    }
}

package com.motorph.reporting;

import com.motorph.config.DatabaseConnection;
import com.motorph.model.Employee;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;


public class TimeCardReportGenerator {

    private static final String REPORT_PATH = "/JasperReport/EmployeeTimecard.jrxml";
    private static final String LOGO_PATH = "/com/motorph/img/MotorPH-icon.png";

    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public static void view(int employeeId, int month, int year) throws Exception {
        try (InputStream reportStream = TimeCardReportGenerator.class.getResourceAsStream(REPORT_PATH);
             InputStream logoStream = TimeCardReportGenerator.class.getResourceAsStream(LOGO_PATH);
             Connection connection = DatabaseConnection.getConnection()) {

            if (reportStream == null) {
                throw new IllegalStateException("Report template not found: " + REPORT_PATH);
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> params = new HashMap<>();
            params.put("EMPLOYEE_ID", employeeId);
            params.put("MONTH", month);
            params.put("YEAR", year);
            params.put("LOGO", logoStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
            JasperViewer.viewReport(jasperPrint, false);
        }
    }

   
    public static boolean promptAndView(Component parent, int employeeId) {
        LocalDate now = LocalDate.now();
        JComboBox<String> monthBox = monthComboBox(now);
        JComboBox<Integer> yearBox = yearComboBox(now);

        JPanel picker = new JPanel(new GridLayout(2, 2, 8, 8));
        picker.add(new JLabel("Month:"));
        picker.add(monthBox);
        picker.add(new JLabel("Year:"));
        picker.add(yearBox);

        if (!confirm(parent, picker, "Select Timecard Period")) {
            return false;
        }

        return generate(parent, employeeId, monthBox.getSelectedIndex() + 1, (Integer) yearBox.getSelectedItem());
    }

  
    public static boolean promptAndView(Component parent, List<Employee> employees) {
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "There are no employees to print a timecard for.",
                    "Print Timecard", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        JComboBox<Employee> employeeBox = new JComboBox<>(employees.toArray(new Employee[0]));
        employeeBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Employee employee = (Employee) value;
                String text = employee == null ? ""
                        : employee.getEmployeeId() + " - " + employee.getFullName();
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });

        LocalDate now = LocalDate.now();
        JComboBox<String> monthBox = monthComboBox(now);
        JComboBox<Integer> yearBox = yearComboBox(now);

        JPanel picker = new JPanel(new GridLayout(3, 2, 8, 8));
        picker.add(new JLabel("Employee:"));
        picker.add(employeeBox);
        picker.add(new JLabel("Month:"));
        picker.add(monthBox);
        picker.add(new JLabel("Year:"));
        picker.add(yearBox);

        if (!confirm(parent, picker, "Print Employee Timecard")) {
            return false;
        }

        Employee selected = (Employee) employeeBox.getSelectedItem();
        return generate(parent, Integer.parseInt(selected.getEmployeeId()),
                monthBox.getSelectedIndex() + 1, (Integer) yearBox.getSelectedItem());
    }

    private static JComboBox<String> monthComboBox(LocalDate defaultDate) {
        JComboBox<String> monthBox = new JComboBox<>(MONTH_NAMES);
        monthBox.setSelectedIndex(defaultDate.getMonthValue() - 1);
        return monthBox;
    }

    private static JComboBox<Integer> yearComboBox(LocalDate defaultDate) {
        JComboBox<Integer> yearBox = new JComboBox<>();
        int currentYear = defaultDate.getYear();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            yearBox.addItem(y);
        }
        yearBox.setSelectedItem(currentYear);
        return yearBox;
    }

    private static boolean confirm(Component parent, JPanel picker, String title) {
        int result = JOptionPane.showConfirmDialog(
                parent, picker, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    private static boolean generate(Component parent, int employeeId, int month, int year) {
        try {
            view(employeeId, month, year);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Failed to generate PDF:\n" + ex.getMessage(),
                    "Save PDF Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

package com.motorph.ui;

import model.Employee;
import repository.CsvEmployeeRepository;
import repository.EmployeeRepository;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MainDashboardLauncher {

    private static final Logger LOGGER = Logger.getLogger(MainDashboardLauncher.class.getName());

    private static final String APP_TITLE = "MotorPH Payroll System";
    private static final String EMPLOYEE_CSV_FILE_NAME = "MotorPH Employee Record.csv";

    private MainDashboardLauncher() {
        // Utility class. Prevents object creation.
    }

    public static void launch(Employee loggedInEmployee) {
        runOnEventDispatchThread(() -> {
            try {
                applySystemLookAndFeel();

                Path employeeCsvPath = resolveEmployeeCsvPath();
                EmployeeRepository employeeRepository = createEmployeeRepository(employeeCsvPath);

                MainDashboardFrame dashboardFrame = new MainDashboardFrame(
                        employeeRepository,
                        employeeCsvPath,
                        loggedInEmployee
                );

                dashboardFrame.setVisible(true);

            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Unable to launch the main dashboard.", exception);
                showLaunchErrorDialog(exception);
            }
        });
    }

    public static void main(String[] args) {
        runOnEventDispatchThread(() -> {
            applySystemLookAndFeel();

            JFrame owner = new JFrame();
            owner.setUndecorated(true);
            owner.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            owner.setLocationRelativeTo(null);

            LoginDialog loginDialog = new LoginDialog(owner);
            loginDialog.setVisible(true);

            if (!loginDialog.isSucceeded()) {
                owner.dispose();
                System.exit(0);
                return;
            }

            Employee loggedInEmployee = loginDialog.getLoggedInEmployee();
            owner.dispose();

            launch(loggedInEmployee);
        });
    }

    private static EmployeeRepository createEmployeeRepository(Path employeeCsvPath) {
        return new CsvEmployeeRepository(employeeCsvPath.toString());
    }

    private static Path resolveEmployeeCsvPath() {
        Path[] candidates = buildEmployeeCsvCandidates();

        for (Path candidate : candidates) {
            Path normalizedPath = candidate.toAbsolutePath().normalize();

            if (Files.exists(normalizedPath) && Files.isRegularFile(normalizedPath)) {
                return normalizedPath;
            }
        }

        Path fallbackPath = candidates[0].toAbsolutePath().normalize();

        LOGGER.warning(
                "Employee CSV file was not found. Using fallback path: "
                        + fallbackPath
        );

        return fallbackPath;
    }

    private static Path[] buildEmployeeCsvCandidates() {
        String userDirectory = System.getProperty("user.dir");

        return new Path[]{
                Paths.get(userDirectory, "data", EMPLOYEE_CSV_FILE_NAME),
                Paths.get(userDirectory, "src", "data", EMPLOYEE_CSV_FILE_NAME),
                Paths.get("data", EMPLOYEE_CSV_FILE_NAME),
                Paths.get("src", "data", EMPLOYEE_CSV_FILE_NAME)
        };
    }

    private static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 14));

        } catch (Exception exception) {
            LOGGER.log(Level.WARNING, "System look and feel could not be applied.", exception);
        }
    }

    private static void showLaunchErrorDialog(Exception exception) {
        JOptionPane.showMessageDialog(
                null,
                "The dashboard could not be opened.\n\n"
                        + "Reason: " + exception.getMessage(),
                APP_TITLE,
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static void runOnEventDispatchThread(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
}

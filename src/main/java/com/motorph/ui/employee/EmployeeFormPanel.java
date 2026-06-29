package com.motorph.ui.employee;

import com.motorph.model.Employee;
import com.motorph.service.EmployeeService;
import com.motorph.util.AppContext;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.*;

public class EmployeeFormPanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color PLACEHOLDER_GRAY = new Color(185, 185, 185);
    private static final String FONT = "Segoe UI";

    private final Runnable onBack;
    private final EmployeeService employeeService = AppContext.getEmployeeService();

    private boolean updateMode = false;
    private Employee selectedEmployee;

    private JTextField employeeIdField, firstNameField, lastNameField;
    private JTextField positionField, supervisorField, statusField;
    private JTextField birthdateField, cellphoneField;
    private JTextField emailField, addressField;
    private JTextField sssField, philhealthField, pagibigField, tinField;
    private JTextField basicSalaryField, semiMonthlyRateField, hourlyRateField;
    private JTextField riceSubsidyField, phoneAllowanceField, clothingAllowanceField;

    private JButton submitButton;

    public EmployeeFormPanel(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(null);
        main.setBackground(Color.WHITE);

        main.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeForm(main);
            }
        });

        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(new Font(FONT, Font.PLAIN, 17));
        back.setForeground(new Color(80, 80, 80));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setBounds(64, 55, 80, 24);
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onBack != null) onBack.run();
            }
        });
        main.add(back);

        addSection(main, "Basic Information", 64, 111,
                new String[]{
                    "Employee ID", "First Name", "Last Name",
                    "Position", "Immediate Supervisor", "Status"
                },
                new String[]{
                    "e.g., 10001", "e.g., Juan", "e.g., Dela Cruz",
                    "e.g., Software Engineer", "e.g., Maria Santos", "e.g., Regular"
                });

        addSection(main, "Personal Detail", 290, 111,
                new String[]{
                    "Birthdate", "Cellphone No.", "E-mail", "Address"
                },
                new String[]{
                    "MM-DD-YYYY", "e.g., 0917-123-4567",
                    "e.g., juan@email.com", "e.g., Quezon City"
                });

        addSection(main, "Government ID", 516, 111,
                new String[]{
                    "SSS No.", "PhilHealth No.", "PAG-IBIG No.", "TIN"
                },
                new String[]{
                    "XX-XXXXXXX-Y", "XX-XXXXXXXXX-X",
                    "XXXX-XXXX-XXXX", "XXX-XXX-XXX-XXX"
                });

        addSection(main, "Compensation", 742, 111,
                new String[]{
                    "Basic Salary", "Gross Semi-Monthly Rate", "Hourly Rate",
                    "Rice Subsidy", "Phone Allowance", "Clothing Allowance"
                },
                new String[]{
                    "e.g., 50000.00", "e.g., 25000.00", "e.g., 300.00",
                    "e.g., 1500.00", "e.g., 1000.00", "e.g., 1000.00"
                });

        submitButton = new JButton("Submit");
        submitButton.setName("submit");
        submitButton.setBounds(830, 577, 113, 39);
        submitButton.setBackground(NAVY);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setFont(new Font(FONT, Font.PLAIN, 14));
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> saveEmployee());
        main.add(submitButton);

        return main;
    }

    private void addSection(JPanel parent, String title, int x, int y,
                            String[] labels, String[] placeholders) {
        int sectionIndex = getSectionIndex(title);

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setName("sectionTitle" + sectionIndex);
        sectionTitle.setFont(new Font(FONT, Font.BOLD, 20));
        sectionTitle.setBounds(x, y, 210, 28);
        parent.add(sectionTitle);

        int currentY = y + 42;

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setName("fieldLabel_" + sectionIndex + "_" + i);
            label.setFont(new Font(FONT, Font.PLAIN, 13));
            label.setBounds(x, currentY, 180, 17);
            parent.add(label);

            JTextField field = new JTextField();
            field.setName("field_" + sectionIndex + "_" + i);
            field.setFont(new Font(FONT, Font.PLAIN, 13));
            field.setBounds(x, currentY + 18, 174, 28);
            field.setBorder(new CompoundBorder(
                    new RoundedBorder(6),
                    new EmptyBorder(2, 8, 2, 8)
            ));

            assignFieldReference(sectionIndex, i, field);
            setPlaceholder(field, placeholders[i]);

            parent.add(field);
            currentY += 57;
        }
    }

    private void assignFieldReference(int sectionIndex, int fieldIndex, JTextField field) {
        if (sectionIndex == 0) {
            switch (fieldIndex) {
                case 0 -> employeeIdField = field;
                case 1 -> firstNameField = field;
                case 2 -> lastNameField = field;
                case 3 -> positionField = field;
                case 4 -> supervisorField = field;
                case 5 -> statusField = field;
            }
        } else if (sectionIndex == 1) {
            switch (fieldIndex) {
                case 0 -> birthdateField = field;
                case 1 -> cellphoneField = field;
                case 2 -> emailField = field;
                case 3 -> addressField = field;
            }
        } else if (sectionIndex == 2) {
            switch (fieldIndex) {
                case 0 -> sssField = field;
                case 1 -> philhealthField = field;
                case 2 -> pagibigField = field;
                case 3 -> tinField = field;
            }
        } else if (sectionIndex == 3) {
            switch (fieldIndex) {
                case 0 -> basicSalaryField = field;
                case 1 -> semiMonthlyRateField = field;
                case 2 -> hourlyRateField = field;
                case 3 -> riceSubsidyField = field;
                case 4 -> phoneAllowanceField = field;
                case 5 -> clothingAllowanceField = field;
            }
        }
    }

    private void saveEmployee() {
        try {
            Employee employee = buildEmployeeFromForm();

            if (updateMode) {
                employeeService.updateEmployee(employee);
                JOptionPane.showMessageDialog(this, "Employee updated successfully.");
            } else {
                employeeService.addEmployee(employee);
                JOptionPane.showMessageDialog(this, "Employee added successfully.");
            }

            if (onBack != null) onBack.run();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save employee:\n" + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Employee buildEmployeeFromForm() {
        Employee employee = new Employee();

        employee.setEmployeeId(getValue(employeeIdField));
        employee.setFirstName(getValue(firstNameField));
        employee.setLastName(getValue(lastNameField));
        employee.setPosition(getValue(positionField));
        employee.setImmediateSupervisor(getValue(supervisorField));
        employee.setStatus(getValue(statusField));

        employee.setBirthday(parseDate(getValue(birthdateField)));
        employee.setPhoneNumber(getValue(cellphoneField));
        employee.setEmail(getValue(emailField));
        employee.setAddress(getValue(addressField));

        employee.setSSSNumber(getValue(sssField));
        employee.setPhilhealthNumber(getValue(philhealthField));
        employee.setPagIbigNumber(getValue(pagibigField));
        employee.setTIN(getValue(tinField));

        employee.setBasicSalary(parseDouble(getValue(basicSalaryField)));
        employee.setRiceSubsidy(parseDouble(getValue(riceSubsidyField)));
        employee.setPhoneAllowance(parseDouble(getValue(phoneAllowanceField)));
        employee.setClothingAllowance(parseDouble(getValue(clothingAllowanceField)));
        employee.setHourlyRate(parseDouble(getValue(hourlyRateField)));

        if (updateMode && selectedEmployee != null) {
            employee.setPositionId(selectedEmployee.getPositionId());
            employee.setImmediateSupervisorId(selectedEmployee.getImmediateSupervisorId());
            employee.setEmploymentStatusId(selectedEmployee.getEmploymentStatusId());
        }

        return employee;
    }

    public void setAddMode() {
        updateMode = false;
        selectedEmployee = null;
        clearFields();
        restorePlaceholders();
        setFieldsEditable(true);

        employeeIdField.setEditable(true);

        if (submitButton != null) {
            submitButton.setVisible(true);
        }
    }

    public void setUpdateMode(Employee employee) {
        updateMode = true;
        selectedEmployee = employee;
        clearFields();
        populateFields(employee);
        setFieldsEditable(true);

        employeeIdField.setEditable(false);

        if (submitButton != null) {
            submitButton.setVisible(true);
        }
    }

    public void setViewMode(Employee employee) {
        updateMode = false;
        selectedEmployee = employee;
        clearFields();
        populateFields(employee);
        setFieldsEditable(false);

        if (submitButton != null) {
            submitButton.setVisible(false);
        }
    }

    private void setFieldsEditable(boolean editable) {
        for (Component c : getAllComponents(this)) {
            if (c instanceof JTextField field) {
                field.setEditable(editable);
                field.setFocusable(editable);
            }
        }
    }

    private void populateFields(Employee emp) {
        employeeIdField.setText(safe(emp.getEmployeeId()));
        firstNameField.setText(safe(emp.getFirstName()));
        lastNameField.setText(safe(emp.getLastName()));
        positionField.setText(safe(emp.getPosition()));
        supervisorField.setText(safe(emp.getImmediateSupervisor()));
        statusField.setText(safe(emp.getStatus()));

        birthdateField.setText(emp.getBirthday() == null ? "" : emp.getBirthday().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        cellphoneField.setText(safe(emp.getPhoneNumber()));
        emailField.setText(safe(emp.getEmail()));
        addressField.setText(safe(emp.getAddress()));

        sssField.setText(safe(emp.getSSSNumber()));
        philhealthField.setText(safe(emp.getPhilhealthNumber()));
        pagibigField.setText(safe(emp.getPagibigNumber()));
        tinField.setText(safe(emp.getTIN()));

        basicSalaryField.setText(String.valueOf(emp.getBasicSalary()));
        semiMonthlyRateField.setText(String.valueOf(emp.getSemiMonthlyRate()));
        hourlyRateField.setText(String.valueOf(emp.getHourlyRate()));
        riceSubsidyField.setText(String.valueOf(emp.getRiceSubsidy()));
        phoneAllowanceField.setText(String.valueOf(emp.getPhoneAllowance()));
        clothingAllowanceField.setText(String.valueOf(emp.getClothingAllowance()));

        setAllFieldsBlack();
    }

    private String getValue(JTextField field) {
        String text = field.getText().trim();
        String placeholder = (String) field.getClientProperty("placeholder");

        if (placeholder != null && text.equals(placeholder)) {
            return "";
        }

        return text;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    }

    private double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }

        return Double.parseDouble(value.replace(",", ""));
    }

    private void clearFields() {
        for (Component c : getAllComponents(this)) {
            if (c instanceof JTextField field) {
                field.setText("");
                field.setForeground(Color.BLACK);
            }
        }
    }

    private void restorePlaceholders() {
        for (Component c : getAllComponents(this)) {
            if (c instanceof JTextField field) {
                String placeholder = (String) field.getClientProperty("placeholder");

                if (placeholder != null) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_GRAY);
                }
            }
        }
    }

    private void setPlaceholder(JTextField field, String placeholder) {
        field.putClientProperty("placeholder", placeholder);
        field.setText(placeholder);
        field.setForeground(PLACEHOLDER_GRAY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_GRAY);
                }
            }
        });
    }

    private void setAllFieldsBlack() {
        for (Component c : getAllComponents(this)) {
            if (c instanceof JTextField field) {
                field.setForeground(Color.BLACK);
            }
        }
    }

    private java.util.List<Component> getAllComponents(Container container) {
        java.util.List<Component> components = new java.util.ArrayList<>();

        for (Component c : container.getComponents()) {
            components.add(c);

            if (c instanceof Container child) {
                components.addAll(getAllComponents(child));
            }
        }

        return components;
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    private void resizeForm(JPanel main) {
        int leftMargin = 64;
        int rightMargin = 80;
        int gap = 60;
        int columnCount = 4;

        int availableWidth = main.getWidth() - leftMargin - rightMargin;
        int fieldWidth = (availableWidth - (gap * (columnCount - 1))) / columnCount;

        int[] xPositions = new int[columnCount];

        for (int i = 0; i < columnCount; i++) {
            xPositions[i] = leftMargin + i * (fieldWidth + gap);
        }

        for (Component c : main.getComponents()) {
            if (c instanceof JLabel label && label.getName() != null && label.getName().startsWith("sectionTitle")) {
                int index = Integer.parseInt(label.getName().replace("sectionTitle", ""));
                label.setBounds(xPositions[index], label.getY(), fieldWidth, label.getHeight());
            }

            if (c instanceof JLabel label && label.getName() != null && label.getName().startsWith("fieldLabel")) {
                int index = Integer.parseInt(label.getName().split("_")[1]);
                label.setBounds(xPositions[index], label.getY(), fieldWidth, label.getHeight());
            }

            if (c instanceof JTextField field && field.getName() != null && field.getName().startsWith("field")) {
                int index = Integer.parseInt(field.getName().split("_")[1]);
                field.setBounds(xPositions[index], field.getY(), fieldWidth, field.getHeight());
            }

            if (c instanceof JButton button && "submit".equals(button.getName())) {
                button.setBounds(main.getWidth() - rightMargin - 113, 577, 113, 39);
            }
        }

        main.revalidate();
        main.repaint();
    }

    private int getSectionIndex(String title) {
        return switch (title) {
            case "Basic Information" -> 0;
            case "Personal Detail" -> 1;
            case "Government ID" -> 2;
            case "Compensation" -> 3;
            default -> 0;
        };
    }

    private static class CircleAvatar extends JPanel {
        private final int size;

        CircleAvatar(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(NAVY);
            g2.fillOval(0, 0, size - 1, size - 1);
            g2.dispose();
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(75, 75, 75));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }
}
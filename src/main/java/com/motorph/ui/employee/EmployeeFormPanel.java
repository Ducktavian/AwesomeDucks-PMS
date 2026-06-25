package com.motorph.ui.employee;

import com.motorph.model.Employee;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class EmployeeFormPanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color PLACEHOLDER_GRAY = new Color(185, 185, 185);
    private static final String FONT = "Segoe UI";

    private final Runnable onBack;

    private boolean updateMode = false;
    private Employee selectedEmployee;

    private JTextField employeeIdField, firstNameField, lastNameField, departmentField;
    private JTextField positionField, supervisorField, roleField, statusField;
    private JTextField genderField, birthdateField, cellphoneField, telephoneField;
    private JTextField emailField, addressField;
    private JTextField sssField, philhealthField, pagibigField, tinField;
    private JTextField basicSalaryField, semiMonthlyRateField, hourlyRateField;
    private JTextField riceSubsidyField, phoneAllowanceField, clothingAllowanceField;

    public EmployeeFormPanel(Runnable onBack) {
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildTopBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(100, 80));
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(180, 180, 180)));

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 15));
        profile.setOpaque(false);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("Name");
        name.setFont(new Font(FONT, Font.BOLD, 16));
        name.setForeground(NAVY);
        name.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel position = new JLabel("Position");
        position.setFont(new Font(FONT, Font.PLAIN, 13));
        position.setForeground(Color.GRAY);
        position.setAlignmentX(Component.RIGHT_ALIGNMENT);

        text.add(name);
        text.add(position);

        profile.add(text);
        profile.add(new CircleAvatar(48));

        topBar.add(profile, BorderLayout.EAST);
        return topBar;
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
                new String[]{"Employee ID", "First Name", "Last Name", "Department",
                    "Position", "Immediate Supervisor", "Role", "Status"},
                new String[]{"e.g., 10001", "e.g., Juan", "e.g., Dela Cruz", "e.g., IT",
                    "e.g., Software Engineer", "e.g., Maria Santos", "e.g., Employee", "e.g., Regular"});

        addSection(main, "Personal Detail", 290, 111,
                new String[]{"Gender", "Birthdate", "Cellphone No.",
                    "Telephone No.", "E-mail", "Address"},
                new String[]{"e.g., Male", "MM-DD-YYYY", "e.g., 0917-123-4567",
                    "e.g., 8123-4567", "e.g., juan@email.com", "e.g., Quezon City"});

        addSection(main, "Government ID", 516, 111,
                new String[]{"SSS No.", "PhilHealth No.", "PAG-IBIG No.", "TIN"},
                new String[]{"XX-XXXXXXX-Y", "XX-XXXXXXXXX-X",
                    "XXXX-XXXX-XXXX", "XXX-XXX-XXX-XXX"});

        addSection(main, "Compensation", 742, 111,
                new String[]{"Basic Salary", "Gross Semi-Monthly Rate", "Hourly Rate",
                    "Rice Subsidy", "Phone Allowance", "Clothing Allowance"},
                new String[]{"e.g., 50000.00", "e.g., 25000.00", "e.g., 300.00",
                    "e.g., 1500.00", "e.g., 1000.00", "e.g., 1000.00"});

        JButton submit = new JButton("Submit");
        submit.setName("submit");
        submit.setBounds(830, 577, 113, 39);
        submit.setBackground(NAVY);
        submit.setForeground(Color.WHITE);
        submit.setFocusPainted(false);
        submit.setBorderPainted(false);
        submit.setFont(new Font(FONT, Font.PLAIN, 14));
        submit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        main.add(submit);

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
                case 3 -> departmentField = field;
                case 4 -> positionField = field;
                case 5 -> supervisorField = field;
                case 6 -> roleField = field;
                case 7 -> statusField = field;
            }
        } else if (sectionIndex == 1) {
            switch (fieldIndex) {
                case 0 -> genderField = field;
                case 1 -> birthdateField = field;
                case 2 -> cellphoneField = field;
                case 3 -> telephoneField = field;
                case 4 -> emailField = field;
                case 5 -> addressField = field;
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

    public void setAddMode() {
        updateMode = false;
        selectedEmployee = null;
        clearFields();
        restorePlaceholders();
        employeeIdField.setEditable(true);
    }

    public void setUpdateMode(Employee employee) {
        updateMode = true;
        selectedEmployee = employee;
        clearFields();
        populateFields(employee);
        employeeIdField.setEditable(false);
    }

    private void populateFields(Employee emp) {
        employeeIdField.setText(safe(emp.getEmployeeId()));
        firstNameField.setText(safe(emp.getFirstName()));
        lastNameField.setText(safe(emp.getLastName()));
        departmentField.setText(safe(emp.getDepartment()));
        positionField.setText(safe(emp.getPosition()));
        supervisorField.setText(safe(emp.getImmediateSupervisor()));
        roleField.setText("");
        statusField.setText(safe(emp.getStatus()));

        genderField.setText(safe(emp.getGender()));
        birthdateField.setText(String.valueOf(emp.getBirthday()));
        cellphoneField.setText(safe(emp.getPhoneNumber()));
        telephoneField.setText("");
        emailField.setText("");
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
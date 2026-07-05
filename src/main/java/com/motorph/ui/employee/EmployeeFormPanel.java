package com.motorph.ui.employee;

import com.motorph.exception.UnauthorizedException;
import com.motorph.model.Employee;
import com.motorph.service.EmployeeService;
import com.motorph.util.AppContext;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EmployeeFormPanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color PLACEHOLDER_GRAY = new Color(185, 185, 185);
    private static final String FONT = "Segoe UI";
    private static final String NO_SUPERVISOR = "N/A";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Runnable onBack;
    private final EmployeeService employeeService = AppContext.getEmployeeService();
    private final Map<String, Integer> supervisorIdsByName = new LinkedHashMap<>();
    private Map<String, Integer> positionIdsByName = new LinkedHashMap<>();
    private Map<String, Integer> statusIdsByName = new LinkedHashMap<>();
    private Map<String, Integer> departmentIdsByName = new LinkedHashMap<>();

    private boolean updateMode = false;
    private Employee selectedEmployee;
    // Guards the department combo's listener while we repopulate it programmatically.
    private boolean loadingDepartments = false;

    private JTextField employeeIdField, firstNameField, lastNameField;
    private JComboBox<String> departmentComboBox;
    private JTextField departmentField;
    private JComboBox<String> positionComboBox;
    private JTextField positionField;
    private JTextField statusField;
    private JComboBox<String> supervisorComboBox;
    private JTextField supervisorField;
    private JComboBox<String> statusComboBox;

    private JTextField birthdateField, cellphoneField;
    private JButton datePickerButton;

    private JTextField emailField;
    private JTextArea addressField;

    private JTextField sssField, philhealthField, pagibigField, tinField;
    private JTextField basicSalaryField, semiMonthlyRateField, hourlyRateField;
    private JTextField riceSubsidyField, phoneAllowanceField, clothingAllowanceField;

    private JButton submitButton;

    private static final int FIELD_WIDTH = 360;
    private static final int FIELD_HEIGHT = 30;
    private static final int LABEL_WIDTH = 180;

    public EmployeeFormPanel(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createScrollableContent(), BorderLayout.CENTER);
    }

    private JComponent createScrollableContent() {
        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(28, 40, 32, 40));

        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(new Font(FONT, Font.PLAIN, 17));
        back.setForeground(new Color(80, 80, 80));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setAlignmentX(LEFT_ALIGNMENT);
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onBack != null) onBack.run();
            }
        });
        content.add(back);
        content.add(Box.createVerticalStrut(26));

        content.add(buildSection("Basic Information",
                new String[]{
                    "Employee ID", "First Name", "Last Name",
                    "Department", "Position", "Immediate Supervisor", "Status"
                },
                new String[]{
                    "e.g., 10001", "e.g., Juan", "e.g., Dela Cruz",
                    "", "", "", ""
                }));
        content.add(Box.createVerticalStrut(30));

        content.add(buildSection("Personal Detail",
                new String[]{
                    "Birthdate", "Cellphone No.", "E-mail", "Address"
                },
                new String[]{
                    "MM-DD-YYYY", "e.g., 917123456",
                    "e.g., juan@email.com", "e.g., Quezon City"
                }));
        content.add(Box.createVerticalStrut(30));

        content.add(buildSection("Government ID",
                new String[]{
                    "SSS No.", "PhilHealth No.", "PAG-IBIG No.", "TIN"
                },
                new String[]{
                    "44-4506057-3", "820126853951",
                    "691295330870", "442-605-657-000"
                }));
        content.add(Box.createVerticalStrut(30));

        content.add(buildSection("Compensation",
                new String[]{
                    "Basic Salary", "Gross Semi-Monthly Rate", "Hourly Rate",
                    "Rice Subsidy", "Phone Allowance", "Clothing Allowance"
                },
                new String[]{
                    "e.g., 50000.00", "Auto-computed", "Auto-computed",
                    "e.g., 1500.00", "e.g., 1000.00", "e.g., 1000.00"
                }));
        content.add(Box.createVerticalStrut(30));

        submitButton = new JButton("Submit");
        submitButton.setAlignmentX(LEFT_ALIGNMENT);
        submitButton.setPreferredSize(new Dimension(FIELD_WIDTH, 42));
        submitButton.setMaximumSize(new Dimension(FIELD_WIDTH, 42));
        submitButton.setBackground(NAVY);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setFont(new Font(FONT, Font.PLAIN, 14));
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> saveEmployee());

        JPanel submitRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        submitRow.setOpaque(false);
        submitRow.setAlignmentX(LEFT_ALIGNMENT);
        submitRow.setPreferredSize(new Dimension(LABEL_WIDTH + FIELD_WIDTH + 16, 42));
        submitRow.setMaximumSize(new Dimension(LABEL_WIDTH + FIELD_WIDTH + 16, 42));
        submitRow.add(submitButton);
        content.add(submitRow);

        // Keep the stacked content pinned top-left at its natural size so it
        // doesn't stretch to fill the viewport; the scroll pane handles overflow.
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(Color.WHITE);
        holder.add(content, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildSection(String title, String[] labels, String[] placeholders) {
        int sectionIndex = getSectionIndex(title);

        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font(FONT, Font.BOLD, 20));
        sectionTitle.setAlignmentX(LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(16));

        for (int i = 0; i < labels.length; i++) {
            section.add(buildRow(labels[i], buildInput(sectionIndex, i, placeholders)));
            section.add(Box.createVerticalStrut(14));
        }

        return section;
    }

    // One field per row: a consistent label column followed by its input.
    private JComponent buildRow(String labelText, JComponent input) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setPreferredSize(new Dimension(LABEL_WIDTH + FIELD_WIDTH + 16,
                Math.max(FIELD_HEIGHT, input.getPreferredSize().height)));
        row.setMaximumSize(new Dimension(LABEL_WIDTH + FIELD_WIDTH + 16,
                Math.max(FIELD_HEIGHT, input.getPreferredSize().height)));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font(FONT, Font.PLAIN, 13));
        label.setPreferredSize(new Dimension(LABEL_WIDTH, FIELD_HEIGHT));
        label.setVerticalAlignment(SwingConstants.CENTER);

        row.add(label, BorderLayout.WEST);
        row.add(input, BorderLayout.CENTER);
        return row;
    }

    // Builds the input widget for a given section/field, keeping all the
    // special cases (supervisor combo, status combo+field, birthdate picker,
    // address text area) and wiring up the field references exactly as before.
    private JComponent buildInput(int sectionIndex, int i, String[] placeholders) {
        if (sectionIndex == 0 && i == 3) {
            departmentComboBox = new JComboBox<>();
            styleCombo(departmentComboBox);
            loadDepartmentOptions();
            // When the department changes, refresh the Position and Supervisor lists.
            departmentComboBox.addActionListener(e -> onDepartmentChanged());

            departmentField = createReadOnlyField();
            return buildComboFieldStack(departmentComboBox, departmentField);
        }

        if (sectionIndex == 0 && i == 4) {
            positionComboBox = new JComboBox<>();
            styleCombo(positionComboBox);

            positionField = createReadOnlyField();
            return buildComboFieldStack(positionComboBox, positionField);
        }

        if (sectionIndex == 0 && i == 5) {
            supervisorComboBox = new JComboBox<>();
            styleCombo(supervisorComboBox);
            loadSupervisorOptions();

            supervisorField = createReadOnlyField();
            return buildComboFieldStack(supervisorComboBox, supervisorField);
        }

        if (sectionIndex == 0 && i == 6) {
            statusComboBox = new JComboBox<>(new String[]{"Regular", "Probationary"});
            styleCombo(statusComboBox);

            statusField = createReadOnlyField();
            return buildComboFieldStack(statusComboBox, statusField);
        }

        if (sectionIndex == 1 && i == 0) {
            birthdateField = new JTextField();
            birthdateField.setFont(new Font(FONT, Font.PLAIN, 13));
            birthdateField.setBackground(Color.WHITE);
            birthdateField.setOpaque(true);
            birthdateField.setBorder(new CompoundBorder(
                    new RoundedBorder(6),
                    new EmptyBorder(2, 8, 2, 8)
            ));
            setPlaceholder(birthdateField, placeholders[i]);

            datePickerButton = new JButton("📅");
            datePickerButton.setFont(new Font(FONT, Font.PLAIN, 11));
            datePickerButton.setPreferredSize(new Dimension(34, FIELD_HEIGHT));
            datePickerButton.setBackground(Color.WHITE);
            datePickerButton.setFocusPainted(false);
            datePickerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            datePickerButton.addActionListener(e -> openDatePicker());

            JPanel dateRow = new JPanel(new BorderLayout(6, 0));
            dateRow.setOpaque(false);
            sizeInput(dateRow);
            dateRow.add(birthdateField, BorderLayout.CENTER);
            dateRow.add(datePickerButton, BorderLayout.EAST);
            return dateRow;
        }

        if (sectionIndex == 1 && i == 3) {
            JTextArea area = new JTextArea();
            area.setFont(new Font(FONT, Font.PLAIN, 13));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setBackground(Color.WHITE);
            area.setOpaque(true);
            area.setBorder(new CompoundBorder(
                    new RoundedBorder(6),
                    new EmptyBorder(6, 8, 6, 8)
            ));
            area.setPreferredSize(new Dimension(FIELD_WIDTH, 84));
            area.setMaximumSize(new Dimension(FIELD_WIDTH, 84));

            addressField = area;
            setPlaceholder(area, placeholders[i]);
            return area;
        }

        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(6),
                new EmptyBorder(2, 8, 2, 8)
        ));
        sizeInput(field);

        assignFieldReference(sectionIndex, i, field);
        setPlaceholder(field, placeholders[i]);

        if (sectionIndex == 3 && i == 0) {
            addSalaryAutoComputeListener(field);
        }

        return field;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(new Font(FONT, Font.PLAIN, 13));
        combo.setOpaque(false);
        sizeInput(combo);
    }

    // Locks an input to the standard field width/height so BoxLayout renders a
    // consistent single column instead of stretching inputs across the page.
    private void sizeInput(JComponent c) {
        c.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        c.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        c.setMinimumSize(new Dimension(160, FIELD_HEIGHT));
    }

    // Read-only text field shown in place of a combo in view mode.
    private JTextField createReadOnlyField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(6),
                new EmptyBorder(2, 8, 2, 8)
        ));
        field.setEditable(false);
        field.setFocusable(false);
        field.setVisible(false);
        return field;
    }

    // Overlaps a combo + read-only field in one cell; visibility decides which
    // one shows (combo for add/update, field for view mode) so read-only mode
    // shows plain black text instead of a low-contrast disabled combo.
    private JPanel buildComboFieldStack(JComboBox<?> combo, JTextField field) {
        JPanel stack = new JPanel(new GridBagLayout());
        stack.setOpaque(false);
        sizeInput(stack);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        stack.add(combo, gc);
        stack.add(field, gc);
        return stack;
    }

    // ---------------------------------------------------------------
    // Department -> Position / Supervisor cascade
    // ---------------------------------------------------------------

    private void loadDepartmentOptions() {
        if (departmentComboBox == null) {
            return;
        }
        loadingDepartments = true;
        departmentIdsByName = employeeService.getDepartments();
        departmentComboBox.removeAllItems();
        for (String name : departmentIdsByName.keySet()) {
            departmentComboBox.addItem(name);
        }
        loadingDepartments = false;
    }

    private void onDepartmentChanged() {
        if (loadingDepartments) {
            return;
        }
        reloadPositionsForSelectedDepartment(null);
        loadSupervisorOptions();
    }

    private Integer getSelectedDepartmentId() {
        Object selected = departmentComboBox == null ? null : departmentComboBox.getSelectedItem();
        return selected == null ? null : departmentIdsByName.get(selected.toString());
    }

    private String getSelectedDepartmentName() {
        Object selected = departmentComboBox == null ? null : departmentComboBox.getSelectedItem();
        return selected == null ? "" : selected.toString();
    }

    // Selects a department without triggering the cascade listener; callers
    // repopulate the dependent lists explicitly afterwards.
    private void selectDepartment(String departmentName) {
        if (departmentComboBox == null) {
            return;
        }
        loadingDepartments = true;
        if (departmentName != null && departmentIdsByName.containsKey(departmentName)) {
            departmentComboBox.setSelectedItem(departmentName);
        } else if (departmentComboBox.getItemCount() > 0) {
            departmentComboBox.setSelectedIndex(0);
        }
        loadingDepartments = false;
    }

    private void reloadPositionsForSelectedDepartment(String positionToSelect) {
        if (positionComboBox == null) {
            return;
        }
        Integer deptId = getSelectedDepartmentId();
        positionIdsByName = deptId == null
                ? new LinkedHashMap<>()
                : employeeService.getPositionsByDepartment(deptId);

        positionComboBox.removeAllItems();
        for (String name : positionIdsByName.keySet()) {
            positionComboBox.addItem(name);
        }

        if (positionToSelect != null && positionIdsByName.containsKey(positionToSelect)) {
            positionComboBox.setSelectedItem(positionToSelect);
        }
    }

    private String getSelectedPosition() {
        Object selected = positionComboBox == null ? null : positionComboBox.getSelectedItem();
        return selected == null ? "" : selected.toString();
    }

    private void assignFieldReference(int sectionIndex, int fieldIndex, JTextField field) {
        if (sectionIndex == 0) {
            switch (fieldIndex) {
                case 0 -> employeeIdField = field;
                case 1 -> firstNameField = field;
                case 2 -> lastNameField = field;
                // 3 = Department, 4 = Position, 5 = Supervisor, 6 = Status are
                // combo-backed and handled directly in buildInput.
            }
        } else if (sectionIndex == 1) {
            switch (fieldIndex) {
                case 1 -> cellphoneField = field;
                case 2 -> emailField = field;
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

    private void addSalaryAutoComputeListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                computeRatesFromBasicSalary();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                computeRatesFromBasicSalary();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                computeRatesFromBasicSalary();
            }
        });
    }

    private void computeRatesFromBasicSalary() {
        if (basicSalaryField == null || semiMonthlyRateField == null || hourlyRateField == null) {
            return;
        }

        String value = getValue(basicSalaryField);

        if (value.isBlank()) {
            semiMonthlyRateField.setText("");
            hourlyRateField.setText("");
            return;
        }

        try {
            double basicSalary = Double.parseDouble(value.replace(",", ""));

            if (basicSalary < 0) {
                semiMonthlyRateField.setText("");
                hourlyRateField.setText("");
                return;
            }

            double semiMonthlyRate = basicSalary / 2.0;
            double hourlyRate = semiMonthlyRate / 2.0 / 5.0 / 8.0;

            semiMonthlyRateField.setText(String.format("%.2f", semiMonthlyRate));
            hourlyRateField.setText(String.format("%.2f", hourlyRate));

            semiMonthlyRateField.setForeground(Color.BLACK);
            hourlyRateField.setForeground(Color.BLACK);

        } catch (NumberFormatException ex) {
            semiMonthlyRateField.setText("");
            hourlyRateField.setText("");
        }
    }

    private void openDatePicker() {
        LocalDate initialDate = LocalDate.now();

        try {
            String current = getValue(birthdateField);
            if (!current.isBlank()) {
                initialDate = LocalDate.parse(current, DATE_FORMATTER);
            }
        } catch (Exception ignored) {
        }

        showCalendarDialog(initialDate);
    }

    private void showCalendarDialog(LocalDate initialDate) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Select Birthdate",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(360, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        final YearMonth[] currentMonth = {YearMonth.from(initialDate)};

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(12, 12, 8, 12));

        JButton prevButton = new JButton("‹");
        JButton nextButton = new JButton("›");

        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font(FONT, Font.BOLD, 18));

        styleCalendarButton(prevButton);
        styleCalendarButton(nextButton);

        header.add(prevButton, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextButton, BorderLayout.EAST);

        JPanel calendarPanel = new JPanel(new GridLayout(0, 7, 6, 6));
        calendarPanel.setBackground(Color.WHITE);
        calendarPanel.setBorder(new EmptyBorder(8, 12, 12, 12));

        Runnable refreshCalendar = () -> {
            calendarPanel.removeAll();

            monthLabel.setText(currentMonth[0].getMonth() + " " + currentMonth[0].getYear());

            String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

            for (String day : days) {
                JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
                dayLabel.setFont(new Font(FONT, Font.BOLD, 12));
                calendarPanel.add(dayLabel);
            }

            LocalDate firstDay = currentMonth[0].atDay(1);
            int startOffset = firstDay.getDayOfWeek().getValue() % 7;

            for (int i = 0; i < startOffset; i++) {
                calendarPanel.add(new JLabel(""));
            }

            int daysInMonth = currentMonth[0].lengthOfMonth();

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate selectedDate = currentMonth[0].atDay(day);

                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setFont(new Font(FONT, Font.PLAIN, 13));
                dayButton.setMargin(new Insets(0, 0, 0, 0));
                dayButton.setBackground(Color.WHITE);
                dayButton.setFocusPainted(false);
                dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                if (selectedDate.equals(initialDate)) {
                    dayButton.setBackground(new Color(225, 230, 245));
                }

                dayButton.addActionListener(e -> {
                    birthdateField.setText(selectedDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
                    birthdateField.setForeground(Color.BLACK);
                    dialog.dispose();
                });

                calendarPanel.add(dayButton);
            }

            calendarPanel.revalidate();
            calendarPanel.repaint();
        };

        prevButton.addActionListener(e -> {
            currentMonth[0] = currentMonth[0].minusMonths(1);
            refreshCalendar.run();
        });

        nextButton.addActionListener(e -> {
            currentMonth[0] = currentMonth[0].plusMonths(1);
            refreshCalendar.run();
        });

        refreshCalendar.run();

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(calendarPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void styleCalendarButton(JButton button) {
        button.setPreferredSize(new Dimension(45, 32));
        button.setBackground(Color.WHITE);
        button.setForeground(NAVY);
        button.setFocusPainted(false);
        button.setFont(new Font(FONT, Font.BOLD, 22));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void saveEmployee() {
        try {
            loadEmployeeLookupValues();
            computeRatesFromBasicSalary();

            String validationError = validateForm();

            if (!validationError.isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        buildValidationMessage(validationError),
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Employee employee = buildEmployeeFromForm();

            if (updateMode) {
                employeeService.updateEmployee(employee);
                JOptionPane.showMessageDialog(this, "Employee updated successfully.");
            } else {
                employeeService.addEmployee(employee);
                JOptionPane.showMessageDialog(this, "Employee added successfully.");
            }

            if (onBack != null) onBack.run();

        } catch (IllegalArgumentException ex) {
            // The service validates every field and throws a precise, field-specific
            // message (e.g. "Address is required."). Show that verbatim so the user
            // knows exactly which field to fix instead of a vague "failed to save".
            JOptionPane.showMessageDialog(
                    this,
                    buildValidationMessage(ex.getMessage()),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (UnauthorizedException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Not Allowed",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (Exception ex) {
            ex.printStackTrace();

            // Only genuinely unexpected failures (e.g. database errors) reach here.
            // Surface the underlying cause so it isn't a dead-end "failed to save".
            Throwable root = ex;
            while (root.getCause() != null) {
                root = root.getCause();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save employee.\n\n" + root.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Turns the raw newline-separated errors into a clear, scannable list so
    // the user can see exactly which fields need fixing (as an HTML bullet list
    // whose count is shown in the header).
    private String buildValidationMessage(String rawErrors) {
        String[] lines = rawErrors.split("\n");

        int count = 0;
        StringBuilder items = new StringBuilder();
        for (String line : lines) {
            if (!line.isBlank()) {
                count++;
                items.append("<li>")
                     .append(escapeHtml(line.trim()))
                     .append("</li>");
            }
        }

        String header = count == 1
                ? "Please fix the following issue before saving:"
                : "Please fix the following " + count + " issues before saving:";

        return "<html><body style='width:320px; font-family:Segoe UI; font-size:11px;'>"
                + "<p style='margin:0 0 6px 0;'><b>" + header + "</b></p>"
                + "<ul style='margin:0; padding-left:18px;'>" + items + "</ul>"
                + "</body></html>";
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }

    private String validateForm() {
        StringBuilder errors = new StringBuilder();

        String employeeId = getValue(employeeIdField);
        String firstName = getValue(firstNameField);
        String lastName = getValue(lastNameField);
        String position = getSelectedPosition();
        String status = getSelectedStatus();
        String birthdate = getValue(birthdateField);
        String cellphone = cleanDigits(getValue(cellphoneField));
        String sss = getValue(sssField);
        String philhealth = getValue(philhealthField);
        String pagibig = getValue(pagibigField);
        String tin = getValue(tinField);

        if (employeeId.isBlank()) {
            errors.append("Employee ID is required.\n");
        } else if (!employeeId.matches("\\d+")) {
            errors.append("Employee ID must contain numbers only.\n");
        } else if (isDuplicateEmployeeId(employeeId)) {
            errors.append("Employee ID already exists. Please use a unique Employee ID.\n");
        }

        if (firstName.isBlank()) errors.append("First Name is required.\n");
        if (lastName.isBlank()) errors.append("Last Name is required.\n");
        if (position.isBlank() || findIdIgnoreCase(positionIdsByName, position) == null) {
            errors.append("Please select a valid position before saving the employee.\n");
        }
        if (status.isBlank() || findIdIgnoreCase(statusIdsByName, status) == null) {
            errors.append("Please select a valid employment status.\n");
        }

        if (birthdate.isBlank()) {
            errors.append("Birthdate is required.\n");
        } else {
            try {
                LocalDate parsedBirthdate = LocalDate.parse(birthdate, DATE_FORMATTER);
                if (java.time.Period.between(parsedBirthdate, LocalDate.now()).getYears() < 18) {
                    errors.append("Employee must be at least 18 years old.\n");
                }
            } catch (java.time.format.DateTimeParseException ex) {
                errors.append("Birthdate must be in MM-DD-YYYY format.\n");
            }
        }

        if (getValue(addressField).isBlank()) {
            errors.append("Address is required.\n");
        }

        String sssDigits = sss.replaceAll("\\D", "");
        String philhealthDigits = philhealth.replaceAll("\\D", "");
        String pagibigDigits = pagibig.replaceAll("\\D", "");
        String tinDigits = tin.replaceAll("\\D", "");

        if (cellphone.isBlank()) {
            errors.append("Cellphone No. is required.\n");
        } else if (!cellphone.matches("\\d{9}")) {
            errors.append("Cellphone No. must have exactly 9 digits. Example: 917123456\n");
        }

        if (sssDigits.length() != 10) {
            errors.append("SSS Number must contain exactly 10 numeric digits.\n");
        }

        if (philhealthDigits.length() != 12) {
            errors.append("PhilHealth No. must have exactly 12 digits.\n");
        }

        if (tinDigits.length() != 12) {
            errors.append("TIN No. must have exactly 12 digits.\n");
        }

        if (pagibigDigits.length() != 12) {
            errors.append("PAG-IBIG No. must have exactly 12 digits.\n");
        }

        validateMoneyField(errors, basicSalaryField, "Basic Salary");
        validateMoneyField(errors, riceSubsidyField, "Rice Subsidy");
        validateMoneyField(errors, phoneAllowanceField, "Phone Allowance");
        validateMoneyField(errors, clothingAllowanceField, "Clothing Allowance");
        validateMoneyField(errors, semiMonthlyRateField, "Gross Semi-Monthly Rate");
        validateMoneyField(errors, hourlyRateField, "Hourly Rate");

        String basicSalary = getValue(basicSalaryField).replace(",", "");
        try {
            if (!basicSalary.isBlank() && Double.parseDouble(basicSalary) <= 0) {
                errors.append("Basic Salary must be greater than 0.\n");
            }
        } catch (NumberFormatException ignored) {
            // validateMoneyField already supplies the numeric validation message.
        }

        return errors.toString();
    }

    private boolean isDuplicateEmployeeId(String employeeId) {
        List<Employee> employees = employeeService.getAllEmployees();

        for (Employee emp : employees) {
            if (emp.getEmployeeId() != null && emp.getEmployeeId().equals(employeeId)) {
                if (updateMode && selectedEmployee != null
                        && employeeId.equals(selectedEmployee.getEmployeeId())) {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    private void validateMoneyField(StringBuilder errors, JTextField field, String fieldName) {
        String value = getValue(field);

        if (value.isBlank()) {
            errors.append(fieldName).append(" is required.\n");
            return;
        }

        try {
            double amount = Double.parseDouble(value.replace(",", ""));

            if (amount < 0) {
                errors.append(fieldName).append(" must not be negative.\n");
            }

            if (amount > 500000) {
                errors.append(fieldName).append(" must not exceed 500,000.\n");
            }

        } catch (NumberFormatException ex) {
            errors.append(fieldName).append(" must be numeric only.\n");
        }
    }

    private Employee buildEmployeeFromForm() {
        computeRatesFromBasicSalary();

        Employee employee = new Employee();

        employee.setEmployeeId(getValue(employeeIdField));
        employee.setFirstName(getValue(firstNameField));
        employee.setLastName(getValue(lastNameField));
        employee.setDepartment(getSelectedDepartmentName());
        employee.setPosition(getSelectedPosition());
        employee.setPositionId(findIdIgnoreCase(positionIdsByName, employee.getPosition()));
        String supervisorName = getSelectedSupervisorName();
        employee.setImmediateSupervisor(supervisorName);
        employee.setImmediateSupervisorId(getSelectedSupervisorId());
        employee.setStatus(getSelectedStatus());
        employee.setEmploymentStatusId(findIdIgnoreCase(statusIdsByName, employee.getStatus()));

        employee.setBirthday(parseDate(getValue(birthdateField)));
        employee.setPhoneNumber(cleanDigits(getValue(cellphoneField)));
        employee.setEmail(getValue(emailField));
        employee.setAddress(getValue(addressField));

        // Government IDs are entered/displayed with hyphens, but stored as digits
        // only — strip separators here so persistence is consistent.
        employee.setSSSNumber(cleanDigits(getValue(sssField)));
        employee.setPhilhealthNumber(cleanDigits(getValue(philhealthField)));
        employee.setPagIbigNumber(cleanDigits(getValue(pagibigField)));
        employee.setTIN(cleanDigits(getValue(tinField)));

        employee.setBasicSalary(parseDouble(getValue(basicSalaryField)));
        employee.setRiceSubsidy(parseDouble(getValue(riceSubsidyField)));
        employee.setPhoneAllowance(parseDouble(getValue(phoneAllowanceField)));
        employee.setClothingAllowance(parseDouble(getValue(clothingAllowanceField)));
        employee.setHourlyRate(parseDouble(getValue(hourlyRateField)));

        return employee;
    }

    private void loadEmployeeLookupValues() {
        // positionIdsByName is maintained per-selected-department by the
        // department -> position cascade, so we only refresh statuses here.
        statusIdsByName = employeeService.getEmploymentStatuses();
    }

    private Integer findIdIgnoreCase(Map<String, Integer> values, String selectedName) {
        if (selectedName == null) return null;
        String normalized = selectedName.trim();
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(normalized)) return entry.getValue();
        }
        return null;
    }

    public void setAddMode() {
        updateMode = false;
        selectedEmployee = null;

        clearFields();
        // Default to the first department, then load its positions/supervisors.
        selectDepartment(null);
        reloadPositionsForSelectedDepartment(null);
        loadSupervisorOptions();
        restorePlaceholders();
        setFieldsEditable(true);

        // Employee ID is system-generated (next sequential id), never typed in.
        employeeIdField.setText(employeeService.getNextEmployeeId());
        employeeIdField.setForeground(Color.BLACK);
        employeeIdField.setEditable(false);
        employeeIdField.setFocusable(false);
        employeeIdField.setBackground(new Color(245, 245, 245));

        departmentComboBox.setVisible(true);
        departmentField.setVisible(false);

        positionComboBox.setVisible(true);
        positionField.setVisible(false);

        statusComboBox.setVisible(true);
        statusComboBox.setSelectedItem("Regular");

        statusField.setVisible(false);

        supervisorComboBox.setVisible(true);
        supervisorField.setVisible(false);

        datePickerButton.setVisible(true);

        birthdateField.setEditable(true);
        birthdateField.setFocusable(true);

        setComputedFieldsReadOnly();

        if (submitButton != null) {
            submitButton.setVisible(true);
        }

        refreshFormLayout();
    }

    public void setUpdateMode(Employee employee) {
        updateMode = true;
        selectedEmployee = employee;

        clearFields();
        loadSupervisorOptions();
        populateFields(employee);
        setFieldsEditable(true);

        employeeIdField.setEditable(false);
        employeeIdField.setFocusable(false);

        departmentComboBox.setVisible(true);
        departmentField.setVisible(false);

        positionComboBox.setVisible(true);
        positionField.setVisible(false);

        statusComboBox.setVisible(true);
        statusField.setVisible(false);

        supervisorComboBox.setVisible(true);
        supervisorField.setVisible(false);

        datePickerButton.setVisible(true);

        birthdateField.setEditable(true);
        birthdateField.setFocusable(true);

        setComputedFieldsReadOnly();
        computeRatesFromBasicSalary();

        if (submitButton != null) {
            submitButton.setVisible(true);
        }

        refreshFormLayout();
    }

    public void setViewMode(Employee employee) {
        updateMode = false;
        selectedEmployee = employee;

        clearFields();
        loadSupervisorOptions();
        populateFields(employee);
        setFieldsEditable(false);

        departmentComboBox.setVisible(false);
        departmentField.setText(getSelectedDepartmentName());
        departmentField.setVisible(true);

        positionComboBox.setVisible(false);
        positionField.setText(getSelectedPosition());
        positionField.setVisible(true);

        statusComboBox.setVisible(false);
        statusField.setVisible(true);

        supervisorComboBox.setVisible(false);
        supervisorField.setText(getSelectedSupervisorName());
        supervisorField.setVisible(true);

        datePickerButton.setVisible(false);

        birthdateField.setEditable(false);
        birthdateField.setFocusable(false);

        if (submitButton != null) {
            submitButton.setVisible(false);
        }

        refreshFormLayout();
    }

    private void setComputedFieldsReadOnly() {
        if (semiMonthlyRateField != null) {
            semiMonthlyRateField.setEditable(false);
            semiMonthlyRateField.setFocusable(false);
        }

        if (hourlyRateField != null) {
            hourlyRateField.setEditable(false);
            hourlyRateField.setFocusable(false);
        }
    }

    private void refreshFormLayout() {
        // Visibility of the status combo/field, date-picker button and submit
        // button changes between modes; a revalidate re-flows the vertical stack.
        revalidate();
        repaint();
    }

    private void setFieldsEditable(boolean editable) {
        for (Component c : getAllComponents(this)) {
            if (c instanceof JTextField field) {
                field.setEditable(editable);
                field.setFocusable(editable);
            }

            if (c instanceof JTextArea area) {
                area.setEditable(editable);
                area.setFocusable(editable);
            }

            if (c instanceof JComboBox<?> comboBox) {
                comboBox.setEnabled(editable);
            }

            if (c instanceof JButton button && button != submitButton) {
                button.setEnabled(editable);
            }
        }

        if (statusField != null) {
            statusField.setEditable(false);
            statusField.setFocusable(false);
        }
    }

    private void populateFields(Employee emp) {
        employeeIdField.setText(safe(emp.getEmployeeId()));
        firstNameField.setText(safe(emp.getFirstName()));
        lastNameField.setText(safe(emp.getLastName()));

        // Department drives the Position + Supervisor lists, so set it first,
        // then repopulate and select the employee's actual position/supervisor.
        selectDepartment(safe(emp.getDepartment()));
        departmentField.setText(safe(emp.getDepartment()));
        reloadPositionsForSelectedDepartment(safe(emp.getPosition()));
        positionField.setText(safe(emp.getPosition()));
        loadSupervisorOptions();
        selectSupervisor(emp.getImmediateSupervisor());

        String status = safe(emp.getStatus());

        if ("Probationary".equalsIgnoreCase(status)) {
            statusComboBox.setSelectedItem("Probationary");
            statusField.setText("Probationary");
        } else {
            statusComboBox.setSelectedItem("Regular");
            statusField.setText("Regular");
        }

        birthdateField.setText(emp.getBirthday() == null
                ? ""
                : emp.getBirthday().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));

        cellphoneField.setText(cleanDigits(safe(emp.getPhoneNumber())));
        emailField.setText(safe(emp.getEmail()));
        addressField.setText(safe(emp.getAddress()));

        sssField.setText(safe(emp.getSSSNumber()));
        philhealthField.setText(safe(emp.getPhilhealthNumber()));
        pagibigField.setText(safe(emp.getPagibigNumber()));
        tinField.setText(safe(emp.getTIN()));

        basicSalaryField.setText(String.valueOf(emp.getBasicSalary()));

        double semiMonthly = emp.getBasicSalary() / 2.0;
        double hourly = semiMonthly / 2.0 / 5.0 / 8.0;

        semiMonthlyRateField.setText(String.format("%.2f", semiMonthly));
        hourlyRateField.setText(String.format("%.2f", hourly));

        riceSubsidyField.setText(String.valueOf(emp.getRiceSubsidy()));
        phoneAllowanceField.setText(String.valueOf(emp.getPhoneAllowance()));
        clothingAllowanceField.setText(String.valueOf(emp.getClothingAllowance()));

        setAllFieldsBlack();
    }

    private String getSelectedStatus() {
        Object selected = statusComboBox.getSelectedItem();
        return selected == null ? "Regular" : selected.toString();
    }

    private void loadSupervisorOptions() {
        if (supervisorComboBox == null) {
            return;
        }

        String currentSelection = getSelectedSupervisorName();
        supervisorIdsByName.clear();
        supervisorComboBox.removeAllItems();
        supervisorComboBox.addItem(NO_SUPERVISOR);

        // Only offer supervisors from the currently selected department, so the
        // available supervisors track the chosen department.
        String selectedDepartment = getSelectedDepartmentName();

        for (Employee employee : employeeService.getAllEmployees()) {
            if (!selectedDepartment.isBlank()
                    && !selectedDepartment.equalsIgnoreCase(safe(employee.getDepartment()))) {
                continue;
            }

            String name = employee.getFullName().trim();
            String employeeId = employee.getEmployeeId();

            if (!name.isBlank() && employeeId != null && !employeeId.isBlank()) {
                try {
                    supervisorIdsByName.putIfAbsent(name, Integer.valueOf(employeeId));
                } catch (NumberFormatException ignored) {
                    // The database supervisor foreign key is numeric, so invalid IDs
                    // cannot safely be offered as writable supervisor choices.
                }
            }
        }

        for (String name : supervisorIdsByName.keySet()) {
            supervisorComboBox.addItem(name);
        }

        selectSupervisor(currentSelection);
    }

    private String getSelectedSupervisorName() {
        if (supervisorComboBox == null) {
            return NO_SUPERVISOR;
        }

        Object selected = supervisorComboBox.getSelectedItem();
        String name = selected == null ? "" : selected.toString().trim();
        return name.isBlank() || NO_SUPERVISOR.equalsIgnoreCase(name)
                ? NO_SUPERVISOR
                : name;
    }

    private Integer getSelectedSupervisorId() {
        return supervisorIdsByName.get(getSelectedSupervisorName());
    }

    private void selectSupervisor(String supervisorName) {
        String normalized = supervisorName == null ? "" : supervisorName.trim();

        if (normalized.isBlank() || NO_SUPERVISOR.equalsIgnoreCase(normalized)) {
            supervisorComboBox.setSelectedItem(NO_SUPERVISOR);
            return;
        }

        if (supervisorIdsByName.containsKey(normalized)) {
            supervisorComboBox.setSelectedItem(normalized);
        } else {
            supervisorComboBox.setSelectedItem(NO_SUPERVISOR);
        }
    }

    private String getValue(JTextField field) {
        String text = field.getText().trim();
        String placeholder = (String) field.getClientProperty("placeholder");

        if (placeholder != null && text.equals(placeholder)) {
            return "";
        }

        return text;
    }

    private String getValue(JTextArea area) {
        String text = area.getText().trim();
        String placeholder = (String) area.getClientProperty("placeholder");

        if (placeholder != null && text.equals(placeholder)) {
            return "";
        }

        return text;
    }

    private String cleanDigits(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.trim().replaceAll("\\D", "");
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value, DATE_FORMATTER);
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

            if (c instanceof JTextArea area) {
                area.setText("");
                area.setForeground(Color.BLACK);
            }
        }
    }

    private void restorePlaceholders() {
        for (Component c : getAllComponents(this)) {
            if (c instanceof JTextField field) {
                String placeholder = (String) field.getClientProperty("placeholder");

                if (placeholder != null && field != statusField) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_GRAY);
                }
            }

            if (c instanceof JTextArea area) {
                String placeholder = (String) area.getClientProperty("placeholder");

                if (placeholder != null) {
                    area.setText(placeholder);
                    area.setForeground(PLACEHOLDER_GRAY);
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

    private void setPlaceholder(JTextArea area, String placeholder) {
        area.putClientProperty("placeholder", placeholder);
        area.setText(placeholder);
        area.setForeground(PLACEHOLDER_GRAY);

        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (area.getText().isBlank()) {
                    area.setText(placeholder);
                    area.setForeground(PLACEHOLDER_GRAY);
                }
            }
        });
    }

    private void setAllFieldsBlack() {
        for (Component c : getAllComponents(this)) {
            if (c instanceof JTextField field) {
                field.setForeground(Color.BLACK);
            }

            if (c instanceof JTextArea area) {
                area.setForeground(Color.BLACK);
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

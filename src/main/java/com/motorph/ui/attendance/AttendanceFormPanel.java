package com.motorph.ui.attendance;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.motorph.model.Attendance;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.service.AttendanceService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

public class AttendanceFormPanel extends JPanel {

    public interface SubmitHandler {
        void onSubmit(Object[] rowData);
    }

    private static final Color NAVY = new Color(5, 24, 108);
    private static final Color BG = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(150, 150, 150);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final String FONT = "Segoe UI";

    private final Runnable onBack;
    private final SubmitHandler onSubmit;
    private final Object[] existingData;
    private final Integer editingAttendanceId; // id of the record being edited (null when creating)

    private final boolean restrictedRole;
    private final boolean adminOrHrRole;

    private final AttendanceService attendanceService = AppContext.getAttendanceService();

    private JTextField employeeIdField;
    private JComboBox<String> typeCombo;
    private JSpinner dateSpinner;
    private JButton timeInButton;
    private JButton timeOutButton;
    private JTextField totalHoursField;
    private JComboBox<String> validityCombo;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    public AttendanceFormPanel(Runnable onBack) {
        this(onBack, null, null);
    }

    public AttendanceFormPanel(Runnable onBack, SubmitHandler onSubmit) {
        this(onBack, null, onSubmit);
    }

    public AttendanceFormPanel(Runnable onBack, Object[] existingData, SubmitHandler onSubmit) {
        this(onBack, existingData, null, onSubmit);
    }

    // Existing 4-arg constructor kept for backward compatibility — it infers the
    // mode from the logged-in user's role, same as before.
    public AttendanceFormPanel(Runnable onBack, Object[] existingData, Integer attendanceId, SubmitHandler onSubmit) {
        this(onBack, existingData, attendanceId, onSubmit, null);
    }

    /**
     * @param viewMine explicitly tells the form which screen opened it:
     *                 true  = "View Mine" (self-service; Employee ID is always
     *                         the current user's own ID and is auto-filled/locked)
     *                 false = "View All"   (HR/Finance/Admin logging attendance
     *                         for any employee; Employee ID stays editable)
     *                 null  = not specified, falls back to inferring from role
     *                         (kept only for old call sites that haven't been
     *                         updated to pass the mode explicitly)
     */
    public AttendanceFormPanel(Runnable onBack, Object[] existingData, Integer attendanceId,
                                SubmitHandler onSubmit, Boolean viewMine) {
        this.onBack = onBack;
        this.existingData = existingData;
        this.editingAttendanceId = attendanceId;
        this.onSubmit = onSubmit;

        // Auto-fill/lock behavior is now driven by which screen opened the form
        // (viewMine), not by the user's role. This matters because an Admin/HR
        // user can also open "View Mine" to log their own attendance, and in
        // that case the ID must still auto-fill even though their role isn't
        // one of the traditionally "restricted" roles.
        this.restrictedRole = (viewMine != null) ? viewMine : isRestrictedAttendanceRole();
        this.adminOrHrRole = isAdminOrHrRole();

        setLayout(new BorderLayout());
        setBackground(BG);
        add(createMainPanel(), BorderLayout.CENTER);

        if (existingData != null) {
            populateFields(existingData);
        }

        applyRoleRestrictions();
        calculateTotalHours();
    }

    private boolean isRestrictedAttendanceRole() {
        UserAccount user = Session.getCurrentUser();
        if (user == null || user.getRole() == null) return false;

        Role role = user.getRole();
        return role == Role.EMPLOYEE || role == Role.IT || role == Role.FINANCE;
    }

    private boolean isAdminOrHrRole() {
        UserAccount user = Session.getCurrentUser();
        if (user == null || user.getRole() == null) return false;

        Role role = user.getRole();
        return role == Role.ADMIN || role == Role.HR;
    }

    private void applyRoleRestrictions() {
        Date today = todayOnly();

        if (restrictedRole) {
            // "View Mine": the employee is always logging their own attendance,
            // so the Employee ID is auto-filled from the session and locked.
            dateSpinner.setValue(today);
            dateSpinner.setEnabled(false);
            validityCombo.setEnabled(false);

            UserAccount user = Session.getCurrentUser();
            if (user != null) {
                employeeIdField.setText(String.valueOf(user.getEmployeeId()));
                employeeIdField.setEditable(false);
                employeeIdField.setFocusable(false);
                employeeIdField.setBackground(new Color(245, 245, 245));
            }

            if (existingData == null) {
                validityCombo.setSelectedItem("Valid");
            }
        }
        // "View All" (adminOrHrRole / others): Employee ID stays editable so
        // HR/Finance can log attendance on behalf of another employee.

        totalHoursField.setEditable(false);
        totalHoursField.setFocusable(false);
        totalHoursField.setBackground(new Color(245, 245, 245));
    }

    private JPanel createMainPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(52, 64, 40, 78));

        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(new Font(FONT, Font.PLAIN, 16));
        back.setForeground(new Color(80, 80, 80));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onBack != null) onBack.run();
            }
        });

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setOpaque(false);
        backRow.add(back);

        outer.add(backRow, BorderLayout.NORTH);
        outer.add(createFormWrapper(), BorderLayout.CENTER);

        return outer;
    }

    private JPanel createFormWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(36, 0, 0, 0));

        JPanel form = new JPanel(new GridLayout(1, 2, 40, 0));
        form.setOpaque(false);

        form.add(createLeftColumn());
        form.add(createRightColumn());

        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(createButtonRow(), BorderLayout.SOUTH);

        return wrapper;
    }

    // Left column rows: Employee ID -> Type -> Date
    private JPanel createLeftColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        employeeIdField = createTextField();
        typeCombo = createComboBox(new String[]{"Regular", "Overtime", "Holiday"});
        dateSpinner = createDatePicker();

        int row = 0;
        addStackedField(col, row++, "Employee ID", employeeIdField);
        addStackedField(col, row++, "Type", typeCombo);
        addStackedField(col, row++, "Date", dateSpinner);

        addVerticalGlue(col, row);
        return col;
    }

    // Right column rows: (Time In / Time Out) -> Total Hours Worked -> Validity
    // so each row lines up with the corresponding left-column row.
    private JPanel createRightColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        timeInButton = createCurrentTimeButton();
        timeOutButton = createCurrentTimeButton();
        totalHoursField = createTextField();
        validityCombo = createComboBox(new String[]{"Valid", "Invalid"});

        int row = 0;
        addTwoTimeFields(col, row++, "Time In", timeInButton, "Time Out", timeOutButton);
        addStackedField(col, row++, "Total Hours Worked", totalHoursField);
        addStackedField(col, row++, "Validity", validityCombo);

        addVerticalGlue(col, row);
        return col;
    }

    private JSpinner createDatePicker() {
        Date today = todayOnly();

        SpinnerDateModel model = restrictedRole
                ? new SpinnerDateModel(today, today, today, Calendar.DAY_OF_MONTH)
                : new SpinnerDateModel(today, null, today, Calendar.DAY_OF_MONTH);

        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MM/dd/yyyy"));
        spinner.setFont(new Font(FONT, Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(0, 44));
        spinner.setMinimumSize(new Dimension(0, 44));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        spinner.setBorder(new CompoundBorder(
                new RoundedBorder(8, FIELD_BORDER),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            JFormattedTextField textField = defaultEditor.getTextField();
            textField.setFont(new Font(FONT, Font.PLAIN, 13));
            textField.setBorder(BorderFactory.createEmptyBorder());
            textField.setBackground(Color.WHITE);
            textField.setForeground(TEXT_DARK);
            textField.setCaretColor(NAVY);
        }

        return spinner;
    }

    private JButton createCurrentTimeButton() {
        JButton button = new JButton("Set Time");
        button.setFont(new Font(FONT, Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(0, 44));
        button.setMinimumSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_DARK);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(8, FIELD_BORDER));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            button.setText(timeFormat.format(new Date()));
            button.setEnabled(false);
            button.setCursor(Cursor.getDefaultCursor());
            calculateTotalHours();
        });

        return button;
    }

    private void calculateTotalHours() {
        LocalTime timeIn = parseButtonTime(timeInButton);
        LocalTime timeOut = parseButtonTime(timeOutButton);

        double hours = attendanceService.computeDailyHoursWithBreaks(
                timeIn,
                null,
                null,
                timeOut
        );

        totalHoursField.setText(hours <= 0 ? "" : String.format("%.2f", hours));
    }

    private LocalTime parseButtonTime(JButton button) {
        String value = getTimeValue(button);
        if (value.isBlank()) return null;

        try {
            Date parsed = timeFormat.parse(value);
            return parsed.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    .withSecond(0)
                    .withNano(0);
        } catch (ParseException e) {
            return null;
        }
    }

    private String getTimeValue(JButton button) {
        return button.getText().equals("Set Time") ? "" : button.getText();
    }

    private JPanel createButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton submit = navyButton(existingData == null ? "Submit" : "Update");
        submit.addActionListener(e -> submitAttendance());

        row.add(submit);
        return row;
    }

    private JButton navyButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT, Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(128, 44));
        button.setBackground(NAVY);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void submitAttendance() {
        try {
            String employeeId = employeeIdField.getText().trim();
            Date selectedDate = (Date) dateSpinner.getValue();
            LocalDate attendanceDate = selectedDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (employeeId.isBlank()) {
                throw new IllegalArgumentException("Please enter the Employee ID.");
            }

            LocalTime timeIn = parseButtonTime(timeInButton);
            LocalTime timeOut = parseButtonTime(timeOutButton);

            Attendance attendance = new Attendance(
                    editingAttendanceId == null ? 0 : editingAttendanceId,
                    employeeId,
                    "",
                    "",
                    attendanceDate,
                    timeIn,
                    timeOut,
                    1
            );

            // persist to the database (both calls validate internally)
            if (editingAttendanceId == null) {
                attendanceService.submitAttendance(attendance);
            } else {
                attendanceService.updateAttendance(attendance);
            }

            calculateTotalHours();

            Object[] rowData = {
                    employeeId,
                    typeCombo.getSelectedItem().toString(),
                    dateFormat.format(selectedDate),
                    getTimeValue(timeInButton),
                    getTimeValue(timeOutButton),
                    totalHoursField.getText().trim(),
                    validityCombo.getSelectedItem().toString()
            };

            if (onSubmit != null) {
                onSubmit.onSubmit(rowData);
            }

            JOptionPane.showMessageDialog(
                    this,
                    existingData == null
                            ? "Attendance entry added successfully."
                            : "Attendance entry updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (onBack != null) {
                onBack.run();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Attendance Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private Date todayOnly() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private boolean isFutureDate(Date date) {
        return date.after(todayOnly());
    }

    // NOTE: existingData / rowData layout (no more Break Out / Break In):
    // 0 EmployeeId, 1 Type, 2 Date, 3 TimeIn, 4 TimeOut, 5 TotalHours, 6 Validity
    private void populateFields(Object[] data) {
        employeeIdField.setText(value(data, 0));
        typeCombo.setSelectedItem(value(data, 1));

        if (restrictedRole) {
            dateSpinner.setValue(todayOnly());
        } else {
            try {
                Date parsedDate = dateFormat.parse(value(data, 2));
                dateSpinner.setValue(isFutureDate(parsedDate) ? todayOnly() : parsedDate);
            } catch (ParseException ignored) {
                dateSpinner.setValue(todayOnly());
            }
        }

        setButtonValue(timeInButton, value(data, 3));
        setButtonValue(timeOutButton, value(data, 4));

        validityCombo.setSelectedItem(value(data, 6));
        calculateTotalHours();
    }

    private void addStackedField(JPanel parent, int row, String labelText, JComponent field) {
        GridBagConstraints gbc = baseGbc(row);
        gbc.insets = new Insets(0, 0, 22, 0);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(createLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        parent.add(panel, gbc);
    }

    private void addTwoTimeFields(JPanel parent, int row,
                                  String firstLabel, JComponent firstField,
                                  String secondLabel, JComponent secondField) {

        GridBagConstraints gbc = baseGbc(row);
        gbc.insets = new Insets(0, 0, 22, 0);

        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setOpaque(false);
        panel.add(timeFieldPanel(firstLabel, firstField));
        panel.add(timeFieldPanel(secondLabel, secondField));

        parent.add(panel, gbc);
    }

    private JPanel timeFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(createLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createLabel(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(FONT, Font.PLAIN, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private GridBagConstraints baseGbc(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    private void addVerticalGlue(JPanel parent, int row) {
        GridBagConstraints gbc = baseGbc(row);
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        parent.add(Box.createVerticalGlue(), gbc);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 44));
        field.setMinimumSize(new Dimension(0, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(NAVY);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(8, FIELD_BORDER),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font(FONT, Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(0, 44));
        combo.setMinimumSize(new Dimension(0, 44));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
        combo.setBorder(new RoundedBorder(8, FIELD_BORDER));
        return combo;
    }

    private String value(Object[] data, int index) {
        if (data == null || index >= data.length || data[index] == null) {
            return "";
        }
        return data[index].toString();
    }

    private void setButtonValue(JButton button, String value) {
        if (value != null && !value.isBlank()) {
            button.setText(value);
            button.setEnabled(false);
            button.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static class RoundedBorder extends AbstractBorder {

        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }
}

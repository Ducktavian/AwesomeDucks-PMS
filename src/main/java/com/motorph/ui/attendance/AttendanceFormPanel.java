package com.motorph.ui.attendance;

import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.util.Session;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;

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

    private final boolean restrictedRole;
    private final boolean adminOrHrRole;

    private JTextField employeeIdField;
    private JComboBox<String> typeCombo;
    private JSpinner dateSpinner;
    private JButton timeInButton;
    private JButton breakOutButton;
    private JButton breakInButton;
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
        this.onBack = onBack;
        this.existingData = existingData;
        this.onSubmit = onSubmit;

        this.restrictedRole = isRestrictedAttendanceRole();
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

        if (user == null || user.getRole() == null) {
            return false;
        }

        Role role = user.getRole();

        return role == Role.EMPLOYEE
                || role == Role.IT
                || role == Role.FINANCE;
    }

    private boolean isAdminOrHrRole() {
        UserAccount user = Session.getCurrentUser();

        if (user == null || user.getRole() == null) {
            return false;
        }

        Role role = user.getRole();

        return role == Role.ADMIN || role == Role.HR;
    }

    private void applyRoleRestrictions() {
        Date today = todayOnly();

        if (restrictedRole) {
            dateSpinner.setValue(today);
            dateSpinner.setEnabled(false);
            validityCombo.setEnabled(false);

            if (existingData == null) {
                validityCombo.setSelectedItem("Valid");
            }
        }

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
                if (onBack != null) {
                    onBack.run();
                }
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
        wrapper.setBorder(new EmptyBorder(62, 0, 0, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.anchor = GridBagConstraints.NORTHWEST;
        leftGbc.insets = new Insets(0, 0, 0, 68);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.anchor = GridBagConstraints.NORTHWEST;

        form.add(createLeftColumn(), leftGbc);
        form.add(createRightColumn(), rightGbc);

        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(createButtonRow(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createLeftColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        employeeIdField = createTextField();
        typeCombo = createComboBox(new String[]{"Regular", "Overtime", "Holiday"});
        dateSpinner = createDatePicker();
        validityCombo = createComboBox(new String[]{"Valid", "Invalid"});

        int row = 0;
        addStackedField(col, row++, "Employee ID", employeeIdField);
        addStackedField(col, row++, "Type", typeCombo);
        addStackedField(col, row++, "Date", dateSpinner);
        addStackedField(col, row++, "Validity", validityCombo);

        addVerticalGlue(col, row);
        return col;
    }

    private JPanel createRightColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        timeInButton = createCurrentTimeButton();
        breakOutButton = createCurrentTimeButton();
        breakInButton = createCurrentTimeButton();
        timeOutButton = createCurrentTimeButton();
        totalHoursField = createTextField();

        int row = 0;
        addTwoTimeFields(col, row++, "Time In", timeInButton, "Break Out", breakOutButton);
        addTwoTimeFields(col, row++, "Break In", breakInButton, "Time Out", timeOutButton);
        addStackedField(col, row++, "Total Hours Worked", totalHoursField);

        addVerticalGlue(col, row);
        return col;
    }

    private JSpinner createDatePicker() {
        Date today = todayOnly();

        SpinnerDateModel model;

        if (restrictedRole) {
            model = new SpinnerDateModel(today, today, today, Calendar.DAY_OF_MONTH);
        } else {
            model = new SpinnerDateModel(today, null, today, Calendar.DAY_OF_MONTH);
        }

        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MM/dd/yyyy"));
        spinner.setFont(new Font(FONT, Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(320, 44));
        spinner.setMinimumSize(new Dimension(250, 44));
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
        button.setPreferredSize(new Dimension(152, 44));
        button.setMinimumSize(new Dimension(120, 44));
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
        String timeIn = getTimeValue(timeInButton);
        String breakOut = getTimeValue(breakOutButton);
        String breakIn = getTimeValue(breakInButton);
        String timeOut = getTimeValue(timeOutButton);

        if (timeIn.isBlank() || breakOut.isBlank() || breakIn.isBlank() || timeOut.isBlank()) {
            totalHoursField.setText("");
            return;
        }

        try {
            Date in = timeFormat.parse(timeIn);
            Date bOut = timeFormat.parse(breakOut);
            Date bIn = timeFormat.parse(breakIn);
            Date out = timeFormat.parse(timeOut);

            long workBeforeBreak = bOut.getTime() - in.getTime();
            long workAfterBreak = out.getTime() - bIn.getTime();
            long totalMillis = workBeforeBreak + workAfterBreak;

            if (totalMillis < 0) {
                totalHoursField.setText("");
                return;
            }

            double hours = totalMillis / (1000.0 * 60.0 * 60.0);
            totalHoursField.setText(String.format("%.2f", hours));

        } catch (ParseException e) {
            totalHoursField.setText("");
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
        String employeeId = employeeIdField.getText().trim();
        Date selectedDate = (Date) dateSpinner.getValue();

        if (employeeId.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Employee ID.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (restrictedRole && !isToday(selectedDate)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Employee, IT, and Finance users can only submit attendance for today's date.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
            );
            dateSpinner.setValue(todayOnly());
            return;
        }

        if (adminOrHrRole && isFutureDate(selectedDate)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Admin and HR users can only use past dates or today's date. Future dates are not allowed.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
            );
            dateSpinner.setValue(todayOnly());
            return;
        }

        calculateTotalHours();

        Object[] rowData = {
            employeeId,
            typeCombo.getSelectedItem().toString(),
            dateFormat.format(selectedDate),
            getTimeValue(timeInButton),
            getTimeValue(breakOutButton),
            getTimeValue(breakInButton),
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
    }

    private Date todayOnly() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private boolean isToday(Date date) {
        return dateFormat.format(date).equals(dateFormat.format(new Date()));
    }

    private boolean isFutureDate(Date date) {
        return date.after(todayOnly());
    }

    private void populateFields(Object[] data) {
        employeeIdField.setText(value(data, 0));
        typeCombo.setSelectedItem(value(data, 1));

        if (restrictedRole) {
            dateSpinner.setValue(todayOnly());
        } else {
            try {
                Date parsedDate = dateFormat.parse(value(data, 2));

                if (isFutureDate(parsedDate)) {
                    dateSpinner.setValue(todayOnly());
                } else {
                    dateSpinner.setValue(parsedDate);
                }

            } catch (ParseException ignored) {
                dateSpinner.setValue(todayOnly());
            }
        }

        setButtonValue(timeInButton, value(data, 3));
        setButtonValue(breakOutButton, value(data, 4));
        setButtonValue(breakInButton, value(data, 5));
        setButtonValue(timeOutButton, value(data, 6));

        validityCombo.setSelectedItem(value(data, 8));
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

    private void addTwoTimeFields(
            JPanel parent,
            int row,
            String firstLabel,
            JComponent firstField,
            String secondLabel,
            JComponent secondField) {

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
        field.setPreferredSize(new Dimension(320, 44));
        field.setMinimumSize(new Dimension(250, 44));
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
        combo.setPreferredSize(new Dimension(320, 44));
        combo.setMinimumSize(new Dimension(250, 44));
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
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
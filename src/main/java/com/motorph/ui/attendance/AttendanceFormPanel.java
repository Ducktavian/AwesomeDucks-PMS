package com.motorph.ui.attendance;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.motorph.model.Attendance;
import com.motorph.model.Employee;
import com.motorph.model.UserAccount;
import com.motorph.reporting.TimeCardReportGenerator;
import com.motorph.service.AttendanceService;
import com.motorph.service.EmployeeService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;


public class AttendanceFormPanel extends JPanel {

    public enum Mode { ADD_SELF, ADD_OTHER, EDIT, VIEW }

    private static final Color NAVY = new Color(5, 24, 108);
    private static final Color BG = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(150, 150, 150);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final Color READONLY_BG = new Color(245, 245, 245);
    private static final String FONT = "Segoe UI";

    private final Runnable onClose;
    private final Mode mode;
    private final Object[] existingData;
    private Integer editingAttendanceId;

    // Self-service state (ADD_SELF): set when the employee already has an open
    // record today, so this form completes it with a time-out instead of
    // inserting a duplicate. alreadyComplete blocks a third punch for the day.
    private boolean selfTimeOutMode = false;
    private boolean alreadyComplete = false;

    private final AttendanceService attendanceService = AppContext.getAttendanceService();
    private final EmployeeService employeeService = AppContext.getEmployeeService();

    private JComboBox<String> employeeCombo;   
    private JTextField nameField;              
    private JTextField positionField;          
    private JSpinner dateSpinner;
    private JButton timeInButton;
    private JButton timeOutButton;
    private JTextField totalHoursField;        
    private JTextField validityField;         
    private JButton savePdfButton;

    private final Map<String, Employee> employeesByLabel = new LinkedHashMap<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    private static final java.time.format.DateTimeFormatter TIME_FMT =
            java.time.format.DateTimeFormatter.ofPattern("hh:mm a");

    public AttendanceFormPanel(Runnable onClose, Mode mode) {
        this(onClose, mode, null, null);
    }

    public AttendanceFormPanel(Runnable onClose, Mode mode, Object[] existingData, Integer attendanceId) {
        this.onClose = onClose;
        this.mode = mode;
        this.existingData = existingData;
        this.editingAttendanceId = attendanceId;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildScroll(buildContent()), BorderLayout.CENTER);
        add(buildButtonRow(), BorderLayout.SOUTH);

        if (existingData != null) {
            populateFields(existingData);
        }
        recompute();
    }

    // ── scroll wrapper ───────────────────────────────────────────────────────
    private JScrollPane buildScroll(JComponent content) {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(BG);
        holder.add(content, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ── form content ─────────────────────────────────────────────────────────
    private JComponent buildContent() {
        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(28, 40, 20, 40));

        buildFields();

        if (mode == Mode.ADD_OTHER) {
            addRow(content, stacked("Employee", employeeCombo));
        } else {
            addRow(content, stacked("Employee", nameField));
        }
        addRow(content, stacked("Position", positionField));
        addRow(content, stacked("Date", dateSpinner));
        addRow(content, twoLine("Time In", timeInButton, "Time Out", timeOutButton));
        addRow(content, twoLine("Total Hours Worked", totalHoursField, "Validity", validityField));

        return content;
    }

    private void buildFields() {
        nameField = readOnlyField();
        positionField = readOnlyField();
        totalHoursField = readOnlyField();
        validityField = readOnlyField();

        dateSpinner = createDatePicker();
        timeInButton = createTimeButton();
        timeOutButton = createTimeButton();

        boolean viewOnly = (mode == Mode.VIEW);

        if (mode == Mode.ADD_OTHER) {
            employeeCombo = comboBox(new String[0]);
            loadEmployeeChoices();
            employeeCombo.addActionListener(e -> fillFromSelectedEmployee());
            fillFromSelectedEmployee();
        } else if (mode == Mode.ADD_SELF) {
            prefillSelf();
            // ADD_SELF always logs today's attendance for the current user.
            dateSpinner.setValue(todayOnly());
            dateSpinner.setEnabled(false);
            configureSelfPunch();
        }

        if (viewOnly) {
            dateSpinner.setEnabled(false);
            timeInButton.setEnabled(false);
            timeOutButton.setEnabled(false);
        }
    }

    // ── identity helpers ─────────────────────────────────────────────────────
    private void loadEmployeeChoices() {
        employeesByLabel.clear();
        employeeCombo.removeAllItems();
        for (Employee emp : employeeService.getAllEmployees()) {
            String label = emp.getEmployeeId() + " - " + emp.getFullName();
            employeesByLabel.put(label, emp);
            employeeCombo.addItem(label);
        }
    }

    private Employee selectedEmployee() {
        if (employeeCombo == null) {
            return null;
        }
        Object sel = employeeCombo.getSelectedItem();
        return sel == null ? null : employeesByLabel.get(sel.toString());
    }

    private void fillFromSelectedEmployee() {
        Employee emp = selectedEmployee();
        positionField.setText(emp == null ? "" : emp.getPosition());
    }

    private void prefillSelf() {
        UserAccount user = Session.getCurrentUser();
        if (user == null) {
            return;
        }
        Employee me = employeeService.findEmployee(String.valueOf(user.getEmployeeId()));
        if (me != null) {
            nameField.setText(me.getEmployeeId() + " - " + me.getFullName());
            positionField.setText(me.getPosition());
        } else {
            nameField.setText(String.valueOf(user.getEmployeeId()));
        }
    }

    /**
     * Inspects today's record for the logged-in user and puts the self-service
     * form into the right mode:
     *   - no record yet   → time-in (default; both times editable)
     *   - open record     → time-out completion (time-in locked, updates the row)
     *   - complete record → blocked (already timed in and out today)
     */
    private void configureSelfPunch() {
        UserAccount user = Session.getCurrentUser();
        if (user == null) {
            return;
        }

        Attendance today = attendanceService.getTodaysAttendance(String.valueOf(user.getEmployeeId()));
        if (today == null) {
            return; // fresh time-in
        }

        if (today.getLogOut() == null) {
            // Timed in already, still open → this form completes the time-out.
            selfTimeOutMode = true;
            editingAttendanceId = today.getAttendanceId();
            if (today.getLogIn() != null) {
                setButtonValue(timeInButton, today.getLogIn().format(TIME_FMT));
            }
            timeInButton.setEnabled(false); // time-in is already recorded
        } else {
            // Both punches exist for today → nothing more to log.
            alreadyComplete = true;
            if (today.getLogIn() != null) {
                setButtonValue(timeInButton, today.getLogIn().format(TIME_FMT));
            }
            setButtonValue(timeOutButton, today.getLogOut().format(TIME_FMT));
        }
    }

    private void fillIdentityFromId(String employeeId) {
        Employee emp = employeeId.isBlank() ? null : employeeService.findEmployee(employeeId);
        if (emp != null) {
            nameField.setText(emp.getEmployeeId() + " - " + emp.getFullName());
            positionField.setText(emp.getPosition());
        } else {
            nameField.setText(employeeId);
        }
    }

    /** The employee this record belongs to, resolved from the active mode. */
    private String targetEmployeeId() {
        if (mode == Mode.ADD_OTHER) {
            Employee emp = selectedEmployee();
            return emp == null ? "" : emp.getEmployeeId();
        }
        if (mode == Mode.ADD_SELF) {
            UserAccount user = Session.getCurrentUser();
            return user == null ? "" : String.valueOf(user.getEmployeeId());
        }
        // EDIT / VIEW: identity is fixed to the record being opened.
        return value(existingData, 0);
    }

    // ── time / totals ────────────────────────────────────────────────────────
    private JButton createTimeButton() {
        JButton button = new JButton("Set Time");
        button.setFont(new Font(FONT, Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(0, 44));
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_DARK);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(8, FIELD_BORDER));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            button.setText(timeFormat.format(new Date()));
            button.setEnabled(false);
            button.setCursor(Cursor.getDefaultCursor());
            recompute();
        });
        return button;
    }

    /** Recomputes the derived Total Hours and Validity read-only fields. */
    private void recompute() {
        LocalTime timeIn = parseButtonTime(timeInButton);
        LocalTime timeOut = parseButtonTime(timeOutButton);

        double hours = attendanceService.computeDailyHours(timeIn, timeOut);
        totalHoursField.setText(hours <= 0 ? "" : String.format("%.2f", hours));

        boolean valid = timeIn != null && timeOut != null && timeOut.isAfter(timeIn);
        validityField.setText(valid ? "Valid" : "Invalid");
        validityField.setForeground(valid ? new Color(21, 128, 61) : new Color(180, 60, 60));
    }

    private LocalTime parseButtonTime(JButton button) {
        String value = getTimeValue(button);
        if (value.isBlank()) {
            return null;
        }
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

    // ── buttons ──────────────────────────────────────────────────────────────
    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        row.setBackground(BG);
        row.setBorder(new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        if (mode == Mode.VIEW) {
            JButton close = navyButton("Close");
            close.addActionListener(e -> close());
            row.add(close);
            return row;
        }

        savePdfButton = createIconButton("Save PDF", "/com/motorph/img/Save-Icon.png");
        savePdfButton.addActionListener(e -> saveAttendanceAsPdf());

        JButton cancel = navyButton("Cancel");
        cancel.addActionListener(e -> close());

        JButton submit = navyButton(submitButtonLabel());
        submit.addActionListener(e -> submitAttendance());
        if (alreadyComplete) {
            // Nothing left to log today; keep the form open for viewing only.
            submit.setEnabled(false);
        }

        row.add(savePdfButton);
        row.add(cancel);
        row.add(submit);
        return row;
    }

    private String submitButtonLabel() {
        if (mode == Mode.EDIT) {
            return "Update";
        }
        if (selfTimeOutMode) {
            return "Time Out";
        }
        return "Submit";
    }

    private void submitAttendance() {
        try {
            String employeeId = targetEmployeeId();
            if (employeeId.isBlank()) {
                throw new IllegalArgumentException("Please select an employee.");
            }

            LocalDate attendanceDate = spinnerDate(dateSpinner);
            LocalTime timeIn = parseButtonTime(timeInButton);
            LocalTime timeOut = parseButtonTime(timeOutButton);

            if (selfTimeOutMode && timeOut == null) {
                throw new IllegalArgumentException("Please set your Time Out.");
            }

            Attendance attendance = new Attendance(
                    editingAttendanceId == null ? 0 : editingAttendanceId,
                    employeeId,
                    "",
                    "",
                    attendanceDate,
                    timeIn,
                    timeOut,
                    1 // Present; the service/derived validity govern payroll counting
            );

            // EDIT and the self-service time-out both update an existing row;
            // a fresh punch inserts a new one.
            boolean isUpdate = mode == Mode.EDIT || selfTimeOutMode;
            if (isUpdate) {
                attendanceService.updateAttendance(attendance);
            } else {
                attendanceService.submitAttendance(attendance);
            }

            JOptionPane.showMessageDialog(
                    this,
                    successMessage(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Attendance Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String successMessage() {
        if (mode == Mode.EDIT) {
            return "Attendance entry updated successfully.";
        }
        if (selfTimeOutMode) {
            return "Timed out successfully.";
        }
        return "Attendance entry added successfully.";
    }

    private void close() {
        if (onClose != null) {
            onClose.run();
        }
    }

    // Prompts for a month/year, then opens that period's timecard in JasperViewer,
    // where it can be saved as PDF.
    private void saveAttendanceAsPdf() {
        String employeeId = targetEmployeeId();
        if (employeeId.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.",
                    "Save PDF", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TimeCardReportGenerator.promptAndView(this, Integer.parseInt(employeeId));
    }

    // ── populate (EDIT / VIEW) ───────────────────────────────────────────────
    // Row layout from AttendancePanel.toTableRow:
    // 0 EmployeeId, 1 Type, 2 Date, 3 TimeIn, 4 TimeOut, 5 TotalHours, 6 Validity
    private void populateFields(Object[] data) {
        if (mode != Mode.ADD_OTHER) {
            fillIdentityFromId(value(data, 0));
        }
        setSpinnerDate(dateSpinner, value(data, 2));
        setButtonValue(timeInButton, value(data, 3));
        setButtonValue(timeOutButton, value(data, 4));
    }

    // ── layout helpers ───────────────────────────────────────────────────────
    private void addRow(JPanel content, JComponent row) {
        row.setAlignmentX(LEFT_ALIGNMENT);
        content.add(row);
        content.add(Box.createVerticalStrut(18));
    }

    private JPanel stacked(String label, JComponent field) {
        JPanel p = stackedInner(label, field);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }

    private JPanel stackedInner(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(makeLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel twoLine(String l1, JComponent f1, String l2, JComponent f2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
        row.setOpaque(false);
        row.add(stackedInner(l1, f1));
        row.add(stackedInner(l2, f2));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT, Font.PLAIN, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }

    // ── field factories ──────────────────────────────────────────────────────
    private JTextField baseField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 44));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(NAVY);
        field.setBorder(new CompoundBorder(
                new EmptyBorder(1, 2, 1, 2),
                new CompoundBorder(
                        new RoundedBorder(8, FIELD_BORDER),
                        new EmptyBorder(4, 10, 4, 10))));
        return field;
    }

    private JTextField readOnlyField() {
        JTextField field = baseField();
        field.setEditable(false);
        field.setFocusable(false);
        field.setBackground(READONLY_BG);
        return field;
    }

    private JComboBox<String> comboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font(FONT, Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(0, 44));
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
        combo.setBorder(new CompoundBorder(
                new EmptyBorder(1, 2, 1, 2),
                new RoundedBorder(8, FIELD_BORDER)));
        return combo;
    }

    private JSpinner createDatePicker() {
        Date today = todayOnly();
        // Admin/HR (ADD_OTHER, EDIT) may pick past dates up to today; never future.
        SpinnerDateModel model = new SpinnerDateModel(today, null, today, Calendar.DAY_OF_MONTH);

        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MM/dd/yyyy"));
        spinner.setFont(new Font(FONT, Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(0, 44));
        spinner.setBorder(new CompoundBorder(
                new EmptyBorder(1, 2, 1, 2),
                new CompoundBorder(
                        new RoundedBorder(8, FIELD_BORDER),
                        new EmptyBorder(4, 8, 4, 8))));

        if (spinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            JFormattedTextField tf = editor.getTextField();
            tf.setFont(new Font(FONT, Font.PLAIN, 13));
            tf.setBorder(BorderFactory.createEmptyBorder());
            tf.setBackground(Color.WHITE);
            tf.setForeground(TEXT_DARK);
            tf.setCaretColor(NAVY);
        }
        return spinner;
    }

    private JButton createIconButton(String text, String iconPath) {
        JButton button = navyButton(text);
        button.setPreferredSize(new Dimension(140, 40));
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(8);

        URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            Image img = new ImageIcon(iconUrl).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        }
        return button;
    }

    private JButton navyButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(3, 15, 78));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(18, 42, 135));
                } else {
                    g2.setColor(NAVY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                if (getIcon() != null) {
                    int iconX = 14;
                    int iconY = (getHeight() - getIcon().getIconHeight()) / 2;
                    getIcon().paintIcon(this, g2, iconX, iconY);
                }

                g2.setColor(Color.WHITE);
                g2.setFont(new Font(FONT, Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int x = getIcon() != null
                        ? 14 + getIcon().getIconWidth() + getIconTextGap()
                        : (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        button.setPreferredSize(new Dimension(128, 40));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // ── small utilities ──────────────────────────────────────────────────────
    private Date todayOnly() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private LocalDate spinnerDate(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void setSpinnerDate(JSpinner spinner, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        try {
            spinner.setValue(dateFormat.parse(text));
        } catch (ParseException ignored) {
            spinner.setValue(todayOnly());
        }
    }

    private void setButtonValue(JButton button, String value) {
        if (value != null && !value.isBlank()) {
            button.setText(value);
            button.setEnabled(false);
            button.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static String value(Object[] data, int index) {
        if (data == null || index >= data.length || data[index] == null) {
            return "";
        }
        return data[index].toString();
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

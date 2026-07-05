package com.motorph.ui.request;

import com.motorph.model.*;
import com.motorph.service.EmployeeService;
import com.motorph.service.RequestService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;


public class RequestFormPanel extends JPanel {

    public enum Mode { ADD_SELF, ADD_OTHER, EDIT, VIEW }

    public interface SubmitHandler {
        void onSubmit(Object[] rowData);
    }

    private static final Color NAVY = new Color(5, 24, 108);
    private static final Color BG = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(150, 150, 150);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final String FONT = "Segoe UI";

    private static final String LEAVE_CATEGORY = "Leave Request";
    private static final String WORK_TIME_CATEGORY = "Work Time Request";
    private static final String OVERTIME = "Overtime";
    private static final String UNDERTIME = "Undertime";

    private static final String[] CATEGORIES = { LEAVE_CATEGORY, WORK_TIME_CATEGORY };
    private static final String[] WORK_TYPES = { OVERTIME, UNDERTIME };

    // Leave types mirror the leave_type seed rows (id order 1..7).
    private static final String[] LEAVE_TYPES = {
        "Vacation Leave", "Sick Leave", "Emergency Leave", "Maternity Leave",
        "Paternity Leave", "Solo Parent Leave", "Unpaid Leave"
    };

    private static final String[] WORK_TIMES = {
        "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
        "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM"
    };

    private final Runnable onClose;
    private final Mode mode;
    private final Object[] existingData;
    private final SubmitHandler onSubmit;

    private final RequestService requestService = AppContext.getRequestService();
    private final EmployeeService employeeService = AppContext.getEmployeeService();

    // Interactive fields (ADD/EDIT).
    private JComboBox<String> employeeCombo;   // ADD_OTHER only
    private JTextField nameField;              // ADD_SELF / EDIT (read-only)
    private JTextField positionField;          // read-only, auto-filled
    private JComboBox<String> categoryCombo;
    private JComboBox<String> leaveTypeCombo;
    private JComboBox<String> workTypeCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JComboBox<String> startTimeCombo;
    private JComboBox<String> endTimeCombo;
    private JTextArea reasonArea;
    private JComboBox<String> statusCombo;

    private JPanel leaveTypeRow;
    private JPanel workTypeRow;
    private JPanel timeRow;

    private final Map<String, Employee> employeesByLabel = new LinkedHashMap<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public RequestFormPanel(Runnable onClose, Mode mode, Object[] existingData, SubmitHandler onSubmit) {
        this.onClose = onClose;
        this.mode = mode;
        this.existingData = existingData;
        this.onSubmit = onSubmit;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildScroll(mode == Mode.VIEW ? buildViewContent() : buildFormContent()), BorderLayout.CENTER);
        add(buildButtonRow(), BorderLayout.SOUTH);
    }

    private JScrollPane buildScroll(JComponent content) {
        // Pin the stacked content to the top so it keeps its natural height and
        // the scroll pane handles any overflow instead of stretching the rows.
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(BG);
        holder.add(content, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ── interactive form (ADD / EDIT) ───────────────────────────────────────
    private JComponent buildFormContent() {
        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(28, 40, 20, 40));

        buildFields();

        // Name / Employee picker
        if (mode == Mode.ADD_OTHER) {
            addRow(content, stacked("Employee", employeeCombo));
        } else {
            addRow(content, stacked("Name", nameField));
        }
        addRow(content, stacked("Position", positionField));
        addRow(content, stacked("Request Type", categoryCombo));

        leaveTypeRow = stacked("Leave Type", leaveTypeCombo);
        addRow(content, leaveTypeRow);

        workTypeRow = stacked("Work Time Type", workTypeCombo);
        addRow(content, workTypeRow);

        addRow(content, twoLine("Start Date", startDateSpinner, "End Date", endDateSpinner));

        timeRow = twoLine("Start Time", startTimeCombo, "End Time", endTimeCombo);
        addRow(content, timeRow);

        addRow(content, stacked("Reason", reasonScroll()));
        addRow(content, stacked("Status", statusCombo));

        if (existingData != null) {
            populateFields(existingData);
        }
        updateFieldVisibility();
        return content;
    }

    private void buildFields() {
        nameField = readOnlyField();
        positionField = readOnlyField();

        categoryCombo = comboBox(CATEGORIES);
        leaveTypeCombo = comboBox(LEAVE_TYPES);
        workTypeCombo = comboBox(WORK_TYPES);

        startDateSpinner = dateSpinner();
        endDateSpinner = dateSpinner();
        startTimeCombo = comboBox(WORK_TIMES);
        endTimeCombo = comboBox(WORK_TIMES);

        reasonArea = new JTextArea();
        reasonArea.setFont(new Font(FONT, Font.PLAIN, 13));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setForeground(TEXT_DARK);
        reasonArea.setCaretColor(NAVY);
        reasonArea.setBorder(new EmptyBorder(6, 10, 6, 10));

        statusCombo = comboBox(new String[]{ "Pending", "Approved", "Rejected" });

        categoryCombo.addActionListener(e -> updateFieldVisibility());

        if (mode == Mode.ADD_OTHER) {
            employeeCombo = comboBox(new String[0]);
            loadEmployeeChoices();
            employeeCombo.addActionListener(e -> fillPositionFromEmployee());
            fillPositionFromEmployee();
        } else if (mode == Mode.ADD_SELF) {
            prefillSelf();
        }

        if (mode == Mode.EDIT) {
            // Identity and request type are fixed once a request exists.
            categoryCombo.setEnabled(false);
            leaveTypeCombo.setEnabled(false);
            workTypeCombo.setEnabled(false);
        } else {
            // New requests always start Pending.
            statusCombo.setSelectedItem("Pending");
            statusCombo.setEnabled(false);
        }
    }

    private JScrollPane reasonScroll() {
        JScrollPane scroll = new JScrollPane(reasonArea);
        scroll.setPreferredSize(new Dimension(0, 130));
        scroll.setBorder(new CompoundBorder(
                new EmptyBorder(1, 2, 1, 2),
                new RoundedBorder(8, FIELD_BORDER)));
        scroll.getViewport().setBackground(BG);
        return scroll;
    }

    private void loadEmployeeChoices() {
        employeesByLabel.clear();
        employeeCombo.removeAllItems();
        for (Employee emp : employeeService.getAllEmployees()) {
            String label = emp.getEmployeeId() + " - " + emp.getFullName();
            employeesByLabel.put(label, emp);
            employeeCombo.addItem(label);
        }
    }

    private void fillPositionFromEmployee() {
        Employee emp = selectedEmployee();
        positionField.setText(emp == null ? "" : emp.getPosition());
    }

    private Employee selectedEmployee() {
        if (employeeCombo == null) {
            return null;
        }
        Object sel = employeeCombo.getSelectedItem();
        return sel == null ? null : employeesByLabel.get(sel.toString());
    }

    private void prefillSelf() {
        UserAccount user = Session.getCurrentUser();
        if (user == null) {
            return;
        }
        Employee me = employeeService.findEmployee(String.valueOf(user.getEmployeeId()));
        if (me != null) {
            nameField.setText(me.getFullName());
            positionField.setText(me.getPosition());
        }
    }

    /** Show only the fields relevant to the selected category. */
    private void updateFieldVisibility() {
        boolean leave = LEAVE_CATEGORY.equals(String.valueOf(categoryCombo.getSelectedItem()));
        if (leaveTypeRow != null) leaveTypeRow.setVisible(leave);
        if (workTypeRow != null) workTypeRow.setVisible(!leave);
        if (timeRow != null) timeRow.setVisible(!leave);
        revalidate();
        repaint();
    }

    // ── view-only content ───────────────────────────────────────────────────
    private JComponent buildViewContent() {
        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(28, 40, 20, 40));

        String type = value(existingData, 2);
        boolean workTime = OVERTIME.equalsIgnoreCase(type) || UNDERTIME.equalsIgnoreCase(type);

        addRow(content, stacked("Name", viewField(value(existingData, 0))));
        addRow(content, stacked("Position", viewField(value(existingData, 1))));
        addRow(content, stacked("Request Type", viewField(type)));
        addRow(content, twoLine("Start Date", viewField(value(existingData, 3)),
                                "End Date", viewField(value(existingData, 4))));
        if (workTime) {
            addRow(content, twoLine("Start Time", viewField(value(existingData, 5)),
                                    "End Time", viewField(value(existingData, 6))));
        }
        addRow(content, stacked("Reason", viewArea(value(existingData, 7))));
        addRow(content, stacked("Status", viewField(value(existingData, 9))));
        return content;
    }

    // ── buttons ─────────────────────────────────────────────────────────────
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

        JButton cancel = navyButton("Cancel");
        cancel.addActionListener(e -> close());

        JButton submit = navyButton(mode == Mode.EDIT ? "Update" : "Submit");
        submit.addActionListener(e -> onSubmitClicked());

        row.add(cancel);
        row.add(submit);
        return row;
    }

    private void onSubmitClicked() {
        try {
            validateForm();

            if (mode == Mode.ADD_SELF || mode == Mode.ADD_OTHER) {
                requestService.submit(buildRequestFromForm());
            }

            if (onSubmit != null) {
                onSubmit.onSubmit(collectFormData());
            }

            JOptionPane.showMessageDialog(
                    this,
                    mode == Mode.EDIT
                            ? "Request updated successfully."
                            : "Request submitted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void close() {
        if (onClose != null) {
            onClose.run();
        }
    }

    // ── validation + model building ─────────────────────────────────────────
    private void validateForm() {
        if (mode == Mode.ADD_OTHER && selectedEmployee() == null) {
            throw new IllegalArgumentException("Please select an employee.");
        }
        if (nameField != null && mode != Mode.ADD_OTHER && nameField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Employee name could not be resolved.");
        }
        if (reasonArea.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Reason is required.");
        }
        if (Session.getCurrentUser() == null) {
            throw new IllegalStateException("No active session found.");
        }
    }

    private Request buildRequestFromForm() {
        int employeeId = targetEmployeeId();
        String reason = reasonArea.getText().trim();

        LocalDate startDate = spinnerDate(startDateSpinner);
        LocalDate endDate = spinnerDate(endDateSpinner);

        boolean leave = LEAVE_CATEGORY.equals(String.valueOf(categoryCombo.getSelectedItem()));

        if (leave) {
            String leaveName = String.valueOf(leaveTypeCombo.getSelectedItem());
            return new LeaveRequest(
                    0, employeeId, RequestStatus.PENDING, null, reason, null,
                    startDate, endDate, new LeaveType(leaveTypeId(leaveName), leaveName));
        }

        LocalTime startTime = parseTime(String.valueOf(startTimeCombo.getSelectedItem()));
        LocalTime endTime = parseTime(String.valueOf(endTimeCombo.getSelectedItem()));

        if (!startDate.equals(endDate)) {
            throw new IllegalArgumentException(
                    "For a Work Time request, Start Date and End Date must be the same.");
        }

        if (OVERTIME.equals(String.valueOf(workTypeCombo.getSelectedItem()))) {
            return new OvertimeRequest(0, employeeId, RequestStatus.PENDING, null,
                    reason, null, startDate, startTime, endTime);
        }
        return new UndertimeRequest(0, employeeId, RequestStatus.PENDING, null,
                reason, null, startDate, startTime, endTime);
    }

    private int targetEmployeeId() {
        if (mode == Mode.ADD_OTHER) {
            Employee emp = selectedEmployee();
            return Integer.parseInt(emp.getEmployeeId());
        }
        return Session.getCurrentUser().getEmployeeId();
    }

    /**
     * Returns the 10-column row the table expects:
     * name, position, type, startDate, endDate, startTime, endTime, reason, notes, status.
     */
    private Object[] collectFormData() {
        boolean leave = LEAVE_CATEGORY.equals(String.valueOf(categoryCombo.getSelectedItem()));
        String type = leave ? "Leave" : String.valueOf(workTypeCombo.getSelectedItem());

        String name = mode == Mode.ADD_OTHER
                ? (selectedEmployee() == null ? "" : selectedEmployee().getFullName())
                : nameField.getText().trim();

        return new Object[]{
            name,
            positionField.getText().trim(),
            type,
            dateFormat.format((Date) startDateSpinner.getValue()),
            dateFormat.format((Date) endDateSpinner.getValue()),
            leave ? "" : String.valueOf(startTimeCombo.getSelectedItem()),
            leave ? "" : String.valueOf(endTimeCombo.getSelectedItem()),
            reasonArea.getText().trim(),
            "",
            String.valueOf(statusCombo.getSelectedItem())
        };
    }

    private void populateFields(Object[] data) {
        nameField.setText(value(data, 0));
        positionField.setText(value(data, 1));

        String type = value(data, 2);
        if (OVERTIME.equalsIgnoreCase(type) || UNDERTIME.equalsIgnoreCase(type)) {
            categoryCombo.setSelectedItem(WORK_TIME_CATEGORY);
            workTypeCombo.setSelectedItem(OVERTIME.equalsIgnoreCase(type) ? OVERTIME : UNDERTIME);
        } else {
            categoryCombo.setSelectedItem(LEAVE_CATEGORY);
        }

        setSpinnerDate(startDateSpinner, value(data, 3));
        setSpinnerDate(endDateSpinner, value(data, 4));
        setComboValue(startTimeCombo, value(data, 5), "9:00 AM");
        setComboValue(endTimeCombo, value(data, 6), "5:00 PM");
        reasonArea.setText(value(data, 7));

        String status = value(data, 9);
        statusCombo.setSelectedItem(status.isBlank() ? "Pending" : status);
    }

    // ── small helpers ────────────────────────────────────────────────────────
    private static int leaveTypeId(String name) {
        for (int i = 0; i < LEAVE_TYPES.length; i++) {
            if (LEAVE_TYPES[i].equalsIgnoreCase(name)) {
                return i + 1; // seed ids are 1-based in LEAVE_TYPES order
            }
        }
        return 1;
    }

    private void setComboValue(JComboBox<String> combo, String value, String fallback) {
        if (value == null || value.isBlank()) {
            combo.setSelectedItem(fallback);
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equalsIgnoreCase(value.trim())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.setSelectedItem(fallback);
    }

    private static String value(Object[] data, int index) {
        if (data == null || index >= data.length || data[index] == null) {
            return "";
        }
        return data[index].toString();
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
            spinner.setValue(new Date());
        }
    }

    private LocalTime parseTime(String text) {
        try {
            return new SimpleDateFormat("h:mm a").parse(text).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalTime()
                    .withSecond(0).withNano(0);
        } catch (Exception ex) {
            return LocalTime.of(9, 0);
        }
    }

    // ── layout helpers ────────────────────────────────────────────────────────
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

    // ── field factories ───────────────────────────────────────────────────────
    private JTextField baseField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 44));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(NAVY);
        field.setBorder(new CompoundBorder(
                new EmptyBorder(1, 2, 1, 2),   // keep the rounded outline off the edge
                new CompoundBorder(
                        new RoundedBorder(8, FIELD_BORDER),
                        new EmptyBorder(4, 10, 4, 10))));
        return field;
    }

    private JTextField readOnlyField() {
        JTextField field = baseField();
        field.setEditable(false);
        field.setFocusable(false);
        return field;
    }

    /** Read-only, black-text field used for VIEW mode (never a disabled combo). */
    private JTextField viewField(String text) {
        JTextField field = baseField();
        field.setText(text);
        field.setEditable(false);
        field.setFocusable(false);
        field.setForeground(Color.BLACK);
        return field;
    }

    private JScrollPane viewArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font(FONT, Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFocusable(false);
        area.setForeground(Color.BLACK);
        area.setBackground(Color.WHITE);
        area.setBorder(new EmptyBorder(6, 10, 6, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(0, 130));
        scroll.setBorder(new CompoundBorder(
                new EmptyBorder(1, 2, 1, 2),
                new RoundedBorder(8, FIELD_BORDER)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
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

    private JSpinner dateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(
                new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font(FONT, Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(0, 44));
        spinner.setBorder(new CompoundBorder(
                new EmptyBorder(1, 2, 1, 2),
                new CompoundBorder(
                        new RoundedBorder(8, FIELD_BORDER),
                        new EmptyBorder(4, 8, 4, 8))));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MM/dd/yyyy"));

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
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(FONT, Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
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

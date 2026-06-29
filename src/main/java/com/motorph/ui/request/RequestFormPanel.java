package com.motorph.ui.request;

import com.motorph.model.LeaveRequest;
import com.motorph.model.Request;
import com.motorph.model.UserAccount;
import com.motorph.service.RequestService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class RequestFormPanel extends JPanel {

    public interface SubmitHandler {
        void onSubmit(Object[] rowData);
    }

    private static final Color NAVY = new Color(5, 24, 108);
    private static final Color BG = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(150, 150, 150);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final String FONT = "Segoe UI";

    private static final int MAX_SICK_LEAVE = 15;
    private static final int MAX_VACATION_LEAVE = 15;

    private static final String VACATION_LEAVE = "Vacation Leave";
    private static final String SICK_LEAVE = "Sick Leave";
    private static final String UNDERTIME = "Undertime";
    private static final String OVERTIME = "Overtime";

    private static final String[] REQUEST_TYPES = {
        VACATION_LEAVE,
        SICK_LEAVE,
        UNDERTIME,
        OVERTIME
    };

    private static final String[] WORK_TIMES = {
        "9:00 AM",
        "10:00 AM",
        "11:00 AM",
        "12:00 PM",
        "1:00 PM",
        "2:00 PM",
        "3:00 PM",
        "4:00 PM",
        "5:00 PM"
    };

    private final Runnable onBack;
    private final Object[] existingData;
    private final SubmitHandler onSubmit;
    private final RequestService requestService = AppContext.getRequestService();

    private JTextField nameField;
    private JTextField positionField;
    private JComboBox<String> requestTypeCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JComboBox<String> startTimeCombo;
    private JComboBox<String> endTimeCombo;
    private JTextArea reasonArea;
    private JTextArea notesArea;
    private JComboBox<String> statusCombo;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public RequestFormPanel(Runnable onBack) {
        this(onBack, null, null);
    }

    public RequestFormPanel(Runnable onBack, Object[] existingData, SubmitHandler onSubmit) {
        this.onBack = onBack;
        this.existingData = existingData;
        this.onSubmit = onSubmit;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(createMainPanel(), BorderLayout.CENTER);

        if (existingData != null) {
            populateFields(existingData);
        }
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
        wrapper.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 1.0;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.anchor = GridBagConstraints.NORTHWEST;
        leftGbc.insets = new Insets(0, 0, 0, 68);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1.0;
        rightGbc.weighty = 1.0;
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

        int row = 0;

        nameField = createTextField();
        positionField = createTextField();
        requestTypeCombo = createRequestTypeComboBox();
        startDateSpinner = createDatePicker();
        endDateSpinner = createDatePicker();
        startTimeCombo = createTimeComboBox();
        endTimeCombo = createTimeComboBox();

        addStackedField(col, row++, "Name", nameField);
        addStackedField(col, row++, "Position", positionField);
        addStackedField(col, row++, "Request Type", requestTypeCombo);
        addTwoFields(col, row++, "Start Date", startDateSpinner, "End Date", endDateSpinner);
        addTwoFields(col, row++, "Start Time", startTimeCombo, "End Time", endTimeCombo);

        addVerticalGlue(col, row);
        return col;
    }

    private JPanel createRightColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        int row = 0;

        JScrollPane reasonScroll = createTextArea();
        reasonArea = (JTextArea) reasonScroll.getViewport().getView();

        JScrollPane notesScroll = createTextArea();
        notesArea = (JTextArea) notesScroll.getViewport().getView();

        statusCombo = createStatusComboBox();

        addStackedField(col, row++, "Reason", reasonScroll);
        addStackedField(col, row++, "Notes", notesScroll);
        addStackedField(col, row++, "Status", statusCombo);

        addVerticalGlue(col, row);
        return col;
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

    private void addTwoFields(
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

        panel.add(twoFieldPanel(firstLabel, firstField));
        panel.add(twoFieldPanel(secondLabel, secondField));

        parent.add(panel, gbc);
    }

    private JPanel twoFieldPanel(String labelText, JComponent field) {
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
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(NAVY);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(8, FIELD_BORDER),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return field;
    }

    private JScrollPane createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font(FONT, Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(Color.WHITE);
        area.setForeground(TEXT_DARK);
        area.setCaretColor(NAVY);
        area.setBorder(new EmptyBorder(6, 10, 6, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(320, 134));
        scroll.setMinimumSize(new Dimension(250, 134));
        scroll.setBorder(new RoundedBorder(8, FIELD_BORDER));
        scroll.setBackground(Color.WHITE);
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    private JComboBox<String> createRequestTypeComboBox() {
        return createRoundedComboBox(REQUEST_TYPES);
    }

    private JComboBox<String> createStatusComboBox() {
        return createRoundedComboBox(new String[]{
            "Pending",
            "Approved",
            "Rejected"
        });
    }

    private JComboBox<String> createTimeComboBox() {
        return createRoundedComboBox(WORK_TIMES);
    }

    private JComboBox<String> createRoundedComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font(FONT, Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(320, 44));
        combo.setMinimumSize(new Dimension(250, 44));
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
        combo.setBorder(new RoundedBorder(8, FIELD_BORDER));
        return combo;
    }

    private JSpinner createDatePicker() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = createRoundedSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MM/dd/yyyy"));
        return spinner;
    }

    private JSpinner createRoundedSpinner(SpinnerDateModel model) {
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font(FONT, Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(150, 44));
        spinner.setMinimumSize(new Dimension(120, 44));
        spinner.setBorder(new CompoundBorder(
                new RoundedBorder(8, FIELD_BORDER),
                new EmptyBorder(4, 8, 4, 8)
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

    private JPanel createButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton submit = navyButton(existingData == null ? "Submit" : "Update");

        submit.addActionListener(e -> {
            String validationError = validateRequestForm();

            if (!validationError.isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        validationError,
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Object[] rowData = collectFormData();

            if (onSubmit != null) {
                onSubmit.onSubmit(rowData);
            }

            JOptionPane.showMessageDialog(
                    this,
                    existingData == null
                            ? "Request added successfully."
                            : "Request updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        row.add(submit);
        return row;
    }

    private String validateRequestForm() {
        StringBuilder errors = new StringBuilder();

        String name = nameField.getText().trim();
        String position = positionField.getText().trim();
        String requestType = String.valueOf(requestTypeCombo.getSelectedItem());
        String reason = reasonArea.getText().trim();

        LocalDate startDate = getSpinnerLocalDate(startDateSpinner);
        LocalDate endDate = getSpinnerLocalDate(endDateSpinner);
        LocalDate today = LocalDate.now();

        LocalTime startTime = parseTime(String.valueOf(startTimeCombo.getSelectedItem()));
        LocalTime endTime = parseTime(String.valueOf(endTimeCombo.getSelectedItem()));

        if (name.isBlank()) {
            errors.append("Name is required.\n");
        }

        if (position.isBlank()) {
            errors.append("Position is required.\n");
        }

        if (reason.isBlank()) {
            errors.append("Reason is required.\n");
        }

        if (startDate.isBefore(today)) {
            errors.append("Start Date cannot be a past date.\n");
        }

        if (endDate.isBefore(today)) {
            errors.append("End Date cannot be a past date.\n");
        }

        if (endDate.isBefore(startDate)) {
            errors.append("End Date cannot be earlier than Start Date.\n");
        }

        if (isTimeBasedRequest(requestType)) {
            if (!startDate.equals(endDate)) {
                errors.append("For Undertime and Overtime, Start Date and End Date must be the same.\n");
            }

            if (startTime.equals(endTime)) {
                errors.append("For Undertime and Overtime, Start Time and End Time must not be the same.\n");
            }
        }

        if (SICK_LEAVE.equals(requestType) || VACATION_LEAVE.equals(requestType)) {
            long requestedDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

            if (requestedDays <= 0) {
                errors.append("Leave request must be at least 1 day.\n");
            } else {
                int maxAllowed = SICK_LEAVE.equals(requestType)
                        ? MAX_SICK_LEAVE
                        : MAX_VACATION_LEAVE;

                int usedLeaves = getUsedLeaveDays(requestType);
                int existingLeaveDays = getExistingLeaveDaysToExclude(requestType);

                int adjustedUsedLeaves = Math.max(0, usedLeaves - existingLeaveDays);
                long remainingLeaves = maxAllowed - adjustedUsedLeaves;

                if (requestedDays > remainingLeaves) {
                    errors.append(requestType)
                            .append(" exceeds available balance. Remaining: ")
                            .append(remainingLeaves)
                            .append(" day(s).\n");
                }
            }
        }

        return errors.toString();
    }

    private boolean isTimeBasedRequest(String requestType) {
        return UNDERTIME.equals(requestType) || OVERTIME.equals(requestType);
    }

    private LocalDate getSpinnerLocalDate(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private LocalTime parseTime(String text) {
        try {
            Date parsed = new SimpleDateFormat("h:mm a").parse(text);
            return parsed.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    .withSecond(0)
                    .withNano(0);
        } catch (Exception ex) {
            return LocalTime.of(9, 0);
        }
    }

    private int getUsedLeaveDays(String leaveType) {
        try {
            UserAccount user = Session.getCurrentUser();

            if (user == null) {
                return 0;
            }

            int employeeId = user.getEmployeeId();
            List<Request> requests = requestService.findByEmployee(employeeId);

            int total = 0;

            for (Request request : requests) {
                if (!(request instanceof LeaveRequest leave)) {
                    continue;
                }

                String status = request.getStatus() == null ? "" : request.getStatus().name();

                if ("REJECTED".equalsIgnoreCase(status)) {
                    continue;
                }

                String currentLeaveType = getLeaveTypeDisplayName(leave);

                if (!leaveType.equalsIgnoreCase(currentLeaveType)) {
                    continue;
                }

                if (leave.getStartDate() == null || leave.getEndDate() == null) {
                    continue;
                }

                total += (int) ChronoUnit.DAYS.between(
                        leave.getStartDate(),
                        leave.getEndDate()
                ) + 1;
            }

            return total;

        } catch (Exception ex) {
            return 0;
        }
    }

    private int getExistingLeaveDaysToExclude(String selectedLeaveType) {
        if (existingData == null) {
            return 0;
        }

        String oldRequestType = value(existingData, 2);

        if (!selectedLeaveType.equalsIgnoreCase(oldRequestType)) {
            return 0;
        }

        try {
            LocalDate oldStart = parseDateText(value(existingData, 3));
            LocalDate oldEnd = parseDateText(value(existingData, 4));

            if (oldStart == null || oldEnd == null) {
                return 0;
            }

            return (int) ChronoUnit.DAYS.between(oldStart, oldEnd) + 1;

        } catch (Exception ex) {
            return 0;
        }
    }

    private LocalDate parseDateText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        try {
            Date date = dateFormat.parse(text);
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (Exception ex) {
            return null;
        }
    }

    private String getLeaveTypeDisplayName(LeaveRequest leave) {
        try {
            Method method = leave.getClass().getMethod("getLeaveType");
            Object value = method.invoke(leave);

            if (value == null) {
                return "";
            }

            String raw = value.toString().replace("_", " ").trim().toLowerCase();

            if (raw.contains("sick")) {
                return SICK_LEAVE;
            }

            if (raw.contains("vacation")) {
                return VACATION_LEAVE;
            }

        } catch (Exception ignored) {
        }

        String requestType = leave.getRequestType() == null
                ? ""
                : leave.getRequestType().name().replace("_", " ").toLowerCase();

        if (requestType.contains("sick")) {
            return SICK_LEAVE;
        }

        if (requestType.contains("vacation")) {
            return VACATION_LEAVE;
        }

        return "";
    }

    private Object[] collectFormData() {
        return new Object[]{
            nameField.getText().trim(),
            positionField.getText().trim(),
            requestTypeCombo.getSelectedItem().toString(),
            dateFormat.format((Date) startDateSpinner.getValue()),
            dateFormat.format((Date) endDateSpinner.getValue()),
            startTimeCombo.getSelectedItem().toString(),
            endTimeCombo.getSelectedItem().toString(),
            reasonArea.getText().trim(),
            notesArea.getText().trim(),
            statusCombo.getSelectedItem().toString()
        };
    }

    private void populateFields(Object[] data) {
        nameField.setText(value(data, 0));
        positionField.setText(value(data, 1));

        String requestType = value(data, 2);

        if ("Leave".equalsIgnoreCase(requestType)) {
            requestTypeCombo.setSelectedItem(VACATION_LEAVE);
        } else {
            requestTypeCombo.setSelectedItem(requestType);
        }

        setSpinnerDate(startDateSpinner, value(data, 3), dateFormat);
        setSpinnerDate(endDateSpinner, value(data, 4), dateFormat);

        setComboValue(startTimeCombo, value(data, 5), "9:00 AM");
        setComboValue(endTimeCombo, value(data, 6), "5:00 PM");

        reasonArea.setText(value(data, 7));
        notesArea.setText(value(data, 8));
        statusCombo.setSelectedItem(value(data, 9));
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

    private String value(Object[] data, int index) {
        if (data == null || index >= data.length || data[index] == null) {
            return "";
        }

        return data[index].toString();
    }

    private void setSpinnerDate(JSpinner spinner, String text, SimpleDateFormat format) {
        if (text == null || text.isBlank()) {
            return;
        }

        try {
            spinner.setValue(format.parse(text));
        } catch (ParseException ignored) {
            spinner.setValue(new Date());
        }
    }

    private JButton navyButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

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

        button.setPreferredSize(new Dimension(128, 44));
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
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawRoundRect(
                    x,
                    y,
                    width - 1,
                    height - 1,
                    radius,
                    radius
            );
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }
}
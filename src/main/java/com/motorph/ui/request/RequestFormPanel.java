package com.motorph.ui.request;

import com.motorph.model.*;
import com.motorph.service.RequestService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
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

    private static final String VACATION_LEAVE = "Vacation Leave";
    private static final String SICK_LEAVE = "Sick Leave";
    private static final String UNDERTIME = "Undertime";
    private static final String OVERTIME = "Overtime";

    private static final int VACATION_LEAVE_TYPE_ID = 1;
    private static final int SICK_LEAVE_TYPE_ID = 2;

    private static final String[] REQUEST_TYPES = {
        VACATION_LEAVE,
        SICK_LEAVE,
        UNDERTIME,
        OVERTIME
    };

    private static final String[] WORK_TIMES = {
        "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
        "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM"
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

        updateTimeFieldsState();
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
        wrapper.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel form = new JPanel(new GridLayout(1, 2, 40, 0));
        form.setOpaque(false);

        form.add(createLeftColumn());
        form.add(createRightColumn());

        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(createButtonRow(), BorderLayout.SOUTH);

        return wrapper;
    }
    
    private JPanel createLeftColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        nameField = createTextField();
        positionField = createTextField();
        requestTypeCombo = createRequestTypeComboBox();
        startDateSpinner = createDatePicker();
        endDateSpinner = createDatePicker();
        startTimeCombo = createTimeComboBox();
        endTimeCombo = createTimeComboBox();

        requestTypeCombo.addActionListener(e -> updateTimeFieldsState());

        int row = 0;
        addStackedField(col, row++, "Name", nameField);
        addStackedField(col, row++, "Position", positionField);
        addStackedField(col, row++, "Request Type", requestTypeCombo);
        addTwoFields(col, row++, "Start Date", startDateSpinner, "End Date", endDateSpinner);
        addTwoFields(col, row++, "Start Time", startTimeCombo, "End Time", endTimeCombo);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        col.add(Box.createVerticalGlue(), gbc);
        return col;
    }

    private JPanel createRightColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        JScrollPane reasonScroll = createTextArea();
        reasonArea = (JTextArea) reasonScroll.getViewport().getView();

        JScrollPane notesScroll = createTextArea();
        notesArea = (JTextArea) notesScroll.getViewport().getView();

        statusCombo = createStatusComboBox();

        if (existingData == null) {
            statusCombo.setSelectedItem("Pending");
            statusCombo.setEnabled(false);
        }

        int row = 0;
        addStackedField(col, row++, "Reason", reasonScroll);
        addStackedField(col, row++, "Notes", notesScroll);
        addStackedField(col, row++, "Status", statusCombo);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        col.add(Box.createVerticalGlue(), gbc);
        return col;
    }

    private JPanel createButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton submit = navyButton(existingData == null ? "Submit" : "Update");

        submit.addActionListener(e -> {
            try {
                requireUiFields();

                if (existingData == null) {
                    Request request = buildRequestFromForm();
                    requestService.submit(request);
                }

                Object[] rowData = collectFormData();

                if (onSubmit != null) {
                    onSubmit.onSubmit(rowData);
                }

                JOptionPane.showMessageDialog(
                        this,
                        existingData == null
                                ? "Request submitted successfully."
                                : "Request updated successfully.",
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
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        row.add(submit);
        return row;
    }

    private void requireUiFields() {
        if (nameField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }

        if (positionField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Position is required.");
        }

        if (reasonArea.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Reason is required.");
        }

        UserAccount user = Session.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("No active session found.");
        }
    }

    private Request buildRequestFromForm() {
        UserAccount user = Session.getCurrentUser();
        int employeeId = user.getEmployeeId();

        String selectedType = String.valueOf(requestTypeCombo.getSelectedItem());
        String reason = reasonArea.getText().trim();

        LocalDate startDate = getSpinnerLocalDate(startDateSpinner);
        LocalDate endDate = getSpinnerLocalDate(endDateSpinner);
        LocalTime startTime = parseTime(String.valueOf(startTimeCombo.getSelectedItem()));
        LocalTime endTime = parseTime(String.valueOf(endTimeCombo.getSelectedItem()));

        if (VACATION_LEAVE.equals(selectedType)) {
            return new LeaveRequest(
                    0,
                    employeeId,
                    RequestStatus.PENDING,
                    null,
                    reason,
                    null,
                    startDate,
                    endDate,
                    new LeaveType(VACATION_LEAVE_TYPE_ID, VACATION_LEAVE)
            );
        }

        if (SICK_LEAVE.equals(selectedType)) {
            return new LeaveRequest(
                    0,
                    employeeId,
                    RequestStatus.PENDING,
                    null,
                    reason,
                    null,
                    startDate,
                    endDate,
                    new LeaveType(SICK_LEAVE_TYPE_ID, SICK_LEAVE)
            );
        }

        if (OVERTIME.equals(selectedType)) {
            if (!startDate.equals(endDate)) {
                throw new IllegalArgumentException("For Overtime, Start Date and End Date must be the same.");
            }

            return new OvertimeRequest(
                    0,
                    employeeId,
                    RequestStatus.PENDING,
                    null,
                    reason,
                    null,
                    startDate,
                    startTime,
                    endTime
            );
        }

        if (UNDERTIME.equals(selectedType)) {
            if (!startDate.equals(endDate)) {
                throw new IllegalArgumentException("For Undertime, Start Date and End Date must be the same.");
            }

            return new UndertimeRequest(
                    0,
                    employeeId,
                    RequestStatus.PENDING,
                    null,
                    reason,
                    null,
                    startDate,
                    startTime,
                    endTime
            );
        }

        throw new IllegalArgumentException("Unknown request type: " + selectedType);
    }

    private Object[] collectFormData() {
        String selectedType = String.valueOf(requestTypeCombo.getSelectedItem());
        boolean leave = VACATION_LEAVE.equals(selectedType) || SICK_LEAVE.equals(selectedType);

        return new Object[]{
            nameField.getText().trim(),
            positionField.getText().trim(),
            selectedType,
            dateFormat.format((Date) startDateSpinner.getValue()),
            dateFormat.format((Date) endDateSpinner.getValue()),
            leave ? "" : startTimeCombo.getSelectedItem().toString(),
            leave ? "" : endTimeCombo.getSelectedItem().toString(),
            reasonArea.getText().trim(),
            notesArea.getText().trim(),
            statusCombo.getSelectedItem().toString()
        };
    }

    private void updateTimeFieldsState() {
        if (requestTypeCombo == null || startTimeCombo == null || endTimeCombo == null) {
            return;
        }

        String selectedType = String.valueOf(requestTypeCombo.getSelectedItem());
        boolean timeBased = UNDERTIME.equals(selectedType) || OVERTIME.equals(selectedType);

        startTimeCombo.setEnabled(timeBased);
        endTimeCombo.setEnabled(timeBased);

        if (!timeBased) {
            startTimeCombo.setSelectedItem("9:00 AM");
            endTimeCombo.setSelectedItem("5:00 PM");
        }
    }

    private boolean isValidRequestTypeValue(String value) {
        for (String type : REQUEST_TYPES) {
            if (type.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
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

    private void populateFields(Object[] data) {
        nameField.setText(value(data, 0));
        positionField.setText(value(data, 1));

        String requestType = value(data, 2);

        if ("Leave".equalsIgnoreCase(requestType)) {
            requestTypeCombo.setSelectedItem(VACATION_LEAVE);
        } else if (isValidRequestTypeValue(requestType)) {
            requestTypeCombo.setSelectedItem(requestType);
        } else {
            requestTypeCombo.setSelectedItem(VACATION_LEAVE);
        }

        setSpinnerDate(startDateSpinner, value(data, 3), dateFormat);
        setSpinnerDate(endDateSpinner, value(data, 4), dateFormat);

        setComboValue(startTimeCombo, value(data, 5), "9:00 AM");
        setComboValue(endTimeCombo, value(data, 6), "5:00 PM");

        reasonArea.setText(value(data, 7));
        notesArea.setText(value(data, 8));

        String status = value(data, 9);
        statusCombo.setSelectedItem(status.isBlank() ? "Pending" : status);

        updateTimeFieldsState();
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

    private void addStackedField(JPanel parent, int row, String labelText, JComponent field) {
        GridBagConstraints gbc = baseGbc(row);
        gbc.insets = new Insets(0, 0, 22, 0);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(createLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        parent.add(panel, gbc);
    }

    private void addTwoFields(JPanel parent, int row,
                              String firstLabel, JComponent firstField,
                              String secondLabel, JComponent secondField) {

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
        scroll.setPreferredSize(new Dimension(0, 140));
        scroll.setMinimumSize(new Dimension(0, 140));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
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
            "Pending", "Approved", "Rejected"
        });
    }

    private JComboBox<String> createTimeComboBox() {
        return createRoundedComboBox(WORK_TIMES);
    }

    private JComboBox<String> createRoundedComboBox(String[] items) {

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

    private JSpinner createDatePicker() {
        SpinnerDateModel model = new SpinnerDateModel(
                new Date(),
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH
        );

        JSpinner spinner = createRoundedSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MM/dd/yyyy"));
        return spinner;
    }

    private JSpinner createRoundedSpinner(SpinnerDateModel model) {

        JSpinner spinner = new JSpinner(model);

        spinner.setFont(new Font(FONT, Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(0, 44));
        spinner.setMinimumSize(new Dimension(0, 44));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

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
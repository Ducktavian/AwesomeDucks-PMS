package com.motorph.ui.request;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;

public class RequestFormPanel extends JPanel {

    public interface SubmitHandler {
        void onSubmit(Object[] rowData);
    }

    private static final Color NAVY = new Color(5, 24, 108);
    private static final Color BG = Color.WHITE;
    private static final Color LINE_GRAY = new Color(190, 190, 190);
    private static final Color FIELD_BORDER = new Color(150, 150, 150);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final String FONT = "Segoe UI";

    private final Runnable onBack;
    private final Object[] existingData;
    private final SubmitHandler onSubmit;

    private JTextField nameField;
    private JTextField departmentField;
    private JComboBox<String> requestTypeCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JTextArea reasonArea;
    private JTextArea notesArea;
    private JComboBox<String> statusCombo;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    public RequestFormPanel(Runnable onBack) {
        this(onBack, null, null);
    }

    public RequestFormPanel(Runnable onBack, Object[] existingData, SubmitHandler onSubmit) {
        this.onBack = onBack;
        this.existingData = existingData;
        this.onSubmit = onSubmit;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(createTopBar(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        if (existingData != null) {
            populateFields(existingData);
        }
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 80));
        topBar.setBackground(BG);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, LINE_GRAY));

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        profile.setOpaque(false);
        profile.setBorder(new EmptyBorder(0, 0, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("<html><u>Name</u></html>");
        name.setForeground(NAVY);
        name.setFont(new Font(FONT, Font.BOLD, 16));
        name.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel position = new JLabel("Position");
        position.setForeground(Color.GRAY);
        position.setFont(new Font(FONT, Font.PLAIN, 13));
        position.setAlignmentX(Component.RIGHT_ALIGNMENT);

        textPanel.add(name);
        textPanel.add(position);

        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(47, 47));
        avatar.setIcon(new CircleIcon(NAVY, 47));

        profile.add(textPanel);
        profile.add(avatar);

        topBar.add(profile, BorderLayout.EAST);
        return topBar;
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
        departmentField = createTextField();
        requestTypeCombo = createRequestTypeComboBox();
        startDateSpinner = createDatePicker();
        endDateSpinner = createDatePicker();
        startTimeSpinner = createTimePicker();
        endTimeSpinner = createTimePicker();

        addStackedField(col, row++, "Name", nameField);
        addStackedField(col, row++, "Department", departmentField);
        addStackedField(col, row++, "Request Type", requestTypeCombo);
        addTwoFields(col, row++, "Start Date", startDateSpinner, "End Date", endDateSpinner);
        addTwoFields(col, row++, "Start Time", startTimeSpinner, "End Time", endTimeSpinner);

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
        return createRoundedComboBox(new String[]{
            "Leave",
            "Undertime",
            "Overtime"
        });
    }

    private JComboBox<String> createStatusComboBox() {
        return createRoundedComboBox(new String[]{
            "Pending",
            "Approved",
            "Rejected"
        });
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

    private JSpinner createTimePicker() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        JSpinner spinner = createRoundedSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "hh:mm a"));
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
            Object[] rowData = collectFormData();

            if (rowData[0].toString().isBlank()
                    || rowData[1].toString().isBlank()
                    || rowData[7].toString().isBlank()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill out all required fields: Name, Department, and Reason.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

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

    private Object[] collectFormData() {
        return new Object[]{
            nameField.getText().trim(),
            departmentField.getText().trim(),
            requestTypeCombo.getSelectedItem().toString(),
            dateFormat.format((Date) startDateSpinner.getValue()),
            dateFormat.format((Date) endDateSpinner.getValue()),
            timeFormat.format((Date) startTimeSpinner.getValue()),
            timeFormat.format((Date) endTimeSpinner.getValue()),
            reasonArea.getText().trim(),
            notesArea.getText().trim(),
            statusCombo.getSelectedItem().toString()
        };
    }

    private void populateFields(Object[] data) {
        nameField.setText(value(data, 0));
        departmentField.setText(value(data, 1));
        requestTypeCombo.setSelectedItem(value(data, 2));

        setSpinnerDate(startDateSpinner, value(data, 3), dateFormat);
        setSpinnerDate(endDateSpinner, value(data, 4), dateFormat);
        setSpinnerDate(startTimeSpinner, value(data, 5), timeFormat);
        setSpinnerDate(endTimeSpinner, value(data, 6), timeFormat);

        reasonArea.setText(value(data, 7));
        notesArea.setText(value(data, 8));
        statusCombo.setSelectedItem(value(data, 9));
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

    private static class CircleIcon implements Icon {
        private final Color color;
        private final int size;

        CircleIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.fillOval(x, y, size, size);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
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
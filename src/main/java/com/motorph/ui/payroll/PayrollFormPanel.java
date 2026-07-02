package com.motorph.ui.payroll;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.motorph.model.Employee;
import com.motorph.model.Payslip;
import com.motorph.model.PayrollPeriod;
import com.motorph.service.EmployeeService;
import com.motorph.util.AppContext;

public class PayrollFormPanel extends JPanel {

    private static final Color NAVY = new Color(5, 24, 108);
    private static final Color BG = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(150, 150, 150);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final Color TEXT_MUTED = new Color(120, 120, 120);
    private static final String FONT = "Segoe UI";

    private static final String SAVE_ICON_PATH = "/com/motorph/img/Save-Icon.png";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Runnable onBack;
    private final EmployeeService employeeService = AppContext.getEmployeeService();

    private JComboBox<Employee> employeeCombo;
    private JTextField payrollDateField;
    private JButton dateButton;
    private JComboBox<String> payrollPeriodCombo;

    // Earning fields
    private JTextField basicSalaryField;
    private JTextField hoursWorkedField;
    private JTextField hourlyRateField;
    private JTextField overtimeField;
    private JTextField holidayField;
    private JTextField grossPayField;

    // Allowance fields
    private JTextField riceSubsidyField;
    private JTextField phoneAllowanceField;
    private JTextField clothingAllowanceField;
    private JComboBox<String> bonusTypeCombo;
    private JTextField bonusAmountField;

    // Deduction fields
    private JTextField withholdingTaxField;
    private JTextField sssField;
    private JTextField philHealthField;
    private JTextField pagIbigField;
    private JTextField totalDeductionsField;
    private JTextField netPayField;

    public PayrollFormPanel(Runnable onBack) {
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(createMainPanel(), BorderLayout.CENTER);
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
        wrapper.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 1.0;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.anchor = GridBagConstraints.NORTHWEST;
        leftGbc.insets = new Insets(0, 0, 0, 55);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1.0;
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.anchor = GridBagConstraints.NORTHWEST;
        rightGbc.insets = new Insets(0, 0, 0, 0);

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

        // FIX 1: "Employee:" now has a trailing colon so addFormRow's
        // labelText.endsWith(":") check bolds it, matching "Payroll Date:"
        // and "Payroll Period:" on the right column.
        addFormRow(col, row++, "Employee:", createEmployeeCombo());

        addSpacer(col, row++, 16);
        addSectionTitle(col, row++, "Earning");

        addFormRow(col, row++, "Basic Salary", basicSalaryField = createTextField());
        addFormRow(col, row++, "Hours Worked", hoursWorkedField = createTextField());
        addFormRow(col, row++, "Hourly Rate", hourlyRateField = createTextField());
        addFormRow(col, row++, "Overtime", overtimeField = createTextField());
        addFormRow(col, row++, "Holiday", holidayField = createTextField());

        addSpacer(col, row++, 16);
        addSectionTitle(col, row++, "Allowance");

        addFormRow(col, row++, "Rice Subsidy", riceSubsidyField = createTextField());
        addFormRow(col, row++, "Phone Allowance", phoneAllowanceField = createTextField());
        addFormRow(col, row++, "Clothing Allowance", clothingAllowanceField = createTextField());
        addFormRow(col, row++, "Bonus Type", createBonusRow());

        addSpacer(col, row++, 12);
        addEmphasisFormRow(col, row++, "Gross Pay", grossPayField = createTextField());

        GridBagConstraints pushDown = new GridBagConstraints();
        pushDown.gridx = 0;
        pushDown.gridy = row;
        pushDown.gridwidth = 2;
        pushDown.weighty = 1.0;
        pushDown.fill = GridBagConstraints.VERTICAL;
        col.add(Box.createVerticalGlue(), pushDown);

        return col;
    }

    private JPanel createRightColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        int row = 0;

        addFormRow(col, row++, "Payroll Date:", createDateField());
        addFormRow(col, row++, "Payroll Period:", createPeriodCombo());

        addSpacer(col, row++, 16);
        addSectionTitle(col, row++, "Deduction");

        addFormRow(col, row++, "Withholding Tax", withholdingTaxField = createTextField());
        addFormRow(col, row++, "SSS", sssField = createTextField());
        addFormRow(col, row++, "PhilHealth", philHealthField = createTextField());
        addFormRow(col, row++, "PAG-IBIG", pagIbigField = createTextField());

        addSpacer(col, row++, 22);
        addEmphasisFormRow(col, row++, "Total Deductions", totalDeductionsField = createTextField());

        addSpacer(col, row++, 16);
        addEmphasisFormRow(col, row++, "Net Pay", netPayField = createTextField());

        GridBagConstraints pushDown = new GridBagConstraints();
        pushDown.gridx = 0;
        pushDown.gridy = row;
        pushDown.gridwidth = 2;
        pushDown.weighty = 1.0;
        pushDown.fill = GridBagConstraints.VERTICAL;
        col.add(Box.createVerticalGlue(), pushDown);

        return col;
    }

    private void addSectionTitle(JPanel parent, int row, String title) {
        GridBagConstraints gbc = baseGbc(row);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel label = new JLabel(title);
        label.setFont(new Font(FONT, Font.BOLD, 15));
        label.setForeground(TEXT_DARK);

        parent.add(label, gbc);
    }

    private void addFormRow(JPanel parent, int row, String labelText, JComponent field) {
        GridBagConstraints labelGbc = baseGbc(row);
        labelGbc.gridx = 0;
        labelGbc.weightx = 0;
        labelGbc.insets = new Insets(0, 0, 10, 24);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(145, 26));
        label.setFont(new Font(FONT, labelText.endsWith(":") ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(TEXT_DARK);
        label.setVerticalAlignment(SwingConstants.CENTER);

        parent.add(label, labelGbc);

        GridBagConstraints fieldGbc = baseGbc(row);
        fieldGbc.gridx = 1;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets = new Insets(0, 0, 10, 0);
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;

        parent.add(field, fieldGbc);
    }

    private void addSpacer(JPanel parent, int row, int height) {
        GridBagConstraints gbc = baseGbc(row);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(1, height));

        parent.add(spacer, gbc);
    }

    private GridBagConstraints baseGbc(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        return gbc;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 12));
        field.setPreferredSize(new Dimension(180, 26));
        field.setMinimumSize(new Dimension(120, 26));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(NAVY);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(7, FIELD_BORDER),
                new EmptyBorder(2, 8, 2, 8)
        ));
        return field;
    }

    // ---------------------------------------------------------------
    // Employee: combined "ID — Name" dropdown
    // ---------------------------------------------------------------

    private JComboBox<Employee> createEmployeeCombo() {
        employeeCombo = new JComboBox<>();
        employeeCombo.setFont(new Font(FONT, Font.PLAIN, 13));
        employeeCombo.setPreferredSize(new Dimension(220, 30));
        employeeCombo.setMinimumSize(new Dimension(160, 30));
        employeeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        employeeCombo.setBackground(new Color(165, 196, 228));
        employeeCombo.setOpaque(true);
        employeeCombo.setFocusable(false);
        employeeCombo.setBorder(new CompoundBorder(
                new RoundedBorder(7, FIELD_BORDER),
                new EmptyBorder(0, 4, 0, 4)
        ));

        employeeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Employee emp) {
                    setText(formatEmployeeLabel(emp));
                }

                if (!isSelected) {
                    setBackground(Color.WHITE);
                }

                return this;
            }
        });

        loadEmployeesIntoCombo();

        return employeeCombo;
    }

    private void loadEmployeesIntoCombo() {
        if (employeeService == null) {
            return;
        }

        List<Employee> employees = employeeService.getAllEmployees();

        if (employees == null) {
            return;
        }

        for (Employee emp : employees) {
            employeeCombo.addItem(emp);
        }
    }

    private String formatEmployeeLabel(Employee emp) {
        String id = emp.getEmployeeId() == null ? "" : emp.getEmployeeId();
        String first = emp.getFirstName() == null ? "" : emp.getFirstName();
        String last = emp.getLastName() == null ? "" : emp.getLastName();
        return id + " \u2014 " + (first + " " + last).trim();
    }

    /**
     * Currently selected employee, or null if nothing is selected yet.
     * Wire this up wherever the form needs to read which employee the
     * payroll run is for (e.g. on Submit / Save PDF).
     */
    public Employee getSelectedEmployee() {
        return employeeCombo == null ? null : (Employee) employeeCombo.getSelectedItem();
    }



    private JPanel createDateField() {
        JPanel wrap = new JPanel(new BorderLayout(6, 0));
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(180, 26));
        wrap.setMinimumSize(new Dimension(120, 26));
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        payrollDateField = createTextField();
        payrollDateField.setEditable(false);
        payrollDateField.setFocusable(false);
        payrollDateField.setText("");

        dateButton = new JButton("\uD83D\uDCC5"); // 📅
        dateButton.setFont(new Font(FONT, Font.PLAIN, 11));
        dateButton.setPreferredSize(new Dimension(34, 26));
        dateButton.setBackground(Color.WHITE);
        dateButton.setFocusPainted(false);
        dateButton.setBorder(new CompoundBorder(
                new RoundedBorder(7, FIELD_BORDER),
                new EmptyBorder(2, 6, 2, 6)
        ));
        dateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateButton.addActionListener(e -> openDatePicker());

        wrap.add(payrollDateField, BorderLayout.CENTER);
        wrap.add(dateButton, BorderLayout.EAST);

        return wrap;
    }

    private void openDatePicker() {
        LocalDate initialDate = LocalDate.now();

        try {
            String current = payrollDateField.getText().trim();
            if (!current.isBlank()) {
                initialDate = LocalDate.parse(current, DATE_FORMATTER);
            }
        } catch (Exception ignored) {
        }

        showCalendarDialog(initialDate);
    }

    /**
     * Calendar popup for selecting the Payroll Date.
     *
     * NOTE: this mirrors the date picker used in EmployeeFormPanel, but fixes
     * the bug where it was found there: that version used a hardcoded
     * dialog.setSize(360, 350) together with an auto-row GridLayout(0, 7),
     * so months that need 6 week-rows (most months where the 1st doesn't
     * fall on Sunday) ran out of vertical space and the last row of day
     * buttons got clipped outside the visible dialog (unreachable/unclickable).
     * Here the grid always reserves a fixed 7 rows (1 header + 6 week rows)
     * and the dialog is sized with pack() instead of a hardcoded size, so it
     * always fits every row regardless of month.
     */
    private void showCalendarDialog(LocalDate initialDate) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Select Payroll Date",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        final YearMonth[] currentMonth = {YearMonth.from(initialDate)};

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(12, 12, 8, 12));

        JButton prevButton = new JButton("\u2039");
        JButton nextButton = new JButton("\u203A");

        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font(FONT, Font.BOLD, 18));

        styleCalendarButton(prevButton);
        styleCalendarButton(nextButton);

        header.add(prevButton, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextButton, BorderLayout.EAST);

        // Fixed 7 rows: 1 header row + 6 week rows, always reserved so the
        // dialog never needs to clip the final week of a 6-row month.
        JPanel calendarPanel = new JPanel(new GridLayout(7, 7, 6, 6));
        calendarPanel.setBackground(Color.WHITE);
        calendarPanel.setBorder(new EmptyBorder(8, 12, 12, 12));
        calendarPanel.setPreferredSize(new Dimension(320, 280));

        Runnable refreshCalendar = () -> {
            calendarPanel.removeAll();

            monthLabel.setText(currentMonth[0].getMonth() + " " + currentMonth[0].getYear());

            String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

            for (String day : days) {
                JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
                dayLabel.setFont(new Font(FONT, Font.BOLD, 12));
                dayLabel.setForeground(TEXT_DARK);
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

                // FIX 2: day buttons now explicitly set foreground, are
                // opaque, have trimmed margins, and a guaranteed minimum
                // size so the digit text can never be squeezed out / hidden
                // by the look-and-feel or by GridLayout cell sizing.
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setFont(new Font(FONT, Font.PLAIN, 13));
                dayButton.setForeground(TEXT_DARK);
                dayButton.setBackground(Color.WHITE);
                dayButton.setOpaque(true);
                dayButton.setFocusPainted(false);
                dayButton.setMargin(new Insets(2, 2, 2, 2));
                dayButton.setPreferredSize(new Dimension(38, 32));
                dayButton.setMinimumSize(new Dimension(38, 32));
                dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                if (selectedDate.equals(initialDate)) {
                    dayButton.setBackground(new Color(225, 230, 245));
                }

                dayButton.addActionListener(e -> {
                    payrollDateField.setText(selectedDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
                    payrollDateField.setForeground(TEXT_DARK);
                    dialog.dispose();
                });

                calendarPanel.add(dayButton);
            }

            // Pad out remaining cells so the grid always has exactly 7 rows,
            // keeping the dialog size (and pack()) consistent across months.
            int filledCells = startOffset + daysInMonth;
            int totalCells = 7 * 7; // header row + 6 week rows
            for (int i = filledCells; i < totalCells - 7; i++) {
                calendarPanel.add(new JLabel(""));
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
        dialog.pack();
        dialog.setLocationRelativeTo(this);
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

    // ---------------------------------------------------------------
    // Payroll Period: dropdown (1st Cutoff / 2nd Cutoff)
    // ---------------------------------------------------------------

    private JComboBox<String> createPeriodCombo() {
        payrollPeriodCombo = new JComboBox<>(new String[]{"1st Cutoff", "2nd Cutoff"});
        payrollPeriodCombo.setFont(new Font(FONT, Font.PLAIN, 12));
        payrollPeriodCombo.setPreferredSize(new Dimension(180, 26));
        payrollPeriodCombo.setBackground(Color.WHITE);
        payrollPeriodCombo.setFocusable(false);
        return payrollPeriodCombo;
    }

    private JPanel createBonusRow() {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        bonusTypeCombo = new JComboBox<>(new String[]{
            "Bonus Type", "Performance", "13th Month", "Other"
        });
        bonusTypeCombo.setFont(new Font(FONT, Font.PLAIN, 11));
        bonusTypeCombo.setPreferredSize(new Dimension(105, 26));
        bonusTypeCombo.setBackground(Color.WHITE);
        bonusTypeCombo.setFocusable(false);

        bonusAmountField = createTextField();

        row.add(bonusTypeCombo, BorderLayout.WEST);
        row.add(bonusAmountField, BorderLayout.CENTER);

        return row;
    }

    private JPanel createButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 0, 0));

        JButton savePdf = navyButton("Save PDF", loadSaveIcon());
        JButton submit = navyButton("Submit", null);

        row.add(savePdf);
        row.add(submit);

        return row;
    }

    /**
     * Loads Save-Icon.png as a classpath resource, consistent with how the
     * rest of the codebase loads icons (e.g. Add-Icon.png, Update-Icon.png)
     * via getClass().getResource(...) rather than an absolute file path.
     */
    private Icon loadSaveIcon() {
        URL iconUrl = getClass().getResource(SAVE_ICON_PATH);

        if (iconUrl == null) {
            return null;
        }

        ImageIcon raw = new ImageIcon(iconUrl);
        Image scaled = raw.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private JButton navyButton(String text, Icon icon) {
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
                Icon ic = getIcon();
                int textWidth = fm.stringWidth(getText());
                int iconWidth = ic != null ? ic.getIconWidth() + 6 : 0;
                int contentWidth = textWidth + iconWidth;

                int startX = (getWidth() - contentWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                if (ic != null) {
                    int iconY = (getHeight() - ic.getIconHeight()) / 2;
                    ic.paintIcon(this, g2, startX, iconY);
                    startX += iconWidth;
                }

                g2.drawString(getText(), startX, y);
                g2.dispose();
            }
        };

        if (icon != null) {
            button.setIcon(icon);
        }

        button.setPreferredSize(new Dimension(125, 36));
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

    private void addEmphasisFormRow(JPanel parent, int row, String labelText, JComponent field) {
        GridBagConstraints labelGbc = baseGbc(row);
        labelGbc.gridx = 0;
        labelGbc.weightx = 0;
        labelGbc.insets = new Insets(0, 0, 7, 18);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(145, 26));
        label.setFont(new Font(FONT, Font.BOLD, 16));
        label.setForeground(TEXT_DARK);
        label.setVerticalAlignment(SwingConstants.CENTER);

        parent.add(label, labelGbc);

        GridBagConstraints fieldGbc = baseGbc(row);
        fieldGbc.gridx = 1;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets = new Insets(0, 0, 7, 0);
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;

        parent.add(field, fieldGbc);
    }

    public PayrollFormPanel(Runnable onBack, boolean viewOnly) {
        this(onBack, viewOnly, null);
    }

    /**
     * viewOnly form pre-populated from an existing Payslip, e.g. when a row
     * in the payroll table is opened for viewing/updating.
     */
    public PayrollFormPanel(Runnable onBack, boolean viewOnly, Payslip payslip) {
        this(onBack);

        if (viewOnly) {
            hideSubmitButton();
            setFieldsEditable(false);
        }

        if (payslip != null) {
            populateFromPayslip(payslip);
        }
    }

    /**
     * Fills the form from a Payslip. Basic Salary, Overtime, Holiday and the
     * Bonus Type/Amount fields are left blank: the Payslip DTO doesn't carry
     * per-payslip basic salary, overtime pay, holiday pay, or bonus data today.
     */
    private void populateFromPayslip(Payslip payslip) {
        selectEmployeeById(payslip.getEmployeeNumber());

        hoursWorkedField.setText(trimNumber(payslip.getTotalHours()));
        hourlyRateField.setText(trimNumber(payslip.getHourlyRate()));
        grossPayField.setText(money(payslip.getGrossPay()));

        riceSubsidyField.setText(money(payslip.getAllowanceBreakdown().getRiceSubsidy()));
        phoneAllowanceField.setText(money(payslip.getAllowanceBreakdown().getPhoneAllowance()));
        clothingAllowanceField.setText(money(payslip.getAllowanceBreakdown().getClothingAllowance()));

        withholdingTaxField.setText(money(payslip.getDeductionBreakdown().getWithholdingTax()));
        sssField.setText(money(payslip.getDeductionBreakdown().getSss()));
        philHealthField.setText(money(payslip.getDeductionBreakdown().getPhilHealth()));
        pagIbigField.setText(money(payslip.getDeductionBreakdown().getPagIbig()));
        totalDeductionsField.setText(money(payslip.getTotalDeductions()));
        netPayField.setText(money(payslip.getNetPay()));

        if (payslip.getPeriodEnd() != null) {
            payrollDateField.setText(payslip.getPeriodEnd().format(DATE_FORMATTER));
            payrollDateField.setForeground(TEXT_DARK);
        }

        payrollPeriodCombo.setSelectedItem(
                payslip.getPayrollPeriod() == PayrollPeriod.FIRST_PERIOD ? "1st Cutoff" : "2nd Cutoff"
        );
    }

    private void selectEmployeeById(String employeeNumber) {
        for (int i = 0; i < employeeCombo.getItemCount(); i++) {
            Employee emp = employeeCombo.getItemAt(i);

            if (emp != null && employeeNumber.equals(emp.getEmployeeId())) {
                employeeCombo.setSelectedItem(emp);
                return;
            }
        }
    }

    private String money(double value) {
        return String.format("%,.2f", value);
    }

    private String trimNumber(double value) {
        return value == Math.floor(value) ? String.valueOf((long) value) : String.valueOf(value);
    }

    private void hideSubmitButton() {
        hideButtonRecursive(this, "Submit");
    }

    private void hideButtonRecursive(Component component, String buttonText) {
        if (component instanceof JButton) {
            JButton button = (JButton) component;

            if (button.getText() != null && button.getText().equalsIgnoreCase(buttonText)) {
                button.setVisible(false);
            }
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                hideButtonRecursive(child, buttonText);
            }
        }
    }

    private void setFieldsEditable(boolean editable) {
        setEditableRecursive(this, editable);
    }

    private void setEditableRecursive(Component component, boolean editable) {
        if (component == dateButton) {
            component.setEnabled(editable);
            return;
        }

        if (component instanceof JTextField) {
            ((JTextField) component).setEditable(editable);
        } else if (component instanceof JTextArea) {
            ((JTextArea) component).setEditable(editable);
        } else if (component instanceof JComboBox) {
            component.setEnabled(editable);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setEditableRecursive(child, editable);
            }
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HRPayrollPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color BORDER_GRAY = new Color(195, 195, 195);
    private static final Color FIELD_BORDER = new Color(65, 65, 65);
    private static final Color MUTED_TEXT = new Color(150, 150, 150);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color BACK_TEXT = new Color(80, 80, 80);

    public HRPayrollPanel() {
        this(null, null);
    }

    public HRPayrollPanel(Runnable onBack, Runnable onSavePdf) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(WHITE);

        add(new TopProfilePanel());

        BackLink backLink = new BackLink(onBack);
        backLink.setBounds(63, 91, 50, 22);
        add(backLink);

        JLabel title = label("Payslip", 50, Font.BOLD, BLACK);
        title.setBounds(67, 135, 240, 62);
        add(title);

        SavePdfButton saveButton = new SavePdfButton(onSavePdf);
        saveButton.setBounds(847, 142, 100, 34);
        add(saveButton);

        PayslipFormPanel formPanel = new PayslipFormPanel();
        formPanel.setBounds(67, 215, 879, 515);
        add(formPanel);
    }

    private static class TopProfilePanel extends JPanel {

        public TopProfilePanel() {
            setBounds(817, 40, 126, 58);
            setLayout(null);
            setOpaque(false);

            JLabel name = label("Name", 18, Font.BOLD, NAVY);
            name.setHorizontalAlignment(SwingConstants.RIGHT);
            name.setBounds(0, 6, 62, 22);
            add(name);

            JLabel position = label("Position", 16, Font.PLAIN, MUTED_TEXT);
            position.setHorizontalAlignment(SwingConstants.RIGHT);
            position.setBounds(0, 31, 62, 20);
            add(position);

            ProfileCircle circle = new ProfileCircle();
            circle.setBounds(69, 0, 56, 56);
            add(circle);
        }
    }

    private static class ProfileCircle extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(NAVY);
            g2.fillOval(0, 0, 56, 56);

            g2.dispose();
        }
    }

    private static class BackLink extends JComponent {

        private final Runnable action;

        public BackLink(Runnable action) {
            this.action = action;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (BackLink.this.action != null) {
                        BackLink.this.action.run();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(BACK_TEXT);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g2.drawString("Back", 0, 17);
            g2.drawLine(0, 20, 43, 20);

            g2.dispose();
        }
    }

    private static class SavePdfButton extends JButton {

        private final Runnable action;

        public SavePdfButton(Runnable action) {
            super("Save PDF");
            this.action = action;

            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(SwingConstants.RIGHT);
            setIconTextGap(6);

            addActionListener(e -> {
                if (this.action != null) {
                    this.action.run();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(1.1f));

            int iconX = 11;
            int iconY = 10;

            g2.drawLine(iconX + 5, iconY, iconX + 5, iconY + 11);
            g2.drawLine(iconX + 1, iconY + 7, iconX + 5, iconY + 11);
            g2.drawLine(iconX + 9, iconY + 7, iconX + 5, iconY + 11);
            g2.drawLine(iconX, iconY + 14, iconX + 11, iconY + 14);

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class PayslipFormPanel extends JPanel {

        public PayslipFormPanel() {
            setLayout(null);
            setBackground(WHITE);
            setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));

            addEmployeeInformation();
            addPayrollInformation();
            addEarnings();
            addBenefits();
            addDeductions();
            addTotals();
        }

        private void addEmployeeInformation() {
            JLabel employeeNameLabel = formLabel("Employee Name:", Font.BOLD);
            employeeNameLabel.setBounds(44, 27, 120, 18);
            add(employeeNameLabel);

            RoundedField employeeNameField = new RoundedField("");
            employeeNameField.setBounds(166, 26, 198, 21);
            add(employeeNameField);

            JLabel employeeIdLabel = formLabel("Employee ID:", Font.BOLD);
            employeeIdLabel.setBounds(44, 56, 120, 18);
            add(employeeIdLabel);

            RoundedField employeeIdField = new RoundedField("");
            employeeIdField.setBounds(166, 55, 198, 21);
            add(employeeIdField);
        }

        private void addPayrollInformation() {
            JLabel payrollDateLabel = formLabel("Payroll Date:", Font.PLAIN);
            payrollDateLabel.setBounds(512, 28, 120, 18);
            add(payrollDateLabel);

            RoundedField payrollDateField = new RoundedField("MM-DD-YYYY");
            payrollDateField.setBounds(635, 23, 198, 21);
            add(payrollDateField);

            JLabel payrollPeriodLabel = formLabel("Payroll Period:", Font.PLAIN);
            payrollPeriodLabel.setBounds(512, 57, 120, 18);
            add(payrollPeriodLabel);

            RoundedField payrollPeriodField = new RoundedField("");
            payrollPeriodField.setBounds(635, 52, 198, 21);
            add(payrollPeriodField);
        }

        private void addEarnings() {
            JLabel heading = sectionHeading("Earnings");
            heading.setBounds(43, 99, 180, 34);
            add(heading);

            addFormRow("Basic Salary", 43, 141);
            addFormRow("Hours Worked", 43, 168);
            addFormRow("Hourly Rate", 43, 195);
            addFormRow("Overtime", 43, 222);
            addFormRow("Holiday", 43, 249);
        }

        private void addBenefits() {
            JLabel heading = sectionHeading("Benefits");
            heading.setBounds(43, 294, 180, 34);
            add(heading);

            addFormRow("Rice Subsidy", 43, 337);
            addFormRow("Phone Allowance", 43, 364);
            addFormRow("Clothing Allowance", 43, 391);

            RoundedField bonusTypeField = new RoundedField("Bonus Type");
            bonusTypeField.setBounds(43, 414, 111, 21);
            add(bonusTypeField);

            RoundedField bonusAmountField = new RoundedField("");
            bonusAmountField.setBounds(166, 414, 198, 21);
            add(bonusAmountField);
        }

        private void addDeductions() {
            JLabel heading = sectionHeading("Deductions");
            heading.setBounds(511, 101, 210, 34);
            add(heading);

            addRightFormRow("Withholding Tax", 511, 141);
            addRightFormRow("SSS", 511, 168);
            addRightFormRow("PhilHealth", 511, 195);
            addRightFormRow("PAG-IBIG", 511, 222);
            addRightFormRow("Undertime", 511, 249);

            JLabel totalDeductions = label("Total\nDeductions", 21, Font.BOLD, BLACK);
            totalDeductions.setBounds(511, 284, 120, 48);
            add(new MultiLineLabel("Total\nDeductions", 511, 283, 125, 52, 21));

            RoundedField totalDeductionField = new RoundedField("");
            totalDeductionField.setBounds(635, 270, 198, 47);
            add(totalDeductionField);
        }

        private void addTotals() {
            JLabel grossPayLabel = label("Gross Pay", 21, Font.BOLD, BLACK);
            grossPayLabel.setBounds(43, 468, 120, 26);
            add(grossPayLabel);

            RoundedField grossPayField = new RoundedField("");
            grossPayField.setBounds(166, 470, 198, 21);
            add(grossPayField);

            JLabel netPayLabel = label("Net Pay", 21, Font.BOLD, BLACK);
            netPayLabel.setBounds(507, 432, 110, 28);
            add(netPayLabel);

            RoundedField netPayField = new RoundedField("");
            netPayField.setBounds(631, 409, 198, 47);
            add(netPayField);
        }

        private void addFormRow(String labelText, int x, int y) {
            JLabel label = formLabel(labelText, Font.PLAIN);
            label.setBounds(x, y, 125, 18);
            add(label);

            RoundedField field = new RoundedField("");
            field.setBounds(166, y - 4, 198, 21);
            add(field);
        }

        private void addRightFormRow(String labelText, int x, int y) {
            JLabel label = formLabel(labelText, Font.PLAIN);
            label.setBounds(x, y, 125, 18);
            add(label);

            RoundedField field = new RoundedField("");
            field.setBounds(635, y - 4, 198, 21);
            add(field);
        }

        private JLabel sectionHeading(String text) {
            return label(text, 24, Font.BOLD, BLACK);
        }

        private JLabel formLabel(String text, int style) {
            return label(text, 13, style, BLACK);
        }
    }

    private static class RoundedField extends JTextField {

        private final String placeholder;

        public RoundedField(String placeholder) {
            this.placeholder = placeholder;

            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setForeground(BLACK);
            setCaretColor(BLACK);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

            if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(PLACEHOLDER);

                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(placeholder);
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2.drawString(placeholder, x, y);
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class MultiLineLabel extends JComponent {

        private final String[] lines;

        public MultiLineLabel(String text, int x, int y, int width, int height, int size) {
            this.lines = text.split("\\n");
            setBounds(x, y, width, height);
            setFont(new Font("Segoe UI", Font.BOLD, size));
            setForeground(BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setFont(getFont());
            g2.setColor(getForeground());

            int y = 20;
            for (String line : lines) {
                g2.drawString(line, 0, y);
                y += 18;
            }

            g2.dispose();
        }
    }

    private static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        label.setOpaque(false);
        return label;
    }
}
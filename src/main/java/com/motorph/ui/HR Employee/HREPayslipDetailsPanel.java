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
import javax.swing.border.AbstractBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

public class HREPayslipDetailsPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color WHITE = Color.WHITE;
    private static final Color NAVY = new Color(6, 20, 104);
    private static final Color TEXT_BLACK = new Color(10, 10, 10);
    private static final Color MUTED_GRAY = new Color(145, 145, 145);
    private static final Color BORDER_GRAY = new Color(205, 205, 205);
    private static final Color FIELD_BORDER = new Color(70, 70, 70);
    private static final Color PLACEHOLDER_GRAY = new Color(205, 205, 205);

    private static final String HEADER_FONT = "Segoe UI";
    private static final String TEXT_FONT = "Open Sans";

    public HREPayslipDetailsPanel() {
        setLayout(null);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        add(createProfileArea());
        add(createBackLink());
        add(createTitle());
        add(createSavePdfButton());
        add(createPayslipForm());
    }

    private JComponent createProfileArea() {
        JPanel profile = new JPanel(null);
        profile.setOpaque(false);
        profile.setBounds(820, 40, 125, 60);

        JLabel name = new JLabel("Name");
        name.setFont(new Font(HEADER_FONT, Font.BOLD, 18));
        name.setForeground(NAVY);
        name.setBounds(0, 4, 70, 24);
        profile.add(name);

        JLabel position = new JLabel("Position");
        position.setFont(new Font(TEXT_FONT, Font.PLAIN, 16));
        position.setForeground(MUTED_GRAY);
        position.setBounds(0, 28, 80, 22);
        profile.add(position);

        CircleAvatar avatar = new CircleAvatar();
        avatar.setBounds(66, 0, 57, 57);
        profile.add(avatar);

        return profile;
    }

    private JLabel createBackLink() {
        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(new Font(TEXT_FONT, Font.PLAIN, 16));
        back.setForeground(new Color(80, 80, 80));
        back.setBounds(63, 88, 55, 24);
        return back;
    }

    private JLabel createTitle() {
        JLabel title = new JLabel("Payslip");
        title.setFont(new Font(HEADER_FONT, Font.BOLD, 50));
        title.setForeground(Color.BLACK);
        title.setBounds(67, 133, 230, 62);
        return title;
    }

    private JComponent createSavePdfButton() {
        SavePdfButton button = new SavePdfButton();
        button.setBounds(847, 142, 99, 34);
        return button;
    }

    private JComponent createPayslipForm() {
        JPanel form = new JPanel(null);
        form.setBackground(WHITE);
        form.setBorder(new LineBorder(BORDER_GRAY, 1));
        form.setBounds(67, 215, 879, 515);

        addEmployeeHeaderFields(form);
        addPayrollHeaderFields(form);
        addEarningsSection(form);
        addBenefitsSection(form);
        addDeductionsSection(form);
        addTotalsSection(form);

        return form;
    }

    private void addEmployeeHeaderFields(JPanel form) {
        addBoldLabel(form, "Employee Name:", 43, 29, 120, 22, 12);
        addField(form, 166, 26, 198, 22);

        addBoldLabel(form, "Employee ID:", 43, 57, 120, 22, 12);
        addField(form, 166, 54, 198, 22);
    }

    private void addPayrollHeaderFields(JPanel form) {
        addPlainLabel(form, "Payroll Date:", 511, 28, 120, 22, 12);
        addFieldWithPlaceholder(form, "MM-DD-YYYY", 635, 23, 198, 22, true);

        addPlainLabel(form, "Payroll Period:", 511, 56, 120, 22, 12);
        addField(form, 635, 51, 198, 22);
    }

    private void addEarningsSection(JPanel form) {
        addSectionTitle(form, "Earnings", 42, 99, 160, 35);

        int labelX = 43;
        int fieldX = 166;
        int y = 136;

        addPlainLabel(form, "Basic Salary", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);

        y += 27;
        addPlainLabel(form, "Hours Worked", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);

        y += 27;
        addPlainLabel(form, "Hourly Rate", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);

        y += 27;
        addPlainLabel(form, "Overtime", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);

        y += 27;
        addPlainLabel(form, "Holiday", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);
    }

    private void addBenefitsSection(JPanel form) {
        addSectionTitle(form, "Benefits", 42, 291, 160, 35);

        int labelX = 43;
        int fieldX = 166;
        int y = 337;

        addPlainLabel(form, "Rice Subsidy", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 3, 198, 22);

        y += 27;
        addPlainLabel(form, "Phone Allowance", labelX, y, 130, 20, 12);
        addField(form, fieldX, y - 3, 198, 22);

        y += 27;
        addPlainLabel(form, "Clothing Allowance", labelX, y, 130, 20, 12);
        addField(form, fieldX, y - 3, 198, 22);

        y += 27;
        addFieldWithPlaceholder(form, "Bonus Type", labelX, y - 4, 110, 22, false);
        addField(form, fieldX, y - 4, 198, 22);
    }

    private void addDeductionsSection(JPanel form) {
        addSectionTitle(form, "Deductions", 511, 101, 180, 35);

        int labelX = 511;
        int fieldX = 635;
        int y = 136;

        addPlainLabel(form, "Withholding Tax", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);

        y += 27;
        addPlainLabel(form, "SSS", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);

        y += 27;
        addPlainLabel(form, "PhilHealth", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);

        y += 27;
        addPlainLabel(form, "PAG-IBIG", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);

        y += 27;
        addPlainLabel(form, "Undertime", labelX, y, 120, 20, 12);
        addField(form, fieldX, y - 1, 198, 22);
    }

    private void addTotalsSection(JPanel form) {
        addBoldLabel(form, "Total", 510, 278, 120, 24, 20);
        addBoldLabel(form, "Deductions", 510, 296, 145, 24, 20);
        addLargeField(form, 635, 269, 198, 47);

        addBoldLabel(form, "Gross Pay", 43, 468, 130, 25, 21);
        addField(form, 166, 470, 198, 22);

        addBoldLabel(form, "Net Pay", 507, 428, 120, 28, 21);
        addLargeField(form, 631, 409, 198, 47);
    }

    private void addSectionTitle(JPanel parent, String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(HEADER_FONT, Font.BOLD, 25));
        label.setForeground(TEXT_BLACK);
        label.setBounds(x, y, width, height);
        parent.add(label);
    }

    private void addBoldLabel(JPanel parent, String text, int x, int y, int width, int height, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(TEXT_FONT, Font.BOLD, size));
        label.setForeground(TEXT_BLACK);
        label.setBounds(x, y, width, height);
        parent.add(label);
    }

    private void addPlainLabel(JPanel parent, String text, int x, int y, int width, int height, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(TEXT_FONT, Font.PLAIN, size));
        label.setForeground(TEXT_BLACK);
        label.setBounds(x, y, width, height);
        parent.add(label);
    }

    private void addField(JPanel parent, int x, int y, int width, int height) {
        RoundedTextField field = new RoundedTextField("");
        field.setBounds(x, y, width, height);
        parent.add(field);
    }

    private void addLargeField(JPanel parent, int x, int y, int width, int height) {
        RoundedTextField field = new RoundedTextField("");
        field.setBounds(x, y, width, height);
        parent.add(field);
    }

    private void addFieldWithPlaceholder(
            JPanel parent,
            String placeholder,
            int x,
            int y,
            int width,
            int height,
            boolean centered
    ) {
        RoundedTextField field = new RoundedTextField(placeholder);
        field.setPlaceholderCentered(centered);
        field.setBounds(x, y, width, height);
        parent.add(field);
    }

    private static class RoundedTextField extends JTextField {

        private final String placeholder;
        private boolean placeholderCentered;

        public RoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            this.placeholderCentered = false;

            setOpaque(false);
            setBorder(new RoundedLineBorder(FIELD_BORDER, 1, 6));
            setFont(new Font(TEXT_FONT, Font.PLAIN, 12));
            setForeground(TEXT_BLACK);
            setCaretColor(TEXT_BLACK);
            setMargin(new Insets(0, 8, 0, 8));
        }

        public void setPlaceholderCentered(boolean placeholderCentered) {
            this.placeholderCentered = placeholderCentered;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setFont(getFont());
                g2.setColor(PLACEHOLDER_GRAY);

                FontMetrics fm = g2.getFontMetrics();

                int x;
                if (placeholderCentered) {
                    x = (getWidth() - fm.stringWidth(placeholder)) / 2;
                } else {
                    x = 8;
                }

                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2.drawString(placeholder, x, y);
                g2.dispose();
            }
        }
    }

    private static class SavePdfButton extends JButton {

        public SavePdfButton() {
            setLayout(null);
            setText("");
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel label = new JLabel("Save PDF");
            label.setFont(new Font(TEXT_FONT, Font.PLAIN, 14));
            label.setForeground(Color.WHITE);
            label.setBounds(29, 7, 70, 20);
            add(label);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.2f));

            int x = 15;
            int y = 10;

            g2.drawLine(x, y, x, y + 9);
            g2.drawLine(x - 4, y + 6, x, y + 10);
            g2.drawLine(x + 4, y + 6, x, y + 10);
            g2.drawLine(x - 6, y + 14, x + 6, y + 14);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CircleAvatar extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));

            g2.dispose();
        }
    }

    private static class RoundedLineBorder extends AbstractBorder {

        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedLineBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            for (int i = 0; i < thickness; i++) {
                g2.drawRoundRect(
                        x + i,
                        y + i,
                        width - 1 - (i * 2),
                        height - 1 - (i * 2),
                        radius,
                        radius
                );
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(3, 6, 3, 6);
        }

        @Override
        public Insets getBorderInsets(Component component, Insets insets) {
            insets.top = 3;
            insets.left = 6;
            insets.bottom = 3;
            insets.right = 6;
            return insets;
        }
    }
}
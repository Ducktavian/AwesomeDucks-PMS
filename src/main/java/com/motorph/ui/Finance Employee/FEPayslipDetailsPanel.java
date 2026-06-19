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
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FEPayslipDetailsPanel extends JPanel {

    private static final Color PAGE_BG = Color.WHITE;
    private static final Color NAVY = new Color(7, 24, 104);
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color BORDER_GRAY = new Color(190, 190, 190);
    private static final Color FIELD_BORDER = new Color(85, 85, 85);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 50);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 25);
    private static final Font LABEL_FONT = new Font("Open Sans", Font.PLAIN, 13);
    private static final Font LABEL_BOLD = new Font("Open Sans", Font.BOLD, 13);
    private static final Font TOTAL_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font BUTTON_FONT = new Font("Open Sans", Font.PLAIN, 14);
    private static final Font PROFILE_NAME_FONT = new Font("Open Sans", Font.BOLD, 18);
    private static final Font PROFILE_POSITION_FONT = new Font("Open Sans", Font.PLAIN, 16);

    public FEPayslipDetailsPanel() {
        setLayout(null);
        setBackground(PAGE_BG);
        setPreferredSize(new Dimension(1023, 800));

        addTopRightProfile();
        addBackLink();
        addTitle();
        addSavePdfButton();
        addPayslipForm();
    }

    private void addTopRightProfile() {
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(825, 48, 70, 22);
        nameLabel.setFont(PROFILE_NAME_FONT);
        nameLabel.setForeground(NAVY);
        add(nameLabel);

        JLabel positionLabel = new JLabel("Position");
        positionLabel.setBounds(820, 73, 80, 20);
        positionLabel.setFont(PROFILE_POSITION_FONT);
        positionLabel.setForeground(MUTED_TEXT);
        add(positionLabel);

        AvatarCircle avatar = new AvatarCircle();
        avatar.setBounds(887, 40, 56, 56);
        add(avatar);
    }

    private void addBackLink() {
        JLabel backLabel = new JLabel("<html><u>Back</u></html>");
        backLabel.setBounds(63, 88, 70, 24);
        backLabel.setFont(new Font("Open Sans", Font.PLAIN, 18));
        backLabel.setForeground(new Color(80, 80, 80));
        add(backLabel);
    }

    private void addTitle() {
        JLabel titleLabel = new JLabel("Payslip");
        titleLabel.setBounds(67, 135, 260, 58);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(BLACK);
        add(titleLabel);
    }

    private void addSavePdfButton() {
        SavePdfButton saveButton = new SavePdfButton();
        saveButton.setBounds(847, 142, 99, 34);
        add(saveButton);
    }

    private void addPayslipForm() {
        PayslipFormPanel formPanel = new PayslipFormPanel();
        formPanel.setBounds(67, 215, 879, 515);
        add(formPanel);
    }

    private static class PayslipFormPanel extends JPanel {

        public PayslipFormPanel() {
            setLayout(null);
            setOpaque(false);

            addEmployeeHeaderFields();
            addEarningsSection();
            addBenefitsSection();
            addDeductionsSection();
            addNetPaySection();
        }

        private void addEmployeeHeaderFields() {
            addBoldLabel("Employee Name:", 43, 29, 120, 20);
            addField(166, 26, 198, 21);

            addBoldLabel("Employee ID:", 43, 58, 120, 20);
            addField(166, 55, 198, 21);

            addLabel("Payroll Date:", 511, 29, 120, 20);
            addField("MM-DD-YYYY", 635, 23, 198, 21, JTextField.CENTER);

            addLabel("Payroll Period:", 511, 58, 120, 20);
            addField(635, 51, 198, 21);
        }

        private void addEarningsSection() {
            addSectionTitle("Earnings", 42, 101, 180, 35);

            addLabel("Basic Salary", 44, 140, 120, 20);
            addField(166, 137, 198, 21);

            addLabel("Hours Worked", 44, 167, 120, 20);
            addField(166, 164, 198, 21);

            addLabel("Hourly Rate", 44, 194, 120, 20);
            addField(166, 191, 198, 21);

            addLabel("Overtime", 44, 221, 120, 20);
            addField(166, 218, 198, 21);

            addLabel("Holiday", 44, 248, 120, 20);
            addField(166, 245, 198, 21);
        }

        private void addBenefitsSection() {
            addSectionTitle("Benefits", 42, 295, 180, 35);

            addLabel("Rice Subsidy", 44, 337, 120, 20);
            addField(166, 334, 198, 21);

            addLabel("Phone Allowance", 44, 364, 120, 20);
            addField(166, 361, 198, 21);

            addLabel("Clothing Allowance", 44, 391, 130, 20);
            addField(166, 388, 198, 21);

            addField("Bonus Type", 43, 415, 111, 21, JTextField.LEFT);
            addField(166, 415, 198, 21);

            JLabel grossPay = new JLabel("Gross Pay");
            grossPay.setFont(TOTAL_FONT);
            grossPay.setForeground(BLACK);
            grossPay.setBounds(43, 466, 120, 30);
            add(grossPay);

            addField(166, 470, 198, 21);
        }

        private void addDeductionsSection() {
            addSectionTitle("Deductions", 511, 102, 200, 35);

            addLabel("Withholding Tax", 512, 140, 130, 20);
            addField(635, 137, 198, 21);

            addLabel("SSS", 512, 167, 130, 20);
            addField(635, 164, 198, 21);

            addLabel("PhilHealth", 512, 194, 130, 20);
            addField(635, 191, 198, 21);

            addLabel("PAG-IBIG", 512, 221, 130, 20);
            addField(635, 218, 198, 21);

            addLabel("Undertime", 512, 248, 130, 20);
            addField(635, 245, 198, 21);

            JLabel totalDeductions = new JLabel("<html>Total<br>Deductions</html>");
            totalDeductions.setFont(TOTAL_FONT);
            totalDeductions.setForeground(BLACK);
            totalDeductions.setBounds(510, 280, 150, 48);
            add(totalDeductions);

            addField(635, 269, 198, 47);
        }

        private void addNetPaySection() {
            JLabel netPay = new JLabel("Net Pay");
            netPay.setFont(TOTAL_FONT);
            netPay.setForeground(BLACK);
            netPay.setBounds(507, 427, 120, 35);
            add(netPay);

            addField(631, 410, 199, 46);
        }

        private void addSectionTitle(String text, int x, int y, int w, int h) {
            JLabel label = new JLabel(text);
            label.setFont(SECTION_FONT);
            label.setForeground(BLACK);
            label.setBounds(x, y, w, h);
            add(label);
        }

        private void addBoldLabel(String text, int x, int y, int w, int h) {
            JLabel label = new JLabel(text);
            label.setFont(LABEL_BOLD);
            label.setForeground(BLACK);
            label.setBounds(x, y, w, h);
            add(label);
        }

        private void addLabel(String text, int x, int y, int w, int h) {
            JLabel label = new JLabel(text);
            label.setFont(LABEL_FONT);
            label.setForeground(BLACK);
            label.setBounds(x, y, w, h);
            add(label);
        }

        private void addField(int x, int y, int w, int h) {
            addField("", x, y, w, h, JTextField.LEFT);
        }

        private void addField(String placeholder, int x, int y, int w, int h, int alignment) {
            RoundedTextField field = new RoundedTextField(placeholder, alignment);
            field.setBounds(x, y, w, h);
            add(field);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);
            g2.setColor(WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(BORDER_GRAY);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g2.dispose();
        }
    }

    private static class RoundedTextField extends JTextField {

        private final String placeholder;

        private RoundedTextField(String placeholder, int alignment) {
            this.placeholder = placeholder == null ? "" : placeholder;

            setOpaque(false);
            setBorder(new EmptyBorder(1, 8, 1, 8));
            setFont(new Font("Open Sans", Font.PLAIN, 12));
            setForeground(BLACK);
            setCaretColor(BLACK);
            setHorizontalAlignment(alignment);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);

            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty() && !placeholder.isEmpty()) {
                Graphics2D g2Hint = createGraphics(g);
                g2Hint.setFont(getFont());
                g2Hint.setColor(PLACEHOLDER);

                FontMetrics fm = g2Hint.getFontMetrics();
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                int textX;
                if (getHorizontalAlignment() == JTextField.CENTER) {
                    textX = (getWidth() - fm.stringWidth(placeholder)) / 2;
                } else {
                    textX = 8;
                }

                g2Hint.drawString(placeholder, textX, textY);
                g2Hint.dispose();
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = createGraphics(g);
            g2.setColor(FIELD_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
            g2.dispose();
        }
    }

    private static class SavePdfButton extends JButton {

        private SavePdfButton() {
            super("Save PDF");
            setFont(BUTTON_FONT);
            setForeground(WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalTextPosition(SwingConstants.RIGHT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            drawDownloadIcon(g2);

            g2.dispose();

            super.paintComponent(g);
        }

        private void drawDownloadIcon(Graphics2D g2) {
            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(1.2f));

            int x = 16;
            int y = 10;

            g2.drawLine(x, y, x, y + 12);
            g2.drawLine(x - 5, y + 7, x, y + 12);
            g2.drawLine(x + 5, y + 7, x, y + 12);
            g2.drawLine(x - 7, y + 15, x + 7, y + 15);
            g2.drawLine(x - 7, y + 15, x - 7, y + 11);
            g2.drawLine(x + 7, y + 15, x + 7, y + 11);
        }
    }

    private static class AvatarCircle extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);
            g2.setColor(NAVY);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static Graphics2D createGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        return g2;
    }
}
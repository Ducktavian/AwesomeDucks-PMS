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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public final class FinancePayrollDetails extends JPanel {

    private static final int PAGE_WIDTH = 1023;
    private static final int PAGE_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color FIELD_BORDER = new Color(84, 84, 84);
    private static final Color FORM_BORDER = new Color(167, 167, 167);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color LINK_GRAY = new Color(84, 84, 84);

    public FinancePayrollDetails() {
        setLayout(null);
        setOpaque(true);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));

        buildProfileHeader();
        buildBackLink();
        buildActionButtons();
        buildPayrollForm();
    }

    private void buildProfileHeader() {
        JLabel name = createLabel(
                "Name",
                750, 45, 128, 24,
                headerFont(18, Font.BOLD),
                NAVY,
                SwingConstants.RIGHT
        );
        add(name);

        JLabel position = createLabel(
                "Position",
                750, 70, 128, 22,
                textFont(16, Font.PLAIN),
                MUTED_TEXT,
                SwingConstants.RIGHT
        );
        add(position);

        AvatarCircle avatar = new AvatarCircle();
        avatar.setBounds(887, 40, 56, 56);
        add(avatar);
    }

    private void buildBackLink() {
        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setBounds(63, 150, 55, 24);
        back.setFont(textFont(16, Font.PLAIN));
        back.setForeground(LINK_GRAY);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(back);
    }

    private void buildActionButtons() {
        PayrollActionButton savePdf = new PayrollActionButton("Save PDF", true);
        savePdf.setBounds(692, 141, 121, 44);
        add(savePdf);

        PayrollActionButton submit = new PayrollActionButton("Submit", false);
        submit.setBounds(821, 141, 126, 44);
        add(submit);
    }

    private void buildPayrollForm() {
        BorderedFormPanel form = new BorderedFormPanel();
        form.setBounds(67, 215, 880, 515);
        add(form);

        /*
         * Coordinates below are relative to the bordered form panel.
         */

        form.add(createLabel("Employee Name:", 44, 25, 120, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.LEFT));
        form.add(createField(167, 26, 199, 21));

        form.add(createLabel("Employee ID:", 44, 53, 120, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.LEFT));
        form.add(createField(167, 54, 199, 21));

        form.add(createLabel("Payroll Date:", 512, 22, 115, 25, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createFieldWithPlaceholder(635, 23, 199, 21, "MM-DD-YYYY", SwingConstants.CENTER));

        form.add(createLabel("Payroll Period:", 512, 50, 120, 25, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(635, 51, 199, 21));

        form.add(createLabel("Earnings", 42, 101, 180, 35, headerFont(24, Font.BOLD), BLACK, SwingConstants.LEFT));

        form.add(createLabel("Basic Salary", 44, 137, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(167, 137, 199, 21));

        form.add(createLabel("Hours Worked", 44, 163, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(167, 163, 199, 21));

        form.add(createLabel("Hourly Rate", 44, 190, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(167, 190, 199, 21));

        form.add(createLabel("Overtime", 44, 218, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(167, 218, 199, 21));

        form.add(createLabel("Holiday", 44, 247, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(167, 247, 199, 21));

        form.add(createLabel("Benefits", 42, 296, 180, 35, headerFont(24, Font.BOLD), BLACK, SwingConstants.LEFT));

        form.add(createLabel("Rice Subsidy", 44, 333, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(166, 333, 199, 22));

        form.add(createLabel("Phone Allowance", 44, 360, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(166, 360, 199, 21));

        form.add(createLabel("Clothing Allowance", 44, 386, 122, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(166, 386, 199, 21));

        form.add(createFieldWithPlaceholder(43, 415, 111, 21, "Bonus Type", SwingConstants.LEFT));
        form.add(createField(166, 415, 199, 21));

        form.add(createLabel("Gross Pay", 44, 466, 120, 35, headerFont(20, Font.BOLD), BLACK, SwingConstants.LEFT));
        form.add(createField(166, 469, 199, 22));

        form.add(createLabel("Deductions", 512, 102, 220, 35, headerFont(24, Font.BOLD), BLACK, SwingConstants.LEFT));

        form.add(createLabel("Withholding Tax", 512, 137, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(635, 137, 199, 21));

        form.add(createLabel("SSS", 512, 164, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(635, 164, 199, 21));

        form.add(createLabel("PhilHealth", 512, 190, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(635, 190, 199, 21));

        form.add(createLabel("PAG-IBIG", 512, 216, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(635, 216, 199, 21));

        form.add(createLabel("Undertime", 512, 242, 120, 22, textFont(13, Font.PLAIN), BLACK, SwingConstants.LEFT));
        form.add(createField(635, 242, 199, 21));

        JLabel totalDeductions = new JLabel("<html>Total<br>Deductions</html>");
        totalDeductions.setBounds(510, 280, 130, 38);
        totalDeductions.setFont(headerFont(20, Font.BOLD));
        totalDeductions.setForeground(BLACK);
        totalDeductions.setVerticalAlignment(SwingConstants.CENTER);
        form.add(totalDeductions);

        form.add(createField(635, 269, 199, 47));

        form.add(createLabel("Net Pay", 507, 426, 120, 35, headerFont(20, Font.BOLD), BLACK, SwingConstants.LEFT));
        form.add(createField(631, 409, 199, 47));
    }

    private RoundedTextField createField(int x, int y, int width, int height) {
        RoundedTextField field = new RoundedTextField(null, SwingConstants.LEFT);
        field.setBounds(x, y, width, height);
        return field;
    }

    private RoundedTextField createFieldWithPlaceholder(
            int x,
            int y,
            int width,
            int height,
            String placeholder,
            int placeholderAlignment
    ) {
        RoundedTextField field = new RoundedTextField(placeholder, placeholderAlignment);
        field.setBounds(x, y, width, height);
        return field;
    }

    private JLabel createLabel(
            String text,
            int x,
            int y,
            int width,
            int height,
            Font font,
            Color color,
            int alignment
    ) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(font);
        label.setForeground(color);
        label.setHorizontalAlignment(alignment);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(false);
        return label;
    }

    private static Font headerFont(int size, int style) {
        return new Font("Segoe UI", style, size);
    }

    private static Font textFont(int size, int style) {
        return new Font("Open Sans", style, size);
    }

    private static final class BorderedFormPanel extends JPanel {

        private BorderedFormPanel() {
            setLayout(null);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            g2.setColor(WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(FORM_BORDER);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g2.dispose();
        }
    }

    private static final class RoundedTextField extends JTextField {

        private final String placeholder;
        private final int placeholderAlignment;

        private RoundedTextField(String placeholder, int placeholderAlignment) {
            this.placeholder = placeholder;
            this.placeholderAlignment = placeholderAlignment;

            setOpaque(false);
            setFont(textFont(12, Font.PLAIN));
            setForeground(BLACK);
            setCaretColor(BLACK);
            setBorder(new EmptyBorder(0, 8, 1, 8));
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 6, 6));

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1.4f));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 6, 6));

            g2.dispose();

            super.paintComponent(g);

            if (placeholder != null && getText().isEmpty()) {
                Graphics2D placeholderGraphics = (Graphics2D) g.create();
                placeholderGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                placeholderGraphics.setFont(textFont(12, Font.PLAIN));
                placeholderGraphics.setColor(PLACEHOLDER);

                FontMetrics metrics = placeholderGraphics.getFontMetrics();

                int textX;
                if (placeholderAlignment == SwingConstants.CENTER) {
                    textX = (getWidth() - metrics.stringWidth(placeholder)) / 2;
                } else {
                    textX = 8;
                }

                int textY = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;
                placeholderGraphics.drawString(placeholder, textX, textY);
                placeholderGraphics.dispose();
            }
        }
    }

    private static final class PayrollActionButton extends JButton {

        private final String label;
        private final boolean hasDownloadIcon;

        private PayrollActionButton(String label, boolean hasDownloadIcon) {
            this.label = label;
            this.hasDownloadIcon = hasDownloadIcon;

            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(textFont(13, Font.PLAIN));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(WHITE);
            g2.setFont(textFont(13, Font.PLAIN));

            FontMetrics metrics = g2.getFontMetrics();
            int textY = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;

            if (hasDownloadIcon) {
                drawDownloadIcon(g2, 19, 22);
                g2.drawString(label, 41, textY);
            } else {
                int textX = (getWidth() - metrics.stringWidth(label)) / 2;
                g2.drawString(label, textX, textY);
            }

            g2.dispose();
        }

        private void drawDownloadIcon(Graphics2D g2, int centerX, int centerY) {
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.draw(new Line2D.Double(centerX, centerY - 9, centerX, centerY + 3));

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(centerX - 6, centerY - 2);
            arrow.lineTo(centerX, centerY + 4);
            arrow.lineTo(centerX + 6, centerY - 2);
            g2.draw(arrow);

            g2.draw(new Line2D.Double(centerX - 8, centerY + 9, centerX + 8, centerY + 9));
        }
    }

    private static final class AvatarCircle extends JComponent {

        private AvatarCircle() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillOval(0, 0, getWidth(), getHeight());

            g2.dispose();
        }
    }
}
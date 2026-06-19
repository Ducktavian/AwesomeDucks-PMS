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
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

public class HREPayslipListPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color WHITE = Color.WHITE;
    private static final Color NAVY = new Color(6, 20, 104);
    private static final Color TEXT_BLACK = new Color(15, 15, 15);
    private static final Color MUTED_GRAY = new Color(145, 145, 145);
    private static final Color INPUT_BORDER = new Color(220, 220, 220);
    private static final Color INPUT_TEXT = new Color(210, 210, 210);
    private static final Color ROW_GRAY = new Color(211, 211, 211);

    private static final String HEADER_FONT = "Segoe UI";
    private static final String TEXT_FONT = "Open Sans";

    public HREPayslipListPanel() {
        setLayout(null);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        add(createProfileArea());
        add(createSearchLabel());
        add(createDateField());
        add(createEmployeeDropdown());
        add(createRefreshButton());
        add(createPayslipTable());
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

    private JLabel createSearchLabel() {
        JLabel label = new JLabel("Search");
        label.setFont(new Font(HEADER_FONT, Font.PLAIN, 16));
        label.setForeground(Color.BLACK);
        label.setBounds(78, 84, 90, 24);
        return label;
    }

    private JComponent createDateField() {
        PlaceholderInput input = new PlaceholderInput("mm/dd/yyyy", true);
        input.setBounds(78, 107, 304, 39);
        return input;
    }

    private JComponent createEmployeeDropdown() {
        DropdownInput dropdown = new DropdownInput("Employee");
        dropdown.setBounds(78, 159, 107, 36);
        return dropdown;
    }

    private JComponent createRefreshButton() {
        ActionButton refreshButton = new ActionButton("Refresh");
        refreshButton.setBounds(855, 159, 88, 36);
        return refreshButton;
    }

    private JComponent createPayslipTable() {
        JPanel table = new JPanel(null);
        table.setOpaque(false);
        table.setBounds(78, 214, 865, 470);

        table.add(createHeader("Payslip ID", 18, 0, 80));
        table.add(createHeader("Employee ID", 126, 0, 90));
        table.add(createHeader("Start Date", 245, 0, 90));
        table.add(createHeader("End Date", 353, 0, 90));
        table.add(createHeader("Gross Pay", 449, 0, 90));
        table.add(createHeader("Deduction", 557, 0, 90));
        table.add(createHeader("Allowance", 666, 0, 90));
        table.add(createHeader("Net Pay", 770, 0, 80));

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        separator.setBackground(Color.BLACK);
        separator.setBounds(3, 39, 859, 3);
        table.add(separator);

        int[] rowYPositions = {96, 198, 308, 414};

        for (int y : rowYPositions) {
            JPanel row = new JPanel(null);
            row.setBackground(ROW_GRAY);
            row.setBounds(3, y, 862, 49);
            table.add(row);
        }

        return table;
    }

    private JLabel createHeader(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(TEXT_FONT, Font.BOLD, 11));
        label.setForeground(TEXT_BLACK);
        label.setBounds(x, y, width, 18);
        return label;
    }

    private static class ActionButton extends JButton {

        public ActionButton(String text) {
            setLayout(null);
            setText("");
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel label = new JLabel(text);
            label.setFont(new Font(TEXT_FONT, Font.PLAIN, 11));
            label.setForeground(Color.WHITE);
            label.setBounds(36, 9, 50, 17);
            add(label);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.6f));

            g2.draw(new Arc2D.Double(14, 11, 13, 13, 40, 290, Arc2D.OPEN));
            g2.drawLine(23, 10, 27, 10);
            g2.drawLine(27, 10, 27, 14);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PlaceholderInput extends JPanel {

        public PlaceholderInput(String placeholder, boolean hasCalendarIcon) {
            setLayout(null);
            setBackground(Color.WHITE);
            setBorder(new RoundedLineBorder(INPUT_BORDER, 1, 4));

            JLabel placeholderLabel = new JLabel(placeholder);
            placeholderLabel.setFont(new Font(TEXT_FONT, Font.PLAIN, 12));
            placeholderLabel.setForeground(INPUT_TEXT);
            placeholderLabel.setBounds(12, 8, 180, 22);
            add(placeholderLabel);

            if (hasCalendarIcon) {
                CalendarIcon calendarIcon = new CalendarIcon();
                calendarIcon.setBounds(270, 8, 22, 22);
                add(calendarIcon);
            }
        }
    }

    private static class DropdownInput extends JPanel {

        public DropdownInput(String text) {
            setLayout(null);
            setBackground(Color.WHITE);
            setBorder(new RoundedLineBorder(INPUT_BORDER, 1, 4));

            JLabel label = new JLabel(text);
            label.setFont(new Font(TEXT_FONT, Font.PLAIN, 12));
            label.setForeground(INPUT_TEXT);
            label.setBounds(10, 8, 68, 18);
            add(label);

            ArrowIcon arrow = new ArrowIcon();
            arrow.setBounds(82, 10, 15, 15);
            add(arrow);
        }
    }

    private static class CalendarIcon extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(INPUT_TEXT);
            g2.setStroke(new BasicStroke(1.2f));

            g2.drawRoundRect(2, 3, 16, 15, 2, 2);
            g2.drawLine(2, 7, 18, 7);
            g2.drawLine(6, 1, 6, 5);
            g2.drawLine(14, 1, 14, 5);

            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 3; col++) {
                    g2.fillRect(5 + col * 4, 10 + row * 4, 1, 1);
                }
            }

            g2.dispose();
        }
    }

    private static class ArrowIcon extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(INPUT_TEXT);
            g2.setStroke(new BasicStroke(1.5f));

            g2.drawLine(3, 5, 8, 10);
            g2.drawLine(8, 10, 13, 5);

            g2.dispose();
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
            return new Insets(4, 4, 4, 4);
        }

        @Override
        public Insets getBorderInsets(Component component, Insets insets) {
            insets.top = 4;
            insets.left = 4;
            insets.bottom = 4;
            insets.right = 4;
            return insets;
        }
    }
}
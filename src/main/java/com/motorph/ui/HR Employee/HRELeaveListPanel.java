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
import java.util.ArrayList;
import java.util.List;

public class HRELeaveListPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color WHITE = Color.WHITE;
    private static final Color NAVY = new Color(6, 20, 104);
    private static final Color TEXT_BLACK = new Color(15, 15, 15);
    private static final Color MUTED_GRAY = new Color(145, 145, 145);
    private static final Color INPUT_BORDER = new Color(220, 220, 220);
    private static final Color INPUT_TEXT = new Color(210, 210, 210);
    private static final Color ALT_ROW = new Color(211, 211, 211);

    private static final Color PENDING = new Color(246, 211, 72);
    private static final Color REJECTED = new Color(255, 91, 91);
    private static final Color APPROVED = new Color(12, 188, 89);

    private static final String HEADER_FONT = "Segoe UI";
    private static final String TEXT_FONT = "Open Sans";

    public HRELeaveListPanel() {
        setLayout(null);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        add(createProfileArea());
        add(createSearchLabel());
        add(createDateField());
        add(createLeaveDropdown());
        add(createEmployeeDropdown());
        add(createActionButtons());
        add(createLeaveTable());
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

    private JComponent createLeaveDropdown() {
        DropdownInput input = new DropdownInput("Leave");
        input.setBounds(78, 159, 107, 36);
        return input;
    }

    private JComponent createEmployeeDropdown() {
        DropdownInput input = new DropdownInput("Employee");
        input.setBounds(195, 159, 107, 36);
        return input;
    }

    private JComponent createActionButtons() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(762, 159, 181, 36);

        ActionButton fileButton = new ActionButton("File", true, false);
        fileButton.setBounds(0, 0, 87, 36);
        panel.add(fileButton);

        ActionButton refreshButton = new ActionButton("Refresh", false, true);
        refreshButton.setBounds(93, 0, 88, 36);
        panel.add(refreshButton);

        return panel;
    }

    private JComponent createLeaveTable() {
        JPanel table = new JPanel(null);
        table.setOpaque(false);
        table.setBounds(78, 214, 865, 420);

        table.add(createHeader("Name", 18, 0, 80));
        table.add(createHeader("Department", 114, 0, 100));
        table.add(createHeader("Date", 232, 0, 120));
        table.add(createHeader("Start Time", 362, 0, 90));
        table.add(createHeader("End Time", 466, 0, 90));
        table.add(createHeader("Reason", 558, 0, 100));
        table.add(createHeader("Notes", 669, 0, 70));
        table.add(createHeader("Status", 769, 0, 80));

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        separator.setBackground(Color.BLACK);
        separator.setBounds(3, 39, 859, 3);
        table.add(separator);

        List<LeaveRecord> records = new ArrayList<>();
        records.add(new LeaveRecord("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Pending"));
        records.add(new LeaveRecord("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Rejected"));
        records.add(new LeaveRecord("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Approved"));
        records.add(new LeaveRecord("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Pending"));
        records.add(new LeaveRecord("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Rejected"));
        records.add(new LeaveRecord("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Approved"));
        records.add(new LeaveRecord("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Pending"));

        int startY = 53;
        int rowHeight = 49;
        int gap = 4;

        for (int i = 0; i < records.size(); i++) {
            LeaveRow row = new LeaveRow(records.get(i), i % 2 == 1);
            row.setBounds(3, startY + i * (rowHeight + gap), 862, rowHeight);
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

    private static class LeaveRecord {
        private final String name;
        private final String department;
        private final String date;
        private final String startTime;
        private final String endTime;
        private final String reason;
        private final String notes;
        private final String status;

        public LeaveRecord(
                String name,
                String department,
                String date,
                String startTime,
                String endTime,
                String reason,
                String notes,
                String status
        ) {
            this.name = name;
            this.department = department;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.reason = reason;
            this.notes = notes;
            this.status = status;
        }
    }

    private static class LeaveRow extends JPanel {

        public LeaveRow(LeaveRecord record, boolean shaded) {
            setLayout(null);
            setOpaque(true);
            setBackground(shaded ? ALT_ROW : WHITE);

            add(createCell(record.name, 18, 14, 80));
            add(createCell(record.department, 114, 14, 80));
            add(createCell(record.date, 232, 14, 130));
            add(createCell(record.startTime, 362, 14, 80));
            add(createCell(record.endTime, 466, 14, 80));
            add(createCell(record.reason, 558, 14, 100));
            add(createCell(record.notes, 669, 14, 80));

            StatusPill pill = new StatusPill(record.status);
            pill.setBounds(756, 12, 62, 24);
            add(pill);
        }

        private JLabel createCell(String text, int x, int y, int width) {
            JLabel label = new JLabel(text);
            label.setFont(new Font(TEXT_FONT, Font.PLAIN, 11));
            label.setForeground(TEXT_BLACK);
            label.setBounds(x, y, width, 18);
            return label;
        }
    }

    private static class StatusPill extends JComponent {

        private final String status;

        public StatusPill(String status) {
            this.status = status;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if ("Pending".equalsIgnoreCase(status)) {
                g2.setColor(PENDING);
            } else if ("Rejected".equalsIgnoreCase(status)) {
                g2.setColor(REJECTED);
            } else {
                g2.setColor(APPROVED);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font(TEXT_FONT, Font.PLAIN, 10));

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(status);
            int x = (getWidth() - textWidth) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(status, x, y);
            g2.dispose();
        }
    }

    private static class ActionButton extends JButton {

        private final boolean hasPlusIcon;
        private final boolean hasRefreshIcon;

        public ActionButton(String text, boolean hasPlusIcon, boolean hasRefreshIcon) {
            this.hasPlusIcon = hasPlusIcon;
            this.hasRefreshIcon = hasRefreshIcon;

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

            if (hasPlusIcon) {
                label.setBounds(49, 9, 35, 17);
            } else if (hasRefreshIcon) {
                label.setBounds(36, 9, 50, 17);
            }

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

            if (hasPlusIcon) {
                int centerX = 25;
                int centerY = 18;
                g2.drawLine(centerX - 7, centerY, centerX + 7, centerY);
                g2.drawLine(centerX, centerY - 7, centerX, centerY + 7);
            }

            if (hasRefreshIcon) {
                g2.draw(new Arc2D.Double(14, 11, 13, 13, 40, 290, Arc2D.OPEN));
                g2.drawLine(23, 10, 27, 10);
                g2.drawLine(27, 10, 27, 14);
            }

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
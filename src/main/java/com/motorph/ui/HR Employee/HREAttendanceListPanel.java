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

public class HREAttendanceListPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color WHITE = Color.WHITE;
    private static final Color NAVY = new Color(5, 20, 110);
    private static final Color PROFILE_NAVY = new Color(8, 20, 111);
    private static final Color TEXT_BLACK = new Color(20, 20, 20);
    private static final Color MUTED_GRAY = new Color(145, 145, 145);
    private static final Color INPUT_BORDER = new Color(220, 220, 220);
    private static final Color INPUT_TEXT = new Color(210, 210, 210);
    private static final Color ALT_ROW = new Color(209, 209, 209);

    private static final Color PENDING = new Color(241, 207, 73);
    private static final Color REJECTED = new Color(255, 91, 91);
    private static final Color APPROVED = new Color(13, 189, 90);

    private static final String HEADER_FONT = "Segoe UI";
    private static final String TEXT_FONT = "Open Sans";

    public HREAttendanceListPanel() {
        setLayout(null);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        add(createProfileArea());
        add(createSearchTitle());
        add(createDateField());
        add(createEmployeeDropdown());
        add(createActionButtons());
        add(createTablePanel());
    }

    private JComponent createProfileArea() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(820, 40, 125, 60);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font(HEADER_FONT, Font.BOLD, 18));
        nameLabel.setForeground(PROFILE_NAVY);
        nameLabel.setBounds(0, 4, 65, 24);
        panel.add(nameLabel);

        JLabel positionLabel = new JLabel("Position");
        positionLabel.setFont(new Font(TEXT_FONT, Font.PLAIN, 16));
        positionLabel.setForeground(MUTED_GRAY);
        positionLabel.setBounds(0, 28, 80, 22);
        panel.add(positionLabel);

        JComponent avatar = new CircleAvatar();
        avatar.setBounds(66, 0, 57, 57);
        panel.add(avatar);

        return panel;
    }

    private JComponent createSearchTitle() {
        JLabel label = new JLabel("Search");
        label.setFont(new Font(HEADER_FONT, Font.PLAIN, 16));
        label.setForeground(Color.BLACK);
        label.setBounds(78, 84, 90, 24);
        return label;
    }

    private JComponent createDateField() {
        PlaceholderField field = new PlaceholderField("mm/dd/yyyy");
        field.setBounds(78, 109, 304, 38);
        field.setIcon(new CalendarIconLabel());
        return field;
    }

    private JComponent createEmployeeDropdown() {
        DropdownField dropdown = new DropdownField("Employee");
        dropdown.setBounds(78, 159, 107, 36);
        return dropdown;
    }

    private JComponent createActionButtons() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(668, 159, 275, 36);

        panel.add(new FlatBlueButton("Time In", false, 0, 0, 87, 36));
        panel.add(new FlatBlueButton("Time Out", false, 93, 0, 87, 36));
        panel.add(new FlatBlueButton("Refresh", true, 186, 0, 89, 36));

        return panel;
    }

    private JComponent createTablePanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(78, 206, 865, 440);

        panel.add(createHeaderLabel("Employee ID", 19, 0, 140));
        panel.add(createHeaderLabel("Date", 218, 0, 140));
        panel.add(createHeaderLabel("Time In", 425, 0, 100));
        panel.add(createHeaderLabel("Time Out", 590, 0, 100));
        panel.add(createHeaderLabel("Status", 772, 0, 80));

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        separator.setBackground(Color.BLACK);
        separator.setBounds(3, 39, 859, 3);
        panel.add(separator);

        List<AttendanceRecord> records = new ArrayList<>();
        records.add(new AttendanceRecord("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM", "Pending"));
        records.add(new AttendanceRecord("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM", "Rejected"));
        records.add(new AttendanceRecord("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM", "Approved"));
        records.add(new AttendanceRecord("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM", "Pending"));
        records.add(new AttendanceRecord("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM", "Rejected"));
        records.add(new AttendanceRecord("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM", "Approved"));
        records.add(new AttendanceRecord("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM", "Pending"));

        int startY = 53;
        int rowHeight = 49;
        int gap = 3;

        for (int i = 0; i < records.size(); i++) {
            boolean shaded = (i % 2 == 1);
            AttendanceRow row = new AttendanceRow(records.get(i), shaded);
            row.setBounds(3, startY + i * (rowHeight + gap), 862, rowHeight);
            panel.add(row);
        }

        return panel;
    }

    private JLabel createHeaderLabel(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(TEXT_FONT, Font.BOLD, 11));
        label.setForeground(TEXT_BLACK);
        label.setBounds(x, y, width, 18);
        return label;
    }

    private static class AttendanceRecord {
        private final String employeeId;
        private final String date;
        private final String timeIn;
        private final String timeOut;
        private final String status;

        public AttendanceRecord(String employeeId, String date, String timeIn, String timeOut, String status) {
            this.employeeId = employeeId;
            this.date = date;
            this.timeIn = timeIn;
            this.timeOut = timeOut;
            this.status = status;
        }
    }

    private static class AttendanceRow extends JPanel {

        public AttendanceRow(AttendanceRecord record, boolean shaded) {
            setLayout(null);
            setOpaque(true);
            setBackground(shaded ? ALT_ROW : WHITE);

            JLabel employeeLabel = createCell(record.employeeId, 19, 14, 160);
            JLabel dateLabel = createCell(record.date, 218, 14, 180);
            JLabel timeInLabel = createCell(record.timeIn, 425, 14, 100);
            JLabel timeOutLabel = createCell(record.timeOut, 590, 14, 100);

            add(employeeLabel);
            add(dateLabel);
            add(timeInLabel);
            add(timeOutLabel);

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

            Color bg;
            if ("Pending".equalsIgnoreCase(status)) {
                bg = PENDING;
            } else if ("Rejected".equalsIgnoreCase(status)) {
                bg = REJECTED;
            } else {
                bg = APPROVED;
            }

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

            g2.setFont(new Font(TEXT_FONT, Font.PLAIN, 10));
            g2.setColor(Color.WHITE);

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(status);
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(status, x, y);
            g2.dispose();
        }
    }

    private static class FlatBlueButton extends JPanel {
        public FlatBlueButton(String text, boolean withRefreshIcon, int x, int y, int width, int height) {
            setLayout(null);
            setBackground(NAVY);
            setBounds(x, y, width, height);

            if (withRefreshIcon) {
                RefreshIcon icon = new RefreshIcon();
                icon.setBounds(11, 9, 16, 16);
                add(icon);

                JLabel label = new JLabel(text);
                label.setFont(new Font(TEXT_FONT, Font.PLAIN, 11));
                label.setForeground(Color.WHITE);
                label.setBounds(31, 9, 45, 17);
                add(label);
            } else {
                JLabel label = new JLabel(text, SwingConstants.CENTER);
                label.setFont(new Font(TEXT_FONT, Font.PLAIN, 11));
                label.setForeground(Color.WHITE);
                label.setBounds(0, 0, width, height);
                add(label);
            }
        }
    }

    private static class RefreshIcon extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.4f));

            g2.draw(new Arc2D.Double(2, 2, 11, 11, 40, 290, Arc2D.OPEN));
            g2.drawLine(10, 1, 13, 1);
            g2.drawLine(13, 1, 13, 4);

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

    private static class PlaceholderField extends JPanel {
        private final JTextField textField;
        private JComponent icon;

        public PlaceholderField(String placeholder) {
            setLayout(null);
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(new RoundedLineBorder(INPUT_BORDER, 1, 4));

            textField = new JTextField();
            textField.setBorder(null);
            textField.setOpaque(false);
            textField.setFont(new Font(TEXT_FONT, Font.PLAIN, 12));
            textField.setForeground(INPUT_TEXT);
            textField.setText(placeholder);
            textField.setBounds(12, 8, 220, 22);
            add(textField);
        }

        public void setIcon(JComponent icon) {
            this.icon = icon;
            icon.setBounds(270, 8, 22, 22);
            add(icon);
        }
    }

    private static class DropdownField extends JPanel {
        public DropdownField(String text) {
            setLayout(null);
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(new RoundedLineBorder(INPUT_BORDER, 1, 4));

            JLabel label = new JLabel(text);
            label.setFont(new Font(TEXT_FONT, Font.PLAIN, 12));
            label.setForeground(INPUT_TEXT);
            label.setBounds(10, 8, 65, 18);
            add(label);

            ArrowDownIcon arrow = new ArrowDownIcon();
            arrow.setBounds(81, 9, 16, 16);
            add(arrow);
        }
    }

    private static class ArrowDownIcon extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(INPUT_TEXT);

            int[] x = {3, 8, 13};
            int[] y = {5, 10, 5};
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(x[0], y[0], x[1], y[1]);
            g2.drawLine(x[1], y[1], x[2], y[2]);

            g2.dispose();
        }
    }

    private static class CalendarIconLabel extends JComponent {
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
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            for (int i = 0; i < thickness; i++) {
                g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), radius, radius);
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 4;
            insets.right = 4;
            insets.top = 4;
            insets.bottom = 4;
            return insets;
        }
    }
}
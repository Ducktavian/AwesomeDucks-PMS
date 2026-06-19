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
import java.awt.geom.RoundRectangle2D;

public class FEAttendanceListPanel extends JPanel {

    private static final Color PAGE_BG = new Color(245, 245, 245);
    private static final Color NAVY = new Color(7, 24, 104);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color FIELD_BORDER = new Color(218, 218, 218);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color ROW_GRAY = new Color(216, 216, 216);

    private static final Font TEXT_FONT = new Font("Open Sans", Font.PLAIN, 12);
    private static final Font TEXT_FONT_13 = new Font("Open Sans", Font.PLAIN, 13);
    private static final Font TEXT_BOLD = new Font("Open Sans", Font.BOLD, 13);
    private static final Font PROFILE_NAME = new Font("Open Sans", Font.BOLD, 18);
    private static final Font PROFILE_POSITION = new Font("Open Sans", Font.PLAIN, 16);

    public FEAttendanceListPanel() {
        setLayout(null);
        setBackground(PAGE_BG);
        setPreferredSize(new Dimension(1023, 800));

        addTopRightProfile();
        addSearchArea();
        addActionButtons();
        addAttendanceTable();
    }

    private void addTopRightProfile() {
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(825, 48, 70, 22);
        nameLabel.setFont(PROFILE_NAME);
        nameLabel.setForeground(NAVY);
        add(nameLabel);

        JLabel positionLabel = new JLabel("Position");
        positionLabel.setBounds(820, 73, 80, 20);
        positionLabel.setFont(PROFILE_POSITION);
        positionLabel.setForeground(MUTED_TEXT);
        add(positionLabel);

        AvatarCircle avatar = new AvatarCircle();
        avatar.setBounds(887, 40, 56, 56);
        add(avatar);
    }

    private void addSearchArea() {
        JLabel searchLabel = new JLabel("Search");
        searchLabel.setBounds(79, 84, 100, 22);
        searchLabel.setFont(new Font("Open Sans", Font.PLAIN, 18));
        searchLabel.setForeground(BLACK);
        add(searchLabel);

        DateSearchField dateField = new DateSearchField("mm/dd/yyyy");
        dateField.setBounds(79, 106, 304, 40);
        add(dateField);

        JComboBox<String> employeeDropdown = new JComboBox<>(new String[]{"Employee"});
        employeeDropdown.setBounds(79, 159, 107, 36);
        employeeDropdown.setFont(TEXT_FONT_13);
        employeeDropdown.setForeground(PLACEHOLDER);
        employeeDropdown.setBackground(WHITE);
        employeeDropdown.setFocusable(false);
        employeeDropdown.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(employeeDropdown);
    }

    private void addActionButtons() {
        FlatNavyButton timeInButton = new FlatNavyButton("Time In");
        timeInButton.setBounds(668, 159, 89, 36);
        add(timeInButton);

        FlatNavyButton timeOutButton = new FlatNavyButton("Time Out");
        timeOutButton.setBounds(762, 159, 88, 36);
        add(timeOutButton);

        RefreshButton refreshButton = new RefreshButton();
        refreshButton.setBounds(855, 159, 88, 36);
        add(refreshButton);
    }

    private void addAttendanceTable() {
        AttendanceTable table = new AttendanceTable();
        table.setBounds(79, 218, 864, 395);
        add(table);
    }

    private static class AttendanceTable extends JComponent {

        private final String[] headers = {
                "Employee ID", "Date", "Time In", "Time Out", "Status"
        };

        private final AttendanceRow[] rows = {
                new AttendanceRow("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM", "Pending"),
                new AttendanceRow("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM", "Rejected"),
                new AttendanceRow("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM", "Approved"),
                new AttendanceRow("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM", "Pending"),
                new AttendanceRow("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM", "Rejected"),
                new AttendanceRow("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM", "Approved"),
                new AttendanceRow("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM", "Pending")
        };

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);

            drawHeader(g2);
            drawRows(g2);

            g2.dispose();
        }

        private void drawHeader(Graphics2D g2) {
            g2.setFont(TEXT_BOLD);
            g2.setColor(BLACK);

            g2.drawString(headers[0], 18, 12);
            g2.drawString(headers[1], 217, 12);
            g2.drawString(headers[2], 424, 12);
            g2.drawString(headers[3], 588, 12);
            g2.drawString(headers[4], 772, 12);

            g2.setColor(BLACK);
            g2.fillRect(2, 35, 860, 4);
        }

        private void drawRows(Graphics2D g2) {
            int startY = 52;
            int rowHeight = 53;

            for (int i = 0; i < rows.length; i++) {
                int y = startY + (i * rowHeight);

                if (i % 2 == 1) {
                    g2.setColor(ROW_GRAY);
                    g2.fillRect(2, y - 13, 862, 50);
                }

                g2.setFont(TEXT_FONT);
                g2.setColor(BLACK);

                AttendanceRow row = rows[i];

                g2.drawString(row.employeeId, 18, y + 13);
                g2.drawString(row.date, 217, y + 13);
                g2.drawString(row.timeIn, 424, y + 13);
                g2.drawString(row.timeOut, 588, y + 13);

                drawStatusPill(g2, row.status, 758, y - 3);
            }
        }

        private void drawStatusPill(Graphics2D g2, String status, int x, int y) {
            Color pillColor;

            switch (status) {
                case "Approved":
                    pillColor = new Color(18, 190, 98);
                    break;
                case "Rejected":
                    pillColor = new Color(255, 88, 88);
                    break;
                default:
                    pillColor = new Color(255, 214, 73);
                    break;
            }

            g2.setColor(pillColor);
            g2.fillRoundRect(x, y, 62, 25, 22, 22);

            g2.setFont(new Font("Open Sans", Font.PLAIN, 10));
            g2.setColor(WHITE);

            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (62 - fm.stringWidth(status)) / 2;
            int textY = y + ((25 - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(status, textX, textY);
        }
    }

    private static class AttendanceRow {
        private final String employeeId;
        private final String date;
        private final String timeIn;
        private final String timeOut;
        private final String status;

        private AttendanceRow(String employeeId, String date, String timeIn, String timeOut, String status) {
            this.employeeId = employeeId;
            this.date = date;
            this.timeIn = timeIn;
            this.timeOut = timeOut;
            this.status = status;
        }
    }

    private static class DateSearchField extends JTextField {

        private final String placeholder;

        private DateSearchField(String placeholder) {
            this.placeholder = placeholder;

            setOpaque(false);
            setBorder(new EmptyBorder(0, 12, 0, 42));
            setFont(new Font("Open Sans", Font.PLAIN, 13));
            setForeground(BLACK);
            setCaretColor(BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(FIELD_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            drawCalendarIcon(g2);

            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty()) {
                Graphics2D hintGraphics = createGraphics(g);
                hintGraphics.setFont(getFont());
                hintGraphics.setColor(PLACEHOLDER);

                FontMetrics fm = hintGraphics.getFontMetrics();
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                hintGraphics.drawString(placeholder, 12, textY);
                hintGraphics.dispose();
            }
        }

        private void drawCalendarIcon(Graphics2D g2) {
            int iconX = getWidth() - 32;
            int iconY = 11;
            int size = 21;

            g2.setColor(new Color(210, 210, 210));
            g2.drawRoundRect(iconX, iconY, size, size, 3, 3);

            g2.drawLine(iconX, iconY + 6, iconX + size, iconY + 6);
            g2.fillRect(iconX + 5, iconY - 2, 3, 6);
            g2.fillRect(iconX + 13, iconY - 2, 3, 6);

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    g2.fillRect(iconX + 5 + (col * 5), iconY + 10 + (row * 4), 2, 2);
                }
            }
        }
    }

    private static class FlatNavyButton extends JButton {

        private FlatNavyButton(String text) {
            super(text);
            setFont(new Font("Open Sans", Font.PLAIN, 12));
            setForeground(WHITE);
            setBackground(NAVY);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = createGraphics(g);
            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class RefreshButton extends JButton {

        private RefreshButton() {
            super("Refresh");
            setFont(new Font("Open Sans", Font.PLAIN, 12));
            setForeground(WHITE);
            setBackground(NAVY);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setHorizontalTextPosition(SwingConstants.RIGHT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            drawRefreshIcon(g2);

            g2.dispose();

            super.paintComponent(g);
        }

        private void drawRefreshIcon(Graphics2D g2) {
            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(1.2f));

            int x = 16;
            int y = 13;
            int size = 13;

            g2.drawArc(x, y, size, size, 40, 285);

            Polygon arrow = new Polygon();
            arrow.addPoint(x + 11, y);
            arrow.addPoint(x + 16, y + 2);
            arrow.addPoint(x + 12, y + 5);
            g2.fillPolygon(arrow);
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
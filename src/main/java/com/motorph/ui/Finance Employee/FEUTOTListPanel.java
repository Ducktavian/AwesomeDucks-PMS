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

public class FEUTOTListPanel extends JPanel {

    private static final Color PAGE_BG = new Color(245, 245, 245);
    private static final Color NAVY = new Color(7, 24, 104);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color FIELD_BORDER = new Color(218, 218, 218);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color ROW_GRAY = new Color(216, 216, 216);

    private static final Font SEARCH_FONT = new Font("Open Sans", Font.PLAIN, 18);
    private static final Font TEXT_FONT = new Font("Open Sans", Font.PLAIN, 12);
    private static final Font TEXT_FONT_13 = new Font("Open Sans", Font.PLAIN, 13);
    private static final Font HEADER_FONT = new Font("Open Sans", Font.BOLD, 13);
    private static final Font PROFILE_NAME_FONT = new Font("Open Sans", Font.BOLD, 18);
    private static final Font PROFILE_POSITION_FONT = new Font("Open Sans", Font.PLAIN, 16);

    public FEUTOTListPanel() {
        setLayout(null);
        setBackground(PAGE_BG);
        setPreferredSize(new Dimension(1023, 800));

        addTopRightProfile();
        addSearchArea();
        addActionButtons();
        addRequestTable();
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

    private void addSearchArea() {
        JLabel searchLabel = new JLabel("Search");
        searchLabel.setBounds(79, 84, 100, 22);
        searchLabel.setFont(SEARCH_FONT);
        searchLabel.setForeground(BLACK);
        add(searchLabel);

        DateSearchField dateField = new DateSearchField("mm/dd/yyyy");
        dateField.setBounds(79, 106, 304, 40);
        add(dateField);

        JComboBox<String> typeDropdown = createLightDropdown("UT/OT");
        typeDropdown.setBounds(79, 159, 107, 36);
        add(typeDropdown);

        JComboBox<String> employeeDropdown = createLightDropdown("Employee");
        employeeDropdown.setBounds(195, 159, 107, 36);
        add(employeeDropdown);
    }

    private JComboBox<String> createLightDropdown(String value) {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{value});
        comboBox.setFont(TEXT_FONT_13);
        comboBox.setForeground(PLACEHOLDER);
        comboBox.setBackground(WHITE);
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        return comboBox;
    }

    private void addActionButtons() {
        FileButton fileButton = new FileButton();
        fileButton.setBounds(762, 159, 88, 36);
        add(fileButton);

        RefreshButton refreshButton = new RefreshButton();
        refreshButton.setBounds(855, 159, 88, 36);
        add(refreshButton);
    }

    private void addRequestTable() {
        UTOTTable table = new UTOTTable();
        table.setBounds(79, 218, 864, 405);
        add(table);
    }

    private static class UTOTTable extends JComponent {

        private final String[] headers = {
                "Name", "Department", "Date", "Start Time",
                "End Time", "Reason", "Type", "Status"
        };

        private final RequestRow[] rows = {
                new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "Undertime", "Pending"),
                new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "Overtime", "Rejected"),
                new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "Undertime", "Approved"),
                new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "Undertime", "Pending"),
                new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "Undertime", "Rejected"),
                new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "Overtime", "Approved"),
                new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "Overtime", "Pending")
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
            g2.setFont(HEADER_FONT);
            g2.setColor(BLACK);

            g2.drawString(headers[0], 18, 12);
            g2.drawString(headers[1], 115, 12);
            g2.drawString(headers[2], 233, 12);
            g2.drawString(headers[3], 363, 12);
            g2.drawString(headers[4], 466, 12);
            g2.drawString(headers[5], 559, 12);
            g2.drawString(headers[6], 687, 12);
            g2.drawString(headers[7], 772, 12);

            g2.fillRect(2, 35, 860, 4);
        }

        private void drawRows(Graphics2D g2) {
            int startY = 52;
            int rowHeight = 53;

            for (int i = 0; i < rows.length; i++) {
                int y = startY + (i * rowHeight);

                if (i % 2 == 1) {
                    g2.setColor(ROW_GRAY);
                    g2.fillRect(2, y - 13, 860, 50);
                }

                RequestRow row = rows[i];

                g2.setFont(TEXT_FONT);
                g2.setColor(BLACK);

                g2.drawString(row.name, 18, y + 13);
                g2.drawString(row.department, 115, y + 13);
                g2.drawString(row.date, 233, y + 13);
                g2.drawString(row.startTime, 363, y + 13);
                g2.drawString(row.endTime, 466, y + 13);
                g2.drawString(row.reason, 559, y + 13);
                g2.drawString(row.type, 670, y + 13);

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

    private static class RequestRow {
        private final String name;
        private final String department;
        private final String date;
        private final String startTime;
        private final String endTime;
        private final String reason;
        private final String type;
        private final String status;

        private RequestRow(
                String name,
                String department,
                String date,
                String startTime,
                String endTime,
                String reason,
                String type,
                String status
        ) {
            this.name = name;
            this.department = department;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.reason = reason;
            this.type = type;
            this.status = status;
        }
    }

    private static class DateSearchField extends JTextField {

        private final String placeholder;

        private DateSearchField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(0, 12, 0, 42));
            setFont(TEXT_FONT_13);
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

    private static class FileButton extends JButton {

        private FileButton() {
            super("File");
            setFont(new Font("Open Sans", Font.PLAIN, 12));
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

            drawPlusIcon(g2);

            g2.dispose();

            super.paintComponent(g);
        }

        private void drawPlusIcon(Graphics2D g2) {
            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(1.8f));

            int centerX = 25;
            int centerY = 18;

            g2.drawLine(centerX - 7, centerY, centerX + 7, centerY);
            g2.drawLine(centerX, centerY - 7, centerX, centerY + 7);
        }
    }

    private static class RefreshButton extends JButton {

        private RefreshButton() {
            super("Refresh");
            setFont(new Font("Open Sans", Font.PLAIN, 12));
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
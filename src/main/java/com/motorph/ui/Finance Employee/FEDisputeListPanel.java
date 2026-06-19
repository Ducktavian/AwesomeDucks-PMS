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

public class FEDisputeListPanel extends JPanel {

    private static final Color PAGE_BG = new Color(245, 245, 245);
    private static final Color NAVY = new Color(7, 24, 104);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color FIELD_BORDER = new Color(218, 218, 218);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color ROW_GRAY = new Color(216, 216, 216);

    private static final Font SEARCH_FONT = new Font("Open Sans", Font.PLAIN, 18);
    private static final Font TEXT_FONT_13 = new Font("Open Sans", Font.PLAIN, 13);
    private static final Font HEADER_FONT = new Font("Open Sans", Font.BOLD, 13);
    private static final Font STATUS_FONT = new Font("Open Sans", Font.PLAIN, 10);
    private static final Font PROFILE_NAME_FONT = new Font("Open Sans", Font.BOLD, 18);
    private static final Font PROFILE_POSITION_FONT = new Font("Open Sans", Font.PLAIN, 16);

    public FEDisputeListPanel() {
        setLayout(null);
        setBackground(PAGE_BG);
        setPreferredSize(new Dimension(1023, 800));

        addTopRightProfile();
        addSearchArea();
        addRefreshButton();
        addDisputeTable();
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

        LightDropdown employeeDropdown = new LightDropdown("Employee");
        employeeDropdown.setBounds(79, 159, 107, 36);
        add(employeeDropdown);
    }

    private void addRefreshButton() {
        RefreshButton refreshButton = new RefreshButton();
        refreshButton.setBounds(855, 159, 88, 36);
        add(refreshButton);
    }

    private void addDisputeTable() {
        DisputeTable table = new DisputeTable();
        table.setBounds(79, 218, 864, 470);
        add(table);
    }

    private static class DisputeTable extends JComponent {

        private final String[] statuses = {
                "Pending",
                "Resolved",
                "Resolved",
                "Pending",
                "Resolved",
                "Resolved",
                "Pending",
                "Pending"
        };

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);

            drawHeader(g2);
            drawRows(g2);
            drawStatusPills(g2);

            g2.dispose();
        }

        private void drawHeader(Graphics2D g2) {
            g2.setFont(HEADER_FONT);
            g2.setColor(BLACK);

            g2.drawString("Ticket ID", 18, 12);
            g2.drawString("Employee Name", 162, 12);
            g2.drawString("Date", 305, 12);
            g2.drawString("Department", 449, 12);
            g2.drawString("Description", 592, 12);
            g2.drawString("Status", 737, 12);

            g2.fillRect(2, 35, 860, 4);
        }

        private void drawRows(Graphics2D g2) {
            g2.setColor(ROW_GRAY);

            g2.fillRect(2, 92, 862, 49);
            g2.fillRect(2, 194, 862, 57);
            g2.fillRect(2, 303, 862, 50);
            g2.fillRect(2, 410, 862, 49);
        }

        private void drawStatusPills(Graphics2D g2) {
            int[] statusYPositions = {
                    49, 103, 154, 207, 260, 313, 364, 417
            };

            for (int i = 0; i < statuses.length; i++) {
                drawStatusPill(g2, statuses[i], 735, statusYPositions[i]);
            }
        }

        private void drawStatusPill(Graphics2D g2, String status, int x, int y) {
            Color pillColor;

            if ("Resolved".equals(status)) {
                pillColor = new Color(18, 190, 98);
            } else {
                pillColor = new Color(255, 214, 73);
            }

            g2.setColor(pillColor);
            g2.fillRoundRect(x, y, 62, 25, 22, 22);

            g2.setFont(STATUS_FONT);
            g2.setColor(WHITE);

            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (62 - fm.stringWidth(status)) / 2;
            int textY = y + ((25 - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(status, textX, textY);
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
                    g2.fillRect(
                            iconX + 5 + (col * 5),
                            iconY + 10 + (row * 4),
                            2,
                            2
                    );
                }
            }
        }
    }

    private static class LightDropdown extends JComponent {

        private final String text;

        private LightDropdown(String text) {
            this.text = text;
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);

            g2.setColor(WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(230, 230, 230));
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g2.setFont(TEXT_FONT_13);
            g2.setColor(PLACEHOLDER);

            FontMetrics fm = g2.getFontMetrics();
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(text, 11, textY);

            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(getWidth() - 24, 14, getWidth() - 17, 21);
            g2.drawLine(getWidth() - 17, 21, getWidth() - 10, 14);

            g2.dispose();
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

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        return g2;
    }
}
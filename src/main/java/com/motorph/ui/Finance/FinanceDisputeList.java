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
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;

public final class FinanceDisputeList extends JPanel {

    private static final int PAGE_WIDTH = 1023;
    private static final int PAGE_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color BORDER = new Color(214, 214, 214);
    private static final Color LIGHT_BORDER = new Color(232, 232, 232);
    private static final Color PLACEHOLDER = new Color(207, 207, 207);
    private static final Color TABLE_ROW_GRAY = new Color(217, 217, 217);
    private static final Color PENDING_YELLOW = new Color(255, 219, 76);
    private static final Color RESOLVED_GREEN = new Color(0, 191, 99);

    public FinanceDisputeList() {
        setLayout(null);
        setOpaque(true);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));

        buildSearchField();
        buildProfileHeader();
        buildRoleDropdown();
        buildRefreshButton();
        buildTableHeader();
        buildTableRows();
        buildStatusPills();
    }

    private void buildSearchField() {
        SearchField searchField = new SearchField();
        searchField.setBounds(79, 98, 304, 38);
        add(searchField);
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

    private void buildRoleDropdown() {
        RoleDropdown dropdown = new RoleDropdown();
        dropdown.setBounds(79, 159, 107, 36);
        add(dropdown);
    }

    private void buildRefreshButton() {
        RefreshButton refreshButton = new RefreshButton();
        refreshButton.setBounds(855, 159, 88, 36);
        add(refreshButton);
    }

    private void buildTableHeader() {
        add(createLabel("Ticket ID", 82, 216, 96, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Employee Name", 220, 216, 142, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Date", 373, 216, 56, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Department", 502, 216, 126, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Description", 649, 216, 114, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Status", 788, 216, 96, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));

        JPanel divider = new JPanel();
        divider.setBackground(BLACK);
        divider.setBounds(81, 253, 859, 4);
        add(divider);
    }

    private void buildTableRows() {
        addRowPlaceholder(81, 310, 862, 49);
        addRowPlaceholder(81, 412, 862, 57);
        addRowPlaceholder(81, 521, 862, 50);
        addRowPlaceholder(81, 628, 862, 49);
    }

    private void buildStatusPills() {
        addStatusPill("Pending", 814, 267, PENDING_YELLOW);
        addStatusPill("Resolved", 814, 322, RESOLVED_GREEN);
        addStatusPill("Resolved", 814, 373, RESOLVED_GREEN);
        addStatusPill("Pending", 814, 425, PENDING_YELLOW);
        addStatusPill("Resolved", 814, 480, RESOLVED_GREEN);
        addStatusPill("Resolved", 814, 535, RESOLVED_GREEN);
        addStatusPill("Pending", 814, 584, PENDING_YELLOW);
        addStatusPill("Pending", 814, 638, PENDING_YELLOW);
    }

    private void addRowPlaceholder(int x, int y, int width, int height) {
        JPanel row = new JPanel();
        row.setOpaque(true);
        row.setBackground(TABLE_ROW_GRAY);
        row.setBounds(x, y, width, height);
        add(row);
    }

    private void addStatusPill(String text, int x, int y, Color color) {
        StatusPill pill = new StatusPill(text, color);
        pill.setBounds(x, y, 62, 25);
        add(pill);
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

    private static final class SearchField extends JTextField {

        private SearchField() {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 37, 0, 10));
            setFont(textFont(20, Font.PLAIN));
            setForeground(BLACK);
            setCaretColor(BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 6, 6));

            g2.setColor(BORDER);
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 6, 6));

            drawSearchIcon(g2);
            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty()) {
                Graphics2D placeholderGraphics = (Graphics2D) g.create();
                placeholderGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                placeholderGraphics.setFont(textFont(20, Font.PLAIN));
                placeholderGraphics.setColor(PLACEHOLDER);
                placeholderGraphics.drawString("Search", 37, 26);
                placeholderGraphics.dispose();
            }
        }

        private void drawSearchIcon(Graphics2D g2) {
            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.drawOval(12, 11, 13, 13);
            g2.drawLine(23, 23, 30, 30);
        }
    }

    private static final class RoleDropdown extends JComponent {

        private RoleDropdown() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(LIGHT_BORDER);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g2.setFont(textFont(13, Font.PLAIN));
            g2.setColor(PLACEHOLDER);
            g2.drawString("Finance", 11, 23);

            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(83, 14, 90, 21);
            g2.drawLine(97, 14, 90, 21);

            g2.dispose();
        }
    }

    private static final class RefreshButton extends JButton {

        private RefreshButton() {
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
            g2.setStroke(new BasicStroke(1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawRefreshIcon(g2, 21, 18);

            g2.setFont(textFont(13, Font.PLAIN));
            FontMetrics metrics = g2.getFontMetrics();
            int textY = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;
            g2.drawString("Refresh", 38, textY);

            g2.dispose();
        }

        private void drawRefreshIcon(Graphics2D g2, int centerX, int centerY) {
            g2.draw(new Arc2D.Double(centerX - 7, centerY - 7, 14, 14, 40, 285, Arc2D.OPEN));
            g2.drawLine(centerX + 5, centerY - 8, centerX + 9, centerY - 8);
            g2.drawLine(centerX + 9, centerY - 8, centerX + 8, centerY - 4);
        }
    }

    private static final class StatusPill extends JComponent {

        private final String text;
        private final Color pillColor;

        private StatusPill(String text, Color pillColor) {
            this.text = text;
            this.pillColor = pillColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(pillColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 22, 22));

            g2.setColor(WHITE);
            g2.setFont(textFont(10, Font.PLAIN));

            FontMetrics metrics = g2.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(text)) / 2;
            int textY = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;

            g2.drawString(text, textX, textY);

            g2.dispose();
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
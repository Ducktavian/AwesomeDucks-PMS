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
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.*;

public class HRLeavePanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color ROW_GRAY = new Color(217, 217, 217);
    private static final Color LIGHT_BORDER = new Color(220, 220, 220);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color MUTED_TEXT = new Color(150, 150, 150);

    private static final Color PENDING_YELLOW = new Color(255, 213, 79);
    private static final Color REJECTED_RED = new Color(255, 82, 82);
    private static final Color APPROVED_GREEN = new Color(0, 194, 113);

    public HRLeavePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(WHITE);

        add(new TopProfilePanel());

        SearchBox searchBox = new SearchBox();
        searchBox.setBounds(79, 97, 303, 39);
        add(searchBox);

        DropdownPanel leaveDropdown = new DropdownPanel("Leave");
        leaveDropdown.setBounds(78, 159, 108, 36);
        add(leaveDropdown);

        DropdownPanel roleDropdown = new DropdownPanel("HR");
        roleDropdown.setBounds(195, 159, 108, 36);
        add(roleDropdown);

        add(new ActionButton("Add", ActionIcon.ADD, 576, 159));
        add(new ActionButton("Update", ActionIcon.UPDATE, 669, 159));
        add(new ActionButton("Delete", ActionIcon.DELETE, 762, 159));
        add(new ActionButton("Refresh", ActionIcon.REFRESH, 855, 159));

        LeaveTablePanel table = new LeaveTablePanel();
        table.setBounds(78, 223, 865, 430);
        add(table);
    }

    private static class TopProfilePanel extends JPanel {

        public TopProfilePanel() {
            setBounds(817, 40, 126, 58);
            setLayout(null);
            setOpaque(false);

            JLabel name = label("Name", 18, Font.BOLD, NAVY);
            name.setHorizontalAlignment(SwingConstants.RIGHT);
            name.setBounds(0, 6, 62, 22);
            add(name);

            JLabel position = label("Position", 16, Font.PLAIN, MUTED_TEXT);
            position.setHorizontalAlignment(SwingConstants.RIGHT);
            position.setBounds(0, 31, 62, 20);
            add(position);

            ProfileCircle circle = new ProfileCircle();
            circle.setBounds(69, 0, 56, 56);
            add(circle);
        }
    }

    private static class ProfileCircle extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillOval(0, 0, 56, 56);

            g2.dispose();
        }
    }

    private static class SearchBox extends JPanel {

        public SearchBox() {
            setLayout(null);
            setBackground(WHITE);
            setBorder(new LineBorder(LIGHT_BORDER, 1));

            JLabel text = label("Search", 20, Font.PLAIN, PLACEHOLDER);
            text.setBounds(36, 6, 120, 26);
            add(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(11, 10, 13, 13);
            g2.drawLine(22, 21, 29, 28);

            g2.dispose();
        }
    }

    private static class DropdownPanel extends JPanel {

        public DropdownPanel(String text) {
            setLayout(null);
            setBackground(WHITE);
            setBorder(new LineBorder(new Color(238, 238, 238), 1));

            JLabel label = label(text, 13, Font.PLAIN, PLACEHOLDER);
            label.setBounds(11, 8, 60, 18);
            add(label);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(2f));

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(84, 15);
            arrow.lineTo(91, 22);
            arrow.lineTo(98, 15);
            g2.draw(arrow);

            g2.dispose();
        }
    }

    private enum ActionIcon {
        ADD,
        UPDATE,
        DELETE,
        REFRESH
    }

    private static class ActionButton extends JPanel {

        private final String text;
        private final ActionIcon icon;

        public ActionButton(String text, ActionIcon icon, int x, int y) {
            this.text = text;
            this.icon = icon;

            setBounds(x, y, 88, 36);
            setLayout(null);
            setBackground(NAVY);

            JLabel textLabel = label(text, 14, Font.PLAIN, WHITE);

            if (text.equals("Add")) {
                textLabel.setBounds(48, 8, 38, 18);
            } else if (text.equals("Update")) {
                textLabel.setBounds(38, 8, 50, 18);
            } else if (text.equals("Delete")) {
                textLabel.setBounds(41, 8, 48, 18);
            } else {
                textLabel.setBounds(37, 8, 55, 18);
            }

            add(textLabel);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (icon) {
                case ADD:
                    drawAdd(g2);
                    break;
                case UPDATE:
                    drawUpdate(g2);
                    break;
                case DELETE:
                    drawDelete(g2);
                    break;
                case REFRESH:
                    drawRefresh(g2);
                    break;
            }

            g2.dispose();
        }

        private void drawAdd(Graphics2D g2) {
            g2.drawLine(25, 12, 25, 25);
            g2.drawLine(18, 18, 32, 18);
        }

        private void drawUpdate(Graphics2D g2) {
            Path2D pencil = new Path2D.Double();
            pencil.moveTo(15, 24);
            pencil.lineTo(17, 18);
            pencil.lineTo(28, 7);
            pencil.lineTo(32, 11);
            pencil.lineTo(21, 22);
            pencil.closePath();

            g2.draw(pencil);
            g2.drawLine(25, 10, 29, 14);
            g2.drawLine(15, 24, 21, 22);
        }

        private void drawDelete(Graphics2D g2) {
            g2.drawRect(19, 13, 13, 14);
            g2.drawLine(18, 11, 33, 11);
            g2.drawLine(22, 8, 29, 8);
            g2.drawLine(23, 16, 23, 25);
            g2.drawLine(26, 16, 26, 25);
            g2.drawLine(29, 16, 29, 25);
        }

        private void drawRefresh(Graphics2D g2) {
            Arc2D arc = new Arc2D.Double(17, 10, 15, 15, 40, 280, Arc2D.OPEN);
            g2.draw(arc);

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(30, 8);
            arrow.lineTo(34, 8);
            arrow.lineTo(33, 12);
            g2.draw(arrow);
        }
    }

    private static class LeaveTablePanel extends JPanel {

        private final String[][] rows = {
                {"Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", "Pending"},
                {"Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", "Rejected"},
                {"Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", "Approved"},
                {"Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", "Rejected"},
                {"Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", "Pending"},
                {"Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", "Rejected"},
                {"Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", "Approved"}
        };

        public LeaveTablePanel() {
            setLayout(null);
            setBackground(WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawHeader(g2);
            drawRows(g2);

            g2.dispose();
        }

        private void drawHeader(Graphics2D g2) {
            g2.setColor(BLACK);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));

            g2.drawString("Name", 20, 10);
            g2.drawString("Department", 130, 10);
            g2.drawString("Start Date", 250, 10);
            g2.drawString("End Date", 388, 10);
            g2.drawString("Reason", 524, 10);
            g2.drawString("Notes", 644, 10);
            g2.drawString("Status", 764, 10);

            g2.fillRect(3, 37, 859, 3);
        }

        private void drawRows(Graphics2D g2) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            int startY = 40;
            int rowHeight = 54;

            for (int i = 0; i < rows.length; i++) {
                int rowY = startY + (i * rowHeight);

                if (i % 2 == 1) {
                    g2.setColor(ROW_GRAY);
                    g2.fillRect(3, rowY, 859, 55);
                }

                g2.setColor(BLACK);

                int textY = rowY + 34;

                g2.drawString(rows[i][0], 20, textY);
                g2.drawString(rows[i][1], 130, textY);
                g2.drawString(rows[i][2], 250, textY);
                g2.drawString(rows[i][3], 388, textY);
                g2.drawString(rows[i][4], 524, textY);
                g2.drawString(rows[i][5], 644, textY);

                drawStatusBadge(g2, rows[i][6], 759, rowY + 16);
            }
        }

        private void drawStatusBadge(Graphics2D g2, String status, int x, int y) {
            Color badgeColor;

            if (status.equals("Pending")) {
                badgeColor = PENDING_YELLOW;
            } else if (status.equals("Rejected")) {
                badgeColor = REJECTED_RED;
            } else {
                badgeColor = APPROVED_GREEN;
            }

            g2.setColor(badgeColor);
            g2.fillRoundRect(x, y, 62, 25, 25, 25);

            g2.setColor(WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(status);
            int textX = x + ((62 - textWidth) / 2);
            int textY = y + 16;

            g2.drawString(status, textX, textY);
        }
    }

    private static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        label.setOpaque(false);
        return label;
    }
}
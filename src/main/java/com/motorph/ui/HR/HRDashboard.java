/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui;

/**
 *
 * @author Dennise
 */

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class HRDashboard extends JFrame {

    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color ROW_GRAY = new Color(217, 217, 217);
    private static final Color LIGHT_BORDER = new Color(234, 234, 234);
    private static final Color MUTED_TEXT = new Color(150, 150, 150);

    private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 15);

    public HRDashboard() {
        setTitle("MotorPH HR Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        DashboardRootPanel root = new DashboardRootPanel();
        setContentPane(root);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HRDashboard dashboard = new HRDashboard();
            dashboard.setVisible(true);
        });
    }

    private static class DashboardRootPanel extends JPanel {

        public DashboardRootPanel() {
            setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            setLayout(null);
            setBackground(WHITE);

            add(new SidebarPanel());
            add(new TopProfilePanel());
            add(new RoleDropdownPanel());

            add(new StatCardPanel(
                    "Total Number of Employees",
                    "1,001",
                    "Employees",
                    315,
                    158,
                    278,
                    136,
                    false
            ));

            add(new StatCardPanel(
                    "Leave Request",
                    "101",
                    "Pending Leave\nRequest",
                    618,
                    158,
                    279,
                    136,
                    true
            ));

            add(new StatCardPanel(
                    "Overtime Request",
                    "201",
                    "Pending Overtime\nRequest",
                    921,
                    158,
                    279,
                    136,
                    true
            ));

            JLabel leaveTitle = label("On Leave Today", 16, Font.BOLD, BLACK);
            leaveTitle.setBounds(315, 329, 180, 24);
            add(leaveTitle);

            JLabel overtimeTitle = label("On Overtime Today", 16, Font.BOLD, BLACK);
            overtimeTitle.setBounds(778, 329, 220, 24);
            add(overtimeTitle);

            add(new DashboardTablePanel(315, 374));
            add(new DashboardTablePanel(778, 374));
        }
    }

    private static class SidebarPanel extends JPanel {

        public SidebarPanel() {
            setBounds(0, 0, 257, 800);
            setLayout(null);
            setBackground(NAVY);

            JLabel logo = label("MotorPH", 28, Font.BOLD, WHITE);
            logo.setBounds(48, 62, 150, 36);
            add(logo);

            add(new SidebarItem(IconType.DASHBOARD, "Dashboard", true, 48, 154));
            add(new SidebarItem(IconType.EMPLOYEE, "Employees", false, 48, 195));
            add(new SidebarItem(IconType.PAYROLL, "Payroll", false, 48, 236));
            add(new SidebarItem(IconType.REQUESTS, "Requests", false, 48, 277));
            add(new SidebarItem(IconType.ATTENDANCE, "Attendance", false, 48, 318));
            add(new SidebarItem(IconType.USERS, "Users", false, 48, 359));

            add(new SidebarItem(IconType.SETTINGS, "Settings", false, 45, 635));
            add(new SidebarItem(IconType.HELP, "Help Center", false, 45, 674));
            add(new SidebarItem(IconType.LOGOUT, "Log Out", false, 45, 713));
        }
    }

    private static class SidebarItem extends JPanel {

        public SidebarItem(IconType iconType, String text, boolean active, int x, int y) {
            setBounds(x, y, 170, 31);
            setLayout(null);
            setOpaque(false);

            SidebarIcon icon = new SidebarIcon(iconType);
            icon.setBounds(0, 3, 24, 24);
            add(icon);

            JLabel itemLabel = label(text, 18, active ? Font.BOLD : Font.PLAIN, WHITE);
            itemLabel.setBounds(47, 1, 130, 26);
            add(itemLabel);
        }
    }

    private static class RoleDropdownPanel extends JPanel {

        public RoleDropdownPanel() {
            setBounds(314, 107, 108, 36);
            setLayout(null);
            setBackground(WHITE);
            setBorder(new LineBorder(LIGHT_BORDER, 1));

            JLabel role = label("HR", 13, Font.PLAIN, new Color(205, 205, 205));
            role.setBounds(11, 8, 40, 18);
            add(role);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(205, 205, 205));
            g2.setStroke(new BasicStroke(2f));

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(84, 15);
            arrow.lineTo(91, 22);
            arrow.lineTo(98, 15);
            g2.draw(arrow);

            g2.dispose();
        }
    }

    private static class TopProfilePanel extends JPanel {

        public TopProfilePanel() {
            setBounds(1074, 40, 126, 58);
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

    private static class StatCardPanel extends JPanel {

        public StatCardPanel(
                String title,
                String number,
                String description,
                int x,
                int y,
                int width,
                int height,
                boolean multilineDescription
        ) {
            setBounds(x, y, width, height);
            setLayout(null);
            setBackground(NAVY);

            JLabel titleLabel = label(title, 13, Font.BOLD, WHITE);
            titleLabel.setBounds(18, 18, width - 36, 20);
            add(titleLabel);

            JLabel numberLabel = label(number, 50, Font.BOLD, WHITE);
            numberLabel.setBounds(15, 50, 150, 65);
            add(numberLabel);

            if (multilineDescription) {
                MultiLineText text = new MultiLineText(description, 15, Font.BOLD, WHITE);
                text.setBounds(107, 58, 160, 55);
                add(text);
            } else {
                JLabel descLabel = label(description, 16, Font.BOLD, WHITE);
                descLabel.setBounds(157, 70, 110, 25);
                add(descLabel);
            }
        }
    }

    private static class DashboardTablePanel extends JPanel {

        private final List<TableRowData> rows = new ArrayList<>();

        public DashboardTablePanel(int x, int y) {
            setBounds(x, y, 422, 305);
            setLayout(null);
            setBackground(WHITE);

            rows.add(new TableRowData("Juan Cruz", "IT", "Vacation"));
            rows.add(new TableRowData("Super Man", "HR", "Vacation"));
            rows.add(new TableRowData("Juan Cruz", "IT", "Vacation"));
            rows.add(new TableRowData("Super Man", "HR", "Vacation"));
            rows.add(new TableRowData("Juan Cruz", "IT", "Vacation"));
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

            g2.drawString("Name", 19, 13);
            g2.drawString("Department", 157, 13);
            g2.drawString("Reason", 288, 13);

            g2.fillRect(3, 38, 415, 3);
        }

        private void drawRows(Graphics2D g2) {
            int firstRowY = 43;
            int rowHeight = 55;

            for (int i = 0; i < rows.size(); i++) {
                int rowY = firstRowY + (i * rowHeight);

                if (i == 1 || i == 3) {
                    g2.setColor(ROW_GRAY);
                    g2.fillRect(0, rowY, 422, rowHeight);
                }

                TableRowData row = rows.get(i);

                g2.setColor(BLACK);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

                int textY = rowY + 32;

                g2.drawString(row.name, 19, textY);
                g2.drawString(row.department, 157, textY);
                g2.drawString(row.reason, 288, textY);
            }
        }
    }

    private static class TableRowData {
        private final String name;
        private final String department;
        private final String reason;

        public TableRowData(String name, String department, String reason) {
            this.name = name;
            this.department = department;
            this.reason = reason;
        }
    }

    private static class MultiLineText extends JComponent {

        private final String[] lines;
        private final Font font;
        private final Color color;

        public MultiLineText(String text, int size, int style, Color color) {
            this.lines = text.split("\\n");
            this.font = new Font("Segoe UI", style, size);
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setFont(font);
            g2.setColor(color);

            int y = 17;
            for (String line : lines) {
                g2.drawString(line, 0, y);
                y += 20;
            }

            g2.dispose();
        }
    }

    private enum IconType {
        DASHBOARD,
        EMPLOYEE,
        PAYROLL,
        REQUESTS,
        ATTENDANCE,
        USERS,
        SETTINGS,
        HELP,
        LOGOUT
    }

    private static class SidebarIcon extends JComponent {

        private final IconType type;

        public SidebarIcon(IconType type) {
            this.type = type;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case DASHBOARD -> drawDashboard(g2);
                case EMPLOYEE -> drawEmployee(g2);
                case PAYROLL -> drawPayroll(g2);
                case REQUESTS -> drawRequests(g2);
                case ATTENDANCE -> drawAttendance(g2);
                case USERS -> drawUsers(g2);
                case SETTINGS -> drawSettings(g2);
                case HELP -> drawHelp(g2);
                case LOGOUT -> drawLogout(g2);
            }

            g2.dispose();
        }

        private void drawDashboard(Graphics2D g2) {
            g2.drawRoundRect(1, 2, 8, 8, 2, 2);
            g2.drawRoundRect(14, 2, 8, 8, 2, 2);
            g2.drawRoundRect(1, 15, 8, 8, 2, 2);
            g2.drawRoundRect(14, 15, 8, 8, 2, 2);
        }

        private void drawEmployee(Graphics2D g2) {
            g2.drawOval(3, 2, 9, 9);
            g2.draw(new Arc2D.Double(0, 11, 15, 12, 0, 180, Arc2D.OPEN));
            g2.drawLine(16, 6, 23, 6);
            g2.drawLine(16, 12, 23, 12);
        }

        private void drawPayroll(Graphics2D g2) {
            g2.drawRoundRect(2, 3, 18, 18, 2, 2);
            g2.drawRect(5, 6, 4, 4);
            g2.drawLine(12, 8, 17, 8);
            g2.drawRect(5, 13, 4, 4);
            g2.drawLine(12, 15, 17, 15);
        }

        private void drawRequests(Graphics2D g2) {
            g2.drawRect(3, 2, 18, 21);
            g2.drawLine(7, 2, 7, 6);
            g2.drawLine(17, 2, 17, 6);
            g2.drawLine(6, 10, 18, 10);
            g2.drawLine(6, 15, 14, 15);
        }

        private void drawAttendance(Graphics2D g2) {
            g2.drawRoundRect(2, 4, 19, 18, 2, 2);
            g2.drawLine(2, 9, 21, 9);
            g2.drawLine(6, 2, 6, 6);
            g2.drawLine(17, 2, 17, 6);
        }

        private void drawUsers(Graphics2D g2) {
            g2.drawOval(6, 4, 7, 7);
            g2.draw(new Arc2D.Double(3, 11, 13, 10, 0, 180, Arc2D.OPEN));

            g2.drawOval(1, 10, 6, 6);
            g2.draw(new Arc2D.Double(0, 16, 10, 7, 0, 180, Arc2D.OPEN));

            g2.drawOval(14, 2, 5, 5);
            g2.drawLine(17, 0, 17, 2);
            g2.drawLine(17, 7, 17, 9);
            g2.drawLine(12, 4, 14, 4);
            g2.drawLine(19, 4, 22, 4);
        }

        private void drawSettings(Graphics2D g2) {
            g2.drawOval(5, 5, 12, 12);
            g2.drawOval(9, 9, 4, 4);

            g2.drawLine(11, 1, 11, 4);
            g2.drawLine(11, 18, 11, 22);
            g2.drawLine(1, 11, 4, 11);
            g2.drawLine(18, 11, 22, 11);

            g2.drawLine(4, 4, 6, 6);
            g2.drawLine(17, 17, 20, 20);
            g2.drawLine(4, 18, 6, 16);
            g2.drawLine(17, 6, 20, 3);
        }

        private void drawHelp(Graphics2D g2) {
            Path2D cloud = new Path2D.Double();
            cloud.moveTo(5, 18);
            cloud.curveTo(2, 18, 1, 15, 3, 13);
            cloud.curveTo(4, 10, 7, 10, 8, 10);
            cloud.curveTo(10, 6, 16, 6, 18, 10);
            cloud.curveTo(21, 10, 23, 13, 22, 16);
            cloud.curveTo(22, 18, 20, 19, 18, 19);
            cloud.lineTo(5, 19);
            g2.draw(cloud);
        }

        private void drawLogout(Graphics2D g2) {
            g2.drawRect(3, 3, 11, 18);
            g2.drawLine(14, 12, 22, 12);
            g2.drawLine(18, 8, 22, 12);
            g2.drawLine(18, 16, 22, 12);
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
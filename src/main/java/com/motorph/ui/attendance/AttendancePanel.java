/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.attendance;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.*;

public class AttendancePanel extends JFrame {

    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color ROW_GRAY = new Color(217, 217, 217);
    private static final Color LIGHT_BORDER = new Color(220, 220, 220);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color MUTED_TEXT = new Color(150, 150, 150);
    private static final Color APPROVE_GREEN = new Color(0, 194, 113);
    private static final Color DENY_RED = new Color(255, 82, 82);

    public AttendancePanel() {
        setTitle("MotorPH HR Attendance");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setContentPane(new AttendanceRootPanel());

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AttendancePanel().setVisible(true);
            }
        });
    }

    private static class AttendanceRootPanel extends JPanel {

        public AttendanceRootPanel() {
            setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            setLayout(null);
            setBackground(WHITE);

            add(new SidebarPanel());
            add(new TopProfilePanel());

            SearchBox searchBox = new SearchBox();
            searchBox.setBounds(336, 97, 303, 39);
            add(searchBox);

            RoleDropdownPanel dropdown = new RoleDropdownPanel();
            dropdown.setBounds(335, 159, 108, 36);
            add(dropdown);

            add(new ActionButton("Add", ActionIcon.ADD, 926, 159));
            add(new ActionButton("Update", ActionIcon.UPDATE, 1019, 159));
            add(new ActionButton("Refresh", ActionIcon.REFRESH, 1112, 159));

            AttendanceTablePanel table = new AttendanceTablePanel();
            table.setBounds(335, 219, 865, 420);
            add(table);

            FooterButton approveButton = new FooterButton("Approve All", APPROVE_GREEN);
            approveButton.setBounds(960, 684, 118, 36);
            add(approveButton);

            FooterButton denyButton = new FooterButton("Deny All", DENY_RED);
            denyButton.setBounds(1082, 684, 118, 36);
            add(denyButton);
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

            add(new SidebarItem(IconType.DASHBOARD, "Dashboard", false, 48, 154));
            add(new SidebarItem(IconType.EMPLOYEE, "Employees", false, 48, 195));
            add(new SidebarItem(IconType.PAYROLL, "Payroll", false, 48, 236));
            add(new SidebarItem(IconType.REQUESTS, "Requests", false, 48, 277));
            add(new SidebarItem(IconType.ATTENDANCE, "Attendance", true, 48, 318));
            add(new SidebarItem(IconType.USERS, "Users", false, 48, 359));

            add(new SidebarItem(IconType.SETTINGS, "Settings", false, 45, 635));
            add(new SidebarItem(IconType.HELP, "Help Center", false, 45, 674));
            add(new SidebarItem(IconType.LOGOUT, "Log Out", false, 45, 713));
        }
    }

    private static class SidebarItem extends JPanel {

        public SidebarItem(IconType iconType, String text, boolean active, int x, int y) {
            setBounds(x, y, 185, 31);
            setLayout(null);
            setOpaque(false);

            SidebarIcon icon = new SidebarIcon(iconType);
            icon.setBounds(0, 3, 24, 24);
            add(icon);

            JLabel itemLabel = label(text, 18, active ? Font.BOLD : Font.PLAIN, WHITE);
            itemLabel.setBounds(47, 1, 140, 26);
            add(itemLabel);
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

    private static class RoleDropdownPanel extends JPanel {

        public RoleDropdownPanel() {
            setLayout(null);
            setBackground(WHITE);
            setBorder(new LineBorder(new Color(238, 238, 238), 1));

            JLabel role = label("HR", 13, Font.PLAIN, PLACEHOLDER);
            role.setBounds(11, 8, 40, 18);
            add(role);
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

            JLabel label = label(text, 14, Font.PLAIN, WHITE);

            if (text.equals("Add")) {
                label.setBounds(48, 8, 38, 18);
            } else if (text.equals("Update")) {
                label.setBounds(38, 8, 50, 18);
            } else {
                label.setBounds(37, 8, 55, 18);
            }

            add(label);
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

    private static class AttendanceTablePanel extends JPanel {

        private final String[][] rows = {
                {"Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM"},
                {"Super Man", "May 1, 2026", "5:00 PM", "8:00 PM"},
                {"Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM"},
                {"Super Man", "May 1, 2026", "5:00 PM", "8:00 PM"},
                {"Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM"},
                {"Super Man", "May 1, 2026", "5:00 PM", "8:00 PM"},
                {"Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM"}
        };

        public AttendanceTablePanel() {
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

            g2.drawString("Employee ID", 20, 10);
            g2.drawString("Date", 219, 10);
            g2.drawString("Time In", 426, 10);
            g2.drawString("Time Out", 590, 10);
            g2.drawString("Status", 772, 10);

            g2.fillRect(3, 34, 859, 3);
        }

        private void drawRows(Graphics2D g2) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            int startY = 37;
            int rowHeight = 53;

            for (int i = 0; i < rows.length; i++) {
                int rowY = startY + (i * rowHeight);

                if (i % 2 == 1) {
                    g2.setColor(ROW_GRAY);
                    g2.fillRect(3, rowY, 862, 49);
                }

                g2.setColor(BLACK);

                int textY = rowY + 29;
                g2.drawString(rows[i][0], 20, textY);
                g2.drawString(rows[i][1], 219, textY);
                g2.drawString(rows[i][2], 426, textY);
                g2.drawString(rows[i][3], 590, textY);

                drawStatusBox(g2, 782, rowY + 20);
            }
        }

        private void drawStatusBox(Graphics2D g2, int x, int y) {
            g2.setColor(NAVY);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(x, y, 15, 15);
        }
    }

    private static class FooterButton extends JPanel {

        public FooterButton(String text, Color background) {
            setLayout(null);
            setBackground(background);

            JLabel label = label(text, 14, Font.PLAIN, WHITE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBounds(0, 8, 118, 20);
            add(label);
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
                case DASHBOARD:
                    drawDashboard(g2);
                    break;
                case EMPLOYEE:
                    drawEmployee(g2);
                    break;
                case PAYROLL:
                    drawPayroll(g2);
                    break;
                case REQUESTS:
                    drawRequests(g2);
                    break;
                case ATTENDANCE:
                    drawAttendance(g2);
                    break;
                case USERS:
                    drawUsers(g2);
                    break;
                case SETTINGS:
                    drawSettings(g2);
                    break;
                case HELP:
                    drawHelp(g2);
                    break;
                case LOGOUT:
                    drawLogout(g2);
                    break;
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
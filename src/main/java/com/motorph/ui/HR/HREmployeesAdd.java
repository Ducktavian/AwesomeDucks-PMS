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
import java.awt.*;
import java.awt.geom.*;

public class HREmployeesAdd extends JFrame {

    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color MUTED_TEXT = new Color(150, 150, 150);
    private static final Color BACK_TEXT = new Color(80, 80, 80);
    private static final Color FIELD_BORDER = new Color(70, 70, 70);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);

    public HREmployeesAdd() {
        setTitle("MotorPH HR Employees Add");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setContentPane(new EmployeesAddRootPanel());

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HREmployeesAdd().setVisible(true);
            }
        });
    }

    private static class EmployeesAddRootPanel extends JPanel {

        public EmployeesAddRootPanel() {
            setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            setLayout(null);
            setBackground(WHITE);

            add(new SidebarPanel());
            add(new TopProfilePanel());

            BackLink backLink = new BackLink();
            backLink.setBounds(320, 160, 48, 22);
            add(backLink);

            add(new FormSection(
                    "Basic Information",
                    320,
                    218,
                    new FieldData[]{
                            new FieldData("Employee ID", ""),
                            new FieldData("First Name", ""),
                            new FieldData("Last Name", ""),
                            new FieldData("Department", ""),
                            new FieldData("Position", ""),
                            new FieldData("Immediate Supervisor", ""),
                            new FieldData("Role", ""),
                            new FieldData("Status", "")
                    }
            ));

            add(new FormSection(
                    "Personal Detail",
                    546,
                    218,
                    new FieldData[]{
                            new FieldData("Gender", ""),
                            new FieldData("Birthdate", "MM-DD-YYYY"),
                            new FieldData("Cellphone No.", ""),
                            new FieldData("Telephone No.", ""),
                            new FieldData("E-mail", ""),
                            new FieldData("Address", "")
                    }
            ));

            add(new FormSection(
                    "Government ID",
                    772,
                    218,
                    new FieldData[]{
                            new FieldData("SSS No.", "XX-XXXXXXX-Y"),
                            new FieldData("PhilHealth No.", "XX-XXXXXXXXX-X"),
                            new FieldData("PAG-IBIG No.", "XXXX-XXXX-XXXX"),
                            new FieldData("TIN", "XXX-XXX-XXX-XXX")
                    }
            ));

            add(new FormSection(
                    "Compensation",
                    998,
                    218,
                    new FieldData[]{
                            new FieldData("Basic Salary", ""),
                            new FieldData("Gross Semi-Monthly Rate", ""),
                            new FieldData("Hourly Rate", ""),
                            new FieldData("Rice Subsidy", ""),
                            new FieldData("Phone Allowance", ""),
                            new FieldData("Clothing Allowance", "")
                    }
            ));

            SubmitButton submitButton = new SubmitButton();
            submitButton.setBounds(1087, 682, 113, 39);
            add(submitButton);
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
            add(new SidebarItem(IconType.EMPLOYEE, "Employees", true, 48, 195));
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

    private static class BackLink extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(BACK_TEXT);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g2.drawString("Back", 0, 17);

            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(0, 20, 43, 20);

            g2.dispose();
        }
    }

    private static class SubmitButton extends JPanel {

        public SubmitButton() {
            setLayout(null);
            setBackground(NAVY);

            JLabel text = label("Submit", 15, Font.PLAIN, WHITE);
            text.setHorizontalAlignment(SwingConstants.CENTER);
            text.setBounds(0, 9, 113, 20);
            add(text);
        }
    }

    private static class FormSection extends JPanel {

        public FormSection(String title, int x, int y, FieldData[] fields) {
            setBounds(x, y, 180, 430);
            setLayout(null);
            setOpaque(false);

            JLabel heading = label(title, 18, Font.BOLD, BLACK);
            heading.setBounds(0, 0, 180, 24);
            add(heading);

            int labelY = 31;
            int fieldY = 47;

            for (FieldData fieldData : fields) {
                JLabel fieldLabel = label(fieldData.label, 11, Font.PLAIN, BLACK);
                fieldLabel.setBounds(0, labelY, 180, 14);
                add(fieldLabel);

                CustomTextField field = new CustomTextField(fieldData.placeholder);
                field.setBounds(0, fieldY, 174, 25);
                add(field);

                labelY += 50;
                fieldY += 50;
            }
        }
    }

    private static class FieldData {
        private final String label;
        private final String placeholder;

        public FieldData(String label, String placeholder) {
            this.label = label;
            this.placeholder = placeholder;
        }
    }

    private static class CustomTextField extends JTextField {

        private final String placeholder;

        public CustomTextField(String placeholder) {
            this.placeholder = placeholder;

            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setForeground(BLACK);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            setCaretColor(BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(PLACEHOLDER);

                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(placeholder);
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2.drawString(placeholder, x, y);
            }

            g2.dispose();
            super.paintComponent(g);
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
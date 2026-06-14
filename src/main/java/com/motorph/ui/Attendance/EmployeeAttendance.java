/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Attendance;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;

public class EmployeeAttendance {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH - Employee Attendance");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(new EmployeeAttendanceScreen());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

final class EmployeeAttendanceScreen extends JPanel {
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 800;

    EmployeeAttendanceScreen() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(null);
        setBackground(Color.WHITE);

        addAt(new SidebarPanel(), 0, 0, 257, 800);
        addAt(new SearchField("Search"), 336, 98, 304, 38);
        addAt(new ProfileCard(), 1078, 40, 122, 60);

        addAt(new AttendanceActionButton(ActionButtonType.TIME_IN, "Time In"), 926, 159, 88, 36);
        addAt(new AttendanceActionButton(ActionButtonType.TIME_OUT, "Time Out"), 1018, 159, 88, 36);
        addAt(new AttendanceActionButton(ActionButtonType.REFRESH, "Refresh"), 1112, 159, 88, 36);

        addAt(new AttendanceTablePanel(AttendanceRecord.sampleRecords()), 337, 217, 863, 410);
    }

    private void addAt(Component component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        add(component);
    }
}

final class SidebarPanel extends JPanel {
    SidebarPanel() {
        setLayout(null);
        setBackground(UIStyle.NAVY);

        JLabel brand = new JLabel("MotorPH");
        brand.setForeground(Color.WHITE);
        brand.setFont(UIStyle.headerFont(Font.BOLD));
        brand.setBounds(48, 61, 150, 40);
        add(brand);

        addAt(new NavigationItem(NavIcon.DASHBOARD, "Dashboard", false, 18), 48, 151, 170, 36);
        addAt(new NavigationItem(NavIcon.EMPLOYEES, "Employees", false, 18), 48, 209, 170, 36);
        addAt(new NavigationItem(NavIcon.PAYROLL, "Payroll", false, 18), 48, 250, 170, 36);
        addAt(new NavigationItem(NavIcon.REQUESTS, "Requests", true, 18), 48, 291, 170, 36);
        addAt(new NavigationItem(NavIcon.ATTENDANCE, "Attendance", false, 18), 48, 333, 170, 36);

        addAt(new NavigationItem(NavIcon.HELP, "Help Center", false, 15), 49, 654, 170, 32);
        addAt(new NavigationItem(NavIcon.LOGOUT, "Log Out", false, 15), 50, 696, 170, 32);
    }

    private void addAt(Component component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        add(component);
    }
}

final class NavigationItem extends JComponent {
    private final NavIcon icon;
    private final String text;
    private final boolean active;
    private final int textSize;

    NavigationItem(NavIcon icon, String text, boolean active, int textSize) {
        this.icon = icon;
        this.text = text;
        this.active = active;
        this.textSize = textSize;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.applyRenderingHints(g);

        new SidebarVectorIcon(icon, Color.WHITE, 22, 22)
                .paintIcon(this, g, 0, (getHeight() - 22) / 2);

        g.setFont(UIStyle.textFont(active ? Font.BOLD : Font.PLAIN, textSize));
        g.setColor(Color.WHITE);

        FontMetrics metrics = g.getFontMetrics();
        int baseline = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        g.drawString(text, 47, baseline);
        g.dispose();
    }
}

final class SearchField extends JTextField {
    private final String placeholder;

    SearchField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setBorder(new EmptyBorder(0, 36, 0, 10));
        setFont(UIStyle.textFont(Font.PLAIN, 18));
        setForeground(Color.BLACK);
        setCaretColor(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.applyRenderingHints(g);

        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
        g.dispose();

        super.paintComponent(graphics);

        if (getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            UIStyle.applyRenderingHints(g2);

            drawSearchIcon(g2, 11, 11);

            g2.setFont(UIStyle.textFont(Font.PLAIN, 18));
            g2.setColor(UIStyle.PLACEHOLDER_TEXT);

            FontMetrics metrics = g2.getFontMetrics();
            int baseline = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

            g2.drawString(placeholder, 36, baseline);
            g2.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.applyRenderingHints(g);

        g.setColor(UIStyle.INPUT_BORDER);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
        g.dispose();
    }

    private void drawSearchIcon(Graphics2D g, int x, int y) {
        g.setColor(UIStyle.PLACEHOLDER_TEXT);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(x, y, 13, 13);
        g.drawLine(x + 11, y + 11, x + 18, y + 18);
    }
}

final class ProfileCard extends JComponent {
    ProfileCard() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.applyRenderingHints(g);

        g.setFont(UIStyle.heading2Font(Font.BOLD));
        g.setColor(UIStyle.NAVY);
        g.drawString("Name", 4, 23);

        g.setFont(UIStyle.textFont(Font.PLAIN, 15));
        g.setColor(UIStyle.MUTED_TEXT);
        g.drawString("Position", 0, 46);

        g.setColor(UIStyle.NAVY);
        g.fillOval(66, 0, 56, 56);

        g.dispose();
    }
}

enum ActionButtonType {
    TIME_IN,
    TIME_OUT,
    REFRESH
}

final class AttendanceActionButton extends JButton {
    private final ActionButtonType type;
    private final String label;

    AttendanceActionButton(ActionButtonType type, String label) {
        this.type = type;
        this.label = label;

        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.applyRenderingHints(g);

        g.setColor(UIStyle.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(UIStyle.textFont(Font.PLAIN, 12));
        g.setColor(Color.WHITE);

        FontMetrics metrics = g.getFontMetrics();
        int baseline = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        if (type == ActionButtonType.REFRESH) {
            new RefreshGlyph(Color.WHITE, 17, 17)
                    .paintIcon(this, g, 14, (getHeight() - 17) / 2);

            g.drawString(label, 48, baseline);
        } else {
            int x = (getWidth() - metrics.stringWidth(label)) / 2;
            g.drawString(label, x, baseline);
        }

        g.dispose();
    }
}

final class AttendanceTablePanel extends JComponent {
    private static final int HEADER_LINE_Y = 36;
    private static final int ROW_START_Y = 42;
    private static final int ROW_HEIGHT = 52;

    private final List<AttendanceRecord> records;

    AttendanceTablePanel(List<AttendanceRecord> records) {
        this.records = records;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.applyRenderingHints(g);

        drawHeader(g);
        drawRows(g);

        g.dispose();
    }

    private void drawHeader(Graphics2D g) {
        g.setFont(UIStyle.textFont(Font.BOLD, 13));
        g.setColor(Color.BLACK);

        g.drawString("Employee ID", 18, 13);
        g.drawString("Date", 258, 13);
        g.drawString("Time In", 507, 13);
        g.drawString("Time Out", 704, 13);

        g.fillRect(1, HEADER_LINE_Y, getWidth() - 4, 4);
    }

    private void drawRows(Graphics2D g) {
        g.setFont(UIStyle.textFont(Font.PLAIN, 11));

        FontMetrics metrics = g.getFontMetrics();
        int textOffset = (ROW_HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();

        for (int i = 0; i < records.size(); i++) {
            int rowY = ROW_START_Y + i * ROW_HEIGHT;

            if (i % 2 == 1) {
                g.setColor(UIStyle.ROW_GRAY);
                g.fillRect(1, rowY, getWidth() - 2, 50);
            }

            AttendanceRecord record = records.get(i);
            int baseline = rowY + textOffset;

            g.setColor(Color.BLACK);
            g.drawString(record.employeeId(), 18, baseline);
            g.drawString(record.date(), 258, baseline);
            g.drawString(record.timeIn(), 507, baseline);
            g.drawString(record.timeOut(), 704, baseline);
        }
    }
}

final class AttendanceRecord {
    private final String employeeId;
    private final String date;
    private final String timeIn;
    private final String timeOut;

    AttendanceRecord(String employeeId, String date, String timeIn, String timeOut) {
        this.employeeId = employeeId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    String employeeId() {
        return employeeId;
    }

    String date() {
        return date;
    }

    String timeIn() {
        return timeIn;
    }

    String timeOut() {
        return timeOut;
    }

    static List<AttendanceRecord> sampleRecords() {
        return Arrays.asList(
                new AttendanceRecord("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM"),
                new AttendanceRecord("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM"),
                new AttendanceRecord("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM"),
                new AttendanceRecord("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM"),
                new AttendanceRecord("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM"),
                new AttendanceRecord("Super Man", "May 1, 2026", "5:00 PM", "8:00 PM"),
                new AttendanceRecord("Juan Cruz", "September 1, 2026", "5:00 PM", "6:00 PM")
        );
    }
}

enum NavIcon {
    DASHBOARD,
    EMPLOYEES,
    PAYROLL,
    REQUESTS,
    ATTENDANCE,
    HELP,
    LOGOUT
}

final class SidebarVectorIcon implements Icon {
    private final NavIcon type;
    private final Color color;
    private final int width;
    private final int height;

    SidebarVectorIcon(NavIcon type, Color color, int width, int height) {
        this.type = type;
        this.color = color;
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.applyRenderingHints(g);

        g.translate(x, y);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case DASHBOARD:
                drawDashboard(g);
                break;
            case EMPLOYEES:
                drawEmployees(g);
                break;
            case PAYROLL:
                drawPayroll(g);
                break;
            case REQUESTS:
                drawRequests(g);
                break;
            case ATTENDANCE:
                drawAttendance(g);
                break;
            case HELP:
                drawHelp(g);
                break;
            case LOGOUT:
                drawLogout(g);
                break;
            default:
                break;
        }

        g.dispose();
    }

    private void drawDashboard(Graphics2D g) {
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                g.drawRoundRect(col * 11, row * 11, 8, 8, 2, 2);
            }
        }
    }

    private void drawEmployees(Graphics2D g) {
        g.drawOval(1, 4, 10, 10);
        g.drawArc(0, 13, 14, 10, 0, 180);
        g.drawLine(16, 6, 22, 6);
        g.drawLine(16, 11, 22, 11);
        g.drawLine(16, 16, 21, 16);
    }

    private void drawPayroll(Graphics2D g) {
        g.drawRoundRect(1, 2, 17, 18, 2, 2);
        g.drawRect(4, 5, 5, 5);
        g.drawLine(12, 6, 16, 6);
        g.drawLine(12, 10, 16, 10);
        g.drawLine(4, 14, 16, 14);
        g.drawLine(4, 17, 14, 17);
    }

    private void drawRequests(Graphics2D g) {
        g.drawRoundRect(1, 1, 18, 20, 1, 1);
        g.drawLine(4, 6, 16, 6);
        g.drawLine(4, 12, 16, 12);
        g.drawRect(6, 14, 8, 4);
    }

    private void drawAttendance(Graphics2D g) {
        g.drawRoundRect(1, 4, 19, 17, 1, 1);
        g.drawLine(1, 8, 20, 8);
        g.drawLine(5, 2, 5, 6);
        g.drawLine(9, 2, 9, 6);
        g.drawLine(13, 2, 13, 6);
        g.drawLine(17, 2, 17, 6);
    }

    private void drawHelp(Graphics2D g) {
        Path2D cloud = new Path2D.Double();
        cloud.moveTo(5, 17);
        cloud.curveTo(1, 17, 1, 12, 5, 12);
        cloud.curveTo(6, 8, 10, 8, 12, 11);
        cloud.curveTo(15, 10, 19, 12, 19, 16);
        cloud.lineTo(5, 17);
        g.draw(cloud);
    }

    private void drawLogout(Graphics2D g) {
        g.drawRect(2, 2, 12, 18);
        g.drawLine(14, 11, 22, 11);
        g.drawLine(18, 7, 22, 11);
        g.drawLine(18, 15, 22, 11);
        g.drawLine(6, 5, 14, 5);
        g.drawLine(6, 17, 14, 17);
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}

final class RefreshGlyph implements Icon {
    private final Color color;
    private final int width;
    private final int height;

    RefreshGlyph(Color color, int width, int height) {
        this.color = color;
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.applyRenderingHints(g);

        g.translate(x, y);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.drawArc(3, 3, 12, 12, 35, 275);
        g.drawLine(12, 2, 15, 3);
        g.drawLine(15, 3, 13, 6);

        g.dispose();
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}

final class UIStyle {
    static final Color NAVY = new Color(2, 19, 98);
    static final Color ROW_GRAY = new Color(217, 217, 217);
    static final Color INPUT_BORDER = new Color(205, 205, 205);
    static final Color PLACEHOLDER_TEXT = new Color(222, 222, 222);
    static final Color MUTED_TEXT = new Color(153, 153, 153);

    private static final String HEADER_FONT = "Segoe UI";
    private static final String TEXT_FONT = "Open Sans";
    static Color LINK_GRAY;

    private UIStyle() {
    }

    static Font headerFont(int style) {
        return new Font(HEADER_FONT, style, 28);
    }

    static Font heading2Font(int style) {
        return new Font(HEADER_FONT, style, 18);
    }

    static Font textFont(int style, int size) {
        return new Font(TEXT_FONT, style, size);
    }

    static void applyRenderingHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

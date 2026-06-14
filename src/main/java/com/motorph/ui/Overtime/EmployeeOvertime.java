/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Overtime;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class EmployeeOvertime {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH - Employee Overtime Requests");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setUndecorated(true);
            frame.setContentPane(new MainPanel());
            frame.setVisible(true);
        });
    }
}

final class Theme {
    private Theme() {}

    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = Color.BLACK;

    static final Color LIGHT_GRAY_ROW = new Color(217, 217, 217);
    static final Color BORDER_GRAY = new Color(211, 211, 211);
    static final Color PLACEHOLDER_GRAY = new Color(214, 214, 214);
    static final Color POSITION_GRAY = new Color(150, 150, 150);

    static final Color STATUS_PENDING = new Color(255, 222, 89);
    static final Color STATUS_REJECTED = new Color(255, 87, 87);
    static final Color STATUS_APPROVED = new Color(12, 194, 107);

    static final Font HEADING_1 = new Font("Segoe UI", Font.BOLD, 28);
    static final Font HEADING_2 = new Font("Segoe UI", Font.BOLD, 18);
    static final Font PARAGRAPH = new Font("Open Sans", Font.PLAIN, 15);
    static final Font PARAGRAPH_BOLD = new Font("Open Sans", Font.BOLD, 15);

    static final Font SEARCH_FONT = new Font("Open Sans", Font.PLAIN, 18);
    static final Font BUTTON_FONT = new Font("Open Sans", Font.PLAIN, 13);
    static final Font TABLE_HEADER_FONT = new Font("Open Sans", Font.BOLD, 12);
    static final Font TABLE_BODY_FONT = new Font("Open Sans", Font.PLAIN, 11);
    static final Font BADGE_FONT = new Font("Open Sans", Font.PLAIN, 10);
}

class MainPanel extends JPanel {

    MainPanel() {
        setLayout(null);
        setBackground(Theme.WHITE);

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, 257, 800);
        add(sidebar);

        SearchBox searchBox = new SearchBox();
        searchBox.setBounds(335, 118, 305, 39);
        add(searchBox);

        ActionButton addButton = new ActionButton("Add", LineIcon.Type.ADD);
        addButton.setBounds(833, 159, 89, 37);
        add(addButton);

        ActionButton updateButton = new ActionButton("Update", LineIcon.Type.EDIT);
        updateButton.setBounds(926, 159, 88, 37);
        add(updateButton);

        ActionButton deleteButton = new ActionButton("Delete", LineIcon.Type.DELETE);
        deleteButton.setBounds(1019, 159, 87, 37);
        add(deleteButton);

        ActionButton refreshButton = new ActionButton("Refresh", LineIcon.Type.REFRESH);
        refreshButton.setBounds(1112, 159, 88, 37);
        add(refreshButton);

        ProfilePanel profilePanel = new ProfilePanel();
        profilePanel.setBounds(1077, 40, 123, 57);
        add(profilePanel);

        OvertimeTablePanel tablePanel = new OvertimeTablePanel(createOvertimeRows());
        tablePanel.setBounds(338, 216, 862, 410);
        add(tablePanel);
    }

    private List<OvertimeRequest> createOvertimeRows() {
        List<OvertimeRequest> rows = new ArrayList<>();

        rows.add(new OvertimeRequest("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Pending"));
        rows.add(new OvertimeRequest("Super Man", "HR", "May 1, 2026", "5:00 PM", "8:00 PM", "Undermanned", "", "Rejected"));
        rows.add(new OvertimeRequest("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Approved"));
        rows.add(new OvertimeRequest("Super Man", "HR", "May 1, 2026", "5:00 PM", "8:00 PM", "Undermanned", "", "Pending"));
        rows.add(new OvertimeRequest("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Rejected"));
        rows.add(new OvertimeRequest("Super Man", "HR", "May 1, 2026", "5:00 PM", "8:00 PM", "Undermanned", "", "Approved"));
        rows.add(new OvertimeRequest("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", "Pending"));

        return rows;
    }
}

class SidebarPanel extends JPanel {

    SidebarPanel() {
        setLayout(null);
        setBackground(Theme.NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(Theme.HEADING_1);
        logo.setForeground(Theme.WHITE);
        logo.setBounds(48, 63, 150, 36);
        add(logo);

        addNavigationItem("Dashboard", LineIcon.Type.DASHBOARD, 158, false);
        addNavigationItem("Employees", LineIcon.Type.EMPLOYEES, 216, false);
        addNavigationItem("Payroll", LineIcon.Type.PAYROLL, 255, false);
        addNavigationItem("Requests", LineIcon.Type.REQUESTS, 300, true);
        addNavigationItem("Attendance", LineIcon.Type.ATTENDANCE, 341, false);

        addNavigationItem("Help Center", LineIcon.Type.HELP, 660, false);
        addNavigationItem("Log Out", LineIcon.Type.LOGOUT, 702, false);
    }

    private void addNavigationItem(String text, LineIcon.Type iconType, int y, boolean active) {
        NavigationItem item = new NavigationItem(text, iconType, active);
        item.setBounds(48, y, 170, 32);
        add(item);
    }
}

class NavigationItem extends JPanel {

    NavigationItem(String text, LineIcon.Type iconType, boolean active) {
        setLayout(null);
        setOpaque(false);

        JLabel icon = new JLabel(new LineIcon(iconType, 22, Theme.WHITE));
        icon.setBounds(0, 5, 22, 22);
        add(icon);

        JLabel label = new JLabel(text);
        label.setFont(active ? Theme.PARAGRAPH_BOLD : Theme.PARAGRAPH);
        label.setForeground(Theme.WHITE);
        label.setBounds(48, 0, 130, 32);
        add(label);
    }
}

class ProfilePanel extends JPanel {

    ProfilePanel() {
        setLayout(null);
        setOpaque(false);

        JLabel name = new JLabel("Name", SwingConstants.RIGHT);
        name.setFont(Theme.HEADING_2);
        name.setForeground(new Color(0, 6, 67));
        name.setBounds(0, 7, 58, 22);
        add(name);

        JLabel position = new JLabel("Position", SwingConstants.RIGHT);
        position.setFont(Theme.PARAGRAPH);
        position.setForeground(Theme.POSITION_GRAY);
        position.setBounds(0, 30, 58, 22);
        add(position);

        AvatarCircle avatar = new AvatarCircle();
        avatar.setBounds(67, 0, 56, 56);
        add(avatar);
    }
}

class AvatarCircle extends JPanel {

    AvatarCircle() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Theme.NAVY);
        g.fillOval(0, 0, 56, 56);

        g.dispose();
    }
}

class SearchBox extends JPanel {

    SearchBox() {
        setLayout(null);
        setBackground(Theme.WHITE);
        setBorder(new RoundedBorder(Theme.BORDER_GRAY, 4));

        JLabel searchIcon = new JLabel(new LineIcon(LineIcon.Type.SEARCH, 21, Theme.PLACEHOLDER_GRAY));
        searchIcon.setBounds(12, 9, 21, 21);
        add(searchIcon);

        JLabel placeholder = new JLabel("Search");
        placeholder.setFont(Theme.SEARCH_FONT);
        placeholder.setForeground(Theme.PLACEHOLDER_GRAY);
        placeholder.setBounds(36, 6, 120, 26);
        add(placeholder);
    }
}

class ActionButton extends JButton {

    private final String buttonText;
    private final LineIcon.Type iconType;

    ActionButton(String buttonText, LineIcon.Type iconType) {
        this.buttonText = buttonText;
        this.iconType = iconType;

        setLayout(null);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(new LineIcon(iconType, 17, Theme.WHITE));
        icon.setBounds(15, 10, 17, 17);
        add(icon);

        JLabel label = new JLabel(buttonText);
        label.setFont(Theme.BUTTON_FONT);
        label.setForeground(Theme.WHITE);

        int labelX;
        if ("Add".equals(buttonText)) {
            labelX = 48;
        } else if ("Update".equals(buttonText)) {
            labelX = 38;
        } else if ("Delete".equals(buttonText)) {
            labelX = 40;
        } else {
            labelX = 37;
        }

        label.setBounds(labelX, 7, 58, 23);
        add(label);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(Theme.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();

        super.paintComponent(graphics);
    }
}

class OvertimeTablePanel extends JPanel {

    private final List<OvertimeRequest> rows;

    private final int[] columnX = {
            18,
            114,
            232,
            362,
            466,
            558,
            669,
            758
    };

    OvertimeTablePanel(List<OvertimeRequest> rows) {
        this.rows = rows;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHeader(g);
        drawRows(g);

        g.dispose();
    }

    private void drawHeader(Graphics2D g) {
        g.setFont(Theme.TABLE_HEADER_FONT);
        g.setColor(Theme.BLACK);

        g.drawString("Name", columnX[0], 14);
        g.drawString("Department", columnX[1], 14);
        g.drawString("Date", columnX[2], 14);
        g.drawString("Start Time", columnX[3], 14);
        g.drawString("End Time", columnX[4], 14);
        g.drawString("Reason", columnX[5], 14);
        g.drawString("Notes", columnX[6], 14);
        g.drawString("Status", columnX[7], 14);

        g.setColor(Theme.BLACK);
        g.fillRect(0, 36, 860, 4);
    }

    private void drawRows(Graphics2D g) {
        int rowTop = 40;
        int rowHeight = 53;

        for (int index = 0; index < rows.size(); index++) {
            int y = rowTop + index * rowHeight;

            if (index % 2 == 1) {
                g.setColor(Theme.LIGHT_GRAY_ROW);
                g.fillRect(0, y, 859, 50);
            }

            OvertimeRequest row = rows.get(index);

            g.setFont(Theme.TABLE_BODY_FONT);
            g.setColor(Theme.BLACK);

            int baseline = y + 31;

            g.drawString(row.getName(), columnX[0], baseline);
            g.drawString(row.getDepartment(), columnX[1], baseline);
            g.drawString(row.getDate(), columnX[2], baseline);
            g.drawString(row.getStartTime(), columnX[3], baseline);
            g.drawString(row.getEndTime(), columnX[4], baseline);
            g.drawString(row.getReason(), columnX[5], baseline);
            g.drawString(row.getNotes(), columnX[6], baseline);

            StatusBadge badge = new StatusBadge(row.getStatus());
            badge.paint(g, 756, y + 13);
        }
    }
}

class OvertimeRequest {

    private final String name;
    private final String department;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String reason;
    private final String notes;
    private final String status;

    OvertimeRequest(
            String name,
            String department,
            String date,
            String startTime,
            String endTime,
            String reason,
            String notes,
            String status
    ) {
        this.name = name;
        this.department = department;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.notes = notes;
        this.status = status;
    }

    String getName() {
        return name;
    }

    String getDepartment() {
        return department;
    }

    String getDate() {
        return date;
    }

    String getStartTime() {
        return startTime;
    }

    String getEndTime() {
        return endTime;
    }

    String getReason() {
        return reason;
    }

    String getNotes() {
        return notes;
    }

    String getStatus() {
        return status;
    }
}

class StatusBadge {

    private final String status;

    StatusBadge(String status) {
        this.status = status;
    }

    void paint(Graphics2D g, int x, int y) {
        Color badgeColor;

        if ("Rejected".equals(status)) {
            badgeColor = Theme.STATUS_REJECTED;
        } else if ("Approved".equals(status)) {
            badgeColor = Theme.STATUS_APPROVED;
        } else {
            badgeColor = Theme.STATUS_PENDING;
        }

        int width = 62;
        int height = 25;

        g.setColor(badgeColor);
        g.fillRoundRect(x, y, width, height, 25, 25);

        g.setFont(Theme.BADGE_FONT);
        g.setColor(Theme.WHITE);

        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(status)) / 2;
        int textY = y + 16;

        g.drawString(status, textX, textY);
    }
}

class RoundedBorder implements javax.swing.border.Border {

    private final Color color;
    private final int radius;

    RoundedBorder(Color color, int radius) {
        this.color = color;
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(1, 1, 1, 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(color);
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g.dispose();
    }
}

class LineIcon implements Icon {

    enum Type {
        DASHBOARD,
        EMPLOYEES,
        PAYROLL,
        REQUESTS,
        ATTENDANCE,
        HELP,
        LOGOUT,
        SEARCH,
        ADD,
        EDIT,
        DELETE,
        REFRESH
    }

    private final Type type;
    private final int size;
    private final Color color;

    LineIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();

        g.translate(x, y);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
            case SEARCH:
                drawSearch(g);
                break;
            case ADD:
                drawAdd(g);
                break;
            case EDIT:
                drawEdit(g);
                break;
            case DELETE:
                drawDelete(g);
                break;
            case REFRESH:
                drawRefresh(g);
                break;
            default:
                break;
        }

        g.dispose();
    }

    private void drawDashboard(Graphics2D g) {
        g.drawRoundRect(1, 1, 8, 8, 2, 2);
        g.drawRoundRect(13, 1, 8, 8, 2, 2);
        g.drawRoundRect(1, 13, 8, 8, 2, 2);
        g.drawRoundRect(13, 13, 8, 8, 2, 2);
    }

    private void drawEmployees(Graphics2D g) {
        g.drawOval(3, 2, 8, 8);
        g.drawArc(1, 12, 13, 9, 0, 180);
        g.drawLine(16, 5, 22, 5);
        g.drawLine(16, 11, 22, 11);
        g.drawLine(16, 17, 22, 17);
    }

    private void drawPayroll(Graphics2D g) {
        g.drawRoundRect(2, 2, 18, 18, 2, 2);
        g.drawRect(6, 6, 4, 4);
        g.drawRect(13, 6, 4, 4);
        g.drawRect(6, 13, 4, 4);
        g.drawLine(13, 14, 17, 14);
        g.drawLine(13, 17, 17, 17);
    }

    private void drawRequests(Graphics2D g) {
        g.drawRoundRect(2, 2, 18, 18, 1, 1);
        g.drawLine(6, 7, 16, 7);
        g.drawLine(6, 12, 16, 12);
        g.drawLine(6, 17, 12, 17);
        g.drawLine(5, 3, 5, 0);
        g.drawLine(17, 3, 17, 0);
    }

    private void drawAttendance(Graphics2D g) {
        g.drawRoundRect(2, 4, 18, 17, 1, 1);
        g.drawLine(2, 8, 20, 8);
        g.drawLine(6, 1, 6, 6);
        g.drawLine(8, 1, 8, 6);
        g.drawLine(10, 1, 10, 6);
        g.drawLine(12, 1, 12, 6);
        g.drawLine(14, 1, 14, 6);
        g.drawLine(16, 1, 16, 6);
    }

    private void drawHelp(Graphics2D g) {
        Path2D cloud = new Path2D.Double();
        cloud.moveTo(5, 17);
        cloud.curveTo(2, 17, 1, 15, 2, 13);
        cloud.curveTo(2, 10, 5, 9, 7, 10);
        cloud.curveTo(8, 6, 13, 5, 15, 9);
        cloud.curveTo(18, 9, 21, 11, 21, 14);
        cloud.curveTo(21, 16, 19, 17, 17, 17);
        cloud.closePath();
        g.draw(cloud);
    }

    private void drawLogout(Graphics2D g) {
        g.drawRect(3, 2, 11, 18);
        g.drawLine(14, 11, 22, 11);
        g.drawLine(18, 7, 22, 11);
        g.drawLine(18, 15, 22, 11);
    }

    private void drawSearch(Graphics2D g) {
        g.drawOval(1, 1, 13, 13);
        g.drawLine(12, 12, 20, 20);
    }

    private void drawAdd(Graphics2D g) {
        g.drawLine(8, 2, 8, 16);
        g.drawLine(1, 9, 15, 9);
    }

    private void drawEdit(Graphics2D g) {
        g.drawLine(3, 14, 13, 4);
        g.drawLine(6, 17, 16, 7);
        g.drawLine(13, 4, 16, 7);
        g.drawLine(3, 14, 2, 18);
        g.drawLine(2, 18, 6, 17);
    }

    private void drawDelete(Graphics2D g) {
        g.drawRect(4, 5, 11, 13);
        g.drawLine(2, 5, 17, 5);
        g.drawLine(6, 3, 13, 3);
        g.drawLine(7, 8, 7, 16);
        g.drawLine(11, 8, 11, 16);
    }

    private void drawRefresh(Graphics2D g) {
        g.drawArc(3, 3, 14, 14, 45, 270);
        g.drawLine(15, 2, 17, 7);
        g.drawLine(15, 2, 11, 4);
    }
}

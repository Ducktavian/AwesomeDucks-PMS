/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Leave;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;

public final class EmployeeLeave {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            EmployeeLeaveFrame frame = new EmployeeLeaveFrame();
            frame.setVisible(true);
        });
    }
}

final class UITheme {
    private UITheme() {}

    static final int WINDOW_WIDTH = 1280;
    static final int WINDOW_HEIGHT = 800;
    static final int SIDEBAR_WIDTH = 257;

    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = Color.BLACK;
    static final Color BORDER = new Color(214, 214, 214);
    static final Color PLACEHOLDER = new Color(213, 213, 213);
    static final Color ROW_GRAY = new Color(217, 217, 217);
    static final Color PROFILE_GRAY = new Color(150, 150, 150);

    static final Color STATUS_PENDING = new Color(255, 222, 89);
    static final Color STATUS_REJECTED = new Color(255, 87, 87);
    static final Color STATUS_APPROVED = new Color(0, 191, 99);

    static final String HEADER_FONT = "Segoe UI";
    static final String TEXT_FONT = "Open Sans";

    static Font headerFont(int style, int size) {
        return new Font(HEADER_FONT, style, size);
    }

    static Font textFont(int style, int size) {
        return new Font(TEXT_FONT, style, size);
    }

    static void applyRenderingHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}

final class EmployeeLeaveFrame extends JFrame {
    EmployeeLeaveFrame() {
        super("MotorPH - Employee Leave");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UITheme.WINDOW_WIDTH, UITheme.WINDOW_HEIGHT);
        setResizable(false);
        setUndecorated(true);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(null);
        root.setBackground(UITheme.WHITE);
        setContentPane(root);

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, UITheme.SIDEBAR_WIDTH, UITheme.WINDOW_HEIGHT);
        root.add(sidebar);

        MainContentPanel mainContent = new MainContentPanel();
        mainContent.setBounds(
                UITheme.SIDEBAR_WIDTH,
                0,
                UITheme.WINDOW_WIDTH - UITheme.SIDEBAR_WIDTH,
                UITheme.WINDOW_HEIGHT
        );
        root.add(mainContent);
    }
}

final class MainContentPanel extends JPanel {
    MainContentPanel() {
        setLayout(null);
        setBackground(UITheme.WHITE);

        SearchFieldView searchField = new SearchFieldView("Search");
        searchField.setBounds(78, 118, 304, 39);
        add(searchField);

        addActionButton(new ActionButtonView("Add", IconType.ADD), 576, 159, 88, 36);
        addActionButton(new ActionButtonView("Update", IconType.UPDATE), 668, 159, 88, 36);
        addActionButton(new ActionButtonView("Delete", IconType.DELETE), 761, 159, 88, 36);
        addActionButton(new ActionButtonView("Refresh", IconType.REFRESH), 854, 159, 88, 36);

        ProfilePanel profilePanel = new ProfilePanel();
        profilePanel.setBounds(820, 40, 126, 58);
        add(profilePanel);

        LeaveTablePanel tablePanel = new LeaveTablePanel();
        tablePanel.setBounds(81, 224, 860, 416);
        add(tablePanel);
    }

    private void addActionButton(ActionButtonView button, int x, int y, int width, int height) {
        button.setBounds(x, y, width, height);
        add(button);
    }
}

final class SidebarPanel extends JPanel {
    SidebarPanel() {
        setBackground(UITheme.NAVY);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        UITheme.applyRenderingHints(g2);

        g2.setColor(UITheme.WHITE);
        g2.setFont(UITheme.headerFont(Font.BOLD, 28));
        g2.drawString("MotorPH", 48, 89);

        drawNavigationItem(g2, IconType.DASHBOARD, "Dashboard", 48, 158, 95, 174, false);
        drawNavigationItem(g2, IconType.EMPLOYEES, "Employees", 48, 216, 95, 231, false);
        drawNavigationItem(g2, IconType.PAYROLL, "Payroll", 48, 254, 95, 268, false);
        drawNavigationItem(g2, IconType.REQUESTS, "Requests", 48, 296, 95, 310, true);
        drawNavigationItem(g2, IconType.ATTENDANCE, "Attendance", 48, 339, 95, 353, false);

        drawNavigationItem(g2, IconType.HELP, "Help Center", 48, 664, 80, 676, false);
        drawNavigationItem(g2, IconType.LOGOUT, "Log Out", 49, 699, 80, 715, false);

        g2.dispose();
    }

    private void drawNavigationItem(
            Graphics2D g2,
            IconType iconType,
            String label,
            int iconX,
            int iconY,
            int textX,
            int textBaseline,
            boolean active
    ) {
        IconPainter.draw(g2, iconType, iconX, iconY, 22, UITheme.WHITE);

        g2.setColor(UITheme.WHITE);
        g2.setFont(UITheme.textFont(active ? Font.BOLD : Font.PLAIN, 18));
        g2.drawString(label, textX, textBaseline);
    }
}

final class ProfilePanel extends JComponent {
    ProfilePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        UITheme.applyRenderingHints(g2);

        g2.setColor(UITheme.NAVY);
        g2.setFont(UITheme.headerFont(Font.BOLD, 18));
        g2.drawString("Name", 5, 22);

        g2.setColor(UITheme.PROFILE_GRAY);
        g2.setFont(UITheme.textFont(Font.PLAIN, 15));
        g2.drawString("Position", 1, 46);

        g2.setColor(UITheme.NAVY);
        g2.fillOval(66, 0, 56, 56);

        g2.dispose();
    }
}

final class SearchFieldView extends JComponent {
    private final String placeholder;

    SearchFieldView(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        UITheme.applyRenderingHints(g2);

        g2.setColor(UITheme.WHITE);
        g2.fill(new RoundRectangle2D.Double(
                0.5,
                0.5,
                getWidth() - 1,
                getHeight() - 1,
                5,
                5
        ));

        g2.setColor(UITheme.BORDER);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Double(
                0.5,
                0.5,
                getWidth() - 1,
                getHeight() - 1,
                5,
                5
        ));

        g2.setColor(UITheme.PLACEHOLDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(12, 11, 13, 13);
        g2.drawLine(23, 23, 29, 29);

        g2.setFont(UITheme.textFont(Font.PLAIN, 18));
        g2.drawString(placeholder, 36, 26);

        g2.dispose();
    }
}

final class ActionButtonView extends JComponent {
    private final String label;
    private final IconType iconType;
    private boolean hovered;

    ActionButtonView(String label, IconType iconType) {
        this.label = label;
        this.iconType = iconType;

        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        UITheme.applyRenderingHints(g2);

        g2.setColor(hovered ? new Color(4, 24, 115) : UITheme.NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        IconPainter.draw(g2, iconType, 16, 9, 17, UITheme.WHITE);

        g2.setColor(UITheme.WHITE);
        g2.setFont(UITheme.textFont(Font.PLAIN, 13));
        g2.drawString(label, 47, 22);

        g2.dispose();
    }
}

final class LeaveTablePanel extends JComponent {
    private static final String[] HEADERS = {
            "Name",
            "Department",
            "Start Date",
            "End Date",
            "Reason",
            "Notes",
            "Status"
    };

    private static final int[] COLUMN_X = {
            17,
            128,
            247,
            384,
            522,
            642,
            759
    };

    private final LeaveRequest[] leaveRequests = {
            new LeaveRequest("Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", LeaveStatus.PENDING),
            new LeaveRequest("Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", LeaveStatus.REJECTED),
            new LeaveRequest("Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", LeaveStatus.APPROVED),
            new LeaveRequest("Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", LeaveStatus.REJECTED),
            new LeaveRequest("Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", LeaveStatus.PENDING),
            new LeaveRequest("Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", LeaveStatus.REJECTED),
            new LeaveRequest("Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", LeaveStatus.APPROVED)
    };

    LeaveTablePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        UITheme.applyRenderingHints(g2);

        drawHeader(g2);
        drawRows(g2);

        g2.dispose();
    }

    private void drawHeader(Graphics2D g2) {
        g2.setColor(UITheme.BLACK);
        g2.setFont(UITheme.textFont(Font.BOLD, 13));

        for (int i = 0; i < HEADERS.length; i++) {
            g2.drawString(HEADERS[i], COLUMN_X[i], 11);
        }

        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, 37, getWidth(), 37);
    }

    private void drawRows(Graphics2D g2) {
        int rowTopStart = 40;
        int rowHeight = 55;

        for (int i = 0; i < leaveRequests.length; i++) {
            int top = rowTopStart + i * rowHeight;

            if (i % 2 == 1) {
                g2.setColor(UITheme.ROW_GRAY);
                g2.fillRect(0, top, getWidth(), rowHeight);
            }

            drawRow(g2, leaveRequests[i], top);
        }
    }

    private void drawRow(Graphics2D g2, LeaveRequest request, int top) {
        int baseline = top + 32;

        g2.setColor(new Color(20, 20, 20));
        g2.setFont(UITheme.textFont(Font.PLAIN, 10));

        g2.drawString(request.name(), COLUMN_X[0] + 1, baseline);
        g2.drawString(request.department(), COLUMN_X[1], baseline);
        g2.drawString(request.startDate(), COLUMN_X[2], baseline);
        g2.drawString(request.endDate(), COLUMN_X[3], baseline);
        g2.drawString(request.reason(), COLUMN_X[4], baseline);
        g2.drawString(request.notes(), COLUMN_X[5], baseline);

        drawStatusPill(g2, request.status(), COLUMN_X[6] - 4, top + 16, 62, 26);
    }

    private void drawStatusPill(Graphics2D g2, LeaveStatus status, int x, int y, int width, int height) {
        g2.setColor(status.color());
        g2.fillRoundRect(x, y, width, height, height, height);

        g2.setColor(UITheme.WHITE);
        g2.setFont(UITheme.textFont(Font.PLAIN, 9));

        FontMetrics metrics = g2.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(status.label())) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.drawString(status.label(), textX, textY);
    }
}

final class LeaveRequest {
    private final String name;
    private final String department;
    private final String startDate;
    private final String endDate;
    private final String reason;
    private final String notes;
    private final LeaveStatus status;

    LeaveRequest(
            String name,
            String department,
            String startDate,
            String endDate,
            String reason,
            String notes,
            LeaveStatus status
    ) {
        this.name = name;
        this.department = department;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.notes = notes;
        this.status = status;
    }

    String name() {
        return name;
    }

    String department() {
        return department;
    }

    String startDate() {
        return startDate;
    }

    String endDate() {
        return endDate;
    }

    String reason() {
        return reason;
    }

    String notes() {
        return notes;
    }

    LeaveStatus status() {
        return status;
    }
}

enum LeaveStatus {
    PENDING("Pending", UITheme.STATUS_PENDING),
    REJECTED("Rejected", UITheme.STATUS_REJECTED),
    APPROVED("Approved", UITheme.STATUS_APPROVED);

    private final String label;
    private final Color color;

    LeaveStatus(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    String label() {
        return label;
    }

    Color color() {
        return color;
    }
}

enum IconType {
    DASHBOARD,
    EMPLOYEES,
    PAYROLL,
    REQUESTS,
    ATTENDANCE,
    HELP,
    LOGOUT,
    ADD,
    UPDATE,
    DELETE,
    REFRESH
}

final class IconPainter {
    private IconPainter() {}

    static void draw(Graphics2D source, IconType type, int x, int y, int size, Color color) {
        Graphics2D g2 = (Graphics2D) source.create();

        UITheme.applyRenderingHints(g2);

        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(
                Math.max(1f, size / 18f),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        switch (type) {
            case DASHBOARD -> drawDashboardIcon(g2, size);
            case EMPLOYEES -> drawEmployeesIcon(g2, size);
            case PAYROLL -> drawPayrollIcon(g2, size);
            case REQUESTS -> drawRequestsIcon(g2, size);
            case ATTENDANCE -> drawAttendanceIcon(g2, size);
            case HELP -> drawHelpIcon(g2, size);
            case LOGOUT -> drawLogoutIcon(g2, size);
            case ADD -> drawAddIcon(g2, size);
            case UPDATE -> drawUpdateIcon(g2, size);
            case DELETE -> drawDeleteIcon(g2, size);
            case REFRESH -> drawRefreshIcon(g2, size);
        }

        g2.dispose();
    }

    private static void drawDashboardIcon(Graphics2D g2, int size) {
        int box = size / 3;
        int radius = Math.max(2, size / 9);

        g2.drawRoundRect(1, 1, box, box, radius, radius);
        g2.drawRoundRect(size / 2, 1, box, box, radius, radius);
        g2.drawRoundRect(1, size / 2, box, box, radius, radius);
        g2.drawRoundRect(size / 2, size / 2, box, box, radius, radius);
    }

    private static void drawEmployeesIcon(Graphics2D g2, int size) {
        g2.drawOval(2, 2, size / 3, size / 3);
        g2.drawArc(0, size / 2 - 1, size / 2, size / 2, 0, 180);

        g2.drawLine((int) (size * 0.65), 4, size - 1, 4);
        g2.drawLine((int) (size * 0.65), size / 2, size - 1, size / 2);
        g2.drawLine((int) (size * 0.65), (int) (size * 0.78), size - 2, (int) (size * 0.78));
    }

    private static void drawPayrollIcon(Graphics2D g2, int size) {
        g2.drawRoundRect(2, 2, size - 5, size - 4, 2, 2);
        g2.drawLine(6, 7, size - 7, 7);

        g2.drawRect(6, 11, 3, 3);
        g2.drawRect(12, 11, 3, 3);
        g2.drawRect(6, 17, 3, 3);
        g2.drawRect(12, 17, 3, 3);
    }

    private static void drawRequestsIcon(Graphics2D g2, int size) {
        g2.drawRect(2, 2, size - 5, size - 4);
        g2.drawLine(6, 7, size - 8, 7);
        g2.drawLine(6, size / 2, size - 8, size / 2);
        g2.drawLine(7, size - 7, size - 9, size - 7);
    }

    private static void drawAttendanceIcon(Graphics2D g2, int size) {
        g2.drawRect(2, 4, size - 5, size - 5);
        g2.drawLine(2, 9, size - 3, 9);
        g2.drawLine(6, 1, 6, 6);
        g2.drawLine(size - 7, 1, size - 7, 6);

        for (int i = 6; i <= size - 8; i += 5) {
            g2.drawLine(i, 13, i + 1, 13);
        }
    }

    private static void drawHelpIcon(Graphics2D g2, int size) {
        g2.draw(new Arc2D.Double(1, size / 2.0 - 3, size / 3.0, size / 3.0, 90, 180, Arc2D.OPEN));
        g2.draw(new Arc2D.Double(size / 4.0, 3, size / 3.0, size / 2.0, 100, -200, Arc2D.OPEN));
        g2.draw(new Arc2D.Double(size / 2.0, size / 3.0 - 1, size / 3.0, size / 3.0, 70, -160, Arc2D.OPEN));
        g2.drawLine(4, (int) (size * .72), size - 4, (int) (size * .72));
    }

    private static void drawLogoutIcon(Graphics2D g2, int size) {
        g2.drawRect(2, 2, size / 2, size - 4);
        g2.drawLine(size / 2, size / 2, size - 3, size / 2);
        g2.drawLine(size - 8, size / 2 - 5, size - 3, size / 2);
        g2.drawLine(size - 8, size / 2 + 5, size - 3, size / 2);
    }

    private static void drawAddIcon(Graphics2D g2, int size) {
        int center = size / 2;

        g2.drawLine(center, 2, center, size - 2);
        g2.drawLine(2, center, size - 2, center);
    }

    private static void drawUpdateIcon(Graphics2D g2, int size) {
        g2.drawLine(3, size - 4, 4, size - 9);
        g2.drawLine(4, size - 9, size - 7, 2);
        g2.drawLine(size - 7, 2, size - 2, 7);
        g2.drawLine(size - 2, 7, 8, size - 8);
        g2.drawLine(3, size - 4, 8, size - 5);
    }

    private static void drawDeleteIcon(Graphics2D g2, int size) {
        g2.drawRect(4, 6, size - 8, size - 4);
        g2.drawLine(2, 6, size - 2, 6);
        g2.drawLine(7, 3, size - 7, 3);

        g2.drawLine(8, 9, 8, size - 2);
        g2.drawLine(size / 2, 9, size / 2, size - 2);
        g2.drawLine(size - 8, 9, size - 8, size - 2);
    }

    private static void drawRefreshIcon(Graphics2D g2, int size) {
        g2.drawArc(2, 2, size - 4, size - 4, 45, 285);
        g2.drawLine(size - 5, 2, size - 2, 2);
        g2.drawLine(size - 5, 2, size - 5, 6);
    }
}

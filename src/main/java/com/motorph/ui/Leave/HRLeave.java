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


public final class HRLeave {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            LeaveFrame frame = new LeaveFrame();
            frame.setVisible(true);
        });
    }
}

final class Ui {
    private Ui() {}

    static final int APP_W = 1280;
    static final int APP_H = 800;
    static final int SIDEBAR_W = 257;

    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color ROW_GRAY = new Color(217, 217, 217);
    static final Color BLACK = Color.BLACK;
    static final Color BORDER = new Color(214, 214, 214);
    static final Color PLACEHOLDER = new Color(214, 214, 214);
    static final Color PROFILE_GRAY = new Color(150, 150, 150);

    static final Color YELLOW = new Color(255, 222, 89);
    static final Color RED = new Color(255, 87, 87);
    static final Color GREEN = new Color(0, 191, 99);

    static final String HEADER_FONT = "Segoe UI";
    static final String TEXT_FONT = "Open Sans";

    static Font header(int style, int size) {
        return new Font(HEADER_FONT, style, size);
    }

    static Font text(int style, int size) {
        return new Font(TEXT_FONT, style, size);
    }

    static void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}

final class LeaveFrame extends JFrame {
    LeaveFrame() {
        super("MotorPH - HR Leave");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        setSize(Ui.APP_W, Ui.APP_H);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(null);
        root.setBackground(Ui.WHITE);
        setContentPane(root);

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, Ui.SIDEBAR_W, Ui.APP_H);
        root.add(sidebar);

        MainContentPanel main = new MainContentPanel();
        main.setBounds(Ui.SIDEBAR_W, 0, Ui.APP_W - Ui.SIDEBAR_W, Ui.APP_H);
        root.add(main);
    }
}

final class MainContentPanel extends JPanel {
    MainContentPanel() {
        setLayout(null);
        setBackground(Ui.WHITE);

        SearchBox search = new SearchBox("Search");
        search.setBounds(79, 97, 303, 39);
        add(search);

        FilterBox leaveFilter = new FilterBox("Leave");
        leaveFilter.setBounds(79, 159, 106, 36);
        add(leaveFilter);

        FilterBox employeeFilter = new FilterBox("Employee");
        employeeFilter.setBounds(195, 159, 107, 36);
        add(employeeFilter);

        addButton(new ActionButton("Add", IconType.ADD), 576, 159, 88, 36);
        addButton(new ActionButton("Update", IconType.UPDATE), 668, 159, 88, 36);
        addButton(new ActionButton("Delete", IconType.DELETE), 761, 159, 88, 36);
        addButton(new ActionButton("Refresh", IconType.REFRESH), 854, 159, 88, 36);

        ProfilePanel profile = new ProfilePanel();
        profile.setBounds(820, 40, 125, 58);
        add(profile);

        LeaveTablePanel table = new LeaveTablePanel();
        table.setBounds(81, 219, 860, 420);
        add(table);
    }

    private void addButton(ActionButton button, int x, int y, int w, int h) {
        button.setBounds(x, y, w, h);
        add(button);
    }
}

final class ProfilePanel extends JComponent {
    ProfilePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Ui.enableQuality(g2);

        g2.setColor(Ui.NAVY);
        g2.setFont(Ui.header(Font.BOLD, 18));
        g2.drawString("Name", 5, 22);

        g2.setColor(Ui.PROFILE_GRAY);
        g2.setFont(Ui.text(Font.PLAIN, 15));
        g2.drawString("Position", 1, 46);

        g2.setColor(Ui.NAVY);
        g2.fillOval(66, 0, 56, 56);

        g2.dispose();
    }
}

final class SidebarPanel extends JPanel {
    SidebarPanel() {
        setOpaque(true);
        setBackground(Ui.NAVY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Ui.enableQuality(g2);

        g2.setColor(Ui.WHITE);
        g2.setFont(Ui.header(Font.BOLD, 28));
        g2.drawString("MotorPH", 48, 89);

        drawNav(g2, IconType.DASHBOARD, "Dashboard", 48, 158, 95, 174, false);
        drawNav(g2, IconType.EMPLOYEES, "Employees", 48, 216, 95, 231, false);
        drawNav(g2, IconType.PAYROLL, "Payroll", 48, 254, 95, 268, false);
        drawNav(g2, IconType.REQUESTS, "Requests", 48, 296, 95, 310, true);
        drawNav(g2, IconType.ATTENDANCE, "Attendance", 48, 339, 95, 353, false);

        drawNav(g2, IconType.HELP, "Help Center", 48, 664, 80, 676, false);
        drawNav(g2, IconType.LOGOUT, "Log Out", 49, 699, 80, 715, false);

        g2.dispose();
    }

    private void drawNav(
            Graphics2D g2,
            IconType icon,
            String label,
            int iconX,
            int iconY,
            int textX,
            int baseline,
            boolean active
    ) {
        IconPainter.draw(g2, icon, iconX, iconY, 22, Ui.WHITE);

        g2.setColor(Ui.WHITE);
        g2.setFont(Ui.text(active ? Font.BOLD : Font.PLAIN, 18));
        g2.drawString(label, textX, baseline);
    }
}

final class SearchBox extends JComponent {
    private final String placeholder;

    SearchBox(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Ui.enableQuality(g2);

        g2.setColor(Ui.WHITE);
        g2.fill(new RoundRectangle2D.Double(
                0.5,
                0.5,
                getWidth() - 1,
                getHeight() - 1,
                5,
                5
        ));

        g2.setColor(Ui.BORDER);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Double(
                0.5,
                0.5,
                getWidth() - 1,
                getHeight() - 1,
                5,
                5
        ));

        g2.setColor(Ui.PLACEHOLDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(12, 11, 13, 13);
        g2.drawLine(23, 23, 29, 29);

        g2.setFont(Ui.text(Font.PLAIN, 18));
        g2.drawString(placeholder, 36, 26);

        g2.dispose();
    }
}

final class FilterBox extends JComponent {
    private final String label;

    FilterBox(String label) {
        this.label = label;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Ui.enableQuality(g2);

        g2.setColor(Ui.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(234, 234, 234));
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g2.setFont(Ui.text(Font.PLAIN, 14));
        g2.setColor(new Color(218, 218, 218));
        g2.drawString(label, 11, 23);

        int ax = getWidth() - 24;
        int ay = 14;

        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(ax, ay, ax + 7, ay + 7);
        g2.drawLine(ax + 14, ay, ax + 7, ay + 7);

        g2.dispose();
    }
}

final class ActionButton extends JComponent {
    private final String label;
    private final IconType iconType;
    private boolean hovering;

    ActionButton(String label, IconType iconType) {
        this.label = label;
        this.iconType = iconType;

        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Ui.enableQuality(g2);

        g2.setColor(hovering ? new Color(4, 24, 115) : Ui.NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        IconPainter.draw(g2, iconType, 16, 9, 17, Ui.WHITE);

        g2.setFont(Ui.text(Font.PLAIN, 13));
        g2.setColor(Ui.WHITE);
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

    private static final int[] COL_X = {
            17,
            128,
            247,
            384,
            522,
            642,
            759
    };

    private final LeaveRequest[] requests = {
            new LeaveRequest("Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", Status.PENDING),
            new LeaveRequest("Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", Status.REJECTED),
            new LeaveRequest("Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", Status.APPROVED),
            new LeaveRequest("Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", Status.REJECTED),
            new LeaveRequest("Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", Status.PENDING),
            new LeaveRequest("Super Man", "HR", "May 1, 2026", "May 2, 2026", "Vacation", "Begging.", Status.REJECTED),
            new LeaveRequest("Juan Cruz", "IT", "September 1, 2026", "September 3, 2026", "Vacation", "Please.", Status.APPROVED)
    };

    LeaveTablePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Ui.enableQuality(g2);

        drawHeader(g2);
        drawRows(g2);

        g2.dispose();
    }

    private void drawHeader(Graphics2D g2) {
        g2.setColor(Ui.BLACK);
        g2.setFont(Ui.text(Font.BOLD, 13));

        int headerBaseline = 15;

        for (int i = 0; i < HEADERS.length; i++) {
            g2.drawString(HEADERS[i], COL_X[i], headerBaseline);
        }

        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, 42, getWidth(), 42);
    }

    private void drawRows(Graphics2D g2) {
        final int rowTopStart = 45;
        final int rowH = 55;

        g2.setFont(Ui.text(Font.PLAIN, 10));

        for (int i = 0; i < requests.length; i++) {
            int top = rowTopStart + (i * rowH);

            if (i % 2 == 1) {
                g2.setColor(Ui.ROW_GRAY);
                g2.fillRect(0, top, getWidth(), rowH);
            }

            drawRequest(g2, requests[i], top);
        }
    }

    private void drawRequest(Graphics2D g2, LeaveRequest request, int top) {
        int baseline = top + 32;

        g2.setColor(new Color(20, 20, 20));
        g2.setFont(Ui.text(Font.PLAIN, 10));

        g2.drawString(request.name(), COL_X[0] + 1, baseline);
        g2.drawString(request.department(), COL_X[1], baseline);
        g2.drawString(request.startDate(), COL_X[2], baseline);
        g2.drawString(request.endDate(), COL_X[3], baseline);
        g2.drawString(request.reason(), COL_X[4], baseline);
        g2.drawString(request.notes(), COL_X[5], baseline);

        drawStatusPill(g2, request.status(), COL_X[6] - 4, top + 16, 62, 26);
    }

    private void drawStatusPill(Graphics2D g2, Status status, int x, int y, int w, int h) {
        g2.setColor(status.color());
        g2.fillRoundRect(x, y, w, h, h, h);

        g2.setFont(Ui.text(Font.PLAIN, 9));
        g2.setColor(Ui.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        String text = status.text();

        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + ((h - fm.getHeight()) / 2) + fm.getAscent();

        g2.drawString(text, tx, ty);
    }
}

final class LeaveRequest {
    private final String name;
    private final String department;
    private final String startDate;
    private final String endDate;
    private final String reason;
    private final String notes;
    private final Status status;

    LeaveRequest(
            String name,
            String department,
            String startDate,
            String endDate,
            String reason,
            String notes,
            Status status
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

    Status status() {
        return status;
    }
}

enum Status {
    PENDING("Pending", Ui.YELLOW),
    REJECTED("Rejected", Ui.RED),
    APPROVED("Approved", Ui.GREEN);

    private final String text;
    private final Color color;

    Status(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    String text() {
        return text;
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

    static void draw(Graphics2D g2, IconType type, int x, int y, int size, Color color) {
        Graphics2D g = (Graphics2D) g2.create();

        Ui.enableQuality(g);

        g.translate(x, y);
        g.setColor(color);
        g.setStroke(new BasicStroke(
                Math.max(1f, size / 18f),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        switch (type) {
            case DASHBOARD:
                dashboard(g, size);
                break;
            case EMPLOYEES:
                employees(g, size);
                break;
            case PAYROLL:
                payroll(g, size);
                break;
            case REQUESTS:
                requests(g, size);
                break;
            case ATTENDANCE:
                attendance(g, size);
                break;
            case HELP:
                help(g, size);
                break;
            case LOGOUT:
                logout(g, size);
                break;
            case ADD:
                add(g, size);
                break;
            case UPDATE:
                update(g, size);
                break;
            case DELETE:
                delete(g, size);
                break;
            case REFRESH:
                refresh(g, size);
                break;
            default:
                break;
        }

        g.dispose();
    }

    private static void dashboard(Graphics2D g, int s) {
        int r = Math.max(2, s / 9);
        int box = s / 3;

        g.drawRoundRect(1, 1, box, box, r, r);
        g.drawRoundRect(s / 2, 1, box, box, r, r);
        g.drawRoundRect(1, s / 2, box, box, r, r);
        g.drawRoundRect(s / 2, s / 2, box, box, r, r);
    }

    private static void employees(Graphics2D g, int s) {
        g.drawOval(2, 2, s / 3, s / 3);
        g.drawArc(0, s / 2 - 1, s / 2, s / 2, 0, 180);

        g.drawLine((int) (s * 0.65), 4, s - 1, 4);
        g.drawLine((int) (s * 0.65), s / 2, s - 1, s / 2);
        g.drawLine((int) (s * 0.65), (int) (s * 0.78), s - 2, (int) (s * 0.78));
    }

    private static void payroll(Graphics2D g, int s) {
        g.drawRoundRect(2, 2, s - 5, s - 4, 2, 2);
        g.drawLine(6, 7, s - 7, 7);

        g.drawRect(6, 11, 3, 3);
        g.drawRect(12, 11, 3, 3);
        g.drawRect(6, 17, 3, 3);
        g.drawRect(12, 17, 3, 3);
    }

    private static void requests(Graphics2D g, int s) {
        g.drawRect(2, 2, s - 5, s - 4);
        g.drawLine(6, 7, s - 8, 7);
        g.drawLine(6, s / 2, s - 8, s / 2);
        g.drawLine(7, s - 7, s - 9, s - 7);
    }

    private static void attendance(Graphics2D g, int s) {
        g.drawRect(2, 4, s - 5, s - 5);
        g.drawLine(2, 9, s - 3, 9);
        g.drawLine(6, 1, 6, 6);
        g.drawLine(s - 7, 1, s - 7, 6);

        for (int i = 6; i <= s - 8; i += 5) {
            g.drawLine(i, 13, i + 1, 13);
        }
    }

    private static void help(Graphics2D g, int s) {
        g.draw(new Arc2D.Double(1, s / 2 - 3, s / 3, s / 3, 90, 180, Arc2D.OPEN));
        g.draw(new Arc2D.Double(s / 4, 3, s / 3, s / 2, 100, -200, Arc2D.OPEN));
        g.draw(new Arc2D.Double(s / 2, s / 3 - 1, s / 3, s / 3, 70, -160, Arc2D.OPEN));
        g.drawLine(4, (int) (s * .72), s - 4, (int) (s * .72));
    }

    private static void logout(Graphics2D g, int s) {
        g.drawRect(2, 2, s / 2, s - 4);
        g.drawLine(s / 2, s / 2, s - 3, s / 2);
        g.drawLine(s - 8, s / 2 - 5, s - 3, s / 2);
        g.drawLine(s - 8, s / 2 + 5, s - 3, s / 2);
    }

    private static void add(Graphics2D g, int s) {
        int c = s / 2;

        g.drawLine(c, 2, c, s - 2);
        g.drawLine(2, c, s - 2, c);
    }

    private static void update(Graphics2D g, int s) {
        g.drawLine(3, s - 4, 4, s - 9);
        g.drawLine(4, s - 9, s - 7, 2);
        g.drawLine(s - 7, 2, s - 2, 7);
        g.drawLine(s - 2, 7, 8, s - 8);
        g.drawLine(3, s - 4, 8, s - 5);
    }

    private static void delete(Graphics2D g, int s) {
        g.drawRect(4, 6, s - 8, s - 4);
        g.drawLine(2, 6, s - 2, 6);
        g.drawLine(7, 3, s - 7, 3);

        g.drawLine(8, 9, 8, s - 2);
        g.drawLine(s / 2, 9, s / 2, s - 2);
        g.drawLine(s - 8, 9, s - 8, s - 2);
    }

    private static void refresh(Graphics2D g, int s) {
        g.drawArc(2, 2, s - 4, s - 4, 45, 285);
        g.drawLine(s - 5, 2, s - 2, 2);
        g.drawLine(s - 5, 2, s - 5, 6);
    }
}

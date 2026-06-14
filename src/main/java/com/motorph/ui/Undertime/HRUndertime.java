/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Undertime;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HRUndertime extends JFrame {

    public HRUndertime() {
        setTitle("MotorPH - HR Undertime Requests");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        RootPanel rootPanel = new RootPanel();
        setContentPane(rootPanel);
        pack();
        setLocationRelativeTo(null);

        rootPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        rootPanel.getActionMap().put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HRUndertime().setVisible(true));
    }
}

class RootPanel extends JPanel {

    public RootPanel() {
        setPreferredSize(new Dimension(1280, 800));
        setLayout(null);
        setBackground(Color.WHITE);

        SidebarPanel sidebarPanel = new SidebarPanel();
        sidebarPanel.setBounds(0, 0, 257, 800);
        add(sidebarPanel);

        MainContentPanel mainContentPanel = new MainContentPanel();
        mainContentPanel.setBounds(257, 0, 1023, 800);
        add(mainContentPanel);
    }
}

class SidebarPanel extends JPanel {

    private final List<NavItem> mainItems = Arrays.asList(
            new NavItem("Dashboard", IconType.DASHBOARD, 49, 158, 96, 174, false),
            new NavItem("Employees", IconType.EMPLOYEES, 49, 216, 96, 230, false),
            new NavItem("Payroll", IconType.PAYROLL, 49, 254, 96, 269, false),
            new NavItem("Requests", IconType.REQUESTS, 49, 296, 96, 312, true),
            new NavItem("Attendance", IconType.ATTENDANCE, 49, 339, 96, 355, false)
    );

    private final List<NavItem> bottomItems = Arrays.asList(
            new NavItem("Help Center", IconType.HELP, 49, 663, 80, 676, false),
            new NavItem("Log Out", IconType.LOGOUT, 49, 699, 80, 718, false)
    );

    public SidebarPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = GraphicsHelper.prepare(graphics);
        g.setColor(Theme.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(Theme.logoFont());
        g.setColor(Color.WHITE);
        g.drawString("MotorPH", 48, 89);

        for (NavItem item : mainItems) {
            drawNavItem(g, item);
        }

        for (NavItem item : bottomItems) {
            drawNavItem(g, item);
        }

        g.dispose();
    }

    private void drawNavItem(Graphics2D g, NavItem item) {
        IconPainter.paint(g, item.iconType, item.iconX, item.iconY, 22, Color.WHITE);

        g.setFont(item.active ? Theme.navBoldFont() : Theme.navFont());
        g.setColor(Color.WHITE);
        g.drawString(item.text, item.textX, item.textBaseline);
    }
}

class MainContentPanel extends JPanel {

    public MainContentPanel() {
        setLayout(null);
        setBackground(Color.WHITE);

        SearchBox searchBox = new SearchBox();
        searchBox.setBounds(78, 97, 305, 39);
        add(searchBox);

        FilterDropdown undertimeFilter = new FilterDropdown("Undertime");
        undertimeFilter.setBounds(78, 159, 107, 36);
        add(undertimeFilter);

        FilterDropdown employeeFilter = new FilterDropdown("Employee");
        employeeFilter.setBounds(195, 159, 107, 36);
        add(employeeFilter);

        ActionButton addButton = new ActionButton("Add", IconType.ADD);
        addButton.setBounds(576, 159, 89, 36);
        add(addButton);

        ActionButton updateButton = new ActionButton("Update", IconType.UPDATE);
        updateButton.setBounds(668, 159, 89, 36);
        add(updateButton);

        ActionButton deleteButton = new ActionButton("Delete", IconType.DELETE);
        deleteButton.setBounds(761, 159, 89, 36);
        add(deleteButton);

        ActionButton refreshButton = new ActionButton("Refresh", IconType.REFRESH);
        refreshButton.setBounds(854, 159, 89, 36);
        add(refreshButton);

        ProfileHeader profileHeader = new ProfileHeader();
        profileHeader.setBounds(820, 40, 125, 60);
        add(profileHeader);

        RequestTablePanel tablePanel = new RequestTablePanel();
        tablePanel.setBounds(78, 218, 865, 410);
        add(tablePanel);
    }
}

class SearchBox extends JComponent {

    public SearchBox() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = GraphicsHelper.prepare(graphics);

        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        g.setColor(Theme.INPUT_BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        IconPainter.paint(g, IconType.SEARCH, 12, 11, 18, Theme.PLACEHOLDER);

        g.setFont(Theme.placeholderFont());
        g.setColor(Theme.PLACEHOLDER);
        g.drawString("Search", 37, 26);

        g.dispose();
    }
}

class FilterDropdown extends JComponent {

    private final String text;

    public FilterDropdown(String text) {
        this.text = text;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = GraphicsHelper.prepare(graphics);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Theme.FILTER_BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.setFont(Theme.filterFont());
        g.setColor(Theme.PLACEHOLDER);
        g.drawString(text, 11, 24);

        IconPainter.paint(g, IconType.CHEVRON_DOWN, getWidth() - 25, 12, 15, Theme.PLACEHOLDER);

        g.dispose();
    }
}

class ActionButton extends JComponent {

    private final String text;
    private final IconType iconType;

    public ActionButton(String text, IconType iconType) {
        this.text = text;
        this.iconType = iconType;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = GraphicsHelper.prepare(graphics);

        g.setColor(Theme.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());

        IconPainter.paint(g, iconType, 15, 10, 16, Color.WHITE);

        g.setFont(Theme.buttonFont());
        g.setColor(Color.WHITE);

        int textX;
        if ("Add".equals(text)) {
            textX = 47;
        } else if ("Update".equals(text)) {
            textX = 38;
        } else if ("Delete".equals(text)) {
            textX = 42;
        } else {
            textX = 40;
        }

        g.drawString(text, textX, 23);

        g.dispose();
    }
}

class ProfileHeader extends JComponent {

    public ProfileHeader() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = GraphicsHelper.prepare(graphics);

        g.setFont(Theme.profileNameFont());
        g.setColor(Theme.NAVY);
        g.drawString("Name", 5, 24);

        g.setFont(Theme.profilePositionFont());
        g.setColor(Theme.MUTED_TEXT);
        g.drawString("Position", 0, 46);

        g.setColor(Theme.NAVY);
        g.fillOval(67, 0, 56, 56);

        g.dispose();
    }
}

class RequestTablePanel extends JComponent {

    private final List<RequestRow> rows = new ArrayList<>();

    public RequestTablePanel() {
        setOpaque(false);

        rows.add(new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", StatusType.PENDING));
        rows.add(new RequestRow("Super Man", "HR", "May 1, 2026", "5:00 PM", "8:00 PM", "Undermanned", "", StatusType.REJECTED));
        rows.add(new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", StatusType.APPROVED));
        rows.add(new RequestRow("Super Man", "HR", "May 1, 2026", "5:00 PM", "8:00 PM", "Undermanned", "", StatusType.PENDING));
        rows.add(new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", StatusType.REJECTED));
        rows.add(new RequestRow("Super Man", "HR", "May 1, 2026", "5:00 PM", "8:00 PM", "Undermanned", "", StatusType.APPROVED));
        rows.add(new RequestRow("Juan Cruz", "IT", "September 1, 2026", "5:00 PM", "6:00 PM", "Undermanned", "", StatusType.PENDING));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = GraphicsHelper.prepare(graphics);

        drawHeaders(g);
        drawHeaderRule(g);
        drawRows(g);

        g.dispose();
    }

    private void drawHeaders(Graphics2D g) {
        g.setFont(Theme.tableHeaderFont());
        g.setColor(Color.BLACK);

        g.drawString("Name", 20, 12);
        g.drawString("Department", 116, 12);
        g.drawString("Date", 235, 12);
        g.drawString("Start Time", 365, 12);
        g.drawString("End Time", 468, 12);
        g.drawString("Reason", 560, 12);
        g.drawString("Notes", 671, 12);
        g.drawString("Status", 769, 12);
    }

    private void drawHeaderRule(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(3, 34, 860, 4);
    }

    private void drawRows(Graphics2D g) {
        int rowTop = 39;
        int rowHeight = 53;

        for (int i = 0; i < rows.size(); i++) {
            RequestRow row = rows.get(i);
            int y = rowTop + (i * rowHeight);

            if (i % 2 == 1) {
                g.setColor(Theme.TABLE_ROW_GRAY);
                g.fillRect(3, y, 860, 50);
            }

            int baseline = y + 31;
            drawRowText(g, row, baseline);
            drawStatusBadge(g, row.status, y + 13);
        }
    }

    private void drawRowText(Graphics2D g, RequestRow row, int baseline) {
        g.setFont(Theme.tableBodyFont());
        g.setColor(Color.BLACK);

        g.drawString(row.name, 20, baseline);
        g.drawString(row.department, 116, baseline);
        g.drawString(row.date, 235, baseline);
        g.drawString(row.startTime, 365, baseline);
        g.drawString(row.endTime, 468, baseline);
        g.drawString(row.reason, 560, baseline);
        g.drawString(row.notes, 671, baseline);
    }

    private void drawStatusBadge(Graphics2D g, StatusType status, int y) {
        int badgeX = 759;
        int badgeWidth = 63;
        int badgeHeight = 25;

        g.setColor(status.color);
        g.fillRoundRect(badgeX, y, badgeWidth, badgeHeight, 24, 24);

        g.setFont(Theme.statusFont());
        g.setColor(Color.WHITE);

        FontMetrics metrics = g.getFontMetrics();
        int textX = badgeX + (badgeWidth - metrics.stringWidth(status.label)) / 2;
        int textY = y + ((badgeHeight - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(status.label, textX, textY);
    }
}

class RequestRow {
    final String name;
    final String department;
    final String date;
    final String startTime;
    final String endTime;
    final String reason;
    final String notes;
    final StatusType status;

    RequestRow(
            String name,
            String department,
            String date,
            String startTime,
            String endTime,
            String reason,
            String notes,
            StatusType status
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
}

class NavItem {
    final String text;
    final IconType iconType;
    final int iconX;
    final int iconY;
    final int textX;
    final int textBaseline;
    final boolean active;

    NavItem(
            String text,
            IconType iconType,
            int iconX,
            int iconY,
            int textX,
            int textBaseline,
            boolean active
    ) {
        this.text = text;
        this.iconType = iconType;
        this.iconX = iconX;
        this.iconY = iconY;
        this.textX = textX;
        this.textBaseline = textBaseline;
        this.active = active;
    }
}

enum StatusType {
    PENDING("Pending", Theme.PENDING),
    REJECTED("Rejected", Theme.REJECTED),
    APPROVED("Approved", Theme.APPROVED);

    final String label;
    final Color color;

    StatusType(String label, Color color) {
        this.label = label;
        this.color = color;
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
    SEARCH,
    CHEVRON_DOWN,
    ADD,
    UPDATE,
    DELETE,
    REFRESH
}

class IconPainter {

    private IconPainter() {
    }

    public static void paint(Graphics2D g, IconType type, int x, int y, int size, Color color) {
        Graphics2D copy = (Graphics2D) g.create();
        copy.setColor(color);
        copy.setStroke(new BasicStroke(1.35f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        copy.translate(x, y);

        switch (type) {
            case DASHBOARD:
                dashboard(copy);
                break;
            case EMPLOYEES:
                employees(copy);
                break;
            case PAYROLL:
                payroll(copy);
                break;
            case REQUESTS:
                requests(copy);
                break;
            case ATTENDANCE:
                attendance(copy);
                break;
            case HELP:
                help(copy);
                break;
            case LOGOUT:
                logout(copy);
                break;
            case SEARCH:
                search(copy);
                break;
            case CHEVRON_DOWN:
                chevron(copy);
                break;
            case ADD:
                add(copy);
                break;
            case UPDATE:
                update(copy);
                break;
            case DELETE:
                delete(copy);
                break;
            case REFRESH:
                refresh(copy);
                break;
            default:
                break;
        }

        copy.dispose();
    }

    private static void dashboard(Graphics2D g) {
        g.drawRoundRect(0, 0, 8, 8, 2, 2);
        g.drawRoundRect(13, 0, 8, 8, 2, 2);
        g.drawRoundRect(0, 13, 8, 8, 2, 2);
        g.drawRoundRect(13, 13, 8, 8, 2, 2);
    }

    private static void employees(Graphics2D g) {
        g.drawOval(1, 1, 9, 9);
        g.draw(new Arc2D.Double(0, 10, 13, 12, 20, 140, Arc2D.OPEN));
        g.drawLine(15, 4, 22, 4);
        g.drawLine(15, 10, 22, 10);
        g.drawLine(15, 16, 22, 16);
    }

    private static void payroll(Graphics2D g) {
        g.drawRoundRect(1, 1, 18, 19, 2, 2);
        g.drawLine(5, 6, 15, 6);
        g.drawLine(5, 11, 8, 11);
        g.drawLine(11, 11, 15, 11);
        g.drawLine(5, 16, 8, 16);
        g.drawLine(11, 16, 15, 16);
    }

    private static void requests(Graphics2D g) {
        g.drawRoundRect(1, 1, 18, 19, 2, 2);
        g.drawLine(5, 6, 15, 6);
        g.drawLine(5, 11, 15, 11);
        g.drawLine(5, 16, 11, 16);
    }

    private static void attendance(Graphics2D g) {
        g.drawRoundRect(1, 3, 19, 18, 2, 2);
        g.drawLine(1, 8, 20, 8);
        g.drawLine(6, 0, 6, 5);
        g.drawLine(15, 0, 15, 5);
        g.drawLine(5, 12, 8, 12);
        g.drawLine(11, 12, 14, 12);
        g.drawLine(5, 16, 8, 16);
    }

    private static void help(Graphics2D g) {
        Path2D cloud = new Path2D.Double();
        cloud.moveTo(3, 14);
        cloud.curveTo(1, 14, 0, 12, 1, 10);
        cloud.curveTo(1, 8, 3, 7, 5, 7);
        cloud.curveTo(6, 4, 9, 3, 12, 5);
        cloud.curveTo(14, 3, 18, 5, 18, 9);
        cloud.curveTo(21, 9, 22, 11, 21, 13);
        cloud.curveTo(21, 15, 19, 16, 17, 16);
        cloud.lineTo(4, 16);
        cloud.curveTo(4, 16, 3, 15, 3, 14);
        g.draw(cloud);
    }

    private static void logout(Graphics2D g) {
        g.drawRect(2, 1, 13, 20);
        g.drawLine(15, 7, 22, 7);
        g.drawLine(22, 7, 18, 3);
        g.drawLine(22, 7, 18, 11);
        g.drawLine(7, 11, 18, 11);
    }

    private static void search(Graphics2D g) {
        g.drawOval(0, 0, 12, 12);
        g.drawLine(10, 10, 17, 17);
    }

    private static void chevron(Graphics2D g) {
        g.drawLine(1, 3, 7, 9);
        g.drawLine(7, 9, 13, 3);
    }

    private static void add(Graphics2D g) {
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(8, 1, 8, 15);
        g.drawLine(1, 8, 15, 8);
    }

    private static void update(Graphics2D g) {
        g.drawLine(3, 13, 12, 4);
        g.drawLine(12, 4, 15, 7);
        g.drawLine(6, 16, 2, 17);
        g.drawLine(3, 13, 6, 16);
        g.drawLine(2, 17, 3, 13);
    }

    private static void delete(Graphics2D g) {
        g.drawRect(4, 5, 10, 12);
        g.drawLine(2, 5, 16, 5);
        g.drawLine(6, 3, 12, 3);
        g.drawLine(7, 8, 7, 15);
        g.drawLine(11, 8, 11, 15);
    }

    private static void refresh(Graphics2D g) {
        g.draw(new Arc2D.Double(2, 2, 14, 14, 40, 270, Arc2D.OPEN));
        g.drawLine(13, 2, 17, 2);
        g.drawLine(17, 2, 17, 6);
    }
}

class GraphicsHelper {

    private GraphicsHelper() {
    }

    public static Graphics2D prepare(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        return g;
    }
}

class Theme {

    static final Color NAVY = new Color(2, 19, 98);
    static final Color TABLE_ROW_GRAY = new Color(217, 217, 217);
    static final Color INPUT_BORDER = new Color(216, 216, 216);
    static final Color FILTER_BORDER = new Color(235, 235, 235);
    static final Color PLACEHOLDER = new Color(217, 217, 217);
    static final Color MUTED_TEXT = new Color(154, 154, 154);

    static final Color PENDING = new Color(255, 222, 89);
    static final Color REJECTED = new Color(255, 87, 87);
    static final Color APPROVED = new Color(0, 191, 99);

    private static Font font(String family, int style, int size) {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = environment.getAvailableFontFamilyNames();

        for (String fontName : fontNames) {
            if (fontName.equalsIgnoreCase(family)) {
                return new Font(fontName, style, size);
            }
        }

        return new Font("SansSerif", style, size);
    }

    static Font logoFont() {
        return font("Segoe UI", Font.BOLD, 28);
    }

    static Font navFont() {
        return font("Open Sans", Font.PLAIN, 18);
    }

    static Font navBoldFont() {
        return font("Open Sans", Font.BOLD, 18);
    }

    static Font placeholderFont() {
        return font("Open Sans", Font.PLAIN, 18);
    }

    static Font filterFont() {
        return font("Open Sans", Font.PLAIN, 13);
    }

    static Font buttonFont() {
        return font("Open Sans", Font.PLAIN, 13);
    }

    static Font profileNameFont() {
        return font("Segoe UI", Font.BOLD, 18);
    }

    static Font profilePositionFont() {
        return font("Open Sans", Font.PLAIN, 15);
    }

    static Font tableHeaderFont() {
        return font("Open Sans", Font.BOLD, 13);
    }

    static Font tableBodyFont() {
        return font("Open Sans", Font.PLAIN, 10);
    }

    static Font statusFont() {
        return font("Open Sans", Font.PLAIN, 10);
    }
}

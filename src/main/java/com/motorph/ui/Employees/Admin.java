/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Employees;

/**
 *
 * @author Admin
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public final class ADMIN {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminFrame frame = new AdminFrame();
            frame.setVisible(true);
        });
    }
}

final class AdminFrame extends JFrame {
    AdminFrame() {
        super("MotorPH Admin - Employees");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        setContentPane(new AdminEmployeeScreen());
        pack();
        setLocationRelativeTo(null);

        getRootPane().registerKeyboardAction(
                event -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
}

final class AdminEmployeeScreen extends JPanel {
    AdminEmployeeScreen() {
        setLayout(null);
        setBackground(DesignTokens.WHITE);
        setPreferredSize(new Dimension(DesignTokens.SCREEN_WIDTH, DesignTokens.SCREEN_HEIGHT));

        place(new SidebarPanel(), 0, 0, 257, 800);

        place(new SearchBox(), 335, 96, 305, 41);

        place(new ProfileBlock(), 1077, 40, 123, 57);

        place(new FlatActionButton("Add", IconType.ADD), 833, 160, 88, 35);
        place(new FlatActionButton("Update", IconType.UPDATE), 927, 160, 87, 35);
        place(new FlatActionButton("Delete", IconType.DELETE), 1020, 160, 87, 35);
        place(new FlatActionButton("Refresh", IconType.REFRESH), 1113, 160, 86, 35);

        place(new EmployeeTablePanel(), 335, 218, 865, 500);
    }

    private void place(JComponent component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        add(component);
    }
}

final class DesignTokens {
    private DesignTokens() {}

    static final int SCREEN_WIDTH = 1280;
    static final int SCREEN_HEIGHT = 800;

    static final int H1 = 28;
    static final int H2 = 18;
    static final int PARAGRAPH = 15;

    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = Color.BLACK;

    static final Color LIGHT_GRAY = new Color(217, 217, 217);
    static final Color SEARCH_BORDER = new Color(210, 210, 210);
    static final Color SEARCH_PLACEHOLDER = new Color(205, 205, 205);
    static final Color PROFILE_SUBTEXT = new Color(150, 150, 150);
}

final class FontProvider {
    private FontProvider() {}

    static Font segoe(int size, int style) {
        return new Font("Segoe UI", style, size);
    }

    static Font openSans(int size, int style) {
        return new Font("Open Sans", style, size);
    }
}

final class SidebarPanel extends JPanel {
    SidebarPanel() {
        setLayout(null);
        setBackground(DesignTokens.NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setForeground(DesignTokens.WHITE);
        logo.setFont(FontProvider.segoe(DesignTokens.H1, Font.BOLD));
        logo.setBounds(48, 62, 150, 36);
        add(logo);

        addNavItem("Dashboard", IconType.DASHBOARD, false, 48, 154);
        addNavItem("Employees", IconType.EMPLOYEES, true, 48, 211);
        addNavItem("Payroll", IconType.PAYROLL, false, 48, 250);
        addNavItem("Requests", IconType.REQUESTS, false, 48, 293);
        addNavItem("Attendance", IconType.ATTENDANCE, false, 48, 337);

        addNavItem("Help Center", IconType.HELP, false, 48, 659);
        addNavItem("Log Out", IconType.LOGOUT, false, 48, 700);
    }

    private void addNavItem(String text, IconType iconType, boolean active, int x, int y) {
        NavItem item = new NavItem(text, iconType, active);
        item.setBounds(x, y, 180, 32);
        add(item);
    }
}

final class NavItem extends JComponent {
    private final String text;
    private final IconType iconType;
    private final boolean active;

    NavItem(String text, IconType iconType, boolean active) {
        this.text = text;
        this.iconType = iconType;
        this.active = active;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = GraphicsUtil.prepare(graphics);
        IconPainter.draw(g, iconType, 0, 5, 22, DesignTokens.WHITE);

        g.setColor(DesignTokens.WHITE);
        g.setFont(FontProvider.openSans(DesignTokens.H2, active ? Font.BOLD : Font.PLAIN));

        FontMetrics metrics = g.getFontMetrics();
        int baseline = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(text, 48, baseline);
        g.dispose();
    }
}

final class SearchBox extends JComponent {
    SearchBox() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = GraphicsUtil.prepare(graphics);

        Shape field = new RoundRectangle2D.Double(
                0.5,
                0.5,
                getWidth() - 1,
                getHeight() - 2,
                5,
                5
        );

        g.setColor(DesignTokens.WHITE);
        g.fill(field);

        g.setColor(DesignTokens.SEARCH_BORDER);
        g.setStroke(new BasicStroke(1f));
        g.draw(field);

        IconPainter.draw(g, IconType.SEARCH, 12, 12, 17, DesignTokens.SEARCH_PLACEHOLDER);

        g.setFont(FontProvider.openSans(DesignTokens.H2, Font.PLAIN));
        g.setColor(DesignTokens.SEARCH_PLACEHOLDER);
        g.drawString("Search", 37, 27);

        g.dispose();
    }
}

final class ProfileBlock extends JComponent {
    ProfileBlock() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = GraphicsUtil.prepare(graphics);

        g.setFont(FontProvider.segoe(DesignTokens.H2, Font.BOLD));
        g.setColor(DesignTokens.NAVY);
        g.drawString("Name", 5, 18);

        g.setFont(FontProvider.openSans(DesignTokens.PARAGRAPH, Font.PLAIN));
        g.setColor(DesignTokens.PROFILE_SUBTEXT);
        g.drawString("Position", 0, 43);

        g.setColor(DesignTokens.NAVY);
        g.fillOval(67, 0, 56, 56);

        g.dispose();
    }
}

final class FlatActionButton extends JComponent {
    private final String text;
    private final IconType iconType;

    FlatActionButton(String text, IconType iconType) {
        this.text = text;
        this.iconType = iconType;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = GraphicsUtil.prepare(graphics);

        g.setColor(DesignTokens.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());

        int iconX = iconType == IconType.ADD ? 17 : 13;
        IconPainter.draw(g, iconType, iconX, 10, 15, DesignTokens.WHITE);

        g.setColor(DesignTokens.WHITE);
        g.setFont(FontProvider.openSans(13, Font.PLAIN));

        FontMetrics metrics = g.getFontMetrics();
        int baseline = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        int textX = iconType == IconType.ADD ? 48 : 37;
        g.drawString(text, textX, baseline);

        g.dispose();
    }
}

final class EmployeeTablePanel extends JComponent {
    EmployeeTablePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = GraphicsUtil.prepare(graphics);

        drawHeaders(g);
        drawDivider(g);
        drawPlaceholderRows(g);

        g.dispose();
    }

    private void drawHeaders(Graphics2D g) {
        g.setColor(DesignTokens.BLACK);
        g.setFont(FontProvider.openSans(13, Font.BOLD));

        g.drawString("Employee No.", 22, 21);
        g.drawString("Name", 149, 21);
        g.drawString("Status", 288, 21);
        g.drawString("Position", 448, 21);

        g.drawString("Immediate", 607, 11);
        g.drawString("Supervisor", 607, 32);

        g.drawString("Role", 745, 21);
    }

    private void drawDivider(Graphics2D g) {
        g.setColor(DesignTokens.BLACK);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(4, 54, 862, 54);
    }

    private void drawPlaceholderRows(Graphics2D g) {
        g.setColor(DesignTokens.LIGHT_GRAY);

        g.fillRect(4, 113, 861, 53);
        g.fillRect(1, 223, 864, 55);
        g.fillRect(4, 332, 861, 49);
        g.fillRect(1, 435, 864, 55);
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
    ADD,
    UPDATE,
    DELETE,
    REFRESH
}

final class IconPainter {
    private IconPainter() {}

    static void draw(Graphics2D source, IconType type, int x, int y, int size, Color color) {
        Graphics2D g = (Graphics2D) source.create();

        g.setColor(color);
        g.setStroke(new BasicStroke(1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.translate(x, y);

        switch (type) {
            case DASHBOARD -> drawDashboard(g, size);
            case EMPLOYEES -> drawEmployees(g, size);
            case PAYROLL -> drawPayroll(g, size);
            case REQUESTS -> drawRequests(g, size);
            case ATTENDANCE -> drawAttendance(g, size);
            case HELP -> drawHelp(g, size);
            case LOGOUT -> drawLogout(g, size);
            case SEARCH -> drawSearch(g, size);
            case ADD -> drawAdd(g, size);
            case UPDATE -> drawUpdate(g, size);
            case DELETE -> drawDelete(g, size);
            case REFRESH -> drawRefresh(g, size);
        }

        g.dispose();
    }

    private static void drawDashboard(Graphics2D g, int s) {
        int box = s / 3;
        int gap = s / 5;

        g.drawRoundRect(1, 1, box, box, 2, 2);
        g.drawRoundRect(box + gap, 1, box, box, 2, 2);
        g.drawRoundRect(1, box + gap, box, box, 2, 2);
        g.drawRoundRect(box + gap, box + gap, box, box, 2, 2);
    }

    private static void drawEmployees(Graphics2D g, int s) {
        g.drawOval(1, 3, 8, 8);

        Path2D body = new Path2D.Double();
        body.moveTo(0, 19);
        body.curveTo(1, 13, 10, 13, 11, 19);
        g.draw(body);

        g.drawLine(15, 6, s, 6);
        g.drawLine(15, 11, s - 2, 11);
        g.drawLine(15, 16, s, 16);
    }

    private static void drawPayroll(Graphics2D g, int s) {
        g.drawRoundRect(2, 1, s - 5, s - 3, 1, 1);

        g.drawLine(6, 6, s - 6, 6);
        g.drawLine(6, 11, s - 6, 11);
        g.drawLine(6, 16, s - 6, 16);

        g.drawOval(5, 5, 2, 2);
        g.drawOval(5, 10, 2, 2);
        g.drawOval(5, 15, 2, 2);
    }

    private static void drawRequests(Graphics2D g, int s) {
        g.drawRoundRect(2, 1, s - 5, s - 3, 1, 1);
        g.drawLine(2, 8, s - 3, 8);
        g.drawLine(2, 15, s - 3, 15);

        g.drawLine(8, 5, s - 9, 5);
        g.drawLine(8, 12, s - 9, 12);
    }

    private static void drawAttendance(Graphics2D g, int s) {
        g.drawRoundRect(2, 4, s - 4, s - 5, 1, 1);
        g.drawLine(2, 9, s - 2, 9);

        g.drawLine(6, 1, 6, 6);
        g.drawLine(11, 1, 11, 6);
        g.drawLine(16, 1, 16, 6);
    }

    private static void drawHelp(Graphics2D g, int s) {
        Path2D cloud = new Path2D.Double();

        cloud.moveTo(3, 16);
        cloud.curveTo(1, 12, 5, 9, 8, 10);
        cloud.curveTo(9, 5, 16, 5, 17, 10);
        cloud.curveTo(21, 9, 23, 12, 21, 16);
        cloud.closePath();

        g.draw(cloud);
    }

    private static void drawLogout(Graphics2D g, int s) {
        g.drawRect(3, 2, 11, s - 4);

        g.drawLine(13, s / 2, s - 2, s / 2);
        g.drawLine(s - 6, s / 2 - 4, s - 2, s / 2);
        g.drawLine(s - 6, s / 2 + 4, s - 2, s / 2);
    }

    private static void drawSearch(Graphics2D g, int s) {
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.draw(new Ellipse2D.Double(1, 1, s - 7, s - 7));
        g.drawLine(s - 6, s - 6, s - 1, s - 1);
    }

    private static void drawAdd(Graphics2D g, int s) {
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.drawLine(s / 2, 1, s / 2, s - 1);
        g.drawLine(1, s / 2, s - 1, s / 2);
    }

    private static void drawUpdate(Graphics2D g, int s) {
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D pencil = new Path2D.Double();
        pencil.moveTo(3, s - 4);
        pencil.lineTo(4, s - 1);
        pencil.lineTo(7, s - 2);
        pencil.lineTo(s - 2, 5);
        pencil.lineTo(s - 5, 2);
        pencil.closePath();

        g.draw(pencil);
        g.drawLine(s - 6, 4, s - 3, 7);
    }

    private static void drawDelete(Graphics2D g, int s) {
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.drawLine(3, 4, s - 3, 4);
        g.drawLine(6, 2, s - 6, 2);

        g.drawRect(5, 5, s - 10, s - 4);

        g.drawLine(8, 7, 8, s - 2);
        g.drawLine(s / 2, 7, s / 2, s - 2);
        g.drawLine(s - 8, 7, s - 8, s - 2);
    }

    private static void drawRefresh(Graphics2D g, int s) {
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.draw(new Arc2D.Double(2, 2, s - 4, s - 4, 35, 285, Arc2D.OPEN));

        Path2D arrow = new Path2D.Double();
        arrow.moveTo(s - 4, 3);
        arrow.lineTo(s - 1, 7);
        arrow.lineTo(s - 6, 7);
        g.draw(arrow);
    }
}

final class GraphicsUtil {
    private GraphicsUtil() {}

    static Graphics2D prepare(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        return g;
    }
}

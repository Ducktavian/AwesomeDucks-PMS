/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Payroll;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EmployeePayroll {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeePayrollFrame frame = new EmployeePayrollFrame();
            frame.setVisible(true);
        });
    }
}

final class EmployeePayrollFrame extends JFrame {
    public EmployeePayrollFrame() {
        setTitle("MotorPH - Employee Payroll");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        EmployeePayrollPanel panel = new EmployeePayrollPanel();
        setContentPane(panel);

        pack();
        setLocationRelativeTo(null);
    }
}

final class EmployeePayrollPanel extends JPanel {
    public EmployeePayrollPanel() {
        setPreferredSize(new Dimension(1280, 800));
        setLayout(null);
        setBackground(Color.WHITE);

        SidebarPanel sidebarPanel = new SidebarPanel();
        sidebarPanel.setBounds(0, 0, 256, 800);
        add(sidebarPanel);

        SearchBox searchBox = new SearchBox();
        searchBox.setBounds(335, 118, 305, 39);
        add(searchBox);

        ActionButton viewButton = new ActionButton("View", UiIcon.SEARCH);
        viewButton.setBounds(1019, 159, 88, 37);
        add(viewButton);

        ActionButton refreshButton = new ActionButton("Refresh", UiIcon.REFRESH);
        refreshButton.setBounds(1111, 159, 89, 37);
        add(refreshButton);

        ProfilePanel profilePanel = new ProfilePanel();
        profilePanel.setBounds(1070, 38, 135, 62);
        add(profilePanel);

        PayrollTableSkeleton tableSkeleton = new PayrollTableSkeleton();
        tableSkeleton.setBounds(335, 216, 870, 475);
        add(tableSkeleton);
    }
}

final class SidebarPanel extends JPanel {
    public SidebarPanel() {
        setLayout(null);
        setBackground(UIStyle.NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setForeground(Color.WHITE);
        logo.setFont(UIStyle.LOGO_FONT);
        logo.setBounds(48, 61, 160, 38);
        add(logo);

        addNavItem("Dashboard", UiIcon.DASHBOARD, false, 48, 154);
        addNavItem("Employees", UiIcon.EMPLOYEES, false, 48, 210);
        addNavItem("Payroll", UiIcon.PAYROLL, false, 48, 250);
        addNavItem("Requests", UiIcon.REQUESTS, true, 48, 294);
        addNavItem("Attendance", UiIcon.ATTENDANCE, false, 48, 339);

        addNavItem("Help Center", UiIcon.HELP, false, 48, 660);
        addNavItem("Log Out", UiIcon.LOGOUT, false, 48, 702);
    }

    private void addNavItem(String text, UiIcon icon, boolean active, int x, int y) {
        NavItem navItem = new NavItem(text, icon, active);
        navItem.setBounds(x, y, 175, 30);
        add(navItem);
    }
}

final class NavItem extends JComponent {
    private final String text;
    private final UiIcon icon;
    private final boolean active;

    public NavItem(String text, UiIcon icon, boolean active) {
        this.text = text;
        this.icon = icon;
        this.active = active;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        VectorIcon.draw(g, icon, 0, 4, 22, Color.WHITE);

        g.setColor(Color.WHITE);
        g.setFont(active ? UIStyle.NAV_ACTIVE_FONT : UIStyle.NAV_FONT);
        g.drawString(text, 48, 20);

        g.dispose();
    }
}

final class SearchBox extends JPanel {
    private final JTextField textField;

    public SearchBox() {
        setLayout(null);
        setOpaque(false);

        textField = new PlaceholderTextField("Search");
        textField.setFont(UIStyle.SEARCH_FONT);
        textField.setForeground(Color.BLACK);
        textField.setBorder(null);
        textField.setOpaque(false);
        textField.setBounds(36, 1, 255, 36);
        add(textField);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        g.setColor(UIStyle.SEARCH_BORDER);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        VectorIcon.draw(g, UiIcon.SEARCH, 12, 10, 18, UIStyle.PLACEHOLDER_GRAY);

        g.dispose();
    }
}

final class PlaceholderTextField extends JTextField {
    private final String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g = (Graphics2D) graphics.create();
            UIStyle.enableAntialiasing(g);

            g.setFont(getFont());
            g.setColor(UIStyle.PLACEHOLDER_GRAY);
            g.drawString(placeholder, 0, 24);

            g.dispose();
        }
    }
}

final class ActionButton extends JComponent {
    private final String label;
    private final UiIcon icon;
    private boolean hovered;

    public ActionButton(String label, UiIcon icon) {
        this.label = label;
        this.icon = icon;
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

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(hovered ? UIStyle.NAVY_HOVER : UIStyle.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());

        VectorIcon.draw(g, icon, 13, 10, 17, Color.WHITE);

        g.setFont(UIStyle.BUTTON_FONT);
        g.setColor(Color.WHITE);

        FontMetrics metrics = g.getFontMetrics();
        int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(label, 47, textY);

        g.dispose();
    }
}

final class ProfilePanel extends JComponent {
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(UIStyle.NAVY);
        g.setFont(UIStyle.PROFILE_NAME_FONT);
        g.drawString("Name", 12, 26);

        g.setColor(UIStyle.PROFILE_POSITION_GRAY);
        g.setFont(UIStyle.PROFILE_POSITION_FONT);
        g.drawString("Position", 8, 48);

        g.setColor(UIStyle.NAVY);
        g.fillOval(74, 2, 56, 56);

        g.dispose();
    }
}

final class PayrollTableSkeleton extends JComponent {
    private final String[] headers = {
            "Payslip ID",
            "Employee ID",
            "Start Date",
            "End Date",
            "Gross Pay",
            "Deduction",
            "Allowance",
            "Net Pay"
    };

    private final int[] headerXPositions = {
            21, 128, 247, 355, 451, 558, 667, 774
    };

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        drawTableHeaders(g);
        drawHeaderSeparator(g);
        drawPlaceholderRows(g);

        g.dispose();
    }

    private void drawTableHeaders(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(UIStyle.TABLE_HEADER_FONT);

        for (int i = 0; i < headers.length; i++) {
            g.drawString(headers[i], headerXPositions[i], 14);
        }
    }

    private void drawHeaderSeparator(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(3, 37, 858, 4);
    }

    private void drawPlaceholderRows(Graphics2D g) {
        g.setColor(UIStyle.ROW_GRAY);

        g.fillRect(3, 94, 862, 49);
        g.fillRect(0, 196, 865, 57);
        g.fillRect(3, 305, 862, 50);
        g.fillRect(3, 411, 862, 50);
    }
}

enum UiIcon {
    DASHBOARD,
    EMPLOYEES,
    PAYROLL,
    REQUESTS,
    ATTENDANCE,
    HELP,
    LOGOUT,
    SEARCH,
    REFRESH
}

final class VectorIcon {
    private VectorIcon() {
    }

    public static void draw(Graphics2D g, UiIcon icon, int x, int y, int size, Color color) {
        Graphics2D iconGraphics = (Graphics2D) g.create();
        UIStyle.enableAntialiasing(iconGraphics);

        iconGraphics.setColor(color);
        iconGraphics.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (icon) {
            case DASHBOARD -> drawDashboard(iconGraphics, x, y, size);
            case EMPLOYEES -> drawEmployees(iconGraphics, x, y, size);
            case PAYROLL -> drawPayroll(iconGraphics, x, y, size);
            case REQUESTS -> drawRequests(iconGraphics, x, y, size);
            case ATTENDANCE -> drawAttendance(iconGraphics, x, y, size);
            case HELP -> drawHelp(iconGraphics, x, y, size);
            case LOGOUT -> drawLogout(iconGraphics, x, y, size);
            case SEARCH -> drawSearch(iconGraphics, x, y, size);
            case REFRESH -> drawRefresh(iconGraphics, x, y, size);
        }

        iconGraphics.dispose();
    }

    private static void drawDashboard(Graphics2D g, int x, int y, int s) {
        int box = s / 3;
        int gap = s / 5;

        g.drawRoundRect(x, y, box, box, 2, 2);
        g.drawRoundRect(x + box + gap, y, box, box, 2, 2);
        g.drawRoundRect(x, y + box + gap, box, box, 2, 2);
        g.drawRoundRect(x + box + gap, y + box + gap, box, box, 2, 2);
    }

    private static void drawEmployees(Graphics2D g, int x, int y, int s) {
        g.drawOval(x + 2, y + 1, 9, 9);
        g.drawArc(x, y + 10, 14, 11, 0, 180);
        g.drawLine(x + 17, y + 5, x + s, y + 5);
        g.drawLine(x + 17, y + 10, x + s, y + 10);
        g.drawLine(x + 17, y + 15, x + s - 4, y + 15);
    }

    private static void drawPayroll(Graphics2D g, int x, int y, int s) {
        g.drawRoundRect(x + 1, y + 1, s - 4, s - 3, 2, 2);
        g.drawRect(x + 5, y + 5, 4, 4);
        g.drawLine(x + 12, y + 6, x + s - 6, y + 6);
        g.drawLine(x + 5, y + 13, x + s - 6, y + 13);
        g.drawLine(x + 5, y + 17, x + s - 8, y + 17);
    }

    private static void drawRequests(Graphics2D g, int x, int y, int s) {
        g.drawRect(x + 1, y + 1, s - 4, s - 3);
        g.drawLine(x + 5, y + 7, x + s - 7, y + 7);
        g.drawLine(x + 5, y + 12, x + s - 7, y + 12);
        g.drawLine(x + 5, y + 17, x + s - 10, y + 17);
    }

    private static void drawAttendance(Graphics2D g, int x, int y, int s) {
        g.drawRoundRect(x + 1, y + 3, s - 3, s - 3, 2, 2);
        g.drawLine(x + 1, y + 8, x + s - 2, y + 8);
        g.drawLine(x + 5, y, x + 5, y + 5);
        g.drawLine(x + s - 6, y, x + s - 6, y + 5);
        g.drawLine(x + 6, y + 13, x + 8, y + 13);
        g.drawLine(x + 12, y + 13, x + 14, y + 13);
        g.drawLine(x + 6, y + 18, x + 8, y + 18);
    }

    private static void drawHelp(Graphics2D g, int x, int y, int s) {
        g.drawArc(x + 1, y + 8, 8, 8, 90, 180);
        g.drawArc(x + 6, y + 3, 12, 12, 20, 210);
        g.drawArc(x + 13, y + 8, 8, 8, -90, 180);
        g.drawLine(x + 4, y + 16, x + s - 4, y + 16);
    }

    private static void drawLogout(Graphics2D g, int x, int y, int s) {
        g.drawRect(x + 3, y + 1, 12, s - 3);
        g.drawLine(x + 15, y + 7, x + s - 2, y + 7);
        g.drawLine(x + s - 6, y + 3, x + s - 2, y + 7);
        g.drawLine(x + s - 6, y + 11, x + s - 2, y + 7);
        g.drawLine(x + 15, y + 16, x + 18, y + 16);
    }

    private static void drawSearch(Graphics2D g, int x, int y, int s) {
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(x, y, s - 6, s - 6);
        g.drawLine(x + s - 7, y + s - 7, x + s - 1, y + s - 1);
    }

    private static void drawRefresh(Graphics2D g, int x, int y, int s) {
        g.drawArc(x + 2, y + 2, s - 4, s - 4, 45, 260);
        g.drawLine(x + s - 4, y + 2, x + s - 4, y + 7);
        g.drawLine(x + s - 4, y + 2, x + s - 9, y + 2);
    }
}

final class UIStyle {
    private UIStyle() {
    }

    static final Color NAVY = new Color(2, 19, 98);
    static final Color NAVY_HOVER = new Color(5, 27, 118);
    static final Color ROW_GRAY = new Color(217, 217, 217);
    static final Color SEARCH_BORDER = new Color(212, 212, 212);
    static final Color PLACEHOLDER_GRAY = new Color(205, 205, 205);
    static final Color PROFILE_POSITION_GRAY = new Color(150, 150, 150);

    static final Font LOGO_FONT = new Font("Segoe UI", Font.BOLD, 28);
    static final Font NAV_FONT = new Font("Open Sans", Font.PLAIN, 18);
    static final Font NAV_ACTIVE_FONT = new Font("Open Sans", Font.BOLD, 18);
    static final Font SEARCH_FONT = new Font("Open Sans", Font.PLAIN, 18);
    static final Font BUTTON_FONT = new Font("Open Sans", Font.PLAIN, 13);
    static final Font TABLE_HEADER_FONT = new Font("Open Sans", Font.BOLD, 13);
    static final Font PROFILE_NAME_FONT = new Font("Open Sans", Font.BOLD, 18);
    static final Font PROFILE_POSITION_FONT = new Font("Open Sans", Font.PLAIN, 16);

    static void enableAntialiasing(Graphics2D g) {
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
    }
}

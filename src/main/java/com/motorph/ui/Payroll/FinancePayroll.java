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

public class FinancePayroll {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinancePayrollFrame frame = new FinancePayrollFrame();
            frame.setVisible(true);
        });
    }
}

final class FinancePayrollFrame extends JFrame {
    public FinancePayrollFrame() {
        setTitle("MotorPH - Finance Payroll");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        FinancePayrollPanel panel = new FinancePayrollPanel();
        setContentPane(panel);

        pack();
        setLocationRelativeTo(null);
    }
}

final class FinancePayrollPanel extends JPanel {
    public FinancePayrollPanel() {
        setPreferredSize(new Dimension(1280, 800));
        setLayout(null);
        setBackground(Color.WHITE);

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, 256, 800);
        add(sidebar);

        SearchBox searchBox = new SearchBox();
        searchBox.setBounds(335, 97, 305, 39);
        add(searchBox);

        DropdownBox employeeDropdown = new DropdownBox("Employee");
        employeeDropdown.setBounds(335, 159, 108, 36);
        add(employeeDropdown);

        ActionButton addButton = new ActionButton("Add", UiIcon.PLUS);
        addButton.setBounds(833, 159, 89, 37);
        add(addButton);

        ActionButton updateButton = new ActionButton("Update", UiIcon.PENCIL);
        updateButton.setBounds(926, 159, 88, 37);
        add(updateButton);

        ActionButton deleteButton = new ActionButton("Delete", UiIcon.TRASH);
        deleteButton.setBounds(1018, 159, 89, 37);
        add(deleteButton);

        ActionButton refreshButton = new ActionButton("Refresh", UiIcon.REFRESH);
        refreshButton.setBounds(1111, 159, 89, 37);
        add(refreshButton);

        ProfilePanel profilePanel = new ProfilePanel();
        profilePanel.setBounds(1070, 38, 135, 62);
        add(profilePanel);

        PayrollTableSkeleton table = new PayrollTableSkeleton();
        table.setBounds(335, 216, 870, 475);
        add(table);
    }
}

final class SidebarPanel extends JPanel {
    public SidebarPanel() {
        setLayout(null);
        setBackground(UIStyle.NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setForeground(Color.WHITE);
        logo.setFont(UIStyle.LOGO_FONT);
        logo.setBounds(48, 61, 150, 38);
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
        NavItem item = new NavItem(text, icon, active);
        item.setBounds(x, y, 170, 30);
        add(item);
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
        FontMetrics fm = g.getFontMetrics();

        int textX = 48;
        int textY = 19;
        g.drawString(text, textX, textY);

        g.dispose();
    }
}

final class SearchBox extends JPanel {
    private final JTextField field;

    public SearchBox() {
        setLayout(null);
        setOpaque(false);

        field = new PlaceholderTextField("Search");
        field.setFont(UIStyle.PLACEHOLDER_FONT);
        field.setForeground(UIStyle.TEXT_DARK);
        field.setBorder(null);
        field.setOpaque(false);
        field.setBounds(36, 1, 255, 36);
        add(field);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        g.setColor(UIStyle.BORDER_LIGHT);
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
            FontMetrics fm = g.getFontMetrics();
            g.drawString(placeholder, 0, 24);
            g.dispose();
        }
    }
}

final class DropdownBox extends JComponent {
    private final String label;

    public DropdownBox(String label) {
        this.label = label;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.setColor(UIStyle.BORDER_EXTRA_LIGHT);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.setFont(UIStyle.SMALL_FONT);
        g.setColor(UIStyle.FILTER_GRAY);
        g.drawString(label, 12, 24);

        VectorIcon.draw(g, UiIcon.CHEVRON, getWidth() - 25, 13, 14, UIStyle.FILTER_GRAY);

        g.dispose();
    }
}

final class ActionButton extends JComponent {
    private final String text;
    private final UiIcon icon;
    private boolean hovering;

    public ActionButton(String text, UiIcon icon) {
        this.text = text;
        this.icon = icon;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);

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
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(hovering ? UIStyle.NAVY_HOVER : UIStyle.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());

        VectorIcon.draw(g, icon, 14, 10, 17, Color.WHITE);

        g.setColor(Color.WHITE);
        g.setFont(UIStyle.BUTTON_FONT);

        FontMetrics fm = g.getFontMetrics();
        int textX = 48;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        g.drawString(text, textX, textY);

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

    private final int[] headerX = {
            21, 128, 247, 355, 451, 558, 667, 774
    };

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        drawHeaders(g);
        drawSeparator(g);
        drawRows(g);

        g.dispose();
    }

    private void drawHeaders(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(UIStyle.TABLE_HEADER_FONT);

        for (int i = 0; i < headers.length; i++) {
            g.drawString(headers[i], headerX[i], 14);
        }
    }

    private void drawSeparator(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(3, 37, 858, 4);
    }

    private void drawRows(Graphics2D g) {
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
    PLUS,
    PENCIL,
    TRASH,
    REFRESH,
    SEARCH,
    CHEVRON
}

final class VectorIcon {
    private VectorIcon() {
    }

    public static void draw(Graphics2D g, UiIcon icon, int x, int y, int size, Color color) {
        Graphics2D copy = (Graphics2D) g.create();
        UIStyle.enableAntialiasing(copy);

        copy.setColor(color);
        copy.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (icon) {
            case DASHBOARD -> drawDashboard(copy, x, y, size);
            case EMPLOYEES -> drawEmployees(copy, x, y, size);
            case PAYROLL -> drawPayroll(copy, x, y, size);
            case REQUESTS -> drawRequests(copy, x, y, size);
            case ATTENDANCE -> drawAttendance(copy, x, y, size);
            case HELP -> drawHelp(copy, x, y, size);
            case LOGOUT -> drawLogout(copy, x, y, size);
            case PLUS -> drawPlus(copy, x, y, size);
            case PENCIL -> drawPencil(copy, x, y, size);
            case TRASH -> drawTrash(copy, x, y, size);
            case REFRESH -> drawRefresh(copy, x, y, size);
            case SEARCH -> drawSearch(copy, x, y, size);
            case CHEVRON -> drawChevron(copy, x, y, size);
        }

        copy.dispose();
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

    private static void drawPlus(Graphics2D g, int x, int y, int s) {
        int center = s / 2;
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x + center, y + 2, x + center, y + s - 2);
        g.drawLine(x + 2, y + center, x + s - 2, y + center);
    }

    private static void drawPencil(Graphics2D g, int x, int y, int s) {
        g.drawLine(x + 3, y + s - 4, x + s - 4, y + 3);
        g.drawLine(x + 6, y + s - 2, x + s - 1, y + 7);
        g.drawLine(x + s - 5, y + 2, x + s - 1, y + 6);
        g.drawLine(x + 3, y + s - 4, x + 2, y + s - 1);
        g.drawLine(x + 2, y + s - 1, x + 6, y + s - 2);
    }

    private static void drawTrash(Graphics2D g, int x, int y, int s) {
        g.drawRect(x + 4, y + 6, s - 8, s - 7);
        g.drawLine(x + 2, y + 5, x + s - 2, y + 5);
        g.drawLine(x + 7, y + 2, x + s - 7, y + 2);
        g.drawLine(x + 8, y + 9, x + 8, y + s - 4);
        g.drawLine(x + s / 2, y + 9, x + s / 2, y + s - 4);
        g.drawLine(x + s - 8, y + 9, x + s - 8, y + s - 4);
    }

    private static void drawRefresh(Graphics2D g, int x, int y, int s) {
        g.drawArc(x + 2, y + 2, s - 4, s - 4, 45, 260);
        g.drawLine(x + s - 4, y + 2, x + s - 4, y + 7);
        g.drawLine(x + s - 4, y + 2, x + s - 9, y + 2);
    }

    private static void drawSearch(Graphics2D g, int x, int y, int s) {
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(x, y, s - 6, s - 6);
        g.drawLine(x + s - 7, y + s - 7, x + s - 1, y + s - 1);
    }

    private static void drawChevron(Graphics2D g, int x, int y, int s) {
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x + 1, y + 3, x + s / 2, y + s - 4);
        g.drawLine(x + s / 2, y + s - 4, x + s - 1, y + 3);
    }
}

final class UIStyle {
    private UIStyle() {
    }

    static final Color NAVY = new Color(2, 19, 98);
    static final Color NAVY_HOVER = new Color(6, 26, 115);
    static final Color ROW_GRAY = new Color(217, 217, 217);
    static final Color BORDER_LIGHT = new Color(212, 212, 212);
    static final Color BORDER_EXTRA_LIGHT = new Color(230, 230, 230);
    static final Color PLACEHOLDER_GRAY = new Color(205, 205, 205);
    static final Color FILTER_GRAY = new Color(218, 218, 218);
    static final Color TEXT_DARK = new Color(0, 0, 0);
    static final Color PROFILE_POSITION_GRAY = new Color(150, 150, 150);

    static final Font LOGO_FONT = new Font("Segoe UI", Font.BOLD, 28);
    static final Font NAV_FONT = new Font("Open Sans", Font.PLAIN, 18);
    static final Font NAV_ACTIVE_FONT = new Font("Open Sans", Font.BOLD, 18);
    static final Font PLACEHOLDER_FONT = new Font("Open Sans", Font.PLAIN, 18);
    static final Font SMALL_FONT = new Font("Open Sans", Font.PLAIN, 13);
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

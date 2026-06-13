/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Employees;

/**
 *
 * @author Admin
 */
import com.motorph.ui.Employees.AppFonts;
import com.motorph.ui.Employees.ProfileBadge;
import com.motorph.ui.Employees.AppColors;
import com.motorph.ui.Employees.SidebarPanel;
import com.motorph.ui.Employees.EmployeeTablePanel;
import com.motorph.ui.Employees.SearchBox;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class EMPLOYEES {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH - Employees");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setResizable(false);
            frame.setContentPane(new EmployeeEmployeesView());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

final class EmployeeEmployeesView extends JPanel {
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 800;

    EmployeeEmployeesView() {
        setLayout(null);
        setBackground(AppColors.WHITE);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, 257, 800);
        add(sidebar);

        SearchBox searchBox = new SearchBox("Search");
        searchBox.setBounds(335, 118, 305, 39);
        add(searchBox);

        ProfileBadge profileBadge = new ProfileBadge("Name", "Position");
        profileBadge.setBounds(1077, 40, 123, 60);
        add(profileBadge);

        UpdateButton updateButton = new UpdateButton("Update");
        updateButton.setBounds(1112, 159, 88, 36);
        add(updateButton);

        EmployeeTablePanel tablePanel = new EmployeeTablePanel();
        tablePanel.setBounds(335, 217, 865, 492);
        add(tablePanel);
    }
}

final class AppColors {
    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = Color.BLACK;
    static final Color TABLE_GRAY = new Color(217, 217, 217);
    static final Color LIGHT_GRAY_TEXT = new Color(150, 150, 150);
    static final Color SEARCH_BORDER = new Color(210, 210, 210);
    static final Color SEARCH_PLACEHOLDER = new Color(215, 215, 215);
    static final Color PROFILE_NAME = new Color(12, 0, 72);

    private AppColors() {
    }
}

final class AppFonts {
    static final int HEADING_1 = 28;
    static final int HEADING_2 = 18;
    static final int PARAGRAPH = 15;

    private AppFonts() {
    }

    static Font headerBold(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    static Font headerPlain(int size) {
        return new Font("Segoe UI", Font.PLAIN, size);
    }

    static Font textPlain(int size) {
        return new Font("Open Sans", Font.PLAIN, size);
    }

    static Font textBold(int size) {
        return new Font("Open Sans", Font.BOLD, size);
    }
}

enum IconType {
    DASHBOARD,
    EMPLOYEES,
    PAYROLL,
    REQUESTS,
    ATTENDANCE,
    HELP,
    LOGOUT
}

final class NavItem {
    private final String label;
    private final IconType iconType;
    private final int centerY;
    private final boolean active;

    NavItem(String label, IconType iconType, int centerY, boolean active) {
        this.label = label;
        this.iconType = iconType;
        this.centerY = centerY;
        this.active = active;
    }

    String getLabel() {
        return label;
    }

    IconType getIconType() {
        return iconType;
    }

    int getCenterY() {
        return centerY;
    }

    boolean isActive() {
        return active;
    }
}

final class SidebarPanel extends JPanel {
    private final List<NavItem> navigationItems;

    SidebarPanel() {
        setOpaque(false);
        navigationItems = new ArrayList<>();

        navigationItems.add(new NavItem("Dashboard", IconType.DASHBOARD, 169, false));
        navigationItems.add(new NavItem("Employees", IconType.EMPLOYEES, 225, true));
        navigationItems.add(new NavItem("Payroll", IconType.PAYROLL, 264, false));
        navigationItems.add(new NavItem("Requests", IconType.REQUESTS, 307, false));
        navigationItems.add(new NavItem("Attendance", IconType.ATTENDANCE, 351, false));

        navigationItems.add(new NavItem("Help Center", IconType.HELP, 671, false));
        navigationItems.add(new NavItem("Log Out", IconType.LOGOUT, 713, false));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawLogo(g2);
        drawNavigation(g2);

        g2.dispose();
    }

    private void drawLogo(Graphics2D g2) {
        g2.setColor(AppColors.WHITE);
        g2.setFont(AppFonts.headerBold(AppFonts.HEADING_1));
        g2.drawString("MotorPH", 48, 89);
    }

    private void drawNavigation(Graphics2D g2) {
        for (NavItem item : navigationItems) {
            drawIcon(g2, item.getIconType(), 49, item.getCenterY());

            g2.setColor(AppColors.WHITE);
            g2.setFont(item.isActive()
                    ? AppFonts.textBold(17)
                    : AppFonts.textPlain(17));

            g2.drawString(item.getLabel(), 96, item.getCenterY() + 6);
        }
    }

    private void drawIcon(Graphics2D g2, IconType type, int x, int centerY) {
        g2.setColor(AppColors.WHITE);
        g2.setStroke(new BasicStroke(1.15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case DASHBOARD:
                drawDashboardIcon(g2, x, centerY);
                break;
            case EMPLOYEES:
                drawEmployeesIcon(g2, x, centerY);
                break;
            case PAYROLL:
                drawPayrollIcon(g2, x, centerY);
                break;
            case REQUESTS:
                drawRequestsIcon(g2, x, centerY);
                break;
            case ATTENDANCE:
                drawAttendanceIcon(g2, x, centerY);
                break;
            case HELP:
                drawHelpIcon(g2, x, centerY);
                break;
            case LOGOUT:
                drawLogoutIcon(g2, x, centerY);
                break;
            default:
                break;
        }
    }

    private void drawDashboardIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 10;
        g2.drawRoundRect(x, y, 8, 8, 2, 2);
        g2.drawRoundRect(x + 13, y, 8, 8, 2, 2);
        g2.drawRoundRect(x, y + 13, 8, 8, 2, 2);
        g2.drawRoundRect(x + 13, y + 13, 8, 8, 2, 2);
    }

    private void drawEmployeesIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 11;
        g2.drawOval(x + 2, y, 9, 9);
        g2.drawArc(x, y + 9, 14, 13, 0, 180);

        g2.drawLine(x + 17, y + 4, x + 24, y + 4);
        g2.drawLine(x + 17, y + 10, x + 24, y + 10);
        g2.drawLine(x + 17, y + 16, x + 24, y + 16);
    }

    private void drawPayrollIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 10;
        g2.drawRoundRect(x, y, 18, 20, 1, 1);
        g2.drawRect(x + 4, y + 4, 3, 3);
        g2.drawLine(x + 10, y + 5, x + 15, y + 5);
        g2.drawRect(x + 4, y + 10, 3, 3);
        g2.drawLine(x + 10, y + 11, x + 15, y + 11);
        g2.drawRect(x + 4, y + 15, 3, 3);
        g2.drawLine(x + 10, y + 16, x + 15, y + 16);
    }

    private void drawRequestsIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 10;
        g2.drawRect(x, y, 19, 20);
        g2.drawLine(x + 3, y + 6, x + 16, y + 6);
        g2.drawLine(x + 5, y + 11, x + 14, y + 11);
        g2.drawLine(x + 7, y + 15, x + 12, y + 15);
        g2.drawLine(x + 4, y - 3, x + 4, y + 2);
        g2.drawLine(x + 15, y - 3, x + 15, y + 2);
    }

    private void drawAttendanceIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 11;
        g2.drawRoundRect(x, y + 3, 19, 17, 1, 1);
        g2.drawLine(x, y + 8, x + 19, y + 8);
        g2.drawLine(x + 4, y, x + 4, y + 5);
        g2.drawLine(x + 8, y, x + 8, y + 5);
        g2.drawLine(x + 12, y, x + 12, y + 5);
        g2.drawLine(x + 16, y, x + 16, y + 5);
    }

    private void drawHelpIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 9;

        Path2D cloud = new Path2D.Double();
        cloud.moveTo(x + 1, y + 12);
        cloud.curveTo(x + 1, y + 8, x + 5, y + 7, x + 7, y + 8);
        cloud.curveTo(x + 8, y + 4, x + 13, y + 3, x + 15, y + 7);
        cloud.curveTo(x + 19, y + 6, x + 22, y + 9, x + 21, y + 13);
        cloud.lineTo(x + 4, y + 13);
        cloud.curveTo(x + 2, y + 13, x + 1, y + 13, x + 1, y + 12);
        g2.draw(cloud);
    }

    private void drawLogoutIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 10;

        g2.drawRect(x + 2, y, 12, 20);
        g2.drawLine(x + 14, y + 10, x + 22, y + 10);
        g2.drawLine(x + 18, y + 6, x + 22, y + 10);
        g2.drawLine(x + 18, y + 14, x + 22, y + 10);
        g2.drawLine(x + 8, y + 5, x + 8, y + 15);
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

final class SearchBox extends JTextField {
    private final String placeholder;

    SearchBox(String placeholder) {
        this.placeholder = placeholder;

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 37, 1, 10));
        setFont(AppFonts.textPlain(18));
        setForeground(Color.BLACK);
        setCaretColor(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        super.paintComponent(graphics);

        if (getText().isEmpty()) {
            g2.setColor(AppColors.SEARCH_PLACEHOLDER);
            g2.setFont(AppFonts.textPlain(18));
            g2.drawString(placeholder, 37, 26);
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.SEARCH_BORDER);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        drawSearchIcon(g2);

        g2.dispose();
    }

    private void drawSearchIcon(Graphics2D g2) {
        g2.setColor(AppColors.SEARCH_PLACEHOLDER);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.drawOval(12, 11, 14, 14);
        g2.drawLine(23, 23, 29, 29);
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

final class ProfileBadge extends JPanel {
    private final String name;
    private final String position;

    ProfileBadge(String name, String position) {
        this.name = name;
        this.position = position;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.PROFILE_NAME);
        g2.setFont(AppFonts.headerBold(AppFonts.HEADING_2));
        g2.drawString(name, 5, 24);

        g2.setColor(AppColors.LIGHT_GRAY_TEXT);
        g2.setFont(AppFonts.textPlain(AppFonts.PARAGRAPH));
        g2.drawString(position, 0, 47);

        g2.setColor(AppColors.NAVY);
        g2.fillOval(67, 0, 56, 56);

        g2.dispose();
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

final class UpdateButton extends JComponent {
    private final String text;
    private boolean hover;

    UpdateButton(String text) {
        this.text = text;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (hover) {
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        drawPencilIcon(g2, 14, 11);

        g2.setColor(AppColors.WHITE);
        g2.setFont(AppFonts.textPlain(12));
        g2.drawString(text, 38, 23);

        g2.dispose();
    }

    private void drawPencilIcon(Graphics2D g2, int x, int y) {
        g2.setColor(AppColors.WHITE);
        g2.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.drawLine(x, y + 12, x + 12, y);
        g2.drawLine(x + 3, y + 13, x, y + 12);
        g2.drawLine(x + 12, y, x + 15, y + 3);
        g2.drawLine(x + 3, y + 13, x + 15, y + 1);
        g2.drawLine(x + 11, y + 1, x + 14, y + 4);
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

final class EmployeeTablePanel extends JPanel {
    private static final String[] HEADERS = {
            "Employee No.",
            "Name",
            "Status",
            "Position",
            "Immediate\nSupervisor",
            "Role"
    };

    private static final int[] HEADER_X = {
            20, 147, 287, 447, 606, 744
    };

    private static final int[][] ROWS = {
            {3, 113, 862, 55},
            {0, 224, 865, 56},
            {3, 332, 862, 51},
            {0, 436, 865, 56}
    };

    private static final int[] COLUMN_LINES = {
            128, 267, 428, 587, 727
    };

    EmployeeTablePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        drawHeaders(g2);
        drawDivider(g2);
        drawPlaceholderRows(g2);

        g2.dispose();
    }

    private void drawHeaders(Graphics2D g2) {
        g2.setColor(AppColors.BLACK);
        g2.setFont(AppFonts.textBold(13));

        for (int i = 0; i < HEADERS.length; i++) {
            drawHeaderText(g2, HEADERS[i], HEADER_X[i], 22);
        }
    }

    private void drawHeaderText(Graphics2D g2, String text, int x, int y) {
        String[] lines = text.split("\\n");

        for (int i = 0; i < lines.length; i++) {
            g2.drawString(lines[i], x, y + (i * 17));
        }
    }

    private void drawDivider(Graphics2D g2) {
        g2.setColor(AppColors.BLACK);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2.drawLine(2, 55, 862, 55);
    }

    private void drawPlaceholderRows(Graphics2D g2) {
        for (int[] row : ROWS) {
            int x = row[0];
            int y = row[1];
            int width = row[2];
            int height = row[3];

            g2.setColor(AppColors.TABLE_GRAY);
            g2.fillRect(x, y, width, height);

            g2.setColor(new Color(214, 214, 214));
            for (int lineX : COLUMN_LINES) {
                if (lineX >= x && lineX <= x + width) {
                    g2.drawLine(lineX, y, lineX, y + height);
                }
            }
        }
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

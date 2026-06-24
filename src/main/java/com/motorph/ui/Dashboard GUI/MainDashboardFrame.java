package com.motorph.ui;

import model.Employee;

import RBAC.Permission;

import repository.EmployeeRepository;
import repository.CsvLeaveRepository;

import service.AuthorizationService;
import service.LeaveService;
import service.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainDashboardFrame extends JFrame {

    private static final String CARD_DASHBOARD = "dashboard";
    private static final String CARD_EMPLOYEES = "employees";
    private static final String CARD_PAYROLL = "payroll";
    private static final String CARD_REQUESTS = "requests";
    private static final String CARD_ATTENDANCE = "attendance";
    private static final String CARD_USERS = "users";

    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 800;
    private static final int SIDEBAR_WIDTH = 257;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_HOVER = new Color(225, 230, 255);
    private static final Color MUTED_TEXT = new Color(150, 150, 150);
    private static final Color NO_ACCESS_BG = new Color(248, 248, 248);

    private static final Font FONT_APP_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_NAV = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font FONT_NAV_ACTIVE = new Font("Segoe UI", Font.BOLD, 18);

    private final EmployeeRepository employeeRepo;
    private final Path employeeCsvPath;
    private final Employee currentUser;
    private final LeaveService leaveService;

    private final String currentUserId;
    private final String currentUserName;
    private final String currentUserDepartment;
    private final String currentUserPosition;

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final List<SidebarItem> sidebarItems;

    private String activeCardName = CARD_DASHBOARD;

    public MainDashboardFrame(EmployeeRepository employeeRepo, Path employeeCsvPath, Employee loggedInEmployee) {
        super("MotorPH Payroll System");

        this.employeeRepo = employeeRepo;
        this.employeeCsvPath = employeeCsvPath;
        this.currentUser = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser()
                : loggedInEmployee;

        this.leaveService = new LeaveService(new CsvLeaveRepository());

        this.currentUserId = currentUser != null ? safe(currentUser.getId()) : "";
        this.currentUserName = currentUser != null
                ? (safe(currentUser.getFirstName()) + " " + safe(currentUser.getLastName())).trim()
                : "";
        this.currentUserDepartment = currentUser != null ? safe(currentUser.getDepartment()) : "";
        this.currentUserPosition = currentUser != null ? safe(currentUser.getPosition()) : "";

        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.sidebarItems = new ArrayList<>();

        applyGlobalFont();
        initFrame();
        initCards();

        showCard(CARD_DASHBOARD);
    }

    private void applyGlobalFont() {
        Font segoe = new Font("Segoe UI", Font.PLAIN, 14);

        UIManager.put("Label.font", segoe);
        UIManager.put("Button.font", segoe);
        UIManager.put("Table.font", segoe);
        UIManager.put("TableHeader.font", segoe.deriveFont(Font.BOLD, 14f));
        UIManager.put("TextField.font", segoe);
        UIManager.put("PasswordField.font", segoe);
        UIManager.put("ComboBox.font", segoe);
        UIManager.put("OptionPane.font", segoe);
        UIManager.put("OptionPane.messageFont", segoe);
        UIManager.put("OptionPane.buttonFont", segoe);
    }

    private void initFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        JPanel root = new JPanel(new BorderLayout());
        root.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        root.setBackground(WHITE);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createMainArea(), BorderLayout.CENTER);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, FRAME_HEIGHT));

        JLabel logoLabel = new JLabel("MotorPH");
        logoLabel.setBounds(49, 64, 150, 35);
        logoLabel.setForeground(TEXT_LIGHT);
        logoLabel.setFont(FONT_APP_TITLE);
        sidebar.add(logoLabel);

        addSidebarItem(sidebar, "Dashboard", SidebarIcon.DASHBOARD, 156, CARD_DASHBOARD,
                () -> showCard(CARD_DASHBOARD));

        addSidebarItem(sidebar, "Employees", SidebarIcon.EMPLOYEES, 199, CARD_EMPLOYEES,
                () -> showCard(CARD_EMPLOYEES));

        addSidebarItem(sidebar, "Payroll", SidebarIcon.PAYROLL, 242, CARD_PAYROLL,
                () -> showCard(CARD_PAYROLL));

        addSidebarItem(sidebar, "Requests", SidebarIcon.REQUESTS, 285, CARD_REQUESTS,
                () -> showCard(CARD_REQUESTS));

        addSidebarItem(sidebar, "Attendance", SidebarIcon.ATTENDANCE, 328, CARD_ATTENDANCE,
                () -> showCard(CARD_ATTENDANCE));

        addSidebarItem(sidebar, "Users", SidebarIcon.USERS, 371, CARD_USERS,
                () -> showCard(CARD_USERS));

        addSidebarItem(sidebar, "Settings", SidebarIcon.SETTINGS, 637, null,
                () -> showInfoDialog("Settings"));

        addSidebarItem(sidebar, "Help Center", SidebarIcon.HELP, 680, null,
                () -> showInfoDialog("Help Center"));

        addSidebarItem(sidebar, "Log Out", SidebarIcon.LOGOUT, 717, null, this::handleLogout);

        return sidebar;
    }

    private void addSidebarItem(
            JPanel sidebar,
            String text,
            SidebarIcon icon,
            int y,
            String cardName,
            Runnable action
    ) {
        SidebarItem item = new SidebarItem(text, icon, cardName, action);
        item.setBounds(45, y, 170, 30);

        if (cardName != null) {
            sidebarItems.add(item);
        }

        sidebar.add(item);
    }

    private JPanel createMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(WHITE);
        mainArea.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.setBackground(WHITE);
        contentPanel.setOpaque(true);

        mainArea.add(contentPanel, BorderLayout.CENTER);

        return mainArea;
    }

    private void initCards() {
        addCard(CARD_DASHBOARD, createDashboardCard());
        addCard(CARD_EMPLOYEES, createEmployeesCard());
        addCard(CARD_PAYROLL, createPayrollCard());
        addCard(CARD_REQUESTS, createRequestsCard());
        addCard(CARD_ATTENDANCE, createAttendanceCard());
        addCard(CARD_USERS, createUsersCard());
    }

    private void addCard(String cardName, JPanel panel) {
        contentPanel.add(panel, cardName);
    }

    private JPanel createDashboardCard() {
        return new DashboardPanel();
    }

    private JPanel createEmployeesCard() {
        if (!canViewEmployees()) {
            return createNoAccessCard("Employees");
        }

        return new EmployeeManagementPanel(employeeRepo, employeeCsvPath, currentUser);
    }

    private JPanel createPayrollCard() {
        return new PayrollPanel(currentUser, employeeRepo);
    }

    private JPanel createRequestsCard() {
        return new EmployeeLeavesPanel(
                leaveService,
                employeeRepo,
                currentUserId,
                currentUserName,
                currentUserDepartment,
                currentUserPosition
        );
    }

    private JPanel createAttendanceCard() {
        return new AttendancePanel();
    }

    private JPanel createUsersCard() {
        return createNoAccessCard("Users");
    }

    private boolean canViewEmployees() {
        return AuthorizationService.hasPermission(currentUser, Permission.VIEW_EMPLOYEE_LIST)
                || AuthorizationService.hasPermission(currentUser, Permission.VIEW_EMPLOYEE);
    }

    private JPanel createNoAccessCard(String pageName) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(NO_ACCESS_BG);

        JPanel messagePanel = new JPanel();
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(pageName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(NAVY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("This section is not available for your current account.");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageLabel.setForeground(MUTED_TEXT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messagePanel.add(titleLabel);
        messagePanel.add(Box.createVerticalStrut(8));
        messagePanel.add(messageLabel);

        panel.add(messagePanel);

        return panel;
    }

    private void showCard(String cardName) {
        activeCardName = cardName;
        cardLayout.show(contentPanel, cardName);
        refreshSidebarSelection();
    }

    private void refreshSidebarSelection() {
        for (SidebarItem item : sidebarItems) {
            item.setActive(activeCardName.equals(item.getCardName()));
        }
    }

    private void showInfoDialog(String title) {
        JOptionPane.showMessageDialog(
                this,
                title + " section is not available yet.",
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Log Out",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        SessionManager.logout();
        dispose();

        SwingUtilities.invokeLater(() -> {
            JFrame dummy = new JFrame();
            dummy.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            LoginDialog loginDialog = new LoginDialog(dummy);
            loginDialog.setVisible(true);

            if (!loginDialog.isSucceeded()) {
                dummy.dispose();
                System.exit(0);
                return;
            }

            Employee loggedInEmployee = loginDialog.getLoggedInEmployee();
            dummy.dispose();

            MainDashboardLauncher.launch(loggedInEmployee);
        });
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private enum SidebarIcon {
        DASHBOARD,
        EMPLOYEES,
        PAYROLL,
        REQUESTS,
        ATTENDANCE,
        USERS,
        SETTINGS,
        HELP,
        LOGOUT
    }

    private static final class SidebarItem extends JComponent {

        private final String text;
        private final SidebarIcon icon;
        private final String cardName;
        private final Runnable action;

        private boolean active;
        private boolean hovered;

        private SidebarItem(String text, SidebarIcon icon, String cardName, Runnable action) {
            this.text = text;
            this.icon = icon;
            this.cardName = cardName;
            this.action = action;

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

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (action != null) {
                        action.run();
                    }
                }
            });
        }

        private String getCardName() {
            return cardName;
        }

        private void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Color paintColor = hovered ? TEXT_HOVER : TEXT_LIGHT;

            drawSidebarIcon(g2, icon, 0, 4, paintColor);

            g2.setColor(paintColor);
            g2.setFont(active ? FONT_NAV_ACTIVE : FONT_NAV);

            FontMetrics fm = g2.getFontMetrics();
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(text, 50, textY);

            g2.dispose();
        }

        private void drawSidebarIcon(Graphics2D g2, SidebarIcon icon, int x, int y, Color color) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (icon) {
                case DASHBOARD:
                    drawDashboardIcon(g2, x, y);
                    break;
                case EMPLOYEES:
                    drawEmployeesIcon(g2, x, y);
                    break;
                case PAYROLL:
                    drawPayrollIcon(g2, x, y);
                    break;
                case REQUESTS:
                    drawRequestsIcon(g2, x, y);
                    break;
                case ATTENDANCE:
                    drawAttendanceIcon(g2, x, y);
                    break;
                case USERS:
                    drawUsersIcon(g2, x, y);
                    break;
                case SETTINGS:
                    drawSettingsIcon(g2, x, y);
                    break;
                case HELP:
                    drawHelpIcon(g2, x, y);
                    break;
                case LOGOUT:
                    drawLogoutIcon(g2, x, y);
                    break;
                default:
                    break;
            }
        }

        private void drawDashboardIcon(Graphics2D g2, int x, int y) {
            int size = 8;
            int gap = 5;

            g2.drawRoundRect(x + 4, y + 1, size, size, 2, 2);
            g2.drawRoundRect(x + 4 + size + gap, y + 1, size, size, 2, 2);
            g2.drawRoundRect(x + 4, y + 1 + size + gap, size, size, 2, 2);
            g2.drawRoundRect(x + 4 + size + gap, y + 1 + size + gap, size, size, 2, 2);
        }

        private void drawEmployeesIcon(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 6, y + 2, 9, 9);
            g2.drawArc(x + 4, y + 12, 13, 11, 0, 180);
            g2.drawLine(x + 19, y + 6, x + 27, y + 6);
            g2.drawLine(x + 19, y + 12, x + 27, y + 12);
            g2.drawLine(x + 19, y + 18, x + 24, y + 18);
        }

        private void drawPayrollIcon(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 4, y + 2, 19, 20, 2, 2);
            g2.drawRect(x + 7, y + 6, 4, 4);
            g2.drawLine(x + 14, y + 7, x + 20, y + 7);
            g2.drawRect(x + 7, y + 14, 4, 4);
            g2.drawLine(x + 14, y + 15, x + 20, y + 15);
        }

        private void drawRequestsIcon(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 5, y + 2, 18, 21, 2, 2);
            g2.drawLine(x + 8, y + 7, x + 20, y + 7);
            g2.drawLine(x + 8, y + 13, x + 16, y + 13);
            g2.drawLine(x + 8, y + 18, x + 18, y + 18);
        }

        private void drawAttendanceIcon(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 4, y + 4, 20, 18, 2, 2);
            g2.drawLine(x + 4, y + 9, x + 24, y + 9);
            g2.drawLine(x + 9, y + 1, x + 9, y + 6);
            g2.drawLine(x + 19, y + 1, x + 19, y + 6);
        }

        private void drawUsersIcon(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 5, y + 9, 8, 8);
            g2.drawArc(x + 3, y + 18, 12, 8, 0, 180);

            g2.drawOval(x + 13, y + 4, 7, 7);
            g2.drawArc(x + 11, y + 12, 11, 8, 0, 180);

            g2.drawOval(x + 20, y + 2, 6, 6);
            g2.drawLine(x + 23, y + 0, x + 23, y + 2);
            g2.drawLine(x + 23, y + 8, x + 23, y + 10);
            g2.drawLine(x + 19, y + 5, x + 17, y + 5);
            g2.drawLine(x + 29, y + 5, x + 27, y + 5);
        }

        private void drawSettingsIcon(Graphics2D g2, int x, int y) {
            int cx = x + 14;
            int cy = y + 13;

            g2.drawOval(cx - 7, cy - 7, 14, 14);
            g2.drawOval(cx - 3, cy - 3, 6, 6);

            for (int i = 0; i < 8; i++) {
                double angle = Math.toRadians(i * 45);
                int x1 = cx + (int) Math.round(Math.cos(angle) * 9);
                int y1 = cy + (int) Math.round(Math.sin(angle) * 9);
                int x2 = cx + (int) Math.round(Math.cos(angle) * 11);
                int y2 = cy + (int) Math.round(Math.sin(angle) * 11);
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        private void drawHelpIcon(Graphics2D g2, int x, int y) {
            g2.drawArc(x + 4, y + 10, 9, 9, 90, 180);
            g2.drawArc(x + 10, y + 6, 12, 12, 50, 210);
            g2.drawArc(x + 18, y + 11, 8, 8, 0, 180);
            g2.drawLine(x + 8, y + 19, x + 23, y + 19);
        }

        private void drawLogoutIcon(Graphics2D g2, int x, int y) {
            g2.drawRect(x + 4, y + 3, 13, 19);
            g2.drawLine(x + 17, y + 12, x + 27, y + 12);
            g2.drawLine(x + 23, y + 8, x + 27, y + 12);
            g2.drawLine(x + 23, y + 16, x + 27, y + 12);
        }
    }

    private static final class CircleAvatar extends JPanel {

        private CircleAvatar() {
            setOpaque(false);
            setPreferredSize(new Dimension(56, 56));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape circle = new Ellipse2D.Double(0, 0, getWidth(), getHeight());
            g2.setColor(NAVY);
            g2.fill(circle);

            g2.dispose();
        }
    }

    private static final class RoundedPanel extends JPanel {

        private final Color backgroundColor;
        private final int radius;

        private RoundedPanel(Color backgroundColor, int radius) {
            this.backgroundColor = backgroundColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}

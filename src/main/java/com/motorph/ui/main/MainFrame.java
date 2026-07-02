package com.motorph.ui.main;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;

import com.motorph.model.Employee;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.service.EmployeeService;
import com.motorph.ui.attendance.AttendancePanel;
import com.motorph.ui.dashboard.DashboardPanel;
import com.motorph.ui.employee.EmployeePanel;
import com.motorph.ui.helpcenter.HelpCenterPanel;
import com.motorph.ui.it.UserAccountPanel; // NEW
import com.motorph.ui.login.Login;
import com.motorph.ui.payroll.PayrollPanel;
import com.motorph.ui.request.RequestPanel;
import com.motorph.ui.settings.SettingsPanel;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

public class MainFrame extends JFrame {

    private PayrollPanel payrollPanel;
    private EmployeePanel employeePanel;

    public static final Color SIDEBAR_BG = new Color(5, 22, 103);
    public static final Color ACCENT_W = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(210, 218, 240);
    public static final Color TEXT_DISABLED = new Color(95, 108, 150);
    public static final Color CONTENT_BG = Color.WHITE;
    public static final Color NAVY = new Color(5, 24, 108);

    private static final int SIDEBAR_WIDTH = 257;
    private static final int NAV_FONT_SIZE = 16;
    private static final int LOGO_FONT_SIZE = 26;

    private static final String[][] MAIN_NAV = {
        {"Dashboard", "Dashboard-Icon.png"},
        {"Employees", "Employees-Icon.png"},
        {"Payroll", "Payroll-Icon.png"},
        {"Requests", "Requests-icon.png"},
        {"Attendance", "Attendance-Icon.png"},
        {"Users", "Users-Icon.png"},           // NEW: IT user-account management tab
    };

    private static final String[][] BOTTOM_NAV = {
        {"Settings", "Settings-Icon.png"},
        {"Help Center", "HelpCenter-Icon.png"},
        {"Log Out", "Logout-icon.png"},
    };

    private String activeNav = "Dashboard";
    private JPanel navPanel;
    private JPanel bottomNavPanel;
    private JPanel sidebarRoot;
    private JPanel contentCards;
    private CardLayout cardLayout;

    private static final String IMG_DIR = resolveImgDir();

    private static String resolveImgDir() {
        String[] candidates = {
            "src/main/java/com/motorph/img/",
            "src/main/resources/com/motorph/img/",
            "main/java/com/motorph/img/",
            "com/motorph/img/",
            "img/",
        };

        for (String c : candidates) {
            if (new File(c).isDirectory()) {
                return c;
            }
        }

        return "src/main/java/com/motorph/img/";
    }

    public MainFrame() {
        setTitle("MotorPH - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        add(root, BorderLayout.CENTER);

        sidebarRoot = buildSidebar();
        root.add(sidebarRoot, BorderLayout.WEST);

        root.add(buildRightArea(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel buildRightArea() {
        JPanel rightArea = new JPanel(new BorderLayout());
        rightArea.setBackground(Color.WHITE);

        rightArea.add(buildSharedTopBar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(Color.WHITE);

        payrollPanel = new PayrollPanel();

        contentCards.add(buildDashboardPanel(), "Dashboard");
        employeePanel = new EmployeePanel();
        contentCards.add(employeePanel, "Employees");
        contentCards.add(payrollPanel, "Payroll");
        contentCards.add(new RequestPanel(), "Requests");
        contentCards.add(new AttendancePanel(), "Attendance");
        contentCards.add(new UserAccountPanel(), "Users"); // NEW: IT user-account management panel
        contentCards.add(new SettingsPanel(), "Settings");
        contentCards.add(new HelpCenterPanel(), "Help Center");
        contentCards.add(placeholderPanel("Log Out"), "Log Out");

        rightArea.add(contentCards, BorderLayout.CENTER);
        cardLayout.show(contentCards, "Dashboard");

        return rightArea;
    }

    private JPanel buildSharedTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 80));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(190, 190, 190)));

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        profile.setOpaque(false);
        profile.setBorder(new EmptyBorder(0, 0, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("<html><u>Name</u></html>");
        name.setForeground(NAVY);
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        name.setAlignmentX(Component.RIGHT_ALIGNMENT);
        name.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        name.setToolTipText("View my employee information");
        name.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openCurrentUserProfile();
            }
        });

        JLabel position = new JLabel("Position");
        position.setForeground(Color.GRAY);
        position.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        position.setAlignmentX(Component.RIGHT_ALIGNMENT);

        UserAccount user = Session.getCurrentUser();
        if (user != null) {
            Employee emp = AppContext.getEmployeeService()
                    .findEmployee(String.valueOf(user.getEmployeeId()));
            String fullName = emp != null ? emp.getFullName() : user.getUsername();
            String pos = emp != null ? emp.getPosition() : (user.getRole() == null ? "" : user.getRole().toString());
            name.setText("<html><u>" + fullName + "</u></html>");
            position.setText(pos);
        }

        textPanel.add(name);
        textPanel.add(position);

        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(47, 47));
        avatar.setIcon(new CircleIcon(NAVY, 47));

        profile.add(textPanel);
        profile.add(avatar);

        topBar.add(profile, BorderLayout.EAST);
        return topBar;
    }

    private void openCurrentUserProfile() {
        if (employeePanel == null || Session.getCurrentUser() == null) {
            return;
        }

        activeNav = "Employees";
        cardLayout.show(contentCards, "Employees");
        employeePanel.openCurrentEmployeeDetails();
        setTitle("MotorPH - Employees");
        rebuildNavPanels();
    }

    private JPanel buildDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(Color.WHITE);

        dashboard.add(new DashboardPanel(), BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setOpaque(false);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 42));
        logoPanel.setOpaque(false);

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(new Font("Segoe UI", Font.BOLD, LOGO_FONT_SIZE));
        logo.setForeground(ACCENT_W);
        logoPanel.add(logo);

        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(28, 0, 0, 0));

        for (String[] item : MAIN_NAV) {
            navPanel.add(buildNavItem(item[0], item[1]));
        }

        bottomNavPanel = new JPanel();
        bottomNavPanel.setLayout(new BoxLayout(bottomNavPanel, BoxLayout.Y_AXIS));
        bottomNavPanel.setOpaque(false);
        bottomNavPanel.setBorder(new EmptyBorder(0, 0, 42, 0));

        for (String[] item : BOTTOM_NAV) {
            bottomNavPanel.add(buildNavItem(item[0], item[1]));
        }

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(logoPanel, BorderLayout.NORTH);
        topSection.add(navPanel, BorderLayout.CENTER);

        sidebar.add(topSection, BorderLayout.CENTER);
        sidebar.add(bottomNavPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    // NEW: tells whether the logged-in user's role may open a given nav tab.
    // Tabs the role can't open stay visible but are shown disabled (greyed out,
    // not clickable) rather than removed, so the sidebar layout never shifts.
    // Employees is HR/Admin territory; Users is IT user-account management.
    private boolean canAccessNavItem(String label) {
        UserAccount user = Session.getCurrentUser();
        Role role = user == null ? null : user.getRole();

        switch (label) {
            case "Employees":
                // All roles can access the Employees tab; the panel itself restricts
                // what each role can see (view-all vs view-mine) and do (edit vs read-only).
                return true;
            case "Users":
                return role == Role.ADMIN || role == Role.IT;
            default:
                return true;
        }
    }

    private JPanel buildNavItem(String label, String iconFile) {
        boolean isActive = label.equals(activeNav);

        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (label.equals(activeNav)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 32));
                    g2.fillRoundRect(18, 2, getWidth() - 36, getHeight() - 4, 7, 7);
                    g2.dispose();
                }
            }
        };

        boolean accessible = canAccessNavItem(label);

        item.setOpaque(false);
        item.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 40));
        item.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 40));
        item.setCursor(Cursor.getPredefinedCursor(
                accessible ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));

        if (!accessible) {
            item.setToolTipText("Your role doesn't have access to " + label + ".");
        }

        JLabel icon = loadIcon(iconFile);
        icon.setPreferredSize(new Dimension(22, 22));

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, NAV_FONT_SIZE));
        textLbl.setForeground(!accessible ? TEXT_DISABLED : isActive ? ACCENT_W : TEXT_MUTED);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        content.setOpaque(false);
        content.add(icon);
        content.add(textLbl);

        item.add(content);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (accessible) {
                    textLbl.setForeground(ACCENT_W);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (accessible && !label.equals(activeNav)) {
                    textLbl.setForeground(TEXT_MUTED);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Disabled tabs stay put but ignore clicks.
                if (!accessible) {
                    return;
                }

                if ("Log Out".equals(label)) {
                    Session.clear();
                    dispose();
                    SwingUtilities.invokeLater(() -> new Login(null).setVisible(true));
                    return;
                }

                activeNav = label;
                cardLayout.show(contentCards, label);
                setTitle("MotorPH - " + label);
                rebuildNavPanels();
            }
        });

        return item;
    }

    private JLabel loadIcon(String filename) {
        JLabel lbl = new JLabel();
        lbl.setPreferredSize(new Dimension(22, 22));

        try {
            java.net.URL url = getClass().getClassLoader()
                    .getResource("com/motorph/img/" + filename);

            if (url == null) {
                File f = new File(IMG_DIR + filename);
                if (f.exists()) {
                    url = f.toURI().toURL();
                }
            }

            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
            System.err.println("Icon not found: " + filename + " — " + e.getMessage());
        }

        return lbl;
    }

    private void rebuildNavPanels() {
        navPanel.removeAll();

        for (String[] item : MAIN_NAV) {
            navPanel.add(buildNavItem(item[0], item[1]));
        }

        bottomNavPanel.removeAll();

        for (String[] item : BOTTOM_NAV) {
            bottomNavPanel.add(buildNavItem(item[0], item[1]));
        }

        sidebarRoot.revalidate();
        sidebarRoot.repaint();
    }

    private JPanel placeholderPanel(String name) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(name + " — coming soon", JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(new Color(120, 130, 150));

        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private static class CircleIcon implements Icon {
        private final Color color;
        private final int size;

        CircleIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.fillOval(x, y, size, size);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(MainFrame::new);
    }
}

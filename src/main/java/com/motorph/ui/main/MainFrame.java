package com.motorph.ui.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.ui.attendance.AttendancePanel;
import com.motorph.ui.dashboard.DashboardPanel;
import com.motorph.ui.employee.EmployeePanel;
import com.motorph.ui.payroll.PayrollPanel;
import com.motorph.ui.request.RequestPanel;
import com.motorph.ui.it.ITDashboard;
import com.motorph.ui.helpcenter.HelpCenterPanel;
import com.motorph.ui.login.Login;
import com.motorph.ui.settings.SettingsPanel;
import com.motorph.util.Session;

public class MainFrame extends JFrame {

    private PayrollPanel payrollPanel;

    public static final Color SIDEBAR_BG = new Color(5, 22, 103);
    public static final Color ACCENT_W = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(210, 218, 240);
    public static final Color CONTENT_BG = Color.WHITE;

    private static final int SIDEBAR_WIDTH = 257;
    private static final int NAV_FONT_SIZE = 16;
    private static final int LOGO_FONT_SIZE = 26;

    private static final String[][] MAIN_NAV = {
        { "Dashboard",  "Dashboard-Icon.png" },
        { "Employees",  "Employees-Icon.png" },
        { "Payroll",    "Payroll-Icon.png" },
        { "Requests",   "Requests-icon.png" },
        { "Attendance", "Attendance-Icon.png" },
    };

    private static final String[][] BOTTOM_NAV = {
        { "Settings",    "Settings-Icon.png" },
        { "Help Center", "HelpCenter-Icon.png" },
        { "Log Out",     "Logout-icon.png" },
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

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(Color.WHITE);

        payrollPanel = new PayrollPanel();

        contentCards.add(buildDashboardPanel(), "Dashboard");
        contentCards.add(new EmployeePanel(), "Employees");
        contentCards.add(payrollPanel, "Payroll");
        contentCards.add(new RequestPanel(), "Requests");
        contentCards.add(new AttendancePanel(), "Attendance");
        contentCards.add(new SettingsPanel(), "Settings");
        contentCards.add(new HelpCenterPanel(), "Help Center");
        contentCards.add(placeholderPanel("Log Out"), "Log Out");

        root.add(contentCards, BorderLayout.CENTER);
        cardLayout.show(contentCards, "Dashboard");

        setVisible(true);
    }

    private JPanel buildDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(Color.WHITE);

        CardLayout roleLayout = new CardLayout();
        JPanel roleCards = new JPanel(roleLayout);
        roleCards.setBackground(Color.WHITE);

        roleCards.add(placeholderPanel("Employee Dashboard"), "Employee");
        roleCards.add(placeholderPanel("Finance Dashboard"), "Finance");
        roleCards.add(placeholderPanel("HR Dashboard"), "HR");
        roleCards.add(new ITDashboard(), "IT");
        roleCards.add(new DashboardPanel(), "Admin");

        UserAccount user = Session.getCurrentUser();
        String defaultCard = user != null ? roleToCard(user.getRole()) : "Employee";
        roleLayout.show(roleCards, defaultCard);

        dashboard.add(roleCards, BorderLayout.CENTER);
        return dashboard;
    }

    private String roleToCard(Role role) {
        if (role == null) return "Employee";

        return switch (role) {
            case ADMIN -> "Admin";
            case HR -> "HR";
            case IT -> "IT";
            case FINANCE -> "Finance";
            default -> "Employee";
        };
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

        item.setOpaque(false);
        item.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 40));
        item.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 40));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = loadIcon(iconFile);
        icon.setPreferredSize(new Dimension(22, 22));

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, NAV_FONT_SIZE));
        textLbl.setForeground(isActive ? ACCENT_W : TEXT_MUTED);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        content.setOpaque(false);
        content.add(icon);
        content.add(textLbl);

        item.add(content);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                textLbl.setForeground(ACCENT_W);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!label.equals(activeNav)) {
                    textLbl.setForeground(TEXT_MUTED);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
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

    private boolean isAdminOrIT() {
        UserAccount user = Session.getCurrentUser();
        return user != null && (user.getRole() == Role.ADMIN || user.getRole() == Role.IT);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(MainFrame::new);
    }
}
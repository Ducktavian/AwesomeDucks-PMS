package com.motorph.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.motorph.ui.Admin.Admindashboard;
import com.motorph.ui.IT.ITDashboard;
import com.motorph.ui.IT.ITDisputeList;
import com.motorph.ui.IT.ITUserAccountList;
import com.motorph.ui.Settings.AccountSecurity;

public class MainFrame extends JFrame {

    public static final Color SIDEBAR_BG = new Color(13,  36,  89);
    public static final Color ACCENT_W   = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(180, 190, 210);
    public static final Color CONTENT_BG = new Color(245, 247, 252);
    private static final Color NAVY      = new Color(13,  36,  89);
    private static final Color MUTED     = new Color(120, 130, 150);
    private static final Color DIVIDER   = new Color(220, 225, 235);

    private static final String[] ROLES = {
        "Employee", "Finance", "HR", "IT", "Admin"
    };

    private static final String[][] MAIN_NAV = {
        { "Dashboard",  "Dashboard-Icon.png"  },
        { "Employees",  "Employees-Icon.png"   },
        { "Payroll",    "Payroll-Icon.png"    },
        { "Requests",   "Requests-icon.png"    },
        { "Attendance", "Attendance-Icon.png" },
        { "Users",      "Users-Icon.png"   },
    };
    private static final String[][] BOTTOM_NAV = {
        { "Settings",    "Settings-Icon.png"  },
        { "Help Center", "HelpCenter-Icon.png" },
        { "Log Out",     "Logout-icon.png"     },
    };

    private String     activeNav = "Dashboard";
    private JPanel     navPanel;
    private JPanel     bottomNavPanel;
    private JPanel     sidebarRoot;
    private JPanel     contentCards;
    private CardLayout cardLayout;

    // Resolved once at startup — points to src/main/java/com/motorph/img/
    private static final String IMG_DIR = resolveImgDir();

    private static String resolveImgDir() {
        // Try several candidate roots so it works both from IDE and from jar
        String[] candidates = {
            "src/main/java/com/motorph/img/",
            "main/java/com/motorph/img/",
            "com/motorph/img/",
            "img/",
        };
        for (String c : candidates) {
            if (new File(c).isDirectory()) return c;
        }
        return "src/main/java/com/motorph/img/"; // fallback
    }

    public MainFrame() {
        setTitle("MotorPH - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        add(root, BorderLayout.CENTER);

        sidebarRoot = buildSidebar();
        root.add(sidebarRoot, BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(Color.WHITE);
        root.add(mainArea, BorderLayout.CENTER);

        cardLayout   = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(Color.WHITE);

        contentCards.add(buildDashboardPanel(),           "Dashboard");
        contentCards.add(placeholderPanel("Employees"),   "Employees");
        contentCards.add(placeholderPanel("Payroll"),     "Payroll");
        contentCards.add(placeholderPanel("Requests"),    "Requests");
        contentCards.add(placeholderPanel("Attendance"),  "Attendance");
        contentCards.add(new ITUserAccountList(),          "Users");
        contentCards.add(new AccountSecurity(),           "Settings");
        contentCards.add(new ITDisputeList(),             "Help Center");
        contentCards.add(placeholderPanel("Log Out"),     "Log Out");

        mainArea.add(contentCards, BorderLayout.CENTER);
        cardLayout.show(contentCards, "Dashboard");

        setVisible(true);
    }

    private JPanel buildDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(Color.WHITE);

        CardLayout roleLayout = new CardLayout();
        JPanel roleCards = new JPanel(roleLayout);
        roleCards.setBackground(Color.WHITE);

        roleCards.add(placeholderPanel("Employee"), "Employee");
        roleCards.add(placeholderPanel("Finance"),  "Finance");
        roleCards.add(placeholderPanel("HR"),       "HR");
        roleCards.add(new ITDashboard(),            "IT");
        roleCards.add(new Admindashboard(),         "Admin");

        JComboBox<String> roleBox = new JComboBox<>(ROLES);
        roleBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        roleBox.setForeground(new Color(60, 70, 90));
        roleBox.setBackground(Color.WHITE);
        roleBox.setPreferredSize(new Dimension(110, 30));
        roleBox.setSelectedItem("Admin");
        roleLayout.show(roleCards, "Admin");

        roleBox.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            if (role != null) {
                roleLayout.show(roleCards, role);
            }
        });

        dashboard.add(buildDashboardTopBar(roleBox), BorderLayout.NORTH);
        dashboard.add(roleCards, BorderLayout.CENTER);
        return dashboard;
    }

    private JPanel buildDashboardTopBar(JComboBox<String> roleBox) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, DIVIDER),
            new EmptyBorder(10, 24, 10, 24)
        ));

        JPanel userChip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userChip.setOpaque(false);

        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setOpaque(false);

        JLabel nameLbl = new JLabel("Name");
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLbl.setForeground(NAVY);
        nameLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel posLbl = new JLabel("Position");
        posLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        posLbl.setForeground(MUTED);
        posLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        nameBlock.add(nameLbl);
        nameBlock.add(posLbl);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NAVY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);

        userChip.add(nameBlock);
        userChip.add(avatar);

        bar.add(roleBox,  BorderLayout.WEST);
        bar.add(userChip, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setOpaque(false);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 22));
        logoPanel.setOpaque(false);
        JLabel logo = new JLabel("MotorPH");
        logo.setFont(new Font("SansSerif", Font.BOLD, 19));
        logo.setForeground(ACCENT_W);
        logoPanel.add(logo);

        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        for (String[] item : MAIN_NAV)
            navPanel.add(buildNavItem(item[0], item[1]));

        bottomNavPanel = new JPanel();
        bottomNavPanel.setLayout(new BoxLayout(bottomNavPanel, BoxLayout.Y_AXIS));
        bottomNavPanel.setOpaque(false);
        bottomNavPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        for (String[] item : BOTTOM_NAV)
            bottomNavPanel.add(buildNavItem(item[0], item[1]));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(logoPanel, BorderLayout.NORTH);
        topSection.add(navPanel,  BorderLayout.CENTER);

        sidebar.add(topSection,     BorderLayout.CENTER);
        sidebar.add(bottomNavPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    private JLabel loadIcon(String filename) {
        JLabel lbl = new JLabel();
        lbl.setPreferredSize(new Dimension(18, 18));
        try {
            // 1. Try classpath first (works in jar)
            java.net.URL url = getClass().getClassLoader()
                    .getResource("com/motorph/img/" + filename);

            // 2. Fall back to file system (works in IDE)
            if (url == null) {
                File f = new File(IMG_DIR + filename);
                if (f.exists()) url = f.toURI().toURL();
            }

            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
            System.err.println("Icon not found: " + filename + " — " + e.getMessage());
        }
        return lbl;
    }

    private JPanel buildNavItem(String label, String iconFile) {
        boolean[] active = { label.equals(activeNav) };

        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (active[0]) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(255, 255, 255, 28));
                    g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 8, 8, 8);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 4, 4, getHeight() - 8, 3, 3);
                }
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(190, 42));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        item.add(loadIcon(iconFile));

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("SansSerif", active[0] ? Font.BOLD : Font.PLAIN, 13));
        textLbl.setForeground(active[0] ? ACCENT_W : TEXT_MUTED);
        item.add(textLbl);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!active[0]) textLbl.setForeground(ACCENT_W);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!active[0]) textLbl.setForeground(TEXT_MUTED);
            }
            @Override public void mouseClicked(MouseEvent e) {
                activeNav = label;
                cardLayout.show(contentCards, label);
                setTitle("MotorPH - " + label);
                rebuildNavPanels();
            }
        });

        return item;
    }

    private void rebuildNavPanels() {
        navPanel.removeAll();
        for (String[] item : MAIN_NAV)
            navPanel.add(buildNavItem(item[0], item[1]));

        bottomNavPanel.removeAll();
        for (String[] item : BOTTOM_NAV)
            bottomNavPanel.add(buildNavItem(item[0], item[1]));

        sidebarRoot.revalidate();
        sidebarRoot.repaint();
    }

    private JPanel placeholderPanel(String name) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(name + " — coming soon", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lbl.setForeground(new Color(120, 130, 150));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
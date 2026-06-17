package com.motorph.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.motorph.ui.Admin.DashboardPanel;

/**
 * MainFrame — root application window for MotorPH.
 *
 * Package : ui
 * File    : ui/MainFrame.java
 *
 * Connects to:
 *   ui.Admin.DashboardPanel  — shown when "Dashboard" nav item is clicked
 *
 * All other nav items show a placeholder panel (swap in real panels later).
 *
 * FIX: rebuildNavPanels() now calls revalidate()/repaint() on the sidebar
 *      root so the active-highlight re-renders correctly every time.
 */
public class MainFrame extends JFrame {

    // ── Palette
    public static final Color SIDEBAR_BG = new Color(13,  36,  89);
    public static final Color ACCENT_W   = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(180, 190, 210);
    public static final Color CONTENT_BG = new Color(245, 247, 252);
    public static final Color TOPBAR_BG  = Color.WHITE;
    public static final Color NAVY       = new Color(13,  36,  89);
    public static final Color DIVIDER    = new Color(200, 210, 230);

    // ── Nav definitions
    private static final String[][] MAIN_NAV = {
        {"Dashboard",  "\uD83D\uDCCA"},
        {"Employees",  "\uD83D\uDC65"},
        {"Payroll",    "\uD83D\uDCB0"},
        {"Requests",   "\uD83D\uDCCB"},
        {"Attendance", "\uD83D\uDDD3"},
        {"Users",      "\uD83D\uDC64"},
    };
    private static final String[][] BOTTOM_NAV = {
        {"Settings",    "\u2699"},
        {"Help Center", "\u2601"},
        {"Log Out",     "\u2192"},
    };

    // ── State
    private String     activeNav    = "Dashboard";
    private JPanel     navPanel;
    private JPanel     bottomNavPanel;
    private JPanel     sidebarRoot;   // kept so repaint reaches the whole sidebar
    private JPanel     contentCards;
    private CardLayout cardLayout;

    // ─────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────
    public MainFrame() {
        setTitle("MotorPH – Dashboard");
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
        mainArea.setBackground(CONTENT_BG);
        root.add(mainArea, BorderLayout.CENTER);

        mainArea.add(buildTopBar(), BorderLayout.NORTH);

        // ── Card layout — one panel per nav destination
        cardLayout   = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(CONTENT_BG);

        contentCards.add(new DashboardPanel(),           "Dashboard");
        contentCards.add(placeholderPanel("Employees"),  "Employees");
        contentCards.add(placeholderPanel("Payroll"),    "Payroll");
        contentCards.add(placeholderPanel("Requests"),   "Requests");
        contentCards.add(placeholderPanel("Attendance"), "Attendance");
        contentCards.add(placeholderPanel("Users"),      "Users");
        contentCards.add(placeholderPanel("Settings"),   "Settings");
        contentCards.add(placeholderPanel("Help Center"),"Help Center");
        contentCards.add(placeholderPanel("Log Out"),    "Log Out");

        mainArea.add(contentCards, BorderLayout.CENTER);
        cardLayout.show(contentCards, "Dashboard");

        setVisible(true);
    }

    // ─────────────────────────────────────────────
    // SIDEBAR
    // ─────────────────────────────────────────────
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

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 22));
        logoPanel.setOpaque(false);
        JLabel logo = new JLabel("MotorPH");
        logo.setFont(new Font("SansSerif", Font.BOLD, 19));
        logo.setForeground(ACCENT_W);
        logoPanel.add(logo);

        // Main nav
        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        for (String[] item : MAIN_NAV)
            navPanel.add(buildNavItem(item[0], item[1]));

        // Bottom nav
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

    private JPanel buildNavItem(String label, String icon) {
        boolean[] active = { label.equals(activeNav) };

        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (active[0]) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(255, 255, 255, 28));
                    g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 8, 8, 8);
                    // left accent bar
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 4, 4, getHeight() - 8, 3, 3);
                }
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(190, 42));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        iconLbl.setForeground(active[0] ? ACCENT_W : TEXT_MUTED);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("SansSerif", active[0] ? Font.BOLD : Font.PLAIN, 13));
        textLbl.setForeground(active[0] ? ACCENT_W : TEXT_MUTED);

        item.add(iconLbl);
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
                setTitle("MotorPH – " + label);
                rebuildNavPanels();
            }
        });

        return item;
    }

    /**
     * Clears and rebuilds both nav panels so the active-state highlight
     * moves to the newly selected item. Also repaints the full sidebar.
     */
    private void rebuildNavPanels() {
        navPanel.removeAll();
        for (String[] item : MAIN_NAV)
            navPanel.add(buildNavItem(item[0], item[1]));

        bottomNavPanel.removeAll();
        for (String[] item : BOTTOM_NAV)
            bottomNavPanel.add(buildNavItem(item[0], item[1]));

        // Revalidate + repaint the whole sidebar so highlights update
        sidebarRoot.revalidate();
        sidebarRoot.repaint();
    }

    // ─────────────────────────────────────────────
    // TOP BAR
    // ─────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(TOPBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(DIVIDER);
                g.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 50));
        bar.setBorder(new EmptyBorder(0, 24, 0, 24));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(NAVY);

        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    // ─────────────────────────────────────────────
    // PLACEHOLDER panels  (replace with real panels as you build them)
    // ─────────────────────────────────────────────
    private JPanel placeholderPanel(String name) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CONTENT_BG);
        JLabel lbl = new JLabel(name + " — coming soon", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lbl.setForeground(new Color(120, 130, 150));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // ─────────────────────────────────────────────
    // ENTRY POINT
    // ─────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(MainFrame::new);
    }
}
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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public final class HR {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("Button.select", DesignTokens.NAVY);

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private static final class DesignTokens {
        static final int FRAME_WIDTH = 1280;
        static final int FRAME_HEIGHT = 800;
        static final int SIDEBAR_WIDTH = 257;

        static final Color NAVY = new Color(2, 19, 98);
        static final Color WHITE = Color.WHITE;
        static final Color BLACK = Color.BLACK;
        static final Color LIGHT_GRAY = new Color(217, 217, 217);
        static final Color PLACEHOLDER = new Color(210, 210, 210);
        static final Color PROFILE_MUTED = new Color(160, 160, 160);
        static final Color SEARCH_BORDER = new Color(207, 207, 207);

        static final Font HEADER_1 = new Font("Segoe UI", Font.BOLD, 28);
        static final Font HEADER_2 = new Font("Segoe UI", Font.BOLD, 18);
        static final Font TEXT_18 = new Font("Open Sans", Font.PLAIN, 18);
        static final Font TEXT_18_BOLD = new Font("Open Sans", Font.BOLD, 18);
        static final Font TEXT_15 = new Font("Open Sans", Font.PLAIN, 15);
        static final Font TEXT_13 = new Font("Open Sans", Font.PLAIN, 13);
        static final Font TEXT_13_BOLD = new Font("Open Sans", Font.BOLD, 13);

        private DesignTokens() {}
    }

    private static final class MainFrame extends JFrame {
        MainFrame() {
            super("MotorPH - HR Employees");

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            setSize(DesignTokens.FRAME_WIDTH, DesignTokens.FRAME_HEIGHT);
            setLocationRelativeTo(null);

            RootPanel root = new RootPanel();
            root.setLayout(null);
            setContentPane(root);

            SideBarPanel sideBar = new SideBarPanel();
            sideBar.setBounds(0, 0, DesignTokens.SIDEBAR_WIDTH, DesignTokens.FRAME_HEIGHT);
            root.add(sideBar);

            SearchBox searchBox = new SearchBox("Search");
            searchBox.setBounds(336, 97, 304, 39);
            root.add(searchBox);

            ProfileBadge profile = new ProfileBadge("Name", "Position");
            profile.setBounds(1077, 40, 124, 57);
            root.add(profile);

            addActionButtons(root);

            EmployeeTableCanvas table = new EmployeeTableCanvas();
            table.setBounds(335, 216, 866, 510);
            root.add(table);
        }

        private void addActionButtons(JPanel root) {
            int y = 159;
            int h = 36;

            root.add(new ActionButton("Add", ActionIcon.PLUS), new Rectangle(833, y, 88, h));
            root.add(new ActionButton("Update", ActionIcon.EDIT), new Rectangle(926, y, 87, h));
            root.add(new ActionButton("Delete", ActionIcon.TRASH), new Rectangle(1019, y, 87, h));
            root.add(new ActionButton("Refresh", ActionIcon.REFRESH), new Rectangle(1112, y, 88, h));
        }
    }

    private static final class RootPanel extends JPanel {
        RootPanel() {
            setBackground(DesignTokens.WHITE);
            setOpaque(true);
        }
    }

    private enum NavIcon {
        DASHBOARD,
        EMPLOYEES,
        PAYROLL,
        REQUESTS,
        ATTENDANCE,
        HELP,
        LOGOUT
    }

    private static final class SideBarPanel extends JPanel {
        SideBarPanel() {
            setLayout(null);
            setBackground(DesignTokens.NAVY);
            setOpaque(true);

            JLabel logo = new JLabel("MotorPH");
            logo.setFont(DesignTokens.HEADER_1);
            logo.setForeground(DesignTokens.WHITE);
            logo.setBounds(48, 61, 160, 38);
            add(logo);

            add(new NavItem("Dashboard", NavIcon.DASHBOARD, false, 18), new Rectangle(48, 153, 175, 32));
            add(new NavItem("Employees", NavIcon.EMPLOYEES, true, 18), new Rectangle(48, 210, 175, 32));
            add(new NavItem("Payroll", NavIcon.PAYROLL, false, 18), new Rectangle(48, 251, 175, 32));
            add(new NavItem("Requests", NavIcon.REQUESTS, false, 18), new Rectangle(48, 293, 175, 32));
            add(new NavItem("Attendance", NavIcon.ATTENDANCE, false, 18), new Rectangle(48, 336, 185, 32));

            add(new NavItem("Help Center", NavIcon.HELP, false, 15), new Rectangle(48, 657, 175, 31));
            add(new NavItem("Log Out", NavIcon.LOGOUT, false, 15), new Rectangle(49, 698, 150, 31));
        }
    }

    private static final class NavItem extends JComponent {
        private final String text;
        private final NavIcon icon;
        private final boolean selected;
        private final int fontSize;

        NavItem(String text, NavIcon icon, boolean selected, int fontSize) {
            this.text = text;
            this.icon = icon;
            this.selected = selected;
            this.fontSize = fontSize;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            enableQuality(g2);

            g2.setColor(DesignTokens.WHITE);
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            IconPainter.paintNavigationIcon(g2, icon, 0, 5, 22, 22);

            Font font = selected
                    ? new Font("Open Sans", Font.BOLD, fontSize)
                    : new Font("Open Sans", Font.PLAIN, fontSize);

            g2.setFont(font);

            FontMetrics fm = g2.getFontMetrics();
            int baseline = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + 1;

            int textX = fontSize >= 18 ? 47 : 31;
            g2.drawString(text, textX, baseline);

            g2.dispose();
        }
    }

    private enum ActionIcon {
        PLUS,
        EDIT,
        TRASH,
        REFRESH
    }

    private static final class ActionButton extends JButton {
        private final String label;
        private final ActionIcon icon;
        private boolean hover;

        ActionButton(String label, ActionIcon icon) {
            this.label = label;
            this.icon = icon;

            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
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
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableQuality(g2);

            g2.setColor(DesignTokens.NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (hover) {
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.setColor(DesignTokens.WHITE);
            g2.setStroke(new BasicStroke(1.15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            IconPainter.paintActionIcon(g2, icon, 14, 10, 16, 16);

            g2.setFont(DesignTokens.TEXT_13);

            FontMetrics fm = g2.getFontMetrics();
            int baseline = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            int textX = 47;

            if (icon == ActionIcon.PLUS) {
                textX = 48;
            }

            if (icon == ActionIcon.REFRESH) {
                textX = 37;
            }

            g2.drawString(label, textX, baseline);
            g2.dispose();
        }
    }

    private static final class SearchBox extends JTextField {
        private final String placeholder;

        SearchBox(String placeholder) {
            this.placeholder = placeholder;

            setFont(DesignTokens.TEXT_18);
            setForeground(Color.DARK_GRAY);
            setOpaque(false);
            setBorder(new EmptyBorder(0, 36, 0, 10));
            setCaretColor(DesignTokens.NAVY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableQuality(g2);

            g2.setColor(DesignTokens.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(DesignTokens.SEARCH_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(DesignTokens.PLACEHOLDER);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(12, 11, 14, 14);
            g2.drawLine(23, 23, 30, 30);

            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D textG = (Graphics2D) g.create();
                enableQuality(textG);

                textG.setFont(DesignTokens.TEXT_18);
                textG.setColor(DesignTokens.PLACEHOLDER);

                FontMetrics fm = textG.getFontMetrics();
                int baseline = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                textG.drawString(placeholder, 36, baseline);
                textG.dispose();
            }
        }
    }

    private static final class ProfileBadge extends JComponent {
        private final String name;
        private final String position;

        ProfileBadge(String name, String position) {
            this.name = name;
            this.position = position;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableQuality(g2);

            g2.setColor(DesignTokens.NAVY);
            g2.fillOval(67, 0, 57, 57);

            g2.setColor(DesignTokens.NAVY);
            g2.setFont(DesignTokens.HEADER_2);
            g2.drawString(name, 5, 23);

            g2.setColor(DesignTokens.PROFILE_MUTED);
            g2.setFont(DesignTokens.TEXT_15);
            g2.drawString(position, 0, 47);

            g2.dispose();
        }
    }

    private static final class EmployeeTableCanvas extends JComponent {
        private static final int[] COLUMN_X = {20, 147, 287, 446, 606, 743};
        private static final int[] ROW_Y = {114, 224, 334, 437};
        private static final int[] ROW_H = {55, 57, 50, 56};

        EmployeeTableCanvas() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableQuality(g2);

            g2.setColor(DesignTokens.BLACK);
            g2.setFont(DesignTokens.TEXT_13_BOLD);

            g2.drawString("Employee No.", COLUMN_X[0], 22);
            g2.drawString("Name", COLUMN_X[1], 22);
            g2.drawString("Status", COLUMN_X[2], 22);
            g2.drawString("Position", COLUMN_X[3], 22);
            g2.drawString("Immediate", COLUMN_X[4], 13);
            g2.drawString("Supervisor", COLUMN_X[4], 31);
            g2.drawString("Role", COLUMN_X[5], 22);

            g2.setColor(DesignTokens.BLACK);
            g2.fillRect(2, 55, 862, 3);

            g2.setColor(DesignTokens.LIGHT_GRAY);

            for (int i = 0; i < ROW_Y.length; i++) {
                g2.fillRect(3, ROW_Y[i], 862, ROW_H[i]);
            }

            g2.dispose();
        }
    }

    private static final class IconPainter {
        private IconPainter() {}

        static void paintNavigationIcon(Graphics2D g2, NavIcon icon, int x, int y, int w, int h) {
            switch (icon) {
                case DASHBOARD -> drawDashboard(g2, x, y);
                case EMPLOYEES -> drawEmployees(g2, x, y);
                case PAYROLL -> drawPayroll(g2, x, y);
                case REQUESTS -> drawRequests(g2, x, y);
                case ATTENDANCE -> drawAttendance(g2, x, y);
                case HELP -> drawHelp(g2, x, y);
                case LOGOUT -> drawLogout(g2, x, y);
            }
        }

        static void paintActionIcon(Graphics2D g2, ActionIcon icon, int x, int y, int w, int h) {
            switch (icon) {
                case PLUS -> drawPlus(g2, x, y, w, h);
                case EDIT -> drawEdit(g2, x, y);
                case TRASH -> drawTrash(g2, x, y);
                case REFRESH -> drawRefresh(g2, x, y);
            }
        }

        private static void drawDashboard(Graphics2D g2, int x, int y) {
            int s = 8;
            int gap = 5;

            g2.drawRoundRect(x + 1, y + 1, s, s, 2, 2);
            g2.drawRoundRect(x + 1 + s + gap, y + 1, s, s, 2, 2);
            g2.drawRoundRect(x + 1, y + 1 + s + gap, s, s, 2, 2);
            g2.drawRoundRect(x + 1 + s + gap, y + 1 + s + gap, s, s, 2, 2);
        }

        private static void drawEmployees(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 3, y + 2, 8, 8);

            Path2D body = new Path2D.Double();
            body.moveTo(x + 1, y + 20);
            body.curveTo(x + 2, y + 14, x + 6, y + 11, x + 8, y + 11);
            body.curveTo(x + 12, y + 11, x + 15, y + 14, x + 16, y + 20);
            body.closePath();

            g2.draw(body);
            g2.drawLine(x + 18, y + 5, x + 25, y + 5);
            g2.drawLine(x + 18, y + 11, x + 25, y + 11);
            g2.drawLine(x + 18, y + 17, x + 23, y + 17);
        }

        private static void drawPayroll(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 2, y + 2, 18, 18, 2, 2);
            g2.drawLine(x + 6, y + 7, x + 16, y + 7);
            g2.drawRect(x + 6, y + 11, 3, 3);
            g2.drawRect(x + 12, y + 11, 3, 3);
            g2.drawLine(x + 6, y + 17, x + 16, y + 17);
        }

        private static void drawRequests(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 2, y + 2, 18, 18, 2, 2);
            g2.drawLine(x + 6, y + 7, x + 16, y + 7);
            g2.drawLine(x + 6, y + 12, x + 16, y + 12);
            g2.drawLine(x + 6, y + 16, x + 16, y + 16);
            g2.drawLine(x + 6, y + 7, x + 6, y + 3);
            g2.drawLine(x + 16, y + 7, x + 16, y + 3);
        }

        private static void drawAttendance(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 1, y + 3, 19, 17, 1, 1);
            g2.drawLine(x + 1, y + 7, x + 20, y + 7);

            for (int i = 4; i <= 16; i += 4) {
                g2.drawLine(x + i, y + 1, x + i, y + 5);
            }
        }

        private static void drawHelp(Graphics2D g2, int x, int y) {
            Path2D cloud = new Path2D.Double();

            cloud.moveTo(x + 2, y + 17);
            cloud.lineTo(x + 18, y + 17);
            cloud.curveTo(x + 22, y + 17, x + 23, y + 12, x + 19, y + 10);
            cloud.curveTo(x + 18, y + 5, x + 12, y + 4, x + 9, y + 8);
            cloud.curveTo(x + 6, y + 7, x + 3, y + 9, x + 4, y + 12);
            cloud.curveTo(x + 1, y + 13, x, y + 16, x + 2, y + 17);

            g2.draw(cloud);
        }

        private static void drawLogout(Graphics2D g2, int x, int y) {
            g2.drawRect(x + 2, y + 2, 13, 18);
            g2.drawLine(x + 15, y + 11, x + 23, y + 11);
            g2.drawLine(x + 20, y + 7, x + 24, y + 11);
            g2.drawLine(x + 20, y + 15, x + 24, y + 11);
            g2.drawLine(x + 9, y + 6, x + 15, y + 6);
            g2.drawLine(x + 9, y + 16, x + 15, y + 16);
        }

        private static void drawPlus(Graphics2D g2, int x, int y, int w, int h) {
            g2.drawLine(x + w / 2, y + 2, x + w / 2, y + h - 2);
            g2.drawLine(x + 2, y + h / 2, x + w - 2, y + h / 2);
        }

        private static void drawEdit(Graphics2D g2, int x, int y) {
            Path2D pencil = new Path2D.Double();

            pencil.moveTo(x + 2, y + 13);
            pencil.lineTo(x + 3, y + 16);
            pencil.lineTo(x + 6, y + 15);
            pencil.lineTo(x + 15, y + 6);
            pencil.lineTo(x + 11, y + 2);
            pencil.closePath();

            g2.draw(pencil);
            g2.drawLine(x + 10, y + 4, x + 14, y + 8);
        }

        private static void drawTrash(Graphics2D g2, int x, int y) {
            g2.drawLine(x + 3, y + 4, x + 14, y + 4);
            g2.drawLine(x + 6, y + 2, x + 11, y + 2);
            g2.drawRoundRect(x + 4, y + 5, 9, 11, 1, 1);
            g2.drawLine(x + 7, y + 7, x + 7, y + 14);
            g2.drawLine(x + 10, y + 7, x + 10, y + 14);
        }

        private static void drawRefresh(Graphics2D g2, int x, int y) {
            Arc2D arc = new Arc2D.Double(x + 1, y + 2, 14, 14, 45, 280, Arc2D.OPEN);
            g2.draw(arc);

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(x + 12, y + 2);
            arrow.lineTo(x + 15, y + 2);
            arrow.lineTo(x + 14, y + 5);

            g2.draw(arrow);
        }
    }

    private static void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}

package com.motorph.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

public class TicketDetails extends JDialog {

    // ── Colors ──────────────────────────────────────────────────────────────────
    private static final Color NAVY        = new Color(2, 19, 98);
    private static final Color HOVER_NAVY  = new Color(25, 40, 140);
    private static final Color FIELD_BORDER = new Color(140, 140, 140);
    private static final Color FIELD_BG    = new Color(225, 225, 230);
    private static final Color LABEL_GRAY  = new Color(60, 60, 60);

    public TicketDetails(Window owner) {
        super(owner, "MotorPH Payroll System - Ticket Details", ModalityType.APPLICATION_MODAL);
        setSize(1400, 800);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        JPanel sidebar = createSidebar();
        sidebar.setPreferredSize(new Dimension(257, 800));
        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    // ── Sidebar ─────────────────────────────────────────────────────────────────
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(Color.WHITE);
        logo.setBounds(48, 63, 160, 36);
        sidebar.add(logo);

        addNavItem(sidebar, "Dashboard",  TDLineIcon.Type.DASHBOARD,  158, false);
        addNavItem(sidebar, "Employees",  TDLineIcon.Type.EMPLOYEES,  216, false);
        addNavItem(sidebar, "Payroll",    TDLineIcon.Type.PAYROLL,    255, false);
        addNavItem(sidebar, "Requests",   TDLineIcon.Type.REQUESTS,   300, true);
        addNavItem(sidebar, "Attendance", TDLineIcon.Type.ATTENDANCE, 341, false);

        addNavItem(sidebar, "Help Center", TDLineIcon.Type.HELP,   660, false);
        addNavItem(sidebar, "Log Out",     TDLineIcon.Type.LOGOUT, 702, false);

        return sidebar;
    }

    private void addNavItem(JPanel sidebar, String text, TDLineIcon.Type iconType, int y, boolean active) {
        TDNavigationItem item = new TDNavigationItem(text, iconType, active);
        item.setBounds(48, y, 170, 32);
        sidebar.add(item);
    }

    // ── Profile panel (top-right) ────────────────────────────────────────────────
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(null);
        profilePanel.setOpaque(false);
        profilePanel.setPreferredSize(new Dimension(123, 57));

        JLabel nameLabel = new JLabel("Name", SwingConstants.RIGHT);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(0, 6, 67));
        nameLabel.setBounds(0, 7, 58, 22);
        profilePanel.add(nameLabel);

        JLabel roleLabel = new JLabel("Position", SwingConstants.RIGHT);
        roleLabel.setFont(new Font("Open Sans", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(150, 150, 150));
        roleLabel.setBounds(0, 30, 58, 22);
        profilePanel.add(roleLabel);

        TDAvatarCircle avatar = new TDAvatarCircle();
        avatar.setBounds(67, 0, 56, 56);
        profilePanel.add(avatar);

        return profilePanel;
    }

    // ── Content Panel ───────────────────────────────────────────────────────────
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        // Top: profile (top-right)
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        topRow.add(createProfilePanel(), BorderLayout.EAST);
        contentPanel.add(topRow, BorderLayout.NORTH);

        // Center: back link + form
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Back link
        JLabel backLabel = new JLabel("Back");
        backLabel.setForeground(Color.BLACK);
        Font baseFont = new Font("SansSerif", Font.PLAIN, 13);
        Map<TextAttribute, Object> attrs = new HashMap<>(baseFont.getAttributes());
        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        backLabel.setFont(baseFont.deriveFont(attrs));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        backLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dispose(); }
        });
        centerPanel.add(backLabel);

        // Form grid
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(0, 0, 25, 60);
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.anchor  = GridBagConstraints.NORTHWEST;

        // Row 0: Ticket ID | Department
        JTextField ticketIdField = createTextField("Enter ticket ID");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        formPanel.add(labeledField("Ticket ID", ticketIdField), gbc);

        JComboBox<String> departmentBox = createComboBox(new String[]{
                "Select Department", "Human Resources", "Finance", "IT", "Operations", "Payroll"});
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(labeledField("Department", departmentBox), gbc);

        // Row 1: Employee Name | Notes (tall)
        JTextField employeeNameField = createTextField("Enter employee name");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridheight = 1; gbc.weighty = 0;
        formPanel.add(labeledField("Employee Name", employeeNameField), gbc);

        JTextArea notesArea = new JTextArea();
        notesArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(FIELD_BG);
        notesArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(new TDRoundedBorder(FIELD_BORDER, 6));
        notesScroll.getViewport().setBackground(FIELD_BG);
        notesScroll.setPreferredSize(new Dimension(300, 150));

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridheight = 1; gbc.weighty = 1;
        formPanel.add(labeledField("Notes", notesScroll), gbc);

        // Row 2: (spacer) | Status
        gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 0;
        formPanel.add(Box.createVerticalStrut(0), gbc);

        JComboBox<String> statusBox = createComboBox(new String[]{"Select Status", "Pending", "Resolved"});
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(labeledField("Status", statusBox), gbc);

        centerPanel.add(formPanel);

        // Submit button (right-aligned)
        JPanel submitWrapper = new JPanel(new BorderLayout());
        submitWrapper.setOpaque(false);
        submitWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitWrapper.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton submitBtn = createSubmitButton("Submit");
        submitBtn.addActionListener(e -> onSubmit());

        JPanel submitRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        submitRow.setOpaque(false);
        submitRow.setPreferredSize(new Dimension(660, 50));
        submitRow.add(submitBtn);
        submitWrapper.add(submitRow, BorderLayout.EAST);

        centerPanel.add(submitWrapper);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        return contentPanel;
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────
    private JPanel labeledField(String labelText, Component field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(LABEL_GRAY);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                new TDRoundedBorder(FIELD_BORDER, 6),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setPreferredSize(new Dimension(300, 45));
        field.setText(placeholder);
        field.setForeground(new Color(170, 170, 170));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(170, 170, 170));
                }
            }
        });
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.setBackground(FIELD_BG);
        box.setBorder(new TDRoundedBorder(FIELD_BORDER, 6));
        box.setPreferredSize(new Dimension(300, 45));
        return box;
    }

    private JButton createSubmitButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(NAVY);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(140, 45));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(HOVER_NAVY); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(NAVY); }
        });
        return btn;
    }

    private void onSubmit() {
        JOptionPane.showMessageDialog(this, "Ticket details submitted.", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    // ── Entry point ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            JFrame dummyOwner = new JFrame();
            new TicketDetails(dummyOwner).setVisible(true);
        });
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Inner classes (self-contained so TicketDetails needs no external imports)
    // ════════════════════════════════════════════════════════════════════════════

    // ── Navigation item (icon + label row) ──────────────────────────────────────
    static class TDNavigationItem extends JPanel {

        TDNavigationItem(String text, TDLineIcon.Type iconType, boolean active) {
            setLayout(null);
            setOpaque(false);

            JLabel icon = new JLabel(new TDLineIcon(iconType, 22, Color.WHITE));
            icon.setBounds(0, 5, 22, 22);
            add(icon);

            JLabel label = new JLabel(text);
            label.setFont(new Font("Open Sans", active ? Font.BOLD : Font.PLAIN, 15));
            label.setForeground(Color.WHITE);
            label.setBounds(32, 0, 138, 32);
            add(label);

            if (active) {
                setOpaque(true);
                setBackground(new Color(25, 40, 140));
            }

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    setOpaque(true);
                    setBackground(new Color(25, 40, 140));
                    repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!active) {
                        setOpaque(false);
                        repaint();
                    }
                }
            });
        }
    }

    // ── Avatar circle ────────────────────────────────────────────────────────────
    static class TDAvatarCircle extends JPanel {

        TDAvatarCircle() {
            setOpaque(false);
            setPreferredSize(new Dimension(56, 56));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(2, 19, 98));
            g2.fillOval(0, 0, 56, 56);
            g2.dispose();
        }
    }

    // ── Rounded border ───────────────────────────────────────────────────────────
    static class TDRoundedBorder extends AbstractBorder {

        private final Color color;
        private final int   radius;

        TDRoundedBorder(Color color, int radius) {
            this.color  = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(4, 4, 4, 4); }
    }

    // ── Vector line icon ─────────────────────────────────────────────────────────
    static class TDLineIcon implements Icon {

        enum Type { DASHBOARD, EMPLOYEES, PAYROLL, REQUESTS, ATTENDANCE, HELP, LOGOUT }

        private final Type  type;
        private final int   size;
        private final Color color;

        TDLineIcon(Type type, int size, Color color) {
            this.type  = type;
            this.size  = size;
            this.color = color;
        }

        @Override public int getIconWidth()  { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(x, y);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            switch (type) {
                case DASHBOARD:
                    g.drawRoundRect(1, 1, 8, 8, 2, 2);
                    g.drawRoundRect(13, 1, 8, 8, 2, 2);
                    g.drawRoundRect(1, 13, 8, 8, 2, 2);
                    g.drawRoundRect(13, 13, 8, 8, 2, 2);
                    break;
                case EMPLOYEES:
                    g.drawOval(3, 2, 8, 8);
                    g.drawArc(1, 12, 13, 9, 0, 180);
                    g.drawLine(16, 5, 22, 5);
                    g.drawLine(16, 11, 22, 11);
                    g.drawLine(16, 17, 22, 17);
                    break;
                case PAYROLL:
                    g.drawRoundRect(2, 2, 18, 18, 2, 2);
                    g.drawRect(6, 6, 4, 4);
                    g.drawRect(13, 6, 4, 4);
                    g.drawRect(6, 13, 4, 4);
                    g.drawLine(13, 14, 17, 14);
                    g.drawLine(13, 17, 17, 17);
                    break;
                case REQUESTS:
                    g.drawRoundRect(2, 2, 18, 18, 1, 1);
                    g.drawLine(6, 7, 16, 7);
                    g.drawLine(6, 12, 16, 12);
                    g.drawLine(6, 17, 12, 17);
                    g.drawLine(5, 3, 5, 0);
                    g.drawLine(17, 3, 17, 0);
                    break;
                case ATTENDANCE:
                    g.drawRoundRect(2, 4, 18, 17, 1, 1);
                    g.drawLine(2, 8, 20, 8);
                    g.drawLine(6, 1, 6, 6);
                    g.drawLine(16, 1, 16, 6);
                    break;
                case HELP:
                    Path2D cloud = new Path2D.Double();
                    cloud.moveTo(5, 17);
                    cloud.curveTo(2, 17, 1, 15, 2, 13);
                    cloud.curveTo(2, 10, 5, 9, 7, 10);
                    cloud.curveTo(8, 6, 13, 5, 15, 9);
                    cloud.curveTo(18, 9, 21, 11, 21, 14);
                    cloud.curveTo(21, 16, 19, 17, 17, 17);
                    cloud.closePath();
                    g.draw(cloud);
                    break;
                case LOGOUT:
                    g.drawRect(3, 2, 11, 18);
                    g.drawLine(14, 11, 22, 11);
                    g.drawLine(18, 7, 22, 11);
                    g.drawLine(18, 15, 22, 11);
                    break;
            }
            g.dispose();
        }
    }
}
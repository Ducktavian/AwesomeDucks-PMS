package com.motorph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

public class TicketDetails extends JDialog {

    private static final Color NAVY = new Color(15, 28, 113);
    private static final Color HOVER_NAVY = new Color(25, 40, 140);
    private static final Color FIELD_BORDER = new Color(140, 140, 140);
    private static final Color FIELD_BG = new Color(225, 225, 230);
    private static final Color LABEL_GRAY = new Color(60, 60, 60);

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
        sidebar.setPreferredSize(new Dimension(250, 800));
        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    // ── Sidebar ─────────────────────────────────────────────────────────────────
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel("MotorPH");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 40, 20));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoLabel);

        String[][] menuItems = {
            {"Dashboard", "🏠"},
            {"Employees", "👥"},
            {"Payroll", "💼"},
            {"Requests", "📩"},
            {"Attendance", "⏰"}
        };
        for (String[] item : menuItems) {
            boolean active = "Requests".equals(item[0]);
            sidebar.add(createMenuLabel(item[0], item[1], active));
        }

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(createMenuLabel("Help Center", "❓", false));
        sidebar.add(createMenuLabel("Log Out", "↩", false));
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JLabel createMenuLabel(String text, String icon, boolean active) {
        JLabel label = new JLabel(icon + "   " + text);
        label.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height + 24));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (active) {
            label.setOpaque(true);
            label.setBackground(HOVER_NAVY);
        }
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBackground(HOVER_NAVY);
                label.setOpaque(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    label.setOpaque(false);
                }
            }
        });
        return label;
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel();
        profilePanel.setOpaque(false);
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.X_AXIS));

        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(NAVY);

        JLabel roleLabel = new JLabel("Position");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(120, 120, 120));

        namePanel.add(nameLabel);
        namePanel.add(roleLabel);

        CircleAvatar avatar = new CircleAvatar(52, NAVY, "N");
        avatar.setPreferredSize(new Dimension(52, 52));

        profilePanel.add(namePanel);
        profilePanel.add(Box.createHorizontalStrut(18));
        profilePanel.add(avatar);

        return profilePanel;
    }

    // ── Content Panel ───────────────────────────────────────────────────────────
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        // ── Top: Profile (top-right) ───────────────────────────────────────────
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        topRow.add(createProfilePanel(), BorderLayout.EAST);
        contentPanel.add(topRow, BorderLayout.NORTH);

        // ── Center: Back link + Form ───────────────────────────────────────────
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel backLabel = new JLabel("Back");
        backLabel.setForeground(Color.BLACK);
        Font baseFont = new Font("SansSerif", Font.PLAIN, 13);
        java.util.Map<java.awt.font.TextAttribute, Object> attributes = new java.util.HashMap<>(baseFont.getAttributes());
        attributes.put(java.awt.font.TextAttribute.UNDERLINE, java.awt.font.TextAttribute.UNDERLINE_ON);
        Font underlineFont = baseFont.deriveFont(attributes);
        backLabel.setFont(underlineFont);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        backLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        centerPanel.add(backLabel);

        // ── Form grid ───────────────────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 25, 60);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Row 0: Ticket ID | Department
        JTextField ticketIdField = createTextField("Enter ticket ID");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        formPanel.add(labeledField("Ticket ID", ticketIdField), gbc);

        JComboBox<String> departmentBox = createComboBox(new String[]{
                "Select Department", "Human Resources", "Finance", "IT", "Operations", "Payroll"});
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(labeledField("Department", departmentBox), gbc);

        // Row 1: Employee Name | Notes (spans 2 rows)
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
        notesScroll.setBorder(new RoundedBorder(FIELD_BORDER, 6));
        notesScroll.getViewport().setBackground(FIELD_BG);
        notesScroll.setPreferredSize(new Dimension(300, 150));

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridheight = 1; gbc.weighty = 1;
        formPanel.add(labeledField("Notes", notesScroll), gbc);

        // Row 2: (empty under Employee Name) | Status
        gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 0;
        formPanel.add(Box.createVerticalStrut(0), gbc);

        JComboBox<String> statusBox = createComboBox(new String[]{
                "Select Status", "Pending", "Resolved"});
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(labeledField("Status", statusBox), gbc);

        centerPanel.add(formPanel);

        // ── Submit button ───────────────────────────────────────────────────────
        JPanel submitWrapper = new JPanel(new BorderLayout());
        submitWrapper.setOpaque(false);
        submitWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitWrapper.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton submitBtn = createSubmitButton("Submit");
        submitBtn.addActionListener(e -> onSubmit());

        JPanel submitRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        submitRow.setOpaque(false);
        submitRow.setPreferredSize(new Dimension(660, 50));
        submitRow.add(submitBtn);
        submitWrapper.add(submitRow, BorderLayout.EAST);

        centerPanel.add(submitWrapper);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        return contentPanel;
    }

    // ── Helper: labeled field block ────────────────────────────────────────────
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

    // ── Helper: styled text field ──────────────────────────────────────────────
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(Color.BLACK);
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(FIELD_BORDER, 6),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setPreferredSize(new Dimension(300, 45));

        field.setText(placeholder);
        field.setForeground(new Color(170, 170, 170));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(170, 170, 170));
                }
            }
        });
        return field;
    }

    // ── Helper: styled combo box ───────────────────────────────────────────────
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.setBackground(FIELD_BG);
        box.setBorder(new RoundedBorder(FIELD_BORDER, 6));
        box.setPreferredSize(new Dimension(300, 45));
        return box;
    }

    // ── Helper: submit button ──────────────────────────────────────────────────
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
        javax.swing.JOptionPane.showMessageDialog(this,
                "Ticket details submitted.", "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    // ── Rounded Border ──────────────────────────────────────────────────────────
    static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color color, int radius) {
            this.color = color;
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
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }
    }

    // ── Circle Avatar ───────────────────────────────────────────────────────────
    static class CircleAvatar extends JPanel {
        private final Color fillColor;

        public CircleAvatar(int size, Color fillColor, String initials) {
            this.fillColor = fillColor;
            setPreferredSize(new Dimension(size, size));
            setOpaque(false);
            setLayout(new GridBagLayout());
            JLabel label = new JLabel(initials);
            label.setFont(new Font("SansSerif", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            add(label);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Entry Point (for standalone testing) ────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            JFrame dummyOwner = new JFrame();
            TicketDetails dialog = new TicketDetails(dummyOwner);
            dialog.setVisible(true);
        });
    }
}
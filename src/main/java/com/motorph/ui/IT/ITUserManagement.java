package main.java.com.motorph.ui.IT;

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
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class ITUserManagement extends JFrame {

    // ── Theme ────────────────────────────────────────────────────────────────────
    private static final Color NAVY          = new Color(2, 19, 98);
    private static final Color HOVER_NAVY    = new Color(25, 40, 140);
    private static final Color WHITE         = Color.WHITE;
    private static final Color BLACK         = Color.BLACK;
    private static final Color LIGHT_GRAY    = new Color(217, 217, 217);
    private static final Color BORDER_GRAY   = new Color(211, 211, 211);
    private static final Color POSITION_GRAY = new Color(150, 150, 150);

    private static final Color STATUS_PENDING  = new Color(255, 222, 89);
    private static final Color STATUS_APPROVED = new Color(12, 194, 107);
    private static final Color STATUS_REJECTED = new Color(255, 87, 87);
    private static final Color ACTIVE_BG       = new Color(234, 243, 222);
    private static final Color INACTIVE_BG     = new Color(252, 235, 235);
    private static final Color ACTIVE_FG       = new Color(39, 80, 10);
    private static final Color INACTIVE_FG     = new Color(121, 31, 31);

    private static final Font FONT_LOGO    = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_NAV     = new Font("Open Sans", Font.PLAIN, 15);
    private static final Font FONT_NAV_B   = new Font("Open Sans", Font.BOLD, 15);
    private static final Font FONT_PROFILE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_BTN     = new Font("Open Sans", Font.PLAIN, 13);
    private static final Font FONT_TABLE_H = new Font("Open Sans", Font.BOLD, 12);
    private static final Font FONT_TABLE_B = new Font("Open Sans", Font.PLAIN, 12);
    private static final Font FONT_BADGE   = new Font("Open Sans", Font.PLAIN, 11);
    private static final Font FONT_SEARCH  = new Font("Open Sans", Font.PLAIN, 14);

    private static final String[] ROLES    = {"Admin", "HR", "Payroll", "IT", "Employee"};
    private static final String[] STATUSES = {"Approved", "Pending", "Rejected"};

    // ── Data ─────────────────────────────────────────────────────────────────────
    private final List<Employee> employees = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;

    // ── Constructor ───────────────────────────────────────────────────────────────
    public ITUserManagement() {
        super("MotorPH Payroll System - Employee Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        seedData();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
    }

    // ── Seed data ─────────────────────────────────────────────────────────────────
    private void seedData() {
        employees.add(new Employee("EMP-001", "Juan dela Cruz",  "09171234567", "Maria Santos",   "Admin",    "Approved", true));
        employees.add(new Employee("EMP-002", "Maria Santos",    "09281234567", "Pedro Reyes",    "HR",       "Approved", true));
        employees.add(new Employee("EMP-003", "Pedro Reyes",     "09391234567", "Maria Santos",   "Payroll",  "Pending",  true));
        employees.add(new Employee("EMP-004", "Ana Lim",         "09451234567", "Juan dela Cruz", "IT",       "Rejected", false));
        employees.add(new Employee("EMP-005", "Carlos Bautista", "09561234567", "Maria Santos",   "Employee", "Approved", true));
        employees.add(new Employee("EMP-006", "Rosa Mendoza",    "09671234567", "Pedro Reyes",    "HR",       "Pending",  false));
        employees.add(new Employee("EMP-007", "Jose Garcia",     "09781234567", "Ana Lim",        "Employee", "Approved", true));
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(257, 800));

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(FONT_LOGO);
        logo.setForeground(WHITE);
        logo.setBounds(48, 63, 160, 36);
        sidebar.add(logo);

        addNavItem(sidebar, "Dashboard",   ITLineIcon.Type.DASHBOARD,  158, false);
        addNavItem(sidebar, "Employees",   ITLineIcon.Type.EMPLOYEES,  216, true);
        addNavItem(sidebar, "Payroll",     ITLineIcon.Type.PAYROLL,    255, false);
        addNavItem(sidebar, "Requests",    ITLineIcon.Type.REQUESTS,   300, false);
        addNavItem(sidebar, "Attendance",  ITLineIcon.Type.ATTENDANCE, 341, false);
        addNavItem(sidebar, "Help Center", ITLineIcon.Type.HELP,       660, false);
        addNavItem(sidebar, "Log Out",     ITLineIcon.Type.LOGOUT,     702, false);

        return sidebar;
    }

    private void addNavItem(JPanel sidebar, String text, ITLineIcon.Type icon, int y, boolean active) {
        ITNavItem item = new ITNavItem(text, icon, active);
        item.setBounds(48, y, 170, 32);
        sidebar.add(item);
    }

    // ── Main panel ────────────────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));
        main.add(buildTopBar(),  BorderLayout.NORTH);
        main.add(buildContent(), BorderLayout.CENTER);
        return main;
    }

    // ── Top bar ───────────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JPanel profile = new JPanel(null);
        profile.setOpaque(false);
        profile.setPreferredSize(new Dimension(123, 57));

        JLabel name = new JLabel("Name", SwingConstants.RIGHT);
        name.setFont(FONT_PROFILE);
        name.setForeground(new Color(0, 6, 67));
        name.setBounds(0, 7, 58, 22);
        profile.add(name);

        JLabel role = new JLabel("Position", SwingConstants.RIGHT);
        role.setFont(new Font("Open Sans", Font.PLAIN, 12));
        role.setForeground(POSITION_GRAY);
        role.setBounds(0, 30, 58, 22);
        profile.add(role);

        ITAvatarCircle avatar = new ITAvatarCircle();
        avatar.setBounds(67, 0, 56, 56);
        profile.add(avatar);

        top.add(profile, BorderLayout.EAST);
        return top;
    }

    // ── Content ───────────────────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        JLabel title = new JLabel("Employees");
        title.setFont(FONT_TITLE);
        title.setForeground(BLACK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        content.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(buildToolbar(), BorderLayout.NORTH);
        center.add(buildTable(),   BorderLayout.CENTER);
        content.add(center, BorderLayout.CENTER);

        return content;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setBackground(WHITE);
        searchWrap.setBorder(new ITRoundedBorder(BORDER_GRAY, 6));
        searchWrap.setPreferredSize(new Dimension(260, 36));

        JLabel searchIcon = new JLabel(new ITLineIcon(ITLineIcon.Type.SEARCH, 18, new Color(170, 170, 170)));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        searchWrap.add(searchIcon, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(FONT_SEARCH);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        searchField.setOpaque(false);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filterTable(); }
            public void removeUpdate(DocumentEvent e)  { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
        searchWrap.add(searchField, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(createButton("Add",               ITLineIcon.Type.ADD,     e -> onAdd()));
        btnPanel.add(createButton("Update",            ITLineIcon.Type.EDIT,    e -> onUpdate()));
        btnPanel.add(createButton("Active / Deactive", ITLineIcon.Type.REFRESH, e -> onToggle()));

        bar.add(searchWrap, BorderLayout.WEST);
        bar.add(btnPanel,   BorderLayout.EAST);
        return bar;
    }

    private JButton createButton(String text, ITLineIcon.Type iconType, ActionListener action) {
        int btnWidth = text.length() * 7 + 46;

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setLayout(null);
        btn.setBackground(NAVY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btnWidth, 36));

        JLabel ic = new JLabel(new ITLineIcon(iconType, 16, WHITE));
        ic.setBounds(12, 10, 16, 16);
        btn.add(ic);

        JLabel lb = new JLabel(text);
        lb.setFont(FONT_BTN);
        lb.setForeground(WHITE);
        lb.setBounds(34, 9, btnWidth - 38, 18);
        btn.add(lb);

        btn.addActionListener(action);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(HOVER_NAVY); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(NAVY);       btn.repaint(); }
        });

        return btn;
    }

    // ── Table ─────────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = {
            "Emp #", "Employee Name", "Phone Number",
            "Immediate Supervisor", "Role", "Status", "Is Active"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? WHITE : LIGHT_GRAY);
                }
                return c;
            }
        };

        table.setFont(FONT_TABLE_B);
        table.setRowHeight(46);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(2, 19, 98, 40));
        table.setSelectionForeground(BLACK);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_H);
        header.setBackground(NAVY);
        header.setForeground(WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
                setOpaque(true);
            }
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(NAVY);
                setForeground(WHITE);
                setFont(FONT_TABLE_H);
                return this;
            }
        });

        int[] widths = {70, 140, 120, 155, 90, 100, 90};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer padded = new DefaultTableCellRenderer();
        padded.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        for (int i = 0; i < 5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(padded);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActiveRenderer());

        refreshTable(employees);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new ITRoundedBorder(BORDER_GRAY, 6));
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    // ── Badge renderers ───────────────────────────────────────────────────────────
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            String status = v == null ? "" : v.toString();
            Color bg, fg;
            switch (status) {
                case "Approved": bg = STATUS_APPROVED; fg = new Color(4, 52, 44);  break;
                case "Rejected": bg = STATUS_REJECTED; fg = new Color(80, 19, 19); break;
                default:         bg = STATUS_PENDING;  fg = new Color(65, 36, 2);  break;
            }
            return buildBadgeCell(status, bg, fg, row, sel);
        }
    }

    private class ActiveRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            boolean active = Boolean.TRUE.equals(v);
            return buildBadgeCell(
                active ? "Active" : "Inactive",
                active ? ACTIVE_BG : INACTIVE_BG,
                active ? ACTIVE_FG : INACTIVE_FG,
                row, sel
            );
        }
    }

    private Component buildBadgeCell(String text, Color bg, Color fg, int row, boolean sel) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(FONT_BADGE);
        lbl.setForeground(fg);

        final Color pillBg = bg;
        JPanel pill = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(pillBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.add(lbl, BorderLayout.CENTER);
        pill.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JPanel wrap = new JPanel(new GridBagLayout());
        Color rowBg = sel ? new Color(2, 19, 98, 40) : (row % 2 == 0 ? WHITE : LIGHT_GRAY);
        wrap.setBackground(rowBg);
        wrap.add(pill);
        return wrap;
    }

    // ── Table helpers ─────────────────────────────────────────────────────────────
    private void refreshTable(List<Employee> list) {
        tableModel.setRowCount(0);
        for (Employee emp : list) {
            tableModel.addRow(new Object[]{
                emp.id, emp.name, emp.phone,
                emp.supervisor, emp.role, emp.status, emp.active
            });
        }
    }

    private void filterTable() {
        String q = searchField.getText().toLowerCase().trim();
        if (q.isEmpty()) { refreshTable(employees); return; }
        List<Employee> filtered = new ArrayList<>();
        for (Employee emp : employees) {
            if (emp.id.toLowerCase().contains(q)
                    || emp.name.toLowerCase().contains(q)
                    || emp.phone.contains(q)
                    || emp.supervisor.toLowerCase().contains(q)
                    || emp.role.toLowerCase().contains(q)
                    || emp.status.toLowerCase().contains(q)) {
                filtered.add(emp);
            }
        }
        refreshTable(filtered);
    }

    private int selectedDataIndex() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return -1;
        String id = (String) tableModel.getValueAt(viewRow, 0);
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).id.equals(id)) return i;
        }
        return -1;
    }

    // ── Actions ───────────────────────────────────────────────────────────────────
    private void onAdd() {
        JTextField idF    = new JTextField();
        JTextField nameF  = new JTextField();
        JTextField phoneF = new JTextField();
        JTextField supF   = new JTextField();
        JComboBox<String> roleBox   = new JComboBox<>(ROLES);
        JComboBox<String> statusBox = new JComboBox<>(STATUSES);

        Object[] fields = {
            "Employee #:", idF,
            "Name:",       nameF,
            "Phone:",      phoneF,
            "Supervisor:", supF,
            "Role:",       roleBox,
            "Status:",     statusBox
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Add Employee",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = idF.getText().trim();
            String nm = nameF.getText().trim();
            if (id.isEmpty() || nm.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Employee # and Name are required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            employees.add(new Employee(
                    id, nm,
                    phoneF.getText().trim(),
                    supF.getText().trim(),
                    (String) roleBox.getSelectedItem(),
                    (String) statusBox.getSelectedItem(),
                    true));
            filterTable();
        }
    }

    private void onUpdate() {
        int idx = selectedDataIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a row to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Employee emp = employees.get(idx);

        JTextField nameF  = new JTextField(emp.name);
        JTextField phoneF = new JTextField(emp.phone);
        JTextField supF   = new JTextField(emp.supervisor);
        JComboBox<String> roleBox   = new JComboBox<>(ROLES);
        JComboBox<String> statusBox = new JComboBox<>(STATUSES);
        roleBox.setSelectedItem(emp.role);
        statusBox.setSelectedItem(emp.status);

        Object[] fields = {
            "Name:",       nameF,
            "Phone:",      phoneF,
            "Supervisor:", supF,
            "Role:",       roleBox,
            "Status:",     statusBox
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Update Employee — " + emp.id,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            emp.name       = nameF.getText().trim();
            emp.phone      = phoneF.getText().trim();
            emp.supervisor = supF.getText().trim();
            emp.role       = (String) roleBox.getSelectedItem();
            emp.status     = (String) statusBox.getSelectedItem();
            filterTable();
        }
    }

    private void onToggle() {
        int idx = selectedDataIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a row to activate / deactivate.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Employee emp = employees.get(idx);
        emp.active = !emp.active;
        filterTable();
        JOptionPane.showMessageDialog(this,
                emp.name + " is now " + (emp.active ? "Active" : "Inactive") + ".",
                "Status Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Employee data class
    // ════════════════════════════════════════════════════════════════════════════
    static class Employee {
        String  id, name, phone, supervisor, role, status;
        boolean active;

        Employee(String id, String name, String phone,
                 String supervisor, String role, String status, boolean active) {
            this.id         = id;
            this.name       = name;
            this.phone      = phone;
            this.supervisor = supervisor;
            this.role       = role;
            this.status     = status;
            this.active     = active;
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Navigation item
    // ════════════════════════════════════════════════════════════════════════════
    static class ITNavItem extends JPanel {

        ITNavItem(String text, ITLineIcon.Type iconType, boolean active) {
            setLayout(null);
            setOpaque(active);
            if (active) setBackground(new Color(25, 40, 140));

            JLabel icon = new JLabel(new ITLineIcon(iconType, 22, Color.WHITE));
            icon.setBounds(0, 5, 22, 22);
            add(icon);

            JLabel label = new JLabel(text);
            label.setFont(active ? FONT_NAV_B : FONT_NAV);
            label.setForeground(Color.WHITE);
            label.setBounds(32, 0, 138, 32);
            add(label);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    setOpaque(true);
                    setBackground(new Color(25, 40, 140));
                    repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!active) { setOpaque(false); repaint(); }
                }
            });
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Avatar circle
    // ════════════════════════════════════════════════════════════════════════════
    static class ITAvatarCircle extends JPanel {
        ITAvatarCircle() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(2, 19, 98));
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Rounded border
    // ════════════════════════════════════════════════════════════════════════════
    static class ITRoundedBorder extends AbstractBorder {
        private final Color color;
        private final int   radius;

        ITRoundedBorder(Color color, int radius) {
            this.color  = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(4, 4, 4, 4); }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Vector line icons
    // ════════════════════════════════════════════════════════════════════════════
    static class ITLineIcon implements Icon {

        enum Type {
            DASHBOARD, EMPLOYEES, PAYROLL, REQUESTS,
            ATTENDANCE, HELP, LOGOUT, SEARCH, ADD, EDIT, REFRESH
        }

        private final Type  type;
        private final int   size;
        private final Color color;

        ITLineIcon(Type type, int size, Color color) {
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
                case SEARCH:
                    g.drawOval(1, 1, 13, 13);
                    g.drawLine(12, 12, 20, 20);
                    break;
                case ADD:
                    g.drawLine(8, 2, 8, 16);
                    g.drawLine(1, 9, 15, 9);
                    break;
                case EDIT:
                    g.drawLine(3, 14, 13, 4);
                    g.drawLine(6, 17, 16, 7);
                    g.drawLine(13, 4, 16, 7);
                    g.drawLine(3, 14, 2, 18);
                    g.drawLine(2, 18, 6, 17);
                    break;
                case REFRESH:
                    g.drawArc(3, 3, 14, 14, 45, 270);
                    g.drawLine(15, 2, 17, 7); 
                    g.drawLine(15, 2, 11, 4);
                    break;
                default:
                    break;

                    
            }
            g.dispose();
        }
    }

    // ── Entry point ───────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new ITUserManagement().setVisible(true));
    }
}
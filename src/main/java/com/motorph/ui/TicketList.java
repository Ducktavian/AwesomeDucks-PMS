package com.motorph.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class TicketList extends JFrame {

    // ── Field declarations ─────────────────────────────────────────────────────
    private JTable ticketTable;
    private JTextField searchField;
    private int hoverRow = -1;

    public TicketList() {
        setTitle("MotorPH Payroll System - Ticket List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main container - Sidebar on left, everything else on right
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // ── Left Sidebar (fixed width, full height) ────────────────────────────
        JPanel sidebar = createSidebar();
        sidebar.setPreferredSize(new Dimension(240, 800));
        mainPanel.add(sidebar, BorderLayout.WEST);

        // ── Right Panel: Content with search bar inside ────────────────────────
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // ── Create Sidebar ──────────────────────────────────────────────────────────
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(15, 28, 113));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo/Brand
        JLabel logoLabel = new JLabel("MotorPH");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 40, 20));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoLabel);

        // Menu items
        String[][] menuItems = {
            {"Dashboard", "🏠"},
            {"Employees", "👥"},
            {"Payroll", "💼"},
            {"Requests", "📩"},
            {"Attendance", "⏰"}
        };
        for (String[] item : menuItems) {
            boolean active = "Requests".equals(item[0]);
            JLabel menuItem = createMenuLabel(item[0], item[1], active);
            sidebar.add(menuItem);
        }

        // Spacer
        sidebar.add(Box.createVerticalGlue());

        // Help Center and Log Out
        JLabel helpLabel  = createMenuLabel("Help Center", "❓", false);
        JLabel logoutLabel = createMenuLabel("Log Out",    "↩",  false);
        sidebar.add(helpLabel);
        sidebar.add(logoutLabel);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JLabel createMenuLabel(String text, String icon, boolean active) {
        JLabel label = new JLabel(icon + "   " + text);
        label.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (active) {
            label.setOpaque(true);
            label.setBackground(new Color(25, 40, 140));
        }
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBackground(new Color(25, 40, 140));
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
        profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(new Color(15, 28, 113));

        JLabel roleLabel = new JLabel("Position");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(120, 120, 120));

        namePanel.add(nameLabel);
        namePanel.add(roleLabel);

        CircleAvatar avatar = new CircleAvatar(52, new Color(15, 28, 113), "N");
        avatar.setPreferredSize(new Dimension(52, 52));

        profilePanel.add(namePanel);
        profilePanel.add(Box.createHorizontalStrut(18));
        profilePanel.add(avatar);

        return profilePanel;
    }

    // ── Create Content Panel ────────────────────────────────────────────────────
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // ── Top Row: Search + Profile ──────────────────────────────────────────
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        searchField = new JTextField("Search");
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(420, 50));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        searchField.setForeground(new Color(150, 150, 150));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Search".equals(searchField.getText())) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText("Search");
                    searchField.setForeground(new Color(150, 150, 150));
                }
            }
        });

        // Live search filtering
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterTable(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterTable(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            private void filterTable() {
                String query = searchField.getText().toLowerCase();
                if ("search".equals(query)) return;
                TableRowSorter<?> sorter = (TableRowSorter<?>) ticketTable.getRowSorter();
                if (query.isBlank()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }
        });

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setOpaque(false);
        searchWrapper.setPreferredSize(new Dimension(460, 50));
        searchWrapper.add(searchField, BorderLayout.CENTER);

        topRow.add(searchWrapper, BorderLayout.WEST);
        topRow.add(createProfilePanel(), BorderLayout.EAST);

        // ── Button Row ─────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);

        JButton addBtn     = createActionButton("+ Add");
        JButton updateBtn  = createActionButton("✎ Update");
        JButton deleteBtn  = createActionButton("🗑 Delete");
        JButton refreshBtn = createActionButton("⟲ Refresh");

        addBtn.addActionListener(e -> addTicket());
        updateBtn.addActionListener(e -> updateTicket());
        deleteBtn.addActionListener(e -> deleteTicket());
        refreshBtn.addActionListener(e -> refreshTable());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(buttonPanel, BorderLayout.EAST);

        topPanel.add(topRow);
        topPanel.add(buttonWrapper);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // ── Table ──────────────────────────────────────────────────────────────
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        String[] columnNames = {"Ticket ID", "Employee Name", "Date", "Issue", "Note", "Status"};
        DefaultTableModel model = new DefaultTableModel(createTableData(), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        ticketTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    if (row == hoverRow) {
                        c.setBackground(Color.BLACK);
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(row % 2 == 0
                                ? new Color(245, 245, 245) : Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        ticketTable.setRowSorter(sorter);

        ticketTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ticketTable.setRowHeight(55);
        ticketTable.setFillsViewportHeight(true);
        ticketTable.setIntercellSpacing(new Dimension(0, 0));
        ticketTable.setBorder(null);
        ticketTable.setGridColor(new Color(220, 220, 220));
        ticketTable.setShowHorizontalLines(true);
        ticketTable.setShowVerticalLines(false);

        JTableHeader header = ticketTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(30, 30, 30));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        header.setPreferredSize(new Dimension(header.getWidth(), 42));

        ticketTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        ticketTable.setDefaultRenderer(Object.class, new RowColorRenderer());

        ticketTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = ticketTable.rowAtPoint(e.getPoint());
                if (row != hoverRow) { hoverRow = row; ticketTable.repaint(); }
            }
        });
        ticketTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) { hoverRow = -1; ticketTable.repaint(); }
        });

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        return contentPanel;
    }

    // ── CRUD Actions ────────────────────────────────────────────────────────────
    private void addTicket() {
        DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();
        int nextId = model.getRowCount() + 1;
        model.addRow(new Object[]{
            "TK" + String.format("%03d", nextId),
            "New Employee",
            java.time.LocalDate.now().toString(),
            "New Issue",
            "Pending request",
            "Pending"
        });
    }

    private void updateTicket() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = ticketTable.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();
        String currentStatus = (String) model.getValueAt(modelRow, 5);
        String newStatus = "Pending".equals(currentStatus) ? "Resolved" : "Pending";
        model.setValueAt(newStatus, modelRow, 5);
    }

    private void deleteTicket() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this ticket?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = ticketTable.convertRowIndexToModel(row);
            ((DefaultTableModel) ticketTable.getModel()).removeRow(modelRow);
        }
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();
        model.setRowCount(0);
        for (Object[] row : createTableData()) model.addRow(row);
        searchField.setText("Search");
        searchField.setForeground(new Color(150, 150, 150));
        ((TableRowSorter<?>) ticketTable.getRowSorter()).setRowFilter(null);
    }

    // ── Create Action Button ────────────────────────────────────────────────────
    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(new Color(15, 28, 113));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(110, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(25, 40, 140)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(15, 28, 113)); }
        });
        return btn;
    }

    // ── Table Data ──────────────────────────────────────────────────────────────
    private Object[][] createTableData() {
        return new Object[][] {
            {"TK001", "Juan Dela Cruz",  "2024-01-15", "Salary Inquiry",    "Request for salary breakdown",    "Pending"},
            {"TK002", "Maria Santos",    "2024-01-14", "Leave Request",     "5 days vacation leave",            "Resolved"},
            {"TK003", "Carlos Reyes",    "2024-01-13", "Overtime Pay",      "Claim for overtime compensation",  "Resolved"},
            {"TK004", "Ana Gomez",       "2024-01-12", "Deduction Query",   "Question about tax deduction",     "Pending"},
            {"TK005", "Miguel Flores",   "2024-01-11", "Bonus Payment",     "Inquiry on bonus status",          "Resolved"},
            {"TK006", "Rosa Cruz",       "2024-01-10", "Insurance Issue",   "Health insurance claim",           "Resolved"},
            {"TK007", "Pedro Lopez",     "2024-01-09", "Payroll Adjustment","Request for salary adjustment",    "Pending"},
            {"TK008", "Lisa Wang",       "2024-01-08", "Attendance Record", "Discrepancy in attendance",        "Pending"}
        };
    }

    // ── Status Cell Renderer ────────────────────────────────────────────────────
    static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            StatusBadge badge = new StatusBadge((String) value);
            badge.setHorizontalAlignment(SwingConstants.CENTER);
            badge.setFont(new Font("SansSerif", Font.BOLD, 11));
            if ("Pending".equals(value)) {
                badge.setFillColor(new Color(255, 235, 59));
                badge.setForeground(Color.BLACK);
            } else {
                badge.setFillColor(new Color(76, 175, 80));
                badge.setForeground(Color.WHITE);
            }
            return badge;
        }
    }

    static class StatusBadge extends JLabel {
        private Color fillColor = new Color(255, 235, 59);

        public StatusBadge(String text) {
            super(text);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        }

        public void setFillColor(Color c) { this.fillColor = c; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Row Color Renderer ──────────────────────────────────────────────────────
    static class RowColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0
                        ? new Color(245, 245, 245) : Color.WHITE);
                c.setForeground(Color.BLACK);
            }
            c.setFont(new Font("SansSerif", Font.PLAIN, 13));
            return c;
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

    // ── Entry Point ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(TicketList::new);
    }
}
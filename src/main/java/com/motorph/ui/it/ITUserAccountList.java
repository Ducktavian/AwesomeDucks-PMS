package com.motorph.ui.it;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * IT Users screen — search, role filter, action buttons, and user account table.
 */
public class ITUserAccountList extends JPanel {

    private static final Color NAVY    = new Color(13,  36,  89);
    private static final Color MUTED   = new Color(120, 130, 150);
    private static final Color DIVIDER = new Color(220, 225, 235);
    private static final Color ROW_BG  = new Color(217, 217, 217);

    private static final String[] COLUMNS = {
        "Employee No.", "Name", "Status", "Position",
        "Immediate Supervisor", "Role"
    };

    private static final String[][] SAMPLE_DATA = {
        { "10001", "Juan Dela Cruz",   "Active",   "Software Engineer", "Maria Santos",  "Employee" },
        { "10002", "Maria Santos",     "Active",   "Team Lead",         "Pedro Reyes",   "HR"       },
        { "10003", "Pedro Reyes",      "Active",   "IT Manager",        "Ana Lopez",     "IT"       },
        { "10004", "Ana Lopez",        "Inactive", "HR Director",       "Carlos Mendez", "Admin"    },
    };

    private static final String IMG_DIR = resolveImgDir();

    private DefaultTableModel tableModel;
    private JTable            userTable;
    private JTextField        searchField;
    private JComboBox<String> roleFilter;
    private final List<UserAccountEntry> allEntries = new ArrayList<>();

    public ITUserAccountList() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildTopBar(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(16, 24, 24, 24));

        body.add(buildSearchRow());
        body.add(Box.createVerticalStrut(12));
        body.add(buildToolbar());
        body.add(Box.createVerticalStrut(16));
        body.add(buildTablePanel());

        add(body, BorderLayout.CENTER);
        loadSampleData();
    }

    private JPanel buildTopBar() {
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
        bar.add(userChip, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSearchRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(280, 38));
        searchField.setBorder(new CompoundBorder(
            new RoundedBorder(6, new Color(210, 210, 210)),
            new EmptyBorder(6, 36, 6, 12)
        ));

        JPanel searchWrap = new JPanel(null) {
            @Override
            public boolean isOpaque() { return false; }
        };
        searchWrap.setPreferredSize(new Dimension(280, 38));

        searchField.setBounds(0, 0, 280, 38);

        JLabel searchIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 185, 195));
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(4, 11, 14, 14);
                g2.drawLine(15, 22, 20, 27);
                g2.dispose();
            }
        };
        searchIcon.setBounds(8, 0, 28, 38);

        JLabel placeholder = new JLabel("Search");
        placeholder.setFont(new Font("SansSerif", Font.PLAIN, 13));
        placeholder.setForeground(new Color(190, 195, 205));
        placeholder.setBounds(36, 0, 200, 38);

        searchWrap.add(searchIcon);
        searchWrap.add(placeholder);
        searchWrap.add(searchField);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                placeholder.setVisible(searchField.getText().isEmpty());
                applyFilters();
            }
        });
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                placeholder.setVisible(searchField.getText().isEmpty());
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                placeholder.setVisible(searchField.getText().isEmpty());
            }
        });

        row.add(searchWrap);
        return row;
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        roleFilter = new JComboBox<>(new String[] {
            "Employee", "Finance", "HR", "IT", "Admin"
        });
        roleFilter.setSelectedItem("IT");
        roleFilter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        roleFilter.setPreferredSize(new Dimension(110, 32));
        roleFilter.addActionListener(e -> applyFilters());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        actions.add(navyButton("Add",     "Add-Icon.png",     this::openCreateUserDialog));
        actions.add(navyButton("Update",  "Update-Icon.png",  this::openUpdateUserDialog));
        actions.add(navyButton("Delete",  "Delete-Icon.png",  this::deleteSelectedUser));
        actions.add(navyButton("Refresh", "Refresh-Icon.png", this::refreshTable));

        toolbar.add(roleFilter, BorderLayout.WEST);
        toolbar.add(actions,    BorderLayout.EAST);
        return toolbar;
    }

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(52);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 10));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userTable.setBackground(Color.WHITE);
        userTable.setFillsViewportHeight(true);

        styleTableHeader(userTable.getTableHeader());
        styleTableColumns(userTable);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setOpaque(true);
                if (isSelected) {
                    lbl.setBackground(new Color(230, 235, 245));
                } else {
                    lbl.setBackground(ROW_BG);
                }
                lbl.setForeground(Color.BLACK);
                return lbl;
            }
        };
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void styleTableHeader(JTableHeader header) {
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLACK),
            new EmptyBorder(0, 0, 8, 0)
        ));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 44));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setBackground(Color.WHITE);
                lbl.setForeground(Color.BLACK);
                return lbl;
            }
        };
        header.setDefaultRenderer(headerRenderer);
    }

    private void styleTableColumns(JTable table) {
        TableColumnModel cols = table.getColumnModel();
        int[] widths = { 110, 150, 90, 140, 160, 100 };
        for (int i = 0; i < widths.length && i < cols.getColumnCount(); i++) {
            cols.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void loadSampleData() {
        allEntries.clear();
        for (String[] row : SAMPLE_DATA) {
            allEntries.add(UserAccountEntry.fromSampleRow(row));
        }
        applyFilters();
    }

    private void refreshTable() {
        if (searchField != null) {
            searchField.setText("");
        }
        userTable.clearSelection();
        applyFilters();
        JOptionPane.showMessageDialog(this,
            "User list refreshed.",
            "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private UserAccountEntry getSelectedEntry() {
        int viewRow = userTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = userTable.convertRowIndexToModel(viewRow);
        String empNo = (String) tableModel.getValueAt(modelRow, 0);
        for (UserAccountEntry entry : allEntries) {
            if (entry.getEmployeeNo().equals(empNo)) {
                return entry;
            }
        }
        return null;
    }

    private void openCreateUserDialog() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CreateUserDialog dialog = new CreateUserDialog(owner);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            UserAccountEntry entry = dialog.getResult();
            entry.setEmployeeNo(nextEmployeeNo());
            allEntries.add(entry);
            applyFilters();
            JOptionPane.showMessageDialog(this,
                "User account created successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openUpdateUserDialog() {
        UserAccountEntry selected = getSelectedEntry();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to update.",
                "Update", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CreateUserDialog dialog = new CreateUserDialog(owner, selected);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            UserAccountEntry updated = dialog.getResult();
            for (int i = 0; i < allEntries.size(); i++) {
                if (allEntries.get(i).getEmployeeNo().equals(selected.getEmployeeNo())) {
                    allEntries.set(i, updated);
                    break;
                }
            }
            applyFilters();
            JOptionPane.showMessageDialog(this,
                "User account updated successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        UserAccountEntry selected = getSelectedEntry();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete.",
                "Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
            "Delete user " + selected.getFirstName() + " " + selected.getLastName()
                + " (" + selected.getEmployeeNo() + ")?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            allEntries.removeIf(e -> e.getEmployeeNo().equals(selected.getEmployeeNo()));
            applyFilters();
            JOptionPane.showMessageDialog(this,
                "User account deleted successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String nextEmployeeNo() {
        int max = 10000;
        for (UserAccountEntry entry : allEntries) {
            try {
                max = Math.max(max, Integer.parseInt(entry.getEmployeeNo()));
            } catch (NumberFormatException ignored) {}
        }
        return String.valueOf(max + 1);
    }

    private void applyFilters() {
        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String role  = roleFilter != null ? (String) roleFilter.getSelectedItem() : null;

        tableModel.setRowCount(0);
        for (UserAccountEntry entry : allEntries) {
            String[] row = entry.toTableRow();
            if (role != null && !role.equalsIgnoreCase(row[5])) {
                continue;
            }
            if (!query.isEmpty()) {
                boolean match = false;
                for (String cell : row) {
                    if (cell.toLowerCase().contains(query)) {
                        match = true;
                        break;
                    }
                }
                if (!match) continue;
            }
            tableModel.addRow(row);
        }
    }

    private JButton navyButton(String label, String iconFile, Runnable action) {
        ImageIcon icon = loadIcon(iconFile, 14, 14);
        JButton btn = new JButton(label, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(20, 50, 110) : NAVY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(6);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private static ImageIcon loadIcon(String filename, int w, int h) {
        try {
            java.net.URL url = ITUserAccountList.class.getClassLoader()
                    .getResource("com/motorph/img/" + filename);
            if (url == null) {
                File f = new File(IMG_DIR + filename);
                if (f.exists()) url = f.toURI().toURL();
            }
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String resolveImgDir() {
        String[] candidates = {
            "src/main/java/com/motorph/img/",
            "main/java/com/motorph/img/",
            "com/motorph/img/",
            "img/",
        };
        for (String c : candidates) {
            if (new File(c).isDirectory()) return c;
        }
        return "src/main/java/com/motorph/img/";
    }

    static class RoundedBorder extends AbstractBorder {
        private final int   radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public java.awt.Insets getBorderInsets(Component c) {
            return new java.awt.Insets(radius, radius, radius, radius);
        }
    }
}

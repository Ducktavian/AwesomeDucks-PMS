package com.motorph.ui.it;

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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.motorph.model.Employee;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.service.EmployeeService;
import com.motorph.service.UserService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

/**
 * IT Users screen — search, role filter, CRUD action buttons, and the user
 * account table.
 *
 * NEW (redesign): restyled to match the Employees / Attendance / Payroll panels
 * (shared navy palette, search field, sortable header, alternating rows). The
 * duplicate profile chip that used to live in this panel was removed — MainFrame
 * already renders the shared top bar. Data is now loaded from the database via
 * UserService -> UserAccountDAO, joined with EmployeeService for the employee
 * details, and all CRUD actions persist through the service layer.
 */
public class UserAccountPanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color ROW_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final Color SELECTED_ROW = new Color(225, 230, 245);

    private static final String[] COLUMNS = {
        "Employee No.", "Name", "Status", "Position",
        "Immediate Supervisor", "Role"
    };

    // NEW: live services instead of hardcoded sample data.
    private final UserService userService = AppContext.getUserService();
    private final EmployeeService employeeService = AppContext.getEmployeeService();

    private final List<UserAccountEntry> allEntries = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable userTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    public UserAccountPanel() {
        // The Users tab is reachable only by Admin and IT (gated in MainFrame),
        // and both have identical full management, so there is no lower-privilege
        // view to switch to — hence no role changer here.
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildBody(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(17, 78, 40, 78));

        body.add(buildSearchRow());
        body.add(Box.createVerticalStrut(15));
        body.add(buildControlRow());
        body.add(Box.createVerticalStrut(22));
        body.add(buildTablePanel());

        return body;
    }

    private JPanel buildSearchRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        searchField = new JTextField("Search");
        searchField.setPreferredSize(new Dimension(305, 38));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchField.setForeground(new Color(200, 200, 200));
        searchField.setBorder(new CompoundBorder(
                new RoundedBorder(6, BORDER_GRAY),
                new EmptyBorder(5, 12, 5, 12)
        ));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText("Search");
                    searchField.setForeground(new Color(200, 200, 200));
                    applyFilters();
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        row.add(searchField);
        return row;
    }

    private JPanel buildControlRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(navyButton("+", "Add", 90, this::addUser));
        buttons.add(navyButton("✎", "Update", 105, this::updateUser));
        buttons.add(navyButton("🗑", "Delete", 105, this::deleteUser));
        buttons.add(navyButton("⟳", "Refresh", 110, this::refreshTable));

        row.add(buttons, BorderLayout.EAST);
        return row;
    }

    private JPanel buildTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(56);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userTable.setBackground(Color.WHITE);
        userTable.setFillsViewportHeight(true);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && userTable.getSelectedRow() != -1) {
                    updateUser();
                }
            }
        });

        styleHeader();
        styleColumns();
        styleCells();

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setColumnHeaderView(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUnitIncrement(16);
        verticalBar.setBackground(Color.WHITE);

        tablePanel.add(userTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // ---- data loading ----

    // Pulls accounts from the DB and joins each with its employee record.
    // Only IT and Admin may load this data; other roles skip silently.
    private void loadData() {
        UserAccount currentUser = Session.getCurrentUser();
        Role role = currentUser == null ? null : currentUser.getRole();
        if (role != Role.ADMIN && role != Role.IT) {
            return;
        }

        allEntries.clear();

        try {
            List<UserAccount> accounts = userService.listUsers();
            Map<String, Employee> employeeCache = new HashMap<>();

            for (UserAccount account : accounts) {
                String employeeId = String.valueOf(account.getEmployeeId());
                Employee emp = employeeCache.computeIfAbsent(
                        employeeId, id -> employeeService.findEmployee(id));
                allEntries.add(UserAccountEntry.fromAccount(account, emp));
            }

            applyFilters();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load user accounts:\n" + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void applyFilters() {
        if (tableModel == null) return;

        String query = searchField != null && !searchField.getText().equals("Search")
                ? searchField.getText().trim().toLowerCase()
                : "";

        tableModel.setRowCount(0);

        for (UserAccountEntry entry : allEntries) {
            String[] row = entry.toTableRow();

            if (!query.isEmpty()) {
                boolean match = false;
                for (String cell : row) {
                    if (cell != null && cell.toLowerCase().contains(query)) {
                        match = true;
                        break;
                    }
                }
                if (!match) continue;
            }

            tableModel.addRow(row);
        }
    }

    private UserAccountEntry getSelectedEntry() {
        int viewRow = userTable.getSelectedRow();
        if (viewRow < 0) return null;

        int modelRow = userTable.convertRowIndexToModel(viewRow);
        String empNo = String.valueOf(tableModel.getValueAt(modelRow, 0));

        for (UserAccountEntry entry : allEntries) {
            if (entry.getEmployeeNo().equals(empNo)) {
                return entry;
            }
        }
        return null;
    }

    // ---- CRUD actions (wired to the service layer) ----

    private void addUser() {
        List<Employee> available = employeesWithoutAccount();
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Every employee already has a user account.",
                    "Nothing to add",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        UserAccountFormPanel dialog = new UserAccountFormPanel(owner, available);
        dialog.setVisible(true);

        if (!dialog.isConfirmed()) return;

        try {
            UserAccountEntry e = dialog.getResult();
            userService.createUser(
                    e.getEmployeeId(), e.getUsername(), e.getNewPassword(), e.getRole(), e.isActive());
            loadData();
            JOptionPane.showMessageDialog(this, "User account created successfully.");
        } catch (Exception ex) {
            showError("Failed to create user account", ex);
        }
    }

    private void updateUser() {
        UserAccountEntry selected = getSelectedEntry();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.");
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        UserAccountFormPanel dialog = new UserAccountFormPanel(owner, selected);
        dialog.setVisible(true);

        if (!dialog.isConfirmed()) return;

        try {
            UserAccountEntry e = dialog.getResult();

            // Preserve the existing password hash unless a new password was entered.
            UserAccount account = userService.findById(e.getUserId());
            account.setUsername(e.getUsername());
            account.setActive(e.isActive());
            userService.updateUser(account);

            userService.changeRole(e.getUserId(), e.getRole());

            if (e.getNewPassword() != null && !e.getNewPassword().isBlank()) {
                userService.resetPassword(e.getUserId(), e.getNewPassword());
            }

            loadData();
            JOptionPane.showMessageDialog(this, "User account updated successfully.");
        } catch (Exception ex) {
            showError("Failed to update user account", ex);
        }
    }

    private void deleteUser() {
        UserAccountEntry selected = getSelectedEntry();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete the user account for " + selected.getFullName()
                        + " (" + selected.getEmployeeNo() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            userService.deleteUser(selected.getUserId());
            loadData();
            JOptionPane.showMessageDialog(this, "User account deleted successfully.");
        } catch (Exception ex) {
            showError("Failed to delete user account", ex);
        }
    }

    private void refreshTable() {
        if (searchField != null) {
            searchField.setText("Search");
            searchField.setForeground(new Color(200, 200, 200));
        }
        sortedColumn = -1;
        currentSortOrder = SortOrder.UNSORTED;
        if (sorter != null) {
            sorter.setSortKeys(null);
            sorter.setRowFilter(null);
        }
        userTable.clearSelection();
        loadData();
        userTable.getTableHeader().repaint();
    }

    // Employees that do not yet have a user account — candidates for "Add".
    private List<Employee> employeesWithoutAccount() {
        Set<String> taken = new HashSet<>();
        for (UserAccountEntry entry : allEntries) {
            taken.add(entry.getEmployeeNo());
        }

        List<Employee> available = new ArrayList<>();
        for (Employee emp : employeeService.getAllEmployees()) {
            if (emp.getEmployeeId() != null && !taken.contains(emp.getEmployeeId())) {
                available.add(emp);
            }
        }
        return available;
    }

    private void showError(String title, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                this, title + ":\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ---- table styling (mirrors EmployeePanel) ----

    private void styleHeader() {
        JTableHeader header = userTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 58));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        header.setDefaultRenderer(new HeaderFilterRenderer());

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewColumn = header.columnAtPoint(e.getPoint());
                if (viewColumn < 0) return;

                int modelColumn = userTable.convertColumnIndexToModel(viewColumn);
                toggleColumnSort(modelColumn);
            }
        });
    }

    private void toggleColumnSort(int column) {
        if (sortedColumn == column && currentSortOrder == SortOrder.ASCENDING) {
            currentSortOrder = SortOrder.DESCENDING;
        } else {
            currentSortOrder = SortOrder.ASCENDING;
        }

        sortedColumn = column;

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(column, currentSortOrder));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        userTable.getTableHeader().repaint();
    }

    private void styleColumns() {
        int[] widths = {135, 170, 115, 175, 210, 120};
        TableColumnModel columns = userTable.getColumnModel();

        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void styleCells() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Color bg = isSelected ? SELECTED_ROW : row % 2 == 0 ? Color.WHITE : ROW_GRAY;

                label.setText(value == null ? "" : value.toString());
                label.setOpaque(true);
                label.setBackground(bg);
                label.setForeground(Color.BLACK);
                label.setBorder(new EmptyBorder(0, 18, 0, 18));
                label.setFont(new Font("SansSerif", Font.PLAIN, 13));
                label.setVerticalAlignment(SwingConstants.CENTER);

                return label;
            }
        };

        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private JButton navyButton(String icon, String text, int width, Runnable action) {
        JButton button = new JButton(icon + "  " + text);
        button.setPreferredSize(new Dimension(width, 37));
        button.setBackground(NAVY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMargin(new Insets(0, 10, 0, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private class HeaderFilterRenderer extends JPanel implements TableCellRenderer {

        private final JLabel titleLabel;
        private final JLabel filterLabel;

        HeaderFilterRenderer() {
            setLayout(new BorderLayout(6, 0));
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(0, 18, 10, 18));

            titleLabel = new JLabel();
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            titleLabel.setForeground(Color.BLACK);

            filterLabel = new JLabel("⇅");
            filterLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            filterLabel.setForeground(new Color(130, 130, 130));
            filterLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            add(titleLabel, BorderLayout.CENTER);
            add(filterLabel, BorderLayout.EAST);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            int modelColumn = table.convertColumnIndexToModel(column);
            titleLabel.setText(value == null ? "" : value.toString());

            if (modelColumn == sortedColumn) {
                filterLabel.setText(currentSortOrder == SortOrder.ASCENDING ? "▲" : "▼");
                filterLabel.setForeground(NAVY);
            } else {
                filterLabel.setText("⇅");
                filterLabel.setForeground(new Color(130, 130, 130));
            }

            return this;
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}

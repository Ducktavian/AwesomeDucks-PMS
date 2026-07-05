package com.motorph.ui.employee;

import com.motorph.model.Employee;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.service.EmployeeService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class EmployeePanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color ROW_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final Color SELECTED_ROW = new Color(225, 230, 245);

    private static final String[] COLUMNS = {
        "Employee No.", "Name", "Status", "Position",
        "Immediate Supervisor"
    };

    private final EmployeeService employeeService = AppContext.getEmployeeService();

    private EmployeeFormPanel formPanel;
    private JDialog employeeDialog;

    private DefaultTableModel tableModel;
    private JTable employeeTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    private final List<Employee> allEmployees = new ArrayList<>();

    private String currentEmployeeId;
    private Role currentRole;   // the real logged-in role
    private Role viewAsRole;    // the role whose access is currently active

    private JComboBox<String> roleFilter;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;

    public EmployeePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        applyRBAC();

        JPanel listPanel = buildEmployeeListPanel();

        formPanel = new EmployeeFormPanel(() -> {
            closeEmployeeDialog();
            refreshTable();
        });

        add(listPanel, BorderLayout.CENTER);

        loadEmployees();
    }

    private void applyRBAC() {
        UserAccount user = Session.getCurrentUser();
        Role role = user == null ? null : user.getRole();
        currentRole = role == null ? Role.EMPLOYEE : role;

        currentEmployeeId = user == null ? "" : String.valueOf(user.getEmployeeId());

        // The dropdown lets a privileged user act as a lower role; access starts
        // at their real role on first load, but a previously chosen view is
        // preserved across refreshes (e.g. after a modal closes) so it doesn't
        // snap back.
        if (viewAsRole == null) {
            viewAsRole = currentRole;
        }
    }

    /** Admin/HR/Finance/IT all see the full directory; Employee sees only self. */
    private boolean canViewAll() {
        return viewAsRole != Role.EMPLOYEE;
    }

    /** Only Admin and HR may add/update/delete employees. */
    private boolean canModify() {
        return viewAsRole == Role.ADMIN || viewAsRole == Role.HR;
    }

    private JPanel buildEmployeeListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(buildBody(), BorderLayout.CENTER);
        return panel;
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
                    applySearchFilter();
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
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

        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftControls.setOpaque(false);
        JComboBox<String> filter = buildRoleFilter();
        if (filter != null) {
            leftControls.add(filter);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        addButton = navyButton("+", "Add", 90, this::addEmployee);
        updateButton = navyButton("✎", "Update", 105, this::updateEmployee);
        deleteButton = navyButton("🗑", "Delete", 105, this::deleteEmployee);
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);

        buttons.add(navyButton("⟳", "Refresh", 110, this::refreshTable));

        applyViewCapabilities();

        row.add(leftControls, BorderLayout.WEST);
        row.add(buttons, BorderLayout.EAST);
        return row;
    }

    private JComboBox<String> buildRoleFilter() {
        String[] views = getAllowedEmployeeRoles(currentRole);
        if (views.length <= 1) {
            return null; // pure Employee: nothing to switch, so no role changer
        }
        roleFilter = new JComboBox<>(views);
        // Reflect the currently active view so a rebuilt combo (after a modal
        // closes) stays on the last-selected role instead of resetting.
        roleFilter.setSelectedItem(viewAsRole.getRoleName());
        roleFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        roleFilter.setPreferredSize(new Dimension(140, 37));
        roleFilter.setBackground(Color.WHITE);
        roleFilter.setFocusable(false);
        roleFilter.addActionListener(e -> {
            viewAsRole = Role.fromName((String) roleFilter.getSelectedItem());
            applyViewCapabilities();
            loadEmployees();
        });
        return roleFilter;
    }

    /** Shows the modify buttons only for views that may edit the directory. */
    private void applyViewCapabilities() {
        boolean modify = canModify();
        if (addButton != null) addButton.setVisible(modify);
        if (updateButton != null) updateButton.setVisible(modify);
        if (deleteButton != null) deleteButton.setVisible(modify);
    }

    /** Returns the roles this login may view the panel as (own role + Employee; Admin sees all). */
    private static String[] getAllowedEmployeeRoles(Role role) {
        if (role == null) return new String[]{"Employee"};
        return switch (role) {
            case ADMIN -> new String[]{"Admin", "Finance", "HR", "IT", "Employee"};
            case FINANCE -> new String[]{"Finance", "Employee"};
            case HR -> new String[]{"HR", "Employee"};
            case IT -> new String[]{"IT", "Employee"};
            case EMPLOYEE -> new String[]{"Employee"};
        };
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

        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(56);
        employeeTable.setShowGrid(false);
        employeeTable.setIntercellSpacing(new Dimension(0, 0));
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        employeeTable.setBackground(Color.WHITE);
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);

        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && employeeTable.getSelectedRow() != -1) {
                    openSelectedEmployeeDetails();
                }
            }
        });

        styleHeader();
        styleColumns();
        styleCells();

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setColumnHeaderView(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUnitIncrement(16);
        verticalBar.setBackground(Color.WHITE);

        tablePanel.add(employeeTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void openSelectedEmployeeDetails() {
        Employee selectedEmployee = getSelectedEmployee();

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Employee not found.");
            return;
        }

        boolean isSelf = selectedEmployee.getEmployeeId().equals(currentEmployeeId);

        if (!canViewAll() && !isSelf) {
            JOptionPane.showMessageDialog(
                    this,
                    "You can only view your own employee details.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        formPanel.setViewMode(selectedEmployee);
        showEmployeeDialog("Employee Details");
    }

    /** Opens the logged-in employee's own record without changing list scope. */
    public void openCurrentEmployeeDetails() {
        UserAccount user = Session.getCurrentUser();
        if (user == null) {
            return;
        }

        String employeeId = String.valueOf(user.getEmployeeId());
        Employee employee = employeeService.findEmployee(employeeId);

        if (employee == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Your employee information could not be found.",
                    "Employee Not Found",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        formPanel.setViewMode(employee);
        showEmployeeDialog("Employee Details");
    }

    private Employee getSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow == -1) {
            return null;
        }

        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        String employeeId = tableModel.getValueAt(modelRow, 0).toString();

        for (Employee emp : allEmployees) {
            if (emp.getEmployeeId().equals(employeeId)) {
                return emp;
            }
        }

        return null;
    }

    private void styleHeader() {
        JTableHeader header = employeeTable.getTableHeader();
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

                int modelColumn = employeeTable.convertColumnIndexToModel(viewColumn);
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

        employeeTable.getTableHeader().repaint();
    }

    private void styleColumns() {
        int[] widths = {135, 190, 120, 210, 240};
        TableColumnModel columns = employeeTable.getColumnModel();

        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void styleCells() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
                );

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

        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
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

    private void loadEmployees() {
        allEmployees.clear();

        List<Employee> employees = employeeService.getAllEmployees();

        // The dropdown is a role changer, not a directory filter: an all-access
        // view shows every employee, while the Employee view shows only self.
        if (canViewAll()) {
            allEmployees.addAll(employees);
        } else {
            for (Employee employee : employees) {
                if (employee.getEmployeeId().equals(currentEmployeeId)) {
                    allEmployees.add(employee);
                    break;
                }
            }
        }

        populateTable();
    }

    private void populateTable() {
        tableModel.setRowCount(0);

        for (Employee emp : allEmployees) {
            tableModel.addRow(toTableRow(emp));
        }

        applySearchFilter();
    }

    private void applySearchFilter() {
        if (sorter == null || searchField == null) return;

        String query = searchField.getText().trim();

        if (query.equalsIgnoreCase("Search") || query.isBlank()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query)));
    }

    private String[] toTableRow(Employee emp) {
        return new String[]{
            emp.getEmployeeId(),
            emp.getFullName(),
            emp.getStatus(),
            emp.getPosition(),
            emp.getImmediateSupervisor()
        };
    }
    
    private void refreshTable() {
        searchField.setText("Search");
        searchField.setForeground(new Color(200, 200, 200));

        sortedColumn = -1;
        currentSortOrder = SortOrder.UNSORTED;

        if (sorter != null) {
            sorter.setSortKeys(null);
            sorter.setRowFilter(null);
        }

        employeeTable.clearSelection();
        loadEmployees();
        employeeTable.getTableHeader().repaint();
    }

    private void addEmployee() {
        if (!canModify()) return;

        formPanel.setAddMode();
        showEmployeeDialog("Add Employee");
    }

    private void updateEmployee() {
        if (!canModify()) return;

        Employee selectedEmployee = getSelectedEmployee();

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update.");
            return;
        }

        formPanel.setUpdateMode(selectedEmployee);
        showEmployeeDialog("Update Employee");
    }

    private void showEmployeeDialog(String title) {
        closeEmployeeDialog();

        Window owner = SwingUtilities.getWindowAncestor(this);
        employeeDialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        employeeDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        employeeDialog.setContentPane(formPanel);
        employeeDialog.setSize(720, 760);
        employeeDialog.setMinimumSize(new Dimension(640, 560));
        employeeDialog.setLocationRelativeTo(this);
        employeeDialog.setVisible(true);
    }

    private void closeEmployeeDialog() {
        if (employeeDialog != null) {
            employeeDialog.dispose();
            employeeDialog = null;
        }
    }
    private void deleteEmployee() {
        if (!canModify()) return;

        Employee selectedEmployee = getSelectedEmployee();

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }

        String employeeId = selectedEmployee.getEmployeeId();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete employee " + employeeId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            employeeService.deleteEmployee(employeeId);
            JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
            refreshTable();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to delete employee:\n" + ex.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

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

    static class CircleAvatar extends JPanel {
        private final int size;

        CircleAvatar(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillOval(0, 0, size - 1, size - 1);
            g2.dispose();
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

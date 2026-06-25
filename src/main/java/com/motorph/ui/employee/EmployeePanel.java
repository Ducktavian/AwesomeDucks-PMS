package com.motorph.ui.employee;

import com.motorph.model.Employee;
import com.motorph.service.EmployeeService;
import com.motorph.util.AppContext;

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

    private static final String EMPLOYEE_LIST = "EMPLOYEE_LIST";
    private static final String EMPLOYEE_FORM = "EMPLOYEE_FORM";

    private static final String[] COLUMNS = {
        "Employee No.", "Name", "Status", "Position",
        "Immediate Supervisor", "Role"
    };

    private final EmployeeService employeeService = AppContext.getEmployeeService();

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private EmployeeFormPanel formPanel;

    private DefaultTableModel tableModel;
    private JTable employeeTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    private final List<Employee> allEmployees = new ArrayList<>();

    public EmployeePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel listPanel = buildEmployeeListPanel();

        formPanel = new EmployeeFormPanel(() -> {
            cardLayout.show(cardPanel, EMPLOYEE_LIST);
            refreshTable();
        });

        cardPanel.add(listPanel, EMPLOYEE_LIST);
        cardPanel.add(formPanel, EMPLOYEE_FORM);

        add(cardPanel, BorderLayout.CENTER);

        loadEmployees();
    }

    private JPanel buildEmployeeListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        panel.add(buildTopBar(), BorderLayout.NORTH);
        panel.add(buildBody(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(100, 80));
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(180, 180, 180)));

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 15));
        profile.setOpaque(false);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("Name");
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setForeground(NAVY);
        name.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel position = new JLabel("Position");
        position.setFont(new Font("SansSerif", Font.PLAIN, 13));
        position.setForeground(Color.GRAY);
        position.setAlignmentX(Component.RIGHT_ALIGNMENT);

        text.add(name);
        text.add(position);

        CircleAvatar avatar = new CircleAvatar(48);

        profile.add(text);
        profile.add(avatar);

        topBar.add(profile, BorderLayout.EAST);
        return topBar;
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

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        buttons.add(navyButton("+", "Add", 90, this::addEmployee));
        buttons.add(navyButton("✎", "Update", 105, this::updateEmployee));
        buttons.add(navyButton("🗑", "Delete", 105, this::deleteEmployee));
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
        int[] widths = {135, 170, 115, 175, 210, 120};
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
        allEmployees.addAll(employeeService.getAllEmployees());
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
            emp.getImmediateSupervisor(),
            ""
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
        formPanel.setAddMode();
        cardLayout.show(cardPanel, EMPLOYEE_FORM);
    }

    private void updateEmployee() {
        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update.");
            return;
        }

        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        String employeeId = tableModel.getValueAt(modelRow, 0).toString();

        Employee selectedEmployee = null;

        for (Employee emp : allEmployees) {
            if (emp.getEmployeeId().equals(employeeId)) {
                selectedEmployee = emp;
                break;
            }
        }

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Employee not found.");
            return;
        }

        formPanel.setUpdateMode(selectedEmployee);
        cardLayout.show(cardPanel, EMPLOYEE_FORM);
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }

        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        String employeeId = tableModel.getValueAt(modelRow, 0).toString();

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
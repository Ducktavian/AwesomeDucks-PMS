package com.motorph.ui.attendance;

import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.util.Session;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class AttendancePanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color ROW_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final Color SELECTED_ROW = new Color(225, 230, 245);
    private static final String FONT = "Segoe UI";

    private static final String[] COLUMNS = {
        "Employee ID", "Type", "Date", "Time In", "Break Out", "Break In",
        "Time Out", "Total Hours Worked", "Validity"
    };

    private final List<Object[]> attendanceRows = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable attendanceTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private boolean canViewAllAttendance;
    private boolean canAddAttendance;
    private boolean canModifyAttendance;
    private String currentEmployeeId;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    public AttendancePanel() {
        applyRBAC();
        loadSampleRows();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(buildBody(), BorderLayout.CENTER);
    }

    private void applyRBAC() {
        UserAccount user = Session.getCurrentUser();
        Role role = user == null ? null : user.getRole();

        currentEmployeeId = user == null ? "" : String.valueOf(user.getEmployeeId());

        canViewAllAttendance = role == Role.ADMIN || role == Role.HR;
        canModifyAttendance = role == Role.ADMIN || role == Role.HR;

        canAddAttendance = role == Role.ADMIN
                || role == Role.HR
                || role == Role.IT
                || role == Role.FINANCE
                || role == Role.EMPLOYEE;
    }

    private boolean canSeeRow(String employeeId) {
        return canViewAllAttendance || employeeId.equals(currentEmployeeId);
    }

    private void showAttendanceList() {
        removeAll();
        setLayout(new BorderLayout());
        applyRBAC();
        add(buildBody(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void loadSampleRows() {
        attendanceRows.clear();
        attendanceRows.add(new Object[]{"10001", "Regular", "09/01/2026", "8:00 AM", "12:00 PM", "1:00 PM", "5:00 PM", "8 hrs", "Valid"});
        attendanceRows.add(new Object[]{"10002", "Regular", "05/01/2026", "8:00 AM", "12:00 PM", "1:00 PM", "5:00 PM", "8 hrs", "Valid"});
        attendanceRows.add(new Object[]{"10003", "Overtime", "09/01/2026", "8:00 AM", "12:00 PM", "1:00 PM", "7:00 PM", "10 hrs", "Valid"});
        attendanceRows.add(new Object[]{"10004", "Holiday", "05/01/2026", "8:00 AM", "12:00 PM", "1:00 PM", "5:00 PM", "8 hrs", "Invalid"});
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
        searchField.setFont(new Font(FONT, Font.PLAIN, 18));
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

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        JButton addButton = navyButton("+", "Add", 90);
        addButton.setVisible(canAddAttendance);
        addButton.addActionListener(e -> openAddForm());
        rightButtons.add(addButton);

        JButton updateButton = navyButton("✎", "Update", 105);
        updateButton.setVisible(canModifyAttendance);
        updateButton.addActionListener(e -> openUpdateForm());
        rightButtons.add(updateButton);

        JButton deleteButton = navyButton("🗑", "Delete", 105);
        deleteButton.setVisible(canModifyAttendance);
        deleteButton.addActionListener(e -> deleteSelectedRow());
        rightButtons.add(deleteButton);

        JButton refreshButton = navyButton("⟳", "Refresh", 110);
        refreshButton.addActionListener(e -> {
            loadSampleRows();
            showAttendanceList();
        });
        rightButtons.add(refreshButton);

        row.add(rightButtons, BorderLayout.EAST);
        return row;
    }

    private void openAddForm() {
        removeAll();
        setLayout(new BorderLayout());

        add(new AttendanceFormPanel(
                this::showAttendanceList,
                rowData -> {
                    attendanceRows.add(rowData);
                    showAttendanceList();
                }
        ), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void openUpdateForm() {
        int selectedIndex = getSelectedAttendanceRowIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an attendance entry to update.",
                    "No Entry Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object[] existingData = attendanceRows.get(selectedIndex);

        removeAll();
        setLayout(new BorderLayout());

        add(new AttendanceFormPanel(
                this::showAttendanceList,
                existingData,
                updatedData -> {
                    attendanceRows.set(selectedIndex, updatedData);
                    showAttendanceList();
                }
        ), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void openViewOnlyForm() {
        int selectedIndex = getSelectedAttendanceRowIndex();

        if (selectedIndex == -1) {
            return;
        }

        Object[] existingData = attendanceRows.get(selectedIndex);

        AttendanceFormPanel formPanel = new AttendanceFormPanel(
                this::showAttendanceList,
                existingData,
                updatedData -> {
                    // View-only mode. No update action.
                }
        );

        setViewOnly(formPanel);

        removeAll();
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void deleteSelectedRow() {
        int selectedIndex = getSelectedAttendanceRowIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an attendance entry to delete.",
                    "No Entry Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this attendance entry?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            attendanceRows.remove(selectedIndex);
            showAttendanceList();

            JOptionPane.showMessageDialog(
                    this,
                    "Attendance entry deleted successfully.",
                    "Deleted",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private int getSelectedAttendanceRowIndex() {
        int selectedRow = attendanceTable.getSelectedRow();

        if (selectedRow == -1) {
            return -1;
        }

        int modelRow = attendanceTable.convertRowIndexToModel(selectedRow);
        Object[] selectedData = new Object[tableModel.getColumnCount()];

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            selectedData[i] = tableModel.getValueAt(modelRow, i);
        }

        for (int i = 0; i < attendanceRows.size(); i++) {
            if (rowsMatch(attendanceRows.get(i), selectedData)) {
                return i;
            }
        }

        return -1;
    }

    private boolean rowsMatch(Object[] a, Object[] b) {
        if (a == null || b == null || a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            String valueA = String.valueOf(a[i]);
            String valueB = String.valueOf(b[i]);

            if (!valueA.equals(valueB)) {
                return false;
            }
        }

        return true;
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

        for (Object[] row : attendanceRows) {
            String employeeId = String.valueOf(row[0]);

            if (canSeeRow(employeeId)) {
                tableModel.addRow(row);
            }
        }

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(56);
        attendanceTable.setShowGrid(false);
        attendanceTable.setIntercellSpacing(new Dimension(0, 0));
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attendanceTable.setFont(new Font(FONT, Font.PLAIN, 13));
        attendanceTable.setBackground(Color.WHITE);
        attendanceTable.setFillsViewportHeight(true);
        attendanceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        attendanceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && attendanceTable.getSelectedRow() != -1) {
                    openViewOnlyForm();
                }
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        attendanceTable.setRowSorter(sorter);

        styleHeader();
        styleColumns();
        styleCells();

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setColumnHeaderView(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        tablePanel.add(attendanceTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void styleHeader() {
        JTableHeader header = attendanceTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 58));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font(FONT, Font.BOLD, 13));
        header.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        header.setDefaultRenderer(new HeaderFilterRenderer());

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewColumn = header.columnAtPoint(e.getPoint());
                if (viewColumn < 0) return;

                int modelColumn = attendanceTable.convertColumnIndexToModel(viewColumn);
                toggleColumnSort(modelColumn);
            }
        });
    }

    private void toggleColumnSort(int column) {
        currentSortOrder = sortedColumn == column && currentSortOrder == SortOrder.ASCENDING
                ? SortOrder.DESCENDING
                : SortOrder.ASCENDING;

        sortedColumn = column;

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(column, currentSortOrder));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        attendanceTable.getTableHeader().repaint();
    }

    private void styleColumns() {
        int[] widths = {120, 100, 140, 95, 95, 95, 95, 150, 105};
        TableColumnModel columns = attendanceTable.getColumnModel();

        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void styleCells() {
        for (int i = 0; i < attendanceTable.getColumnCount(); i++) {
            if (i == 8) {
                attendanceTable.getColumnModel().getColumn(i).setCellRenderer(new ValidityRenderer());
            } else {
                attendanceTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultAttendanceCellRenderer());
            }
        }
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

    private void setViewOnly(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setEditable(false);
            } else if (component instanceof JTextArea) {
                ((JTextArea) component).setEditable(false);
            } else if (component instanceof JComboBox) {
                component.setEnabled(false);
            } else if (component instanceof JCheckBox) {
                component.setEnabled(false);
            } else if (component instanceof JRadioButton) {
                component.setEnabled(false);
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;
                String text = button.getText() == null ? "" : button.getText().trim();

                if (text.equalsIgnoreCase("Submit")
                        || text.equalsIgnoreCase("Save")
                        || text.equalsIgnoreCase("Confirm")
                        || text.equalsIgnoreCase("Add")
                        || text.equalsIgnoreCase("Update")) {
                    button.setVisible(false);
                }
            }

            if (component instanceof Container) {
                setViewOnly((Container) component);
            }
        }
    }

    private JButton navyButton(String icon, String text, int width) {
        String label = icon == null || icon.isBlank() ? text : icon + "  " + text;

        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(width, 37));
        button.setBackground(NAVY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(FONT, Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMargin(new Insets(0, 10, 0, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private class HeaderFilterRenderer extends JPanel implements TableCellRenderer {

        private final JLabel titleLabel = new JLabel();
        private final JLabel filterLabel = new JLabel("⇅");

        HeaderFilterRenderer() {
            setLayout(new BorderLayout(6, 0));
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(0, 18, 10, 18));

            titleLabel.setFont(new Font(FONT, Font.BOLD, 13));
            titleLabel.setForeground(Color.BLACK);

            filterLabel.setFont(new Font(FONT, Font.BOLD, 12));
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

    private static class DefaultAttendanceCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

            Color bg = isSelected ? SELECTED_ROW : row % 2 == 0 ? Color.WHITE : ROW_GRAY;

            label.setText(value == null ? "" : value.toString());
            label.setOpaque(true);
            label.setBackground(bg);
            label.setForeground(Color.BLACK);
            label.setBorder(new EmptyBorder(0, 18, 0, 18));
            label.setFont(new Font(FONT, Font.PLAIN, 13));
            label.setVerticalAlignment(SwingConstants.CENTER);

            return label;
        }
    }

    private static class ValidityRenderer extends JPanel implements TableCellRenderer {

        private String status = "";
        private Color rowBackground = Color.WHITE;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            status = value == null ? "" : value.toString();
            rowBackground = isSelected ? SELECTED_ROW : row % 2 == 0 ? Color.WHITE : ROW_GRAY;
            setBackground(rowBackground);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color pillColor = "Valid".equalsIgnoreCase(status)
                    ? new Color(0, 190, 100)
                    : new Color(255, 82, 82);

            int pillW = 72;
            int pillH = 26;
            int x = (getWidth() - pillW) / 2;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pillColor);
            g2.fillRoundRect(x, y, pillW, pillH, pillH, pillH);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font(FONT, Font.PLAIN, 11));

            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (pillW - fm.stringWidth(status)) / 2;
            int textY = y + ((pillH - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(status, textX, textY);
            g2.dispose();
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
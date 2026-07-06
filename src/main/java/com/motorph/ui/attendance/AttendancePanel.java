package com.motorph.ui.attendance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
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

import com.motorph.model.Attendance;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.service.AttendanceService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

public class AttendancePanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color ROW_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final Color SELECTED_ROW = new Color(225, 230, 245);
    private static final String FONT = "Segoe UI";

    private static final String[] COLUMNS = {
        "Employee ID", "Type", "Date", "Time In",
        "Time Out", "Total Hours Worked", "Validity"
    };

    private final List<Object[]> attendanceRows = new ArrayList<>();

    // backing records aligned 1:1 with attendanceRows, so a selected row
    // can be resolved to its real attendance id for update/delete
    private final List<Attendance> records = new ArrayList<>();

    // connection to the database via service -> dao
    private final AttendanceService attendanceService = AppContext.getAttendanceService();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("h:mm a");     

    private DefaultTableModel tableModel;
    private JTable attendanceTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private String currentEmployeeId;
    private Role currentRole;   // the real logged-in role
    private Role viewAsRole;    // the role whose access is currently active

    private JComboBox<String> roleFilter;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;

    private JDialog attendanceDialog;

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
        currentRole = role == null ? Role.EMPLOYEE : role;

        currentEmployeeId = user == null ? "" : String.valueOf(user.getEmployeeId());

        // The dropdown acts as a role changer; access starts at the real role
        // on first load, but a previously chosen view is preserved across
        // refreshes (e.g. after a modal closes) so it doesn't snap back.
        if (viewAsRole == null) {
            viewAsRole = currentRole;
        }
    }

    /** Only Admin and HR see every employee's attendance; others see their own. */
    private boolean canViewAll() {
        return viewAsRole == Role.ADMIN || viewAsRole == Role.HR;
    }

    /** Update/Delete are limited to Admin and HR. */
    private boolean canModify() {
        return viewAsRole == Role.ADMIN || viewAsRole == Role.HR;
    }

    private void showAttendanceList() {
        removeAll();
        setLayout(new BorderLayout());
        applyRBAC();
        loadSampleRows(); // reload from the database whenever the list is shown
        add(buildBody(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // NEW: loads attendance from the database (service -> dao)
    // Privileged roles pull every record; everyone else only pulls their own.
    private void loadSampleRows() {
        attendanceRows.clear();
        records.clear();

        try {
            // Role changer, not a filter: all-access views pull every record,
            // self-scoped views pull only the logged-in user's own attendance.
            List<Attendance> loaded = canViewAll()
                    ? attendanceService.getAllAttendance()
                    : attendanceService.getAllAttendance(currentEmployeeId);

            // The DAO returns records oldest-first; show the most recent on top.
            for (int i = loaded.size() - 1; i >= 0; i--) {
                Attendance record = loaded.get(i);
                records.add(record);
                attendanceRows.add(toTableRow(record));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load attendance:\n" + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // NEW: maps one Attendance record into the table-row format this panel expects.
    // Type ay wala pa, so that cell is left blank.
    private Object[] toTableRow(Attendance a) {
        double hours = attendanceService.computeDailyHours(a);
        // "Valid" mirrors payroll: a record only counts when it is complete AND
        // its status is payable (see AttendanceService.isValidForPayroll).
        boolean valid = attendanceService.isValidForPayroll(a);

        return new Object[]{
            a.getEmployeeId(),
            "",                                       // Type (not modeled yet)
            a.getDate() == null ? "" : a.getDate().format(DATE_FMT),
            a.getLogIn() == null ? "" : a.getLogIn().format(TIME_FMT),
            a.getLogOut() == null ? "" : a.getLogOut().format(TIME_FMT),
            valid ? hours + " hrs" : "",
            valid ? "Valid" : "Invalid"
        };
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

        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftControls.setOpaque(false);
        JComboBox<String> filter = buildRoleFilter();
        if (filter != null) {
            leftControls.add(filter);
        }

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        // Everyone may log attendance, so Add is always present; its label
        // becomes "File Attendance" in a self-scoped view (you log your own).
        addButton = button("+  Add", 105);
        addButton.addActionListener(e -> openAddForm());
        rightButtons.add(addButton);

        updateButton = button("✎  Update", 105);
        updateButton.addActionListener(e -> openUpdateForm());
        rightButtons.add(updateButton);

        deleteButton = button("🗑  Delete", 105);
        deleteButton.addActionListener(e -> deleteSelectedRow());
        rightButtons.add(deleteButton);

        JButton refreshButton = button("⟳  Refresh", 105);
        refreshButton.addActionListener(e -> showAttendanceList()); // reloads from DB
        rightButtons.add(refreshButton);

        applyViewCapabilities();

        row.add(leftControls, BorderLayout.WEST);
        row.add(rightButtons, BorderLayout.EAST);
        return row;
    }

    private JComboBox<String> buildRoleFilter() {
        String[] views = getAllowedAttendanceRoles(currentRole);
        if (views.length <= 1) {
            return null; // pure Employee: nothing to switch, so no role changer
        }
        roleFilter = new JComboBox<>(views);
        // Reflect the currently active view so a rebuilt combo (after a modal
        // closes) stays on the last-selected role instead of resetting.
        roleFilter.setSelectedItem(viewAsRole.getRoleName());
        roleFilter.setFont(new Font(FONT, Font.PLAIN, 13));
        roleFilter.setPreferredSize(new Dimension(140, 37));
        roleFilter.setBackground(Color.WHITE);
        roleFilter.setFocusable(false);
        roleFilter.addActionListener(e -> {
            viewAsRole = Role.fromName((String) roleFilter.getSelectedItem());
            applyViewCapabilities();
            loadSampleRows();
            refreshTableRows();
        });
        return roleFilter;
    }

    /** Relabels Add and toggles Update/Delete to match the active view. */
    private void applyViewCapabilities() {
        if (addButton != null) {
            addButton.setText(canViewAll() ? "+  Add" : "+  File Attendance");
        }
        boolean modify = canModify();
        if (updateButton != null) updateButton.setVisible(modify);
        if (deleteButton != null) deleteButton.setVisible(modify);
    }

    private static String[] getAllowedAttendanceRoles(Role role) {
        if (role == null) return new String[]{"Employee"};
        return switch (role) {
            case ADMIN -> new String[]{"Admin", "Finance", "HR", "IT", "Employee"};
            case FINANCE -> new String[]{"Finance", "Employee"};
            case HR -> new String[]{"HR", "Employee"};
            case IT -> new String[]{"IT", "Employee"};
            case EMPLOYEE -> new String[]{"Employee"};
        };
    }

    // NEW: repopulate the table from the currently loaded rows. Used when the
    // scope dropdown changes so the whole panel doesn't have to be rebuilt.
    private void refreshTableRows() {
        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        for (Object[] row : attendanceRows) {
            tableModel.addRow(row);
        }
    }

    private void openAddForm() {
        // "View All" (Admin/HR) files for anyone through an employee picker;
        // self-scoped views auto-fill and lock the logged-in user's identity.
        AttendanceFormPanel.Mode mode = canViewAll()
                ? AttendanceFormPanel.Mode.ADD_OTHER
                : AttendanceFormPanel.Mode.ADD_SELF;

        AttendanceFormPanel form = new AttendanceFormPanel(this::onFormClosed, mode);
        showAttendanceDialog(form, canViewAll() ? "Add Attendance" : "File Attendance");
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
        int attendanceId = records.get(selectedIndex).getAttendanceId(); // real id for the update

        AttendanceFormPanel form = new AttendanceFormPanel(
                this::onFormClosed, AttendanceFormPanel.Mode.EDIT, existingData, attendanceId);
        showAttendanceDialog(form, "Update Attendance");
    }

    private void openViewOnlyForm() {
        int selectedIndex = getSelectedAttendanceRowIndex();

        if (selectedIndex == -1) {
            return;
        }

        AttendanceFormPanel form = new AttendanceFormPanel(
                this::onFormClosed, AttendanceFormPanel.Mode.VIEW,
                attendanceRows.get(selectedIndex), null);
        showAttendanceDialog(form, "Attendance Details");
    }

    private void showAttendanceDialog(AttendanceFormPanel form, String title) {
        closeAttendanceDialog();

        Window owner = SwingUtilities.getWindowAncestor(this);
        attendanceDialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        attendanceDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        attendanceDialog.setContentPane(form);
        attendanceDialog.setSize(560, 640);
        attendanceDialog.setMinimumSize(new Dimension(520, 520));
        attendanceDialog.setLocationRelativeTo(this);
        attendanceDialog.setVisible(true);
    }

    private void onFormClosed() {
        closeAttendanceDialog();
        showAttendanceList();
    }

    private void closeAttendanceDialog() {
        if (attendanceDialog != null) {
            attendanceDialog.dispose();
            attendanceDialog = null;
        }
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
            try {
                // delete from the database, then reload the list
                int attendanceId = records.get(selectedIndex).getAttendanceId();
                attendanceService.deleteAttendance(String.valueOf(attendanceId));
                showAttendanceList();

                JOptionPane.showMessageDialog(
                        this,
                        "Attendance entry deleted successfully.",
                        "Deleted",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to delete attendance:\n" + ex.getMessage(),
                        "Delete Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
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
            tableModel.addRow(row);
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
        int[] widths = {120, 100, 140, 95, 95, 150, 105};
        TableColumnModel columns = attendanceTable.getColumnModel();

        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void styleCells() {
        for (int i = 0; i < attendanceTable.getColumnCount(); i++) {
            if (i == 6) {
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

    private JButton button(String text, int width) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(width, 37));
        btn.setBackground(NAVY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(0, 8, 0, 8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
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

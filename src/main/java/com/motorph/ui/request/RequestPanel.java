package com.motorph.ui.request;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.motorph.model.Employee;
import com.motorph.model.LeaveRequest;
import com.motorph.model.OvertimeRequest;
import com.motorph.model.Request;
import com.motorph.model.RequestStatus;
import com.motorph.model.RequestType;
import com.motorph.model.Role;
import com.motorph.model.UndertimeRequest;
import com.motorph.model.UserAccount;
import com.motorph.service.EmployeeService;
import com.motorph.service.RequestService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

public class RequestPanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color ROW_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final Color SELECTED_ROW = new Color(225, 230, 245);
    private static final String FONT = "Segoe UI";

    private static final int STATUS_COL = 9;
    private static final int EMPLOYEE_ID_COL = 10;

    private static final String[] COLUMNS = {
        "Name", "Position", "Request Type", "Start Date", "End Date",
        "Start Time", "End Time", "Reason", "Notes", "Status", "Employee ID"
    };

    private final List<Object[]> requestRows = new ArrayList<>();

    // backing requests aligned 1:1 with requestRows, so a selected row can be
    // resolved to its real request (id + type) for update/delete
    private final List<Request> requests = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable requestTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private String currentEmployeeId;
    private Role currentRole;   // the real logged-in role
    private Role viewAsRole;    // the role whose access is currently active

    private JComboBox<String> roleFilter;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JDialog requestDialog;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    private final RequestService requestService = AppContext.getRequestService();
    private final EmployeeService employeeService = AppContext.getEmployeeService();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("h:mm a");

    public RequestPanel() {
        applyRBAC();
        loadRequestRows();

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

    /** Only Admin and HR see every request; other views see their own only. */
    private boolean canViewAll() {
        return viewAsRole == Role.ADMIN || viewAsRole == Role.HR;
    }

    /** Update/Delete of any request are limited to Admin and HR. */
    private boolean canModify() {
        return viewAsRole == Role.ADMIN || viewAsRole == Role.HR;
    }

    private boolean isPending(Object[] row) {
        return row != null
                && row.length > STATUS_COL
                && "Pending".equalsIgnoreCase(String.valueOf(row[STATUS_COL]));
    }

    private boolean canModifyRequest(Object[] row) {
        if (canViewAll()) {
            return true;
        }

        String employeeId = String.valueOf(row[EMPLOYEE_ID_COL]);
        return employeeId.equals(currentEmployeeId) && isPending(row);
    }

    private void showRequestList() {
        removeAll();
        setLayout(new BorderLayout());
        applyRBAC();
        loadRequestRows(); // reload from the database whenever the list is shown
        add(buildBody(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void loadRequestRows() {
        requestRows.clear();
        requests.clear();

        try {
            // Role changer, not a filter: all-access views pull every request,
            // self-scoped views pull only the logged-in user's own requests.
            List<Request> loaded;
            if (canViewAll()) {
                loaded = requestService.findAll();
            } else if (!currentEmployeeId.isBlank()) {
                loaded = requestService.findByEmployee(Integer.parseInt(currentEmployeeId));
            } else {
                loaded = new ArrayList<>();
            }

            Map<String, Employee> employeeCache = new HashMap<>();

            for (Request request : loaded) {
                requests.add(request);
                requestRows.add(toTableRow(request, employeeCache));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load requests:\n" + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Object[] toTableRow(Request request, Map<String, Employee> employeeCache) {
        String employeeId = String.valueOf(request.getEmployeeId());

        Employee employee = employeeCache.computeIfAbsent(
                employeeId,
                id -> employeeService.findEmployee(id)
        );

        String name = employee == null ? "" : employee.getFullName();
        String position = employee == null ? "" : employee.getPosition();

        String startDate = "";
        String endDate = "";
        String startTime = "";
        String endTime = "";

        if (request instanceof LeaveRequest leave) {
            startDate = leave.getStartDate() == null ? "" : leave.getStartDate().format(DATE_FMT);
            endDate = leave.getEndDate() == null ? "" : leave.getEndDate().format(DATE_FMT);

        } else if (request instanceof OvertimeRequest overtime) {
            startDate = overtime.getOvertimeDate() == null ? "" : overtime.getOvertimeDate().format(DATE_FMT);
            endDate = startDate;
            startTime = overtime.getStartTime() == null ? "" : overtime.getStartTime().format(TIME_FMT);
            endTime = overtime.getEndTime() == null ? "" : overtime.getEndTime().format(TIME_FMT);

        } else if (request instanceof UndertimeRequest undertime) {
            startDate = undertime.getUndertimeDate() == null ? "" : undertime.getUndertimeDate().format(DATE_FMT);
            endDate = startDate;
            startTime = undertime.getStartTime() == null ? "" : undertime.getStartTime().format(TIME_FMT);
            endTime = undertime.getEndTime() == null ? "" : undertime.getEndTime().format(TIME_FMT);
        }

        return new Object[]{
            name,
            position,
            request.getRequestType() == null ? "" : pretty(request.getRequestType().name()),
            startDate,
            endDate,
            startTime,
            endTime,
            request.getReason() == null ? "" : request.getReason(),
            "",
            request.getStatus() == null ? "" : pretty(request.getStatus().name()),
            employeeId
        };
    }

    private String pretty(String enumName) {
        if (enumName == null || enumName.isBlank()) {
            return "";
        }

        String lower = enumName.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
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

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        // Add is always present; in a self-scoped view it becomes "File Request"
        // (you file your own), while Admin/HR use it to add for anyone.
        addButton = button("+  Add", 105);
        addButton.addActionListener(e -> openAddForm());
        buttons.add(addButton);

        updateButton = button("✎  Update", 105);
        updateButton.addActionListener(e -> openUpdateForm());
        buttons.add(updateButton);

        deleteButton = button("🗑  Delete", 105);
        deleteButton.addActionListener(e -> deleteSelectedRequest());
        buttons.add(deleteButton);

        JButton refreshButton = button("⟳  Refresh", 105);
        refreshButton.addActionListener(e -> showRequestList()); // reloads from DB
        buttons.add(refreshButton);

        applyViewCapabilities();

        row.add(leftControls, BorderLayout.WEST);
        row.add(buttons, BorderLayout.EAST);
        return row;
    }

    private JComboBox<String> buildRoleFilter() {
        String[] views = getAllowedRequestRoles(currentRole);
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
            loadRequestRows();
            refreshTableRows();
        });
        return roleFilter;
    }

    /** Relabels Add and toggles Update/Delete to match the active view. */
    private void applyViewCapabilities() {
        if (addButton != null) {
            addButton.setText(canViewAll() ? "+  Add" : "+  File Request");
        }
        boolean modify = canModify();
        if (updateButton != null) updateButton.setVisible(modify);
        if (deleteButton != null) deleteButton.setVisible(modify);
    }

    /** Returns the roles this login may view the panel as (own role + Employee; Admin sees all). */
    private static String[] getAllowedRequestRoles(Role role) {
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

        for (Object[] row : requestRows) {
            tableModel.addRow(row);
        }
    }

    private void openAddForm() {
        // Employee view files their own (name/position auto-filled); privileged
        // views file for anyone through an employee picker.
        RequestFormPanel.Mode mode = canViewAll()
                ? RequestFormPanel.Mode.ADD_OTHER
                : RequestFormPanel.Mode.ADD_SELF;

        RequestFormPanel form = new RequestFormPanel(
                this::onRequestFormClosed, mode, null, null);

        showRequestDialog(form, canViewAll() ? "Add Request" : "File Request");
    }

    private void openUpdateForm() {
        int selectedIndex = getSelectedRequestRowIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to update.");
            return;
        }

        Object[] selectedRow = requestRows.get(selectedIndex);

        if (!canModifyRequest(selectedRow)) {
            JOptionPane.showMessageDialog(
                    this,
                    "You can only update your own pending requests.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object[] existingData = toFormData(selectedRow);
        Request original = requests.get(selectedIndex); // real request behind the row

        // persist the edit to the DB, keeping the original request's identity/type
        RequestFormPanel form = new RequestFormPanel(
                this::onRequestFormClosed,
                RequestFormPanel.Mode.EDIT,
                existingData,
                updatedData -> persistUpdate(original, updatedData));

        showRequestDialog(form, "Update Request");
    }

    private void openViewOnlyForm() {
        int selectedIndex = getSelectedRequestRowIndex();

        if (selectedIndex == -1) {
            return;
        }

        RequestFormPanel form = new RequestFormPanel(
                this::onRequestFormClosed,
                RequestFormPanel.Mode.VIEW,
                toFormData(requestRows.get(selectedIndex)),
                null);

        showRequestDialog(form, "Request Details");
    }

    private void showRequestDialog(RequestFormPanel form, String title) {
        closeRequestDialog();

        Window owner = SwingUtilities.getWindowAncestor(this);
        requestDialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        requestDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        requestDialog.setContentPane(form);
        requestDialog.setSize(560, 720);
        requestDialog.setMinimumSize(new Dimension(520, 560));
        requestDialog.setLocationRelativeTo(this);
        requestDialog.setVisible(true);
    }

    private void onRequestFormClosed() {
        closeRequestDialog();
        showRequestList();
    }

    private void closeRequestDialog() {
        if (requestDialog != null) {
            requestDialog.dispose();
            requestDialog = null;
        }
    }

    private void deleteSelectedRequest() {
        int selectedIndex = getSelectedRequestRowIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to delete.");
            return;
        }

        Object[] selectedRow = requestRows.get(selectedIndex);

        if (!canModifyRequest(selectedRow)) {
            JOptionPane.showMessageDialog(
                    this,
                    "You can only delete your own pending requests.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this request?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // delete from the database using the real request id + type
                Request original = requests.get(selectedIndex);
                requestService.delete(original.getRequestId(), original.getRequestType());
                showRequestList();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to delete request:\n" + ex.getMessage(),
                        "Delete Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // NEW: rebuild the request from the edited row (keeping its original id, type,
    // approver, and date filed) and persist it. Type is not changed on update.
    private void persistUpdate(Request original, Object[] row) {
        RequestStatus status = parseStatus(String.valueOf(row[STATUS_COL]));
        String reason = String.valueOf(row[7]);
        LocalDate startDate = parseDate(String.valueOf(row[3]));
        LocalDate endDate = parseDate(String.valueOf(row[4]));
        LocalTime startTime = parseTime(String.valueOf(row[5]));
        LocalTime endTime = parseTime(String.valueOf(row[6]));

        Request updated;
        RequestType type = original.getRequestType();

        if (type == RequestType.LEAVE) {
            updated = new LeaveRequest(
                    original.getRequestId(), original.getEmployeeId(), status,
                    original.getApproverId(), reason, original.getDateFiled(),
                    startDate, endDate, ((LeaveRequest) original).getLeaveType());
        } else if (type == RequestType.OVERTIME) {
            updated = new OvertimeRequest(
                    original.getRequestId(), original.getEmployeeId(), status,
                    original.getApproverId(), reason, original.getDateFiled(),
                    startDate, startTime, endTime);
        } else {
            updated = new UndertimeRequest(
                    original.getRequestId(), original.getEmployeeId(), status,
                    original.getApproverId(), reason, original.getDateFiled(),
                    startDate, startTime, endTime);
        }

        requestService.update(updated);
    }

    private RequestStatus parseStatus(String text) {
        if (text == null || text.isBlank()) {
            return RequestStatus.PENDING;
        }
        try {
            return RequestStatus.fromDbValue(text.trim());
        } catch (Exception ex) {
            return RequestStatus.PENDING;
        }
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim(), DATE_FMT);
        } catch (Exception ex) {
            return null;
        }
    }

    private LocalTime parseTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(text.trim().toUpperCase(), TIME_FMT);
        } catch (Exception ex) {
            return null;
        }
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

        for (Object[] row : requestRows) {
            tableModel.addRow(row);
        }

        requestTable = new JTable(tableModel);
        requestTable.setRowHeight(56);
        requestTable.setShowGrid(false);
        requestTable.setIntercellSpacing(new Dimension(0, 0));
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.setFont(new Font(FONT, Font.PLAIN, 13));
        requestTable.setBackground(Color.WHITE);
        requestTable.setFillsViewportHeight(true);
        requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        requestTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && requestTable.getSelectedRow() != -1) {
                    openViewOnlyForm();
                }
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        requestTable.setRowSorter(sorter);

        hideEmployeeIdColumn();

        styleHeader();
        styleColumns();
        styleCells();

        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setColumnHeaderView(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUnitIncrement(16);
        verticalBar.setBackground(Color.WHITE);

        tablePanel.add(requestTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void hideEmployeeIdColumn() {
        TableColumn column = requestTable.getColumnModel().getColumn(EMPLOYEE_ID_COL);
        requestTable.getColumnModel().removeColumn(column);
    }

    private int getSelectedRequestRowIndex() {
        int selectedRow = requestTable.getSelectedRow();

        if (selectedRow == -1) {
            return -1;
        }

        int modelRow = requestTable.convertRowIndexToModel(selectedRow);
        Object[] selectedData = new Object[tableModel.getColumnCount()];

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            selectedData[i] = tableModel.getValueAt(modelRow, i);
        }

        for (int i = 0; i < requestRows.size(); i++) {
            if (Arrays.equals(requestRows.get(i), selectedData)) {
                return i;
            }
        }

        return -1;
    }

    private Object[] toFormData(Object[] fullRow) {
        return Arrays.copyOf(fullRow, EMPLOYEE_ID_COL);
    }

    private void styleHeader() {
        JTableHeader header = requestTable.getTableHeader();
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

                if (viewColumn < 0) {
                    return;
                }

                int modelColumn = requestTable.convertColumnIndexToModel(viewColumn);
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

        requestTable.getTableHeader().repaint();
    }

    private void styleColumns() {
        int[] widths = {120, 145, 120, 105, 105, 95, 95, 130, 120, 105};
        TableColumnModel columns = requestTable.getColumnModel();

        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void styleCells() {
        for (int i = 0; i < requestTable.getColumnCount(); i++) {
            if (requestTable.convertColumnIndexToModel(i) == STATUS_COL) {
                requestTable.getColumnModel().getColumn(i).setCellRenderer(new StatusRenderer());
            } else {
                requestTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultRequestCellRenderer());
            }
        }
    }

    private void applySearchFilter() {
        if (sorter == null || searchField == null) {
            return;
        }

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
            setBorder(new EmptyBorder(0, 14, 10, 14));

            titleLabel.setFont(new Font(FONT, Font.BOLD, 12));
            titleLabel.setForeground(Color.BLACK);

            filterLabel.setFont(new Font(FONT, Font.BOLD, 12));
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

            filterLabel.setText(modelColumn == sortedColumn
                    ? currentSortOrder == SortOrder.ASCENDING ? "▲" : "▼"
                    : "⇅");

            filterLabel.setForeground(modelColumn == sortedColumn
                    ? NAVY
                    : new Color(130, 130, 130));

            return this;
        }
    }

    private static class DefaultRequestCellRenderer extends DefaultTableCellRenderer {

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
            label.setBorder(new EmptyBorder(0, 14, 0, 14));
            label.setFont(new Font(FONT, Font.PLAIN, 12));
            label.setVerticalAlignment(SwingConstants.CENTER);

            return label;
        }
    }

    private static class StatusRenderer extends JPanel implements TableCellRenderer {

        private String status = "";
        private Color rowBackground = Color.WHITE;

        StatusRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            status = value == null ? "" : value.toString();
            rowBackground = isSelected ? SELECTED_ROW : row % 2 == 0 ? Color.WHITE : ROW_GRAY;
            setBackground(rowBackground);

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color pillColor;

            if ("Approved".equalsIgnoreCase(status)) {
                pillColor = new Color(0, 190, 100);
            } else if ("Rejected".equalsIgnoreCase(status)) {
                pillColor = new Color(255, 82, 82);
            } else {
                pillColor = new Color(255, 216, 77);
            }

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
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

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
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}

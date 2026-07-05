package com.motorph.ui.payroll;

import java.awt.BorderLayout;            
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.motorph.model.Payslip;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.service.PayrollService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;
import com.motorph.reporting.PayrollMonthlySummaryReportGenerator;

public class PayrollPanel extends JPanel {

    private final Color NAVY = new Color(5, 24, 108);
    private final Color LIGHT_GRAY_ROW = new Color(245, 245, 245);

    private JTextField searchField;
    private JTable payrollTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JButton savePdfButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private String currentEmployeeId;
    private Role currentRole;   // the real logged-in role
    private Role viewAsRole;    // the role whose access is currently active

    private JComboBox<String> roleFilter;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    // connection to the database via service -> dao
    private final PayrollService payrollService = AppContext.getPayrollService();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public PayrollPanel() {
        applyRBAC();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createContentPanel(), BorderLayout.CENTER);
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

    /** Only Admin and Finance see all payroll; other views see their own only. */
    private boolean canViewAll() {
        return viewAsRole == Role.ADMIN || viewAsRole == Role.FINANCE;
    }

    /** Add/Update/Delete are limited to Admin and Finance. */
    private boolean canModify() {
        return viewAsRole == Role.ADMIN || viewAsRole == Role.FINANCE;
    }

    /** Role changer, not a filter: all-access views see everyone, else own only. */
    private boolean canSeeRow(String employeeId) {
        return canViewAll() || employeeId.equals(currentEmployeeId);
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(18, 78, 35, 78));

        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setOpaque(false);
        topControls.setPreferredSize(new Dimension(0, 115));

        searchField = new JTextField("Search");
        searchField.setPreferredSize(new Dimension(305, 39));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        searchField.setForeground(new Color(180, 180, 180));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 210, 210), 1, true),
                new EmptyBorder(0, 12, 0, 12)
        ));

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();

                if (text.equalsIgnoreCase("Search") || text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

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
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search");
                    searchField.setForeground(new Color(180, 180, 180));
                }
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filterPanel.setOpaque(false);
        JComboBox<String> filter = buildRoleFilter();
        if (filter != null) {
            filterPanel.add(filter);
        }

        JPanel leftColumn = new JPanel();
        leftColumn.setOpaque(false);
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.add(searchPanel);
        leftColumn.add(Box.createVerticalStrut(12));
        leftColumn.add(filterPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 62));
        buttonPanel.setOpaque(false);

        savePdfButton = button("  Save PDF");
        savePdfButton.setIcon(new DownloadIcon(Color.WHITE, 14));
        savePdfButton.setIconTextGap(6);
        addButton = button("+  Add");
        updateButton = button("\u270E  Update");
        deleteButton = button("\uD83D\uDDD1  Delete");
        refreshButton = button("\u27F3  Refresh");

        applyViewCapabilities();

        buttonPanel.add(savePdfButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        savePdfButton.addActionListener(e -> savePayrollAsPdf());

        addButton.addActionListener(e -> openPayrollForm(false, null));

        updateButton.addActionListener(e -> {
            if (payrollTable.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Please select a payroll entry to update.");
                return;
            }

            openPayrollForm(false, getSelectedPayslip());
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = payrollTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a payroll entry to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this payroll entry?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // delete the payslip from the database, then reload
                int modelRow = payrollTable.convertRowIndexToModel(selectedRow);
                String payslipId = String.valueOf(tableModel.getValueAt(modelRow, 0));
                try {
                    payrollService.deletePayslip(payslipId);
                    addSampleRows();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to delete payslip:\n" + ex.getMessage(),
                            "Delete Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        refreshButton.addActionListener(e -> addSampleRows());

        topControls.add(leftColumn, BorderLayout.WEST);
        topControls.add(buttonPanel, BorderLayout.EAST);

        createTable();

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(payrollTable);
        scrollPane.setColumnHeaderView(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        tablePanel.add(payrollTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        content.add(topControls, BorderLayout.NORTH);
        content.add(tablePanel, BorderLayout.CENTER);

        return content;
    }

    // Opens the payroll summary report in JasperViewer, where the user can save it as a PDF.
    private void savePayrollAsPdf() {
        try {
            PayrollMonthlySummaryReportGenerator.view();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to generate PDF:\n" + ex.getMessage(),
                    "Save PDF Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComboBox<String> buildRoleFilter() {
        String[] views = getAllowedPayrollRoles(currentRole);
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
            addSampleRows();
        });
        return roleFilter;
    }

    /** Shows the modify buttons only for views that may edit payroll. */
    private void applyViewCapabilities() {
        boolean modify = canModify();
        if (addButton != null) addButton.setVisible(modify);
        if (updateButton != null) updateButton.setVisible(modify);
        if (deleteButton != null) deleteButton.setVisible(modify);
    }

    /** Returns the roles this login may view the panel as (own role + Employee; Admin sees all). */
    private static String[] getAllowedPayrollRoles(Role role) {
        if (role == null) return new String[]{"Employee"};
        return switch (role) {
            case ADMIN -> new String[]{"Admin", "Finance", "HR", "IT", "Employee"};
            case FINANCE -> new String[]{"Finance", "Employee"};
            case HR -> new String[]{"HR", "Employee"};
            case IT -> new String[]{"IT", "Employee"};
            case EMPLOYEE -> new String[]{"Employee"};
        };
    }

    private JButton button(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(112, 37));
        btn.setBackground(NAVY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(0, 8, 0, 8));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void createTable() {
        String[] columns = {
            "Payslip ID", "Employee ID", "Start Date", "End Date",
            "Gross Pay", "Allowance", "Deduction", "Net Pay"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        payrollTable = new JTable(tableModel);

        sorter = new TableRowSorter<>(tableModel);
        payrollTable.setRowSorter(sorter);

        payrollTable.setRowHeight(52);
        payrollTable.setShowGrid(false);
        payrollTable.setIntercellSpacing(new Dimension(0, 0));
        payrollTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        payrollTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        payrollTable.setFillsViewportHeight(true);
        payrollTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        payrollTable.setBackground(Color.WHITE);
        payrollTable.setBorder(BorderFactory.createEmptyBorder());

        payrollTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && payrollTable.getSelectedRow() != -1) {
                    openPayrollForm(true, getSelectedPayslip());
                }
            }
        });

        styleHeader();
        styleCells();
        addSampleRows();
    }

    // NEW: looks up the full Payslip (with allowance/deduction breakdown) for
    // the currently selected row, since the table only holds summary totals.
    private Payslip getSelectedPayslip() {
        int selectedRow = payrollTable.getSelectedRow();

        if (selectedRow == -1) {
            return null;
        }

        int modelRow = payrollTable.convertRowIndexToModel(selectedRow);
        String payslipId = String.valueOf(tableModel.getValueAt(modelRow, 0));

        try {
            return payrollService.findPayslipById(payslipId);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load payslip details:\n" + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    private void openPayrollForm(boolean viewOnly, Payslip payslip) {
        removeAll();
        setLayout(new BorderLayout());

        add(new PayrollFormPanel(() -> {
            removeAll();
            setLayout(new BorderLayout());
            applyRBAC();
            add(createContentPanel(), BorderLayout.CENTER);
            revalidate();
            repaint();
        }, viewOnly, payslip), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void styleHeader() {
        JTableHeader header = payrollTable.getTableHeader();

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

                if (viewColumn < 0) {
                    return;
                }

                int modelColumn = payrollTable.convertColumnIndexToModel(viewColumn);
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

        payrollTable.getTableHeader().repaint();
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
                        table, value, isSelected, hasFocus, row, column);

                Color bg = isSelected
                        ? new Color(200, 210, 245)
                        : row % 2 == 0 ? Color.WHITE : LIGHT_GRAY_ROW;

                label.setText(value == null ? "" : value.toString());
                label.setOpaque(true);
                label.setBackground(bg);
                label.setForeground(Color.BLACK);
                label.setBorder(new EmptyBorder(0, 8, 0, 8));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.PLAIN, 13));

                return label;
            }
        };

        for (int i = 0; i < payrollTable.getColumnCount(); i++) {
            payrollTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    // NEW: loads payslips from the database (service -> dao)
    private void addSampleRows() {
        tableModel.setRowCount(0);

        try {
            List<Payslip> payslips = payrollService.getAllPayslips();

            for (Payslip p : payslips) {
                addPayrollRow(new Object[]{
                    p.getPayslipId(),
                    p.getEmployeeNumber(),
                    p.getPeriodStart() == null ? "" : p.getPeriodStart().format(DATE_FMT),
                    p.getPeriodEnd() == null ? "" : p.getPeriodEnd().format(DATE_FMT),
                    money(p.getGrossPay()),
                    money(p.getAllowances()),
                    money(p.getTotalDeductions()),
                    money(p.getNetPay())
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load payroll:\n" + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // NEW: formats a peso amount with thousands separators and 2 decimals.
    private String money(double value) {
        return String.format("%,.2f", value);
    }

    private void addPayrollRow(Object[] row) {
        String employeeId = String.valueOf(row[1]);

        if (canSeeRow(employeeId)) {
            tableModel.addRow(row);
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

    private static class DownloadIcon implements Icon {

        private final Color color;
        private final int size;

        public DownloadIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new java.awt.BasicStroke(
                    Math.max(1.5f, size / 8f),
                    java.awt.BasicStroke.CAP_ROUND,
                    java.awt.BasicStroke.JOIN_ROUND));

            int stemX = x + size / 2;
            int stemTop = y + (int) (size * 0.05);
            int stemBottom = y + (int) (size * 0.55);

            // vertical stem
            g2.drawLine(stemX, stemTop, stemX, stemBottom);

            // arrow head
            int armLen = (int) (size * 0.32);
            g2.drawLine(stemX - armLen, stemBottom - armLen, stemX, stemBottom);
            g2.drawLine(stemX + armLen, stemBottom - armLen, stemX, stemBottom);

            // tray
            int trayY1 = y + (int) (size * 0.62);
            int trayY2 = y + (int) (size * 0.82);
            int trayLeft = x + (int) (size * 0.08);
            int trayRight = x + (int) (size * 0.92);

            g2.drawLine(trayLeft, trayY1, trayLeft, trayY2);
            g2.drawLine(trayRight, trayY1, trayRight, trayY2);
            g2.drawLine(trayLeft, trayY2, trayRight, trayY2);

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    private static class CircleIcon implements Icon {

        private final Color color;
        private final int size;

        public CircleIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y, size, size);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}

package com.motorph.ui.helpcenter;

import com.motorph.model.*;
import com.motorph.service.InformationDisputeService;
import com.motorph.service.PayrollDisputeService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class HelpCenterPanel extends JPanel {

    // ── category routing (package-visible for DisputeDetail) ─────────────────
    static final Set<String> IT_CATEGORIES = Set.of(
            "System Access", "Password Reset", "Technical Issue",
            "Email & VPN", "Software Support");

    static final Set<String> HR_CATEGORIES = Set.of(
            "Personal Info", "Leave Balance", "Attendance Record",
            "Government ID", "Employment Record");

    // ── styling ───────────────────────────────────────────────────────────────
    private static final Color NAVY         = new Color(8, 25, 105);
    private static final Color ROW_GRAY     = new Color(238, 238, 238);
    private static final Color BORDER_GRAY  = new Color(210, 210, 210);
    private static final Color SELECTED_ROW = new Color(225, 230, 245);
    private static final Color PENDING_CLR  = new Color(240, 190, 40);
    private static final Color RESOLVED_CLR = new Color(40, 180, 80);

    private static final String LIST_CARD   = "LIST_CARD";
    private static final String DETAIL_CARD = "DETAIL_CARD";

    private static final String[] COLUMNS = {
        "Ticket ID", "Employee Name", "Date Filed", "Type", "Reason", "Status"
    };

    // ── session / RBAC ────────────────────────────────────────────────────────
    private final Role role;
    private final int  currentEmployeeId;
    private boolean    canDeleteAny;
    private boolean    canApprove;
    private boolean    viewingAll = true;

    // ── data ──────────────────────────────────────────────────────────────────
    private final List<Dispute>        loadedDisputes = new ArrayList<>();
    private final Map<Integer, String> empNameCache   = new HashMap<>();

    // ── services ──────────────────────────────────────────────────────────────
    private final InformationDisputeService infoSvc;
    private final PayrollDisputeService     payrollSvc;

    // ── UI components ─────────────────────────────────────────────────────────
    private CardLayout    rootCard;
    private JPanel        rootPanel;
    private DisputeDetail detailPanel;

    private DefaultTableModel                tableModel;
    private JTable                           disputeTable;
    private JTextField                       searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private JComboBox<String> scopeCombo;
    private JButton           deleteBtn;

    private int       sortedColumn     = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    // ── constructor ───────────────────────────────────────────────────────────
    public HelpCenterPanel() {
        infoSvc    = AppContext.getInformationDisputeService();
        payrollSvc = AppContext.getPayrollDisputeService();

        UserAccount user  = Session.getCurrentUser();
        role              = (user == null) ? null : user.getRole();
        currentEmployeeId = (user == null) ? -1   : user.getEmployeeId();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        rootCard  = new CardLayout();
        rootPanel = new JPanel(rootCard);
        rootPanel.setBackground(Color.WHITE);

        detailPanel = new DisputeDetail(() -> {
            loadData();
            rootCard.show(rootPanel, LIST_CARD);
        });

        rootPanel.add(buildListPanel(), LIST_CARD);
        rootPanel.add(detailPanel,      DETAIL_CARD);
        add(rootPanel, BorderLayout.CENTER);

        applyRBAC();
        loadData();
        wireRowClick();
    }

    // ── RBAC ──────────────────────────────────────────────────────────────────
    private void applyRBAC() {
        canDeleteAny = (role == Role.ADMIN);
        canApprove   = (role == Role.ADMIN || role == Role.HR
                     || role == Role.IT   || role == Role.FINANCE);

        if (scopeCombo != null) scopeCombo.setVisible(canApprove);
        if (deleteBtn  != null) deleteBtn.setVisible(canDeleteAny);
    }

    // ── data loading ──────────────────────────────────────────────────────────
    private void loadData() {
        loadedDisputes.clear();
        empNameCache.clear();

        try {
            String empIdStr = String.valueOf(currentEmployeeId);
            boolean viewAll = viewingAll && canApprove;

            if (!viewAll) {
                loadedDisputes.addAll(infoSvc.findByEmployee(empIdStr));
                loadedDisputes.addAll(payrollSvc.findByEmployee(empIdStr));
            } else if (role == Role.ADMIN) {
                loadedDisputes.addAll(infoSvc.findAll());
                loadedDisputes.addAll(payrollSvc.findAll());
            } else if (role == Role.HR) {
                infoSvc.findAll().stream()
                       .filter(d -> HR_CATEGORIES.contains(d.getCategory()))
                       .forEach(loadedDisputes::add);
            } else if (role == Role.IT) {
                infoSvc.findAll().stream()
                       .filter(d -> IT_CATEGORIES.contains(d.getCategory()))
                       .forEach(loadedDisputes::add);
            } else if (role == Role.FINANCE) {
                loadedDisputes.addAll(payrollSvc.findAll());
            }

            for (Dispute d : loadedDisputes) {
                int empId = Integer.parseInt(d.getEmployeeId());
                if (!empNameCache.containsKey(empId)) {
                    var emp = AppContext.getEmployeeService().findEmployee(d.getEmployeeId());
                    empNameCache.put(empId, emp != null ? emp.getFullName() : "ID:" + empId);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load disputes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        populateTable();
    }

    private String resolveTypeLabel(Dispute d) {
        if (d instanceof InformationDispute id) {
            String cat = id.getCategory();
            if (IT_CATEGORIES.contains(cat)) return "Info – IT";
            if (HR_CATEGORIES.contains(cat)) return "Info – HR";
            return "Information";
        }
        return "Payroll";
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        for (Dispute d : loadedDisputes) {
            int    empId  = Integer.parseInt(d.getEmployeeId());
            String name   = empNameCache.getOrDefault(empId, "Unknown");
            String date   = d.getDateFiled() != null ? d.getDateFiled().toString() : "";
            String status = d.getStatus() == DisputeStatus.RESOLVED ? "Resolved" : "Pending";
            tableModel.addRow(new Object[]{
                d.getDisputeId(), name, date, resolveTypeLabel(d), d.getReason(), status
            });
        }
        applySearchFilter();
    }

    // ── build list panel ──────────────────────────────────────────────────────
    private JPanel buildListPanel() {
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
            @Override public void focusGained(FocusEvent e) {
                if ("Search".equals(searchField.getText())) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText("Search");
                    searchField.setForeground(new Color(200, 200, 200));
                    applySearchFilter();
                }
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applySearchFilter(); }
        });

        scopeCombo = new JComboBox<>(new String[]{"View All", "View Mine"});
        scopeCombo.setPreferredSize(new Dimension(130, 38));
        scopeCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        scopeCombo.setVisible(false); // shown after applyRBAC()
        scopeCombo.addActionListener(e -> {
            viewingAll = "View All".equals(scopeCombo.getSelectedItem());
            loadData();
        });

        row.add(searchField);
        row.add(Box.createHorizontalStrut(12));
        row.add(scopeCombo);
        return row;
    }

    private JPanel buildControlRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        buttons.add(navyButton("+", "Add", 90, this::addTicket));

        deleteBtn = navyButton("🗑", "Delete", 105, this::deleteTicket);
        deleteBtn.setVisible(false); // shown after applyRBAC()
        buttons.add(deleteBtn);

        buttons.add(navyButton("⟳", "Refresh", 110, this::refreshTable));

        row.add(buttons, BorderLayout.EAST);
        return row;
    }

    private JPanel buildTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        disputeTable = new JTable(tableModel);
        disputeTable.setRowHeight(56);
        disputeTable.setShowGrid(false);
        disputeTable.setIntercellSpacing(new Dimension(0, 0));
        disputeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        disputeTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        disputeTable.setBackground(Color.WHITE);
        disputeTable.setFillsViewportHeight(true);
        disputeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        sorter = new TableRowSorter<>(tableModel);
        disputeTable.setRowSorter(sorter);

        styleHeader();
        styleColumns();
        styleCells();

        JScrollPane scrollPane = new JScrollPane(disputeTable);
        scrollPane.setColumnHeaderView(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        tablePanel.add(disputeTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // ── table styling ─────────────────────────────────────────────────────────
    private void styleHeader() {
        JTableHeader header = disputeTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 58));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        header.setDefaultRenderer(new HeaderFilterRenderer());

        header.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int viewCol = header.columnAtPoint(e.getPoint());
                if (viewCol >= 0) toggleColumnSort(disputeTable.convertColumnIndexToModel(viewCol));
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
        sorter.setSortKeys(List.of(new RowSorter.SortKey(column, currentSortOrder)));
        sorter.sort();
        disputeTable.getTableHeader().repaint();
    }

    private void styleColumns() {
        int[] widths = {90, 160, 110, 100, 320, 110};
        TableColumnModel cols = disputeTable.getColumnModel();
        for (int i = 0; i < widths.length; i++) cols.getColumn(i).setPreferredWidth(widths[i]);
    }

    private void styleCells() {
        for (int i = 0; i < disputeTable.getColumnCount(); i++) {
            disputeTable.getColumnModel().getColumn(i).setCellRenderer(
                    i == 5 ? new StatusRenderer() : new DefaultHelpCenterCellRenderer());
        }
    }

    // ── search ────────────────────────────────────────────────────────────────
    private void applySearchFilter() {
        if (sorter == null || searchField == null) return;
        String q = searchField.getText().trim();
        sorter.setRowFilter(q.equalsIgnoreCase("Search") || q.isBlank()
                ? null
                : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(q)));
    }

    private void refreshTable() {
        searchField.setText("Search");
        searchField.setForeground(new Color(200, 200, 200));
        sortedColumn = -1;
        currentSortOrder = SortOrder.UNSORTED;
        if (sorter != null) { sorter.setSortKeys(null); sorter.setRowFilter(null); }
        disputeTable.clearSelection();
        loadData();
        disputeTable.getTableHeader().repaint();
    }

    // ── actions ───────────────────────────────────────────────────────────────
    private void addTicket() {
        AddDisputeDialog dlg = new AddDisputeDialog(
                SwingUtilities.getWindowAncestor(this), infoSvc, payrollSvc);
        dlg.setVisible(true);
        if (dlg.wasSubmitted()) loadData();
    }

    private void deleteTicket() {
        Dispute selected = getSelectedDispute();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete dispute #" + selected.getDisputeId() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            infoSvc.deleteDispute(selected.getDisputeId());
            loadData();
            JOptionPane.showMessageDialog(this, "Ticket deleted.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Dispute getSelectedDispute() {
        int viewRow = disputeTable.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = disputeTable.convertRowIndexToModel(viewRow);
        String id = tableModel.getValueAt(modelRow, 0).toString();
        return loadedDisputes.stream()
               .filter(d -> d.getDisputeId().equals(id))
               .findFirst().orElse(null);
    }

    // ── row click ─────────────────────────────────────────────────────────────
    private void wireRowClick() {
        disputeTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = disputeTable.rowAtPoint(e.getPoint());
                    if (row >= 0) openDetail(row);
                }
            }
        });
        disputeTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                disputeTable.setCursor(disputeTable.rowAtPoint(e.getPoint()) >= 0
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
    }

    private void openDetail(int viewRow) {
        int modelRow = disputeTable.convertRowIndexToModel(viewRow);
        String id = tableModel.getValueAt(modelRow, 0).toString();
        Dispute d = loadedDisputes.stream()
                .filter(x -> x.getDisputeId().equals(id))
                .findFirst().orElse(null);
        if (d == null) return;

        int    empId  = Integer.parseInt(d.getEmployeeId());
        String empName = empNameCache.getOrDefault(empId, "Unknown");

        detailPanel.load(d, empName, role);
        rootCard.show(rootPanel, DETAIL_CARD);
    }

    // ── button factory ────────────────────────────────────────────────────────
    private JButton navyButton(String icon, String text, int width, Runnable action) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setPreferredSize(new Dimension(width, 37));
        btn.setBackground(NAVY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    // ── inner class: Add Dispute dialog ───────────────────────────────────────
    private static final class AddDisputeDialog extends JDialog {
        private boolean submitted = false;

        AddDisputeDialog(Window owner,
                         InformationDisputeService infoSvc,
                         PayrollDisputeService payrollSvc) {
            super(owner, "File a Dispute", ModalityType.APPLICATION_MODAL);
            setSize(460, 420);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());
            getContentPane().setBackground(Color.WHITE);

            // type selector
            JComboBox<String> typeCombo = new JComboBox<>(
                    new String[]{"Information Dispute", "Payroll Dispute"});
            typeCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));

            // -- info fields --
            JComboBox<String> categoryCombo = new JComboBox<>(buildCategoryItems());
            categoryCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JTextField targetField = new JTextField();
            targetField.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JPanel infoFields = new JPanel();
            infoFields.setOpaque(false);
            infoFields.setLayout(new BoxLayout(infoFields, BoxLayout.Y_AXIS));
            infoFields.add(labelRow("Category*", categoryCombo));
            infoFields.add(Box.createVerticalStrut(8));
            infoFields.add(labelRow("Target Field (optional)", targetField));

            // -- payroll fields --
            JTextField payslipField = new JTextField();
            payslipField.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JPanel payrollFields = new JPanel();
            payrollFields.setOpaque(false);
            payrollFields.setLayout(new BoxLayout(payrollFields, BoxLayout.Y_AXIS));
            payrollFields.add(labelRow("Payslip Number*", payslipField));

            // -- card panel for type-specific fields --
            CardLayout cl = new CardLayout();
            JPanel typeCards = new JPanel(cl);
            typeCards.setOpaque(false);
            typeCards.add(infoFields,    "INFO");
            typeCards.add(payrollFields, "PAYROLL");

            typeCombo.addActionListener(e ->
                cl.show(typeCards, typeCombo.getSelectedIndex() == 0 ? "INFO" : "PAYROLL"));

            // -- reason --
            JTextArea reasonArea = new JTextArea(4, 30);
            reasonArea.setLineWrap(true);
            reasonArea.setWrapStyleWord(true);
            reasonArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
            reasonArea.setBorder(BorderFactory.createLineBorder(new Color(180, 185, 200)));

            // -- assemble form --
            JPanel form = new JPanel();
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.setBackground(Color.WHITE);
            form.setBorder(new EmptyBorder(16, 24, 10, 24));

            form.add(labelRow("Dispute Type", typeCombo));
            form.add(Box.createVerticalStrut(12));
            form.add(typeCards);
            form.add(Box.createVerticalStrut(12));
            form.add(labelRow("Reason*", new JScrollPane(reasonArea)));

            // -- submit / cancel --
            JButton submitBtn = new JButton("Submit");
            submitBtn.setBackground(NAVY);
            submitBtn.setForeground(Color.WHITE);
            submitBtn.setFocusPainted(false);
            submitBtn.setBorderPainted(false);
            submitBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(e -> dispose());

            submitBtn.addActionListener(e -> {
                String reason = reasonArea.getText().trim();
                if (reason.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Reason is required.");
                    return;
                }
                try {
                    if (typeCombo.getSelectedIndex() == 0) {
                        String cat    = (String) categoryCombo.getSelectedItem();
                        String target = targetField.getText().trim();
                        if (target.isBlank()) target = cat.toLowerCase().replace(" ", "_");
                        infoSvc.fileDispute(reason, cat, target);
                    } else {
                        String ps = payslipField.getText().trim();
                        if (ps.isBlank()) {
                            JOptionPane.showMessageDialog(this, "Payslip number is required.");
                            return;
                        }
                        payrollSvc.fileDisputeByPayslipNumber(ps, reason);
                    }
                    submitted = true;
                    JOptionPane.showMessageDialog(this, "Dispute filed successfully.");
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            btnRow.setBackground(Color.WHITE);
            btnRow.add(cancelBtn);
            btnRow.add(submitBtn);

            add(form,   BorderLayout.CENTER);
            add(btnRow, BorderLayout.SOUTH);
        }

        private static String[] buildCategoryItems() {
            return Stream.concat(
                    IT_CATEGORIES.stream().sorted(),
                    HR_CATEGORIES.stream().sorted())
                    .toArray(String[]::new);
        }

        private static JPanel labelRow(String label, JComponent comp) {
            JPanel p = new JPanel(new BorderLayout(0, 4));
            p.setOpaque(false);
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lbl.setForeground(new Color(50, 60, 80));
            p.add(lbl, BorderLayout.NORTH);
            p.add(comp, BorderLayout.CENTER);
            return p;
        }

        boolean wasSubmitted() { return submitted; }
    }

    // ── renderers ─────────────────────────────────────────────────────────────
    private class HeaderFilterRenderer extends JPanel implements TableCellRenderer {
        private final JLabel titleLabel  = new JLabel();
        private final JLabel filterLabel = new JLabel("⇅");

        HeaderFilterRenderer() {
            setLayout(new BorderLayout(6, 0));
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(0, 18, 10, 18));
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            filterLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            filterLabel.setForeground(new Color(130, 130, 130));
            filterLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            add(titleLabel, BorderLayout.CENTER);
            add(filterLabel, BorderLayout.EAST);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            int mc = t.convertColumnIndexToModel(col);
            titleLabel.setText(v == null ? "" : v.toString());
            if (mc == sortedColumn) {
                filterLabel.setText(currentSortOrder == SortOrder.ASCENDING ? "▲" : "▼");
                filterLabel.setForeground(NAVY);
            } else {
                filterLabel.setText("⇅");
                filterLabel.setForeground(new Color(130, 130, 130));
            }
            return this;
        }
    }

    private static class DefaultHelpCenterCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            lbl.setBackground(sel ? SELECTED_ROW : row % 2 == 0 ? Color.WHITE : ROW_GRAY);
            lbl.setForeground(Color.BLACK);
            lbl.setText(v == null ? "" : v.toString());
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(0, 18, 0, 18));
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lbl.setVerticalAlignment(SwingConstants.CENTER);
            return lbl;
        }
    }

    private static class StatusRenderer extends JPanel implements TableCellRenderer {
        private String status      = "";
        private Color  rowBg       = Color.WHITE;

        StatusRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            status = v == null ? "" : v.toString();
            rowBg  = sel ? SELECTED_ROW : row % 2 == 0 ? Color.WHITE : ROW_GRAY;
            setBackground(rowBg);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color pillColor = "Resolved".equalsIgnoreCase(status) ? RESOLVED_CLR : PENDING_CLR;
            int pillW = 80, pillH = 26;
            int x = (getWidth() - pillW) / 2;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pillColor);
            g2.fill(new RoundRectangle2D.Float(x, y, pillW, pillH, pillH, pillH));

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(status,
                    x + (pillW - fm.stringWidth(status)) / 2,
                    y + ((pillH - fm.getHeight()) / 2) + fm.getAscent());
            g2.dispose();
        }
    }

    static class CircleAvatar extends JPanel {
        private final int size;
        CircleAvatar(int sz) { size = sz; setPreferredSize(new Dimension(sz, sz)); setOpaque(false); }

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
        private final int   radius;
        private final Color color;

        RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
    }
}

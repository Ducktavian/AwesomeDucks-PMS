package com.motorph.ui.helpcenter;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class HelpCenterPanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color ROW_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final Color SELECTED_ROW = new Color(225, 230, 245);
    private static final Color PENDING = new Color(240, 190, 40);
    private static final Color RESOLVED = new Color(40, 180, 80);

    private static final String LIST_CARD = "LIST_CARD";
    private static final String DETAIL_CARD = "DETAIL_CARD";

    private static final String[] COLUMNS = {
        "Ticket ID", "Employee Name", "Date", "Department", "Description", "Status"
    };

    private CardLayout rootCard;
    private JPanel rootPanel;
    private DisputeDetail detailPanel;

    private DefaultTableModel tableModel;
    private JTable disputeTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    private final List<DisputeEntry> allDisputes = new ArrayList<>();

    public HelpCenterPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        rootCard = new CardLayout();
        rootPanel = new JPanel(rootCard);
        rootPanel.setBackground(Color.WHITE);

        JPanel listPanel = buildListPanel();

        detailPanel = new DisputeDetail(() -> rootCard.show(rootPanel, LIST_CARD));

        rootPanel.add(listPanel, LIST_CARD);
        rootPanel.add(detailPanel, DETAIL_CARD);

        add(rootPanel, BorderLayout.CENTER);

        loadSampleData();
        wireRowClick();
    }

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

        buttons.add(navyButton("+", "Add", 90, this::addTicket));
        buttons.add(navyButton("✎", "Update", 105, this::updateTicket));
        buttons.add(navyButton("🗑", "Delete", 105, this::deleteTicket));
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
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewColumn = header.columnAtPoint(e.getPoint());
                if (viewColumn < 0) return;

                int modelColumn = disputeTable.convertColumnIndexToModel(viewColumn);
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

        disputeTable.getTableHeader().repaint();
    }

    private void styleColumns() {
        int[] widths = {110, 170, 120, 130, 300, 120};
        TableColumnModel columns = disputeTable.getColumnModel();

        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void styleCells() {
        for (int i = 0; i < disputeTable.getColumnCount(); i++) {
            if (i == 5) {
                disputeTable.getColumnModel().getColumn(i).setCellRenderer(new StatusRenderer());
            } else {
                disputeTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultHelpCenterCellRenderer());
            }
        }
    }

    private void loadSampleData() {
        allDisputes.clear();

        allDisputes.add(new DisputeEntry("TK-1001", "Juan Dela Cruz", "03/15/2026", "IT", "Cannot access payroll portal", "Pending"));
        allDisputes.add(new DisputeEntry("TK-1002", "Maria Santos", "03/14/2026", "IT", "Password reset request", "Resolved"));
        allDisputes.add(new DisputeEntry("TK-1003", "Pedro Reyes", "03/13/2026", "HR", "Leave balance discrepancy", "Pending"));
        allDisputes.add(new DisputeEntry("TK-1004", "Ana Lopez", "03/12/2026", "Finance", "Payslip amount incorrect", "Resolved"));
        allDisputes.add(new DisputeEntry("TK-1005", "Carlos Mendez", "03/11/2026", "IT", "VPN connection failure", "Pending"));
        allDisputes.add(new DisputeEntry("TK-1006", "Lisa Tan", "03/10/2026", "IT", "Email account locked", "Resolved"));
        allDisputes.add(new DisputeEntry("TK-1007", "Mark Rivera", "03/09/2026", "Employee", "Attendance record missing", "Pending"));
        allDisputes.add(new DisputeEntry("TK-1008", "Grace Lim", "03/08/2026", "IT", "System login timeout issue", "Resolved"));

        populateTable();
    }

    private void populateTable() {
        tableModel.setRowCount(0);

        for (DisputeEntry entry : allDisputes) {
            tableModel.addRow(entry.toRow());
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

    private void refreshTable() {
        searchField.setText("Search");
        searchField.setForeground(new Color(200, 200, 200));

        sortedColumn = -1;
        currentSortOrder = SortOrder.UNSORTED;

        if (sorter != null) {
            sorter.setSortKeys(null);
            sorter.setRowFilter(null);
        }

        disputeTable.clearSelection();
        populateTable();
        disputeTable.getTableHeader().repaint();
    }

    private void addTicket() {
        JOptionPane.showMessageDialog(this, "Add ticket form will be added here.");
    }

    private void updateTicket() {
        DisputeEntry selected = getSelectedEntry();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to update.");
            return;
        }

        detailPanel.load(selected);
        rootCard.show(rootPanel, DETAIL_CARD);
    }

    private void deleteTicket() {
        DisputeEntry selected = getSelectedEntry();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete ticket " + selected.ticketId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        allDisputes.removeIf(entry -> entry.ticketId.equals(selected.ticketId));
        populateTable();

        JOptionPane.showMessageDialog(this, "Ticket deleted successfully.");
    }

    private DisputeEntry getSelectedEntry() {
        int selectedRow = disputeTable.getSelectedRow();

        if (selectedRow == -1) {
            return null;
        }

        int modelRow = disputeTable.convertRowIndexToModel(selectedRow);
        String ticketId = tableModel.getValueAt(modelRow, 0).toString();

        for (DisputeEntry entry : allDisputes) {
            if (entry.ticketId.equals(ticketId)) {
                return entry;
            }
        }

        return null;
    }

    private void wireRowClick() {
        disputeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = disputeTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        openDetail(row);
                    }
                }
            }
        });

        disputeTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = disputeTable.rowAtPoint(e.getPoint());
                disputeTable.setCursor(row >= 0
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
    }

    private void openDetail(int viewRow) {
        int modelRow = disputeTable.convertRowIndexToModel(viewRow);
        String ticketId = tableModel.getValueAt(modelRow, 0).toString();

        for (DisputeEntry entry : allDisputes) {
            if (entry.ticketId.equals(ticketId)) {
                detailPanel.load(entry);
                rootCard.show(rootPanel, DETAIL_CARD);
                return;
            }
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

    private static class DefaultHelpCenterCellRenderer extends DefaultTableCellRenderer {
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color pillColor = "Resolved".equalsIgnoreCase(status) ? RESOLVED : PENDING;

            int pillW = 80;
            int pillH = 26;
            int x = (getWidth() - pillW) / 2;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pillColor);
            g2.fill(new RoundRectangle2D.Float(x, y, pillW, pillH, pillH, pillH));

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));

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

    static class DisputeEntry {
        final String ticketId;
        final String employeeName;
        final String date;
        final String department;
        final String description;
        final String status;

        DisputeEntry(String ticketId, String employeeName, String date,
                     String department, String description, String status) {
            this.ticketId = ticketId;
            this.employeeName = employeeName;
            this.date = date;
            this.department = department;
            this.description = description;
            this.status = status;
        }

        String[] toRow() {
            return new String[]{
                ticketId,
                employeeName,
                date,
                department,
                description,
                status
            };
        }
    }
}
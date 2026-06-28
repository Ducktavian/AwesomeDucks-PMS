package com.motorph.ui.request;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class RequestPanel extends JPanel {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final Color ROW_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final Color SELECTED_ROW = new Color(225, 230, 245);
    private static final String FONT = "Segoe UI";

    private static final String[] COLUMNS = {
        "Name", "Department", "Request Type", "Start Date", "End Date",
        "Start Time", "End Time", "Reason", "Notes", "Status"
    };

    private final List<Object[]> requestRows = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable requestTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    public RequestPanel() {
        loadSampleRows();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private void showRequestList() {
        removeAll();
        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void loadSampleRows() {
        requestRows.clear();
        requestRows.add(new Object[]{"Juan Dela Cruz", "IT", "Overtime", "03/01/2026", "03/01/2026", "5:00 PM", "7:00 PM", "Project work", "", "Pending"});
        requestRows.add(new Object[]{"Maria Santos", "HR", "Leave", "03/03/2026", "03/05/2026", "", "", "Vacation", "", "Pending"});
        requestRows.add(new Object[]{"Pedro Reyes", "Finance", "Undertime", "03/04/2026", "03/04/2026", "3:00 PM", "5:00 PM", "Personal", "", "Rejected"});
        requestRows.add(new Object[]{"Ana Lopez", "IT", "Overtime", "03/07/2026", "03/07/2026", "6:00 PM", "9:00 PM", "System update", "", "Approved"});
        requestRows.add(new Object[]{"Carlos Mendez", "HR", "Leave", "03/10/2026", "03/11/2026", "", "", "Family event", "", "Pending"});
        requestRows.add(new Object[]{"Lisa Tan", "Employee", "Overtime", "03/12/2026", "03/12/2026", "5:00 PM", "8:00 PM", "Reports", "", "Pending"});
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
        name.setFont(new Font(FONT, Font.BOLD, 16));
        name.setForeground(NAVY);
        name.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel position = new JLabel("Position");
        position.setFont(new Font(FONT, Font.PLAIN, 13));
        position.setForeground(Color.GRAY);
        position.setAlignmentX(Component.RIGHT_ALIGNMENT);

        text.add(name);
        text.add(position);

        profile.add(text);
        profile.add(new CircleAvatar(48));

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

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton addButton = navyButton("+", "Add", 90);
        addButton.addActionListener(e -> {
            removeAll();
            setLayout(new BorderLayout());

            add(new RequestFormPanel(
                    this::showRequestList,
                    null,
                    rowData -> {
                        requestRows.add(rowData);
                        showRequestList();
                    }
            ), BorderLayout.CENTER);

            revalidate();
            repaint();
        });
        buttons.add(addButton);

        JButton updateButton = navyButton("✎", "Update", 105);
        updateButton.addActionListener(e -> {
            int selectedRow = requestTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a request to update.",
                        "No Request Selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int modelRow = requestTable.convertRowIndexToModel(selectedRow);
            Object[] existingData = requestRows.get(modelRow);

            removeAll();
            setLayout(new BorderLayout());

            add(new RequestFormPanel(
                    this::showRequestList,
                    existingData,
                    updatedData -> {
                        requestRows.set(modelRow, updatedData);
                        showRequestList();
                    }
            ), BorderLayout.CENTER);

            revalidate();
            repaint();
        });
        buttons.add(updateButton);

        JButton deleteButton = navyButton("🗑", "Delete", 105);
        deleteButton.addActionListener(e -> {
            int selectedRow = requestTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a request to delete.",
                        "No Request Selected",
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

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            int modelRow = requestTable.convertRowIndexToModel(selectedRow);
            requestRows.remove(modelRow);
            tableModel.removeRow(modelRow);

            JOptionPane.showMessageDialog(
                    this,
                    "Request deleted successfully.",
                    "Deleted",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        buttons.add(deleteButton);

        JButton refreshButton = navyButton("⟳", "Refresh", 110);
        refreshButton.addActionListener(e -> {
            loadSampleRows();
            showRequestList();
        });
        buttons.add(refreshButton);

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

        sorter = new TableRowSorter<>(tableModel);
        requestTable.setRowSorter(sorter);

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
                if (viewColumn < 0) return;

                int modelColumn = requestTable.convertColumnIndexToModel(viewColumn);
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

        requestTable.getTableHeader().repaint();
    }

    private void styleColumns() {
        int[] widths = {120, 120, 120, 105, 105, 95, 95, 130, 120, 105};
        TableColumnModel columns = requestTable.getColumnModel();

        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void styleCells() {
        for (int i = 0; i < requestTable.getColumnCount(); i++) {
            if (i == 9) {
                requestTable.getColumnModel().getColumn(i).setCellRenderer(new StatusRenderer());
            } else {
                requestTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultRequestCellRenderer());
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

    private JButton navyButton(String icon, String text, int width) {
        JButton button = new JButton(icon + "  " + text);
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

        private final JLabel titleLabel;
        private final JLabel filterLabel;

        HeaderFilterRenderer() {
            setLayout(new BorderLayout(6, 0));
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(0, 14, 10, 14));

            titleLabel = new JLabel();
            titleLabel.setFont(new Font(FONT, Font.BOLD, 12));
            titleLabel.setForeground(Color.BLACK);

            filterLabel = new JLabel("⇅");
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
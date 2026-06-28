package com.motorph.ui.payroll;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class PayrollPanel extends JPanel {

    private final Color NAVY = new Color(5, 24, 108);
    private final Color LIGHT_GRAY_ROW = new Color(245, 245, 245);

    private JTextField searchField;
    private JTable payrollTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private int sortedColumn = -1;
    private SortOrder currentSortOrder = SortOrder.UNSORTED;

    public PayrollPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createContentPanel(), BorderLayout.CENTER);
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

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 62));
        buttonPanel.setOpaque(false);

        JButton addButton = button("+  Add");
        JButton updateButton = button("✎  Update");
        JButton deleteButton = button("🗑  Delete");
        JButton refreshButton = button("⟳  Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        /* ---------- OPEN PAYROLL FORM ---------- */
        addButton.addActionListener(e -> {
            removeAll();
            setLayout(new BorderLayout());
            add(new PayrollFormPanel(() -> {
                removeAll();
                setLayout(new BorderLayout());
                add(createContentPanel(), BorderLayout.CENTER);
                revalidate();
                repaint();
            }), BorderLayout.CENTER);

            revalidate();
            repaint();
        });
        
        topControls.add(searchPanel, BorderLayout.WEST);
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
            "Gross Pay", "Deduction", "Allowance", "Net Pay"
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

        styleHeader();
        styleCells();

        addSampleRows();
    }

    private void styleHeader() {
        JTableHeader header = payrollTable.getTableHeader();

        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 58));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));

        // This creates one continuous black line under the whole table header.
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

    private void addSampleRows() {
        tableModel.addRow(new Object[]{"PS-001", "10001", "06/01/2026", "06/15/2026", "25000", "3000", "1500", "23500"});
        tableModel.addRow(new Object[]{"PS-002", "10002", "06/01/2026", "06/15/2026", "22000", "2500", "1200", "20700"});
        tableModel.addRow(new Object[]{"PS-003", "10003", "06/01/2026", "06/15/2026", "28000", "3500", "1600", "26100"});
        tableModel.addRow(new Object[]{"PS-004", "10004", "06/01/2026", "06/15/2026", "20000", "2200", "1000", "18800"});
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
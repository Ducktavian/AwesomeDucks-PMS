package com.motorph.ui.IT;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * IT Help Center dispute/ticket list.
 */
public class ITDisputeList extends JPanel {

    private static final Color NAVY       = new Color(13,  36,  89);
    private static final Color MUTED      = new Color(120, 130, 150);
    private static final Color DIVIDER    = new Color(220, 225, 235);
    private static final Color ROW_WHITE  = Color.WHITE;
    private static final Color ROW_GRAY   = new Color(235, 235, 235);
    private static final Color PENDING    = new Color(240, 190,  40);
    private static final Color RESOLVED   = new Color(40,  180,  80);

    private static final String[] COLUMNS = {
        "Ticket ID", "Employee Name", "Date", "Department", "Description", "Status"
    };

    private static final String IMG_DIR = resolveImgDir();

    private DefaultTableModel tableModel;
    private JTable            disputeTable;
    private JTextField        dateField;
    private JComboBox<String> departmentFilter;
    private final List<DisputeEntry> allDisputes = new ArrayList<>();

    // Navigation between list and detail
    private java.awt.CardLayout rootCard;
    private ITDisputeDetail     detailPanel;

    public ITDisputeList() {
        rootCard = new java.awt.CardLayout();
        setLayout(rootCard);
        setBackground(Color.WHITE);

        // ── List card ────────────────────────────────────────────────────────
        JPanel listCard = new JPanel(new BorderLayout());
        listCard.setBackground(Color.WHITE);
        listCard.add(buildTopBar(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(16, 24, 24, 24));

        body.add(buildSearchSection());
        body.add(Box.createVerticalStrut(12));
        body.add(buildToolbar());
        body.add(Box.createVerticalStrut(16));
        body.add(buildTablePanel());

        listCard.add(body, BorderLayout.CENTER);

        // ── Detail card ──────────────────────────────────────────────────────
        detailPanel = new ITDisputeDetail(() -> rootCard.show(ITDisputeList.this, "list"));

        add(listCard,    "list");
        add(detailPanel, "detail");
        rootCard.show(this, "list");

        loadSampleData();
        wireRowClick();
    }

    /** Open the detail view for the selected row. */
    private void openDetail(int viewRow) {
        // Map view row → model row (handles sorted/filtered state)
        int modelRow = disputeTable.convertRowIndexToModel(viewRow);
        // Find the matching DisputeEntry using the Ticket ID in column 0
        String ticketId = (String) tableModel.getValueAt(modelRow, 0);
        for (DisputeEntry entry : allDisputes) {
            if (entry.ticketId.equals(ticketId)) {
                detailPanel.load(entry);
                rootCard.show(this, "detail");
                return;
            }
        }
    }

    /** Register mouse listener for double-click on any table row. */
    private void wireRowClick() {
        disputeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = disputeTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        openDetail(row);
                    }
                }
            }
        });
        // Also show hand cursor when hovering over rows
        disputeTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = disputeTable.rowAtPoint(e.getPoint());
                disputeTable.setCursor(row >= 0
                    ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR)
                    : java.awt.Cursor.getDefaultCursor());
            }
        });
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, DIVIDER),
            new EmptyBorder(10, 24, 10, 24)
        ));

        JPanel userChip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userChip.setOpaque(false);

        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setOpaque(false);

        JLabel nameLbl = new JLabel("Name");
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLbl.setForeground(NAVY);
        nameLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel posLbl = new JLabel("Position");
        posLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        posLbl.setForeground(MUTED);
        posLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        nameBlock.add(nameLbl);
        nameBlock.add(posLbl);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NAVY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);

        userChip.add(nameBlock);
        userChip.add(avatar);
        bar.add(userChip, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSearchSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLbl = new JLabel("Search");
        searchLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchLbl.setForeground(Color.BLACK);
        searchLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(searchLbl);
        section.add(Box.createVerticalStrut(6));

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateRow.setOpaque(false);
        dateRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        dateField = new JTextField();
        dateField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dateField.setBorder(new CompoundBorder(
            new RoundedBorder(6, new Color(210, 210, 210)),
            new EmptyBorder(6, 36, 6, 12)
        ));

        JPanel dateWrap = new JPanel(null) {
            @Override
            public boolean isOpaque() { return false; }
        };
        dateWrap.setPreferredSize(new Dimension(280, 38));
        dateField.setBounds(0, 0, 280, 38);

        JLabel calIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 185, 195));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(6, 8, 16, 14, 3, 3);
                g2.drawLine(6, 12, 22, 12);
                g2.drawLine(10, 6, 10, 10);
                g2.drawLine(18, 6, 18, 10);
                g2.dispose();
            }
        };
        calIcon.setBounds(8, 0, 28, 38);

        JLabel datePlaceholder = new JLabel("mm/dd/yyyy");
        datePlaceholder.setFont(new Font("SansSerif", Font.PLAIN, 13));
        datePlaceholder.setForeground(new Color(190, 195, 205));
        datePlaceholder.setBounds(36, 0, 200, 38);

        dateWrap.add(calIcon);
        dateWrap.add(datePlaceholder);
        dateWrap.add(dateField);

        dateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                datePlaceholder.setVisible(dateField.getText().isEmpty());
                applyFilters();
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        dateRow.add(dateWrap);
        section.add(dateRow);
        return section;
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        departmentFilter = new JComboBox<>(new String[] {
            "Employee", "Finance", "HR", "IT", "Admin"
        });
        departmentFilter.setSelectedItem("IT");
        departmentFilter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        departmentFilter.setPreferredSize(new Dimension(110, 32));
        departmentFilter.addActionListener(e -> applyFilters());

        toolbar.add(departmentFilter, BorderLayout.WEST);
        toolbar.add(refreshButton(),   BorderLayout.EAST);
        return toolbar;
    }

    private JButton refreshButton() {
        ImageIcon icon = loadIcon("Refresh-Icon.png", 14, 14);
        JButton btn = new JButton("Refresh", icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(20, 50, 110) : NAVY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(6);
        btn.addActionListener(e -> refreshTable());
        return btn;
    }

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        disputeTable = new JTable(tableModel);
        disputeTable.setRowHeight(48);
        disputeTable.setShowGrid(false);
        disputeTable.setIntercellSpacing(new Dimension(0, 0));
        disputeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        disputeTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        disputeTable.setBackground(Color.WHITE);
        disputeTable.setFillsViewportHeight(true);

        styleTableHeader(disputeTable.getTableHeader());
        styleTableColumns(disputeTable);

        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setOpaque(true);
                Color bg = (row % 2 == 0) ? ROW_WHITE : ROW_GRAY;
                if (isSelected) {
                    bg = new Color(230, 235, 245);
                }
                lbl.setBackground(bg);
                lbl.setForeground(Color.BLACK);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        };

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                StatusBadge badge = new StatusBadge(value != null ? value.toString() : "");
                Color bg = (row % 2 == 0) ? ROW_WHITE : ROW_GRAY;
                if (isSelected) {
                    bg = new Color(230, 235, 245);
                }
                badge.setBackground(bg);
                badge.setOpaque(true);
                return badge;
            }
        };

        for (int i = 0; i < disputeTable.getColumnCount(); i++) {
            if (i == 5) {
                disputeTable.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
            } else {
                disputeTable.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
            }
        }

        JScrollPane scroll = new JScrollPane(disputeTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void styleTableHeader(JTableHeader header) {
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLACK),
            new EmptyBorder(0, 0, 8, 0)
        ));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 44));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setBackground(Color.WHITE);
                lbl.setForeground(Color.BLACK);
                return lbl;
            }
        };
        header.setDefaultRenderer(headerRenderer);
    }

    private void styleTableColumns(JTable table) {
        TableColumnModel cols = table.getColumnModel();
        int[] widths = { 90, 140, 100, 100, 220, 100 };
        for (int i = 0; i < widths.length && i < cols.getColumnCount(); i++) {
            cols.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void loadSampleData() {
        allDisputes.clear();
        allDisputes.add(new DisputeEntry("TK-1001", "Juan Dela Cruz",  "03/15/2026", "IT",       "Cannot access payroll portal",       "Pending"));
        allDisputes.add(new DisputeEntry("TK-1002", "Maria Santos",    "03/14/2026", "IT",       "Password reset request",             "Resolved"));
        allDisputes.add(new DisputeEntry("TK-1003", "Pedro Reyes",     "03/13/2026", "HR",       "Leave balance discrepancy",          "Pending"));
        allDisputes.add(new DisputeEntry("TK-1004", "Ana Lopez",       "03/12/2026", "Finance",  "Payslip amount incorrect",           "Resolved"));
        allDisputes.add(new DisputeEntry("TK-1005", "Carlos Mendez",   "03/11/2026", "IT",       "VPN connection failure",             "Pending"));
        allDisputes.add(new DisputeEntry("TK-1006", "Lisa Tan",        "03/10/2026", "IT",       "Email account locked",               "Resolved"));
        allDisputes.add(new DisputeEntry("TK-1007", "Mark Rivera",     "03/09/2026", "Employee", "Attendance record missing",          "Pending"));
        allDisputes.add(new DisputeEntry("TK-1008", "Grace Lim",       "03/08/2026", "IT",       "System login timeout issue",         "Resolved"));
        applyFilters();
    }

    private void refreshTable() {
        if (dateField != null) {
            dateField.setText("");
        }
        disputeTable.clearSelection();
        applyFilters();
        JOptionPane.showMessageDialog(this,
            "Dispute list refreshed.",
            "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private void applyFilters() {
        String dateQuery = dateField != null ? dateField.getText().trim() : "";
        String dept      = departmentFilter != null
                ? (String) departmentFilter.getSelectedItem() : null;

        tableModel.setRowCount(0);
        for (DisputeEntry entry : allDisputes) {
            if (dept != null && !dept.equalsIgnoreCase(entry.department)) {
                continue;
            }
            if (!dateQuery.isEmpty() && !entry.date.contains(dateQuery)) {
                continue;
            }
            tableModel.addRow(entry.toRow());
        }
    }

    private static ImageIcon loadIcon(String filename, int w, int h) {
        try {
            java.net.URL url = ITDisputeList.class.getClassLoader()
                    .getResource("com/motorph/img/" + filename);
            if (url == null) {
                File f = new File(IMG_DIR + filename);
                if (f.exists()) url = f.toURI().toURL();
            }
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String resolveImgDir() {
        String[] candidates = {
            "src/main/java/com/motorph/img/",
            "main/java/com/motorph/img/",
            "com/motorph/img/",
            "img/",
        };
        for (String c : candidates) {
            if (new File(c).isDirectory()) return c;
        }
        return "src/main/java/com/motorph/img/";
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
            return new String[] { ticketId, employeeName, date, department, description, status };
        }
    }

    static class StatusBadge extends JLabel {
        StatusBadge(String status) {
            super(status, SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setForeground(Color.WHITE);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            if (isOpaque()) {
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            Color pill = "Resolved".equalsIgnoreCase(getText()) ? RESOLVED : PENDING;
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(getText());
            int pillW = textW + 24;
            int pillH = 22;
            int x = 12;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pill);
            g2.fill(new RoundRectangle2D.Float(x, y, pillW, pillH, pillH, pillH));

            g2.setColor(Color.WHITE);
            g2.drawString(getText(), x + (pillW - textW) / 2, y + ((pillH - fm.getHeight()) / 2) + fm.getAscent());

            g2.dispose();
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int   radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public java.awt.Insets getBorderInsets(Component c) {
            return new java.awt.Insets(radius, radius, radius, radius);
        }
    }
}
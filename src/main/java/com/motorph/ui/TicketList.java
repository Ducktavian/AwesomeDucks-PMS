/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 *
 * @author Admin
 */
public class TicketList extends JDialog {

    private TablePanel tablePanel;
    private JTextField searchField;

    public TicketList(Window owner) {
        super(owner, "MotorPH Payroll System - Ticket List", ModalityType.APPLICATION_MODAL);
        setSize(1280, 800);
        setLocationRelativeTo(owner);
        setResizable(false);
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setContentPane(new MainPanel());
    }

    // ── Entry Point (for standalone testing) ────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame dummyOwner = new JFrame();
            TicketList dialog = new TicketList(dummyOwner);
            dialog.setVisible(true);
        });
    }

    // ── Main Panel ───────────────────────────────────────────────────────────────
    class MainPanel extends JPanel {

        MainPanel() {
            setLayout(null);
            setBackground(Theme.WHITE);

            SidebarPanel sidebar = new SidebarPanel();
            sidebar.setBounds(0, 0, 257, 800);
            add(sidebar);

            searchField = new SearchBox();
            searchField.setBounds(335, 118, 305, 39);
            add(searchField);

            ActionButton addButton = new ActionButton("Add", LineIcon.Type.ADD);
            addButton.setBounds(833, 159, 89, 37);
            addButton.addActionListener(e -> addTicket());
            add(addButton);

            ActionButton updateButton = new ActionButton("Update", LineIcon.Type.EDIT);
            updateButton.setBounds(926, 159, 88, 37);
            updateButton.addActionListener(e -> updateTicket());
            add(updateButton);

            ActionButton deleteButton = new ActionButton("Delete", LineIcon.Type.DELETE);
            deleteButton.setBounds(1019, 159, 87, 37);
            deleteButton.addActionListener(e -> deleteTicket());
            add(deleteButton);

            ActionButton refreshButton = new ActionButton("Refresh", LineIcon.Type.REFRESH);
            refreshButton.setBounds(1112, 159, 88, 37);
            refreshButton.addActionListener(e -> refreshTable());
            add(refreshButton);

            ProfilePanel profilePanel = new ProfilePanel();
            profilePanel.setBounds(1077, 40, 123, 57);
            add(profilePanel);

            tablePanel = new TablePanel(createTicketRows());
            tablePanel.setBounds(338, 216, 862, 410);
            add(tablePanel);

            JScrollPane scrollPane = new JScrollPane(tablePanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBounds(338, 216, 862, 410);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(Theme.WHITE);
            add(scrollPane);

            // Live search filtering
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(); }
                @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter(); }
                @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
                private void filter() {
                    String query = searchField.getText().toLowerCase();
                    if ("search".equals(query)) {
                        tablePanel.setFilter("");
                    } else {
                        tablePanel.setFilter(query);
                    }
                }
            });
        }
    }

    // ── CRUD Actions ────────────────────────────────────────────────────────────
    private void addTicket() {
        int nextId = tablePanel.getAllRows().size() + 1;
        TicketRow newRow = new TicketRow(
                "TK" + String.format("%03d", nextId),
                "New Employee",
                java.time.LocalDate.now().toString(),
                "New Issue",
                "Pending request",
                "Pending"
        );
        tablePanel.addRow(newRow);
    }

    private void updateTicket() {
        int index = tablePanel.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TicketRow row = tablePanel.getRowAt(index);

        Color navy = Theme.NAVY;
        Color fieldBorder = new Color(140, 140, 140);
        Color fieldBg = new Color(225, 225, 230);
        Font labelFont = new Font("Open Sans", Font.BOLD, 13);
        Font fieldFont = new Font("Open Sans", Font.PLAIN, 13);

        JTextField idField    = new JTextField(row.ticketId);
        JTextField nameField  = new JTextField(row.employeeName);
        JTextField dateField  = new JTextField(row.date);
        JTextField issueField = new JTextField(row.issue);
        JTextField noteField  = new JTextField(row.note);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending", "Resolved"});
        statusBox.setSelectedItem(row.status);

        idField.setEditable(false);
        idField.setBackground(new Color(245, 245, 245));

        JTextField[] fields = {idField, nameField, dateField, issueField, noteField};
        for (JTextField f : fields) {
            f.setFont(fieldFont);
            f.setBackground(fieldBg);
            f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(fieldBorder, 6),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
        }
        statusBox.setFont(fieldFont);
        statusBox.setBackground(fieldBg);
        statusBox.setBorder(new RoundedBorder(fieldBorder, 6));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Ticket ID:", "Employee Name:", "Date:", "Issue:", "Note:", "Status:"};
        JComponent[] inputs = {idField, nameField, dateField, issueField, noteField, statusBox};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont);
            lbl.setForeground(navy);
            panel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 1;
            inputs[i].setPreferredSize(new Dimension(220, 30));
            panel.add(inputs[i], gbc);
        }

        JLabel titleLabel = new JLabel("Update Ticket");
        titleLabel.setFont(new Font("Open Sans", Font.BOLD, 18));
        titleLabel.setForeground(navy);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(titleLabel, BorderLayout.NORTH);
        wrapper.add(panel, BorderLayout.CENTER);

        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.messageForeground", navy);

        int result = JOptionPane.showConfirmDialog(this, wrapper, "Update Ticket",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            row.employeeName = nameField.getText();
            row.date = dateField.getText();
            row.issue = issueField.getText();
            row.note = noteField.getText();
            row.status = (String) statusBox.getSelectedItem();
            tablePanel.repaint();
        }
    }

    private void deleteTicket() {
        int index = tablePanel.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this ticket?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tablePanel.removeRowAt(index);
        }
    }

    private void refreshTable() {
        tablePanel.setRows(createTicketRows());
        searchField.setText("Search");
        searchField.setForeground(Theme.PLACEHOLDER_GRAY);
        tablePanel.setFilter("");
    }

    // ── Table Data ──────────────────────────────────────────────────────────────
    private List<TicketRow> createTicketRows() {
        List<TicketRow> rows = new ArrayList<>();
        rows.add(new TicketRow("TK001", "Juan Dela Cruz",  "2024-01-15", "Salary Inquiry",     "Request for salary breakdown",   "Pending"));
        rows.add(new TicketRow("TK002", "Maria Santos",    "2024-01-14", "Leave Request",      "5 days vacation leave",           "Resolved"));
        rows.add(new TicketRow("TK003", "Carlos Reyes",    "2024-01-13", "Overtime Pay",       "Claim for overtime compensation", "Resolved"));
        rows.add(new TicketRow("TK004", "Ana Gomez",       "2024-01-12", "Deduction Query",    "Question about tax deduction",    "Pending"));
        rows.add(new TicketRow("TK005", "Miguel Flores",   "2024-01-11", "Bonus Payment",      "Inquiry on bonus status",          "Resolved"));
        rows.add(new TicketRow("TK006", "Rosa Cruz",       "2024-01-10", "Insurance Issue",    "Health insurance claim",            "Resolved"));
        rows.add(new TicketRow("TK007", "Pedro Lopez",     "2024-01-09", "Payroll Adjustment", "Request for salary adjustment",    "Pending"));
        rows.add(new TicketRow("TK008", "Lisa Wang",       "2024-01-08", "Attendance Record",  "Discrepancy in attendance",        "Pending"));
        return rows;
    }
}

// ── Theme ────────────────────────────────────────────────────────────────────────
final class Theme {
    private Theme() {}

    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = Color.BLACK;

    static final Color LIGHT_GRAY_ROW = new Color(217, 217, 217);
    static final Color BORDER_GRAY = new Color(211, 211, 211);
    static final Color PLACEHOLDER_GRAY = new Color(214, 214, 214);
    static final Color POSITION_GRAY = new Color(150, 150, 150);

    static final Color STATUS_PENDING = new Color(255, 222, 89);
    static final Color STATUS_RESOLVED = new Color(12, 194, 107);

    static final Font HEADING_1 = new Font("Segoe UI", Font.BOLD, 28);
    static final Font HEADING_2 = new Font("Segoe UI", Font.BOLD, 18);
    static final Font PARAGRAPH = new Font("Open Sans", Font.PLAIN, 15);
    static final Font PARAGRAPH_BOLD = new Font("Open Sans", Font.BOLD, 15);

    static final Font SEARCH_FONT = new Font("Open Sans", Font.PLAIN, 18);
    static final Font BUTTON_FONT = new Font("Open Sans", Font.PLAIN, 13);
    static final Font TABLE_HEADER_FONT = new Font("Open Sans", Font.BOLD, 12);
    static final Font TABLE_BODY_FONT = new Font("Open Sans", Font.PLAIN, 11);
    static final Font BADGE_FONT = new Font("Open Sans", Font.PLAIN, 10);
}

// ── Sidebar ──────────────────────────────────────────────────────────────────────
class SidebarPanel extends JPanel {

    SidebarPanel() {
        setLayout(null);
        setBackground(Theme.NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(Theme.HEADING_1);
        logo.setForeground(Theme.WHITE);
        logo.setBounds(48, 63, 150, 36);
        add(logo);

        addNavigationItem("Dashboard", LineIcon.Type.DASHBOARD, 158, false);
        addNavigationItem("Employees", LineIcon.Type.EMPLOYEES, 216, false);
        addNavigationItem("Payroll", LineIcon.Type.PAYROLL, 255, false);
        addNavigationItem("Requests", LineIcon.Type.REQUESTS, 300, true);
        addNavigationItem("Attendance", LineIcon.Type.ATTENDANCE, 341, false);

        addNavigationItem("Help Center", LineIcon.Type.HELP, 660, false);
        addNavigationItem("Log Out", LineIcon.Type.LOGOUT, 702, false);
    }

    private void addNavigationItem(String text, LineIcon.Type iconType, int y, boolean active) {
        NavigationItem item = new NavigationItem(text, iconType, active);
        item.setBounds(48, y, 170, 32);
        add(item);
    }
}

class NavigationItem extends JPanel {

    NavigationItem(String text, LineIcon.Type iconType, boolean active) {
        setLayout(null);
        setOpaque(false);

        JLabel iconLabel = new JLabel(new LineIcon(iconType, 22, Theme.WHITE));
        iconLabel.setBounds(0, 5, 22, 22);
        add(iconLabel);

        JLabel label = new JLabel(text);
        label.setFont(active ? Theme.PARAGRAPH_BOLD : Theme.PARAGRAPH);
        label.setForeground(Theme.WHITE);
        label.setBounds(48, 0, 130, 32);
        add(label);
    }
}

// ── Profile ──────────────────────────────────────────────────────────────────────
class ProfilePanel extends JPanel {

    ProfilePanel() {
        setLayout(null);
        setOpaque(false);

        JLabel name = new JLabel("Name", SwingConstants.RIGHT);
        name.setFont(Theme.HEADING_2);
        name.setForeground(new Color(0, 6, 67));
        name.setBounds(0, 7, 58, 22);
        add(name);

        JLabel position = new JLabel("Position", SwingConstants.RIGHT);
        position.setFont(Theme.PARAGRAPH);
        position.setForeground(Theme.POSITION_GRAY);
        position.setBounds(0, 30, 58, 22);
        add(position);

        AvatarCircle avatar = new AvatarCircle();
        avatar.setBounds(67, 0, 56, 56);
        add(avatar);
    }
}

class AvatarCircle extends JPanel {

    AvatarCircle() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Theme.NAVY);
        g.fillOval(0, 0, 56, 56);

        g.dispose();
    }
}

// ── Search Box ───────────────────────────────────────────────────────────────────
class SearchBox extends JTextField {

    SearchBox() {
        super("Search");
        setFont(Theme.SEARCH_FONT);
        setForeground(Theme.PLACEHOLDER_GRAY);
        setBackground(Theme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Theme.BORDER_GRAY, 4),
                BorderFactory.createEmptyBorder(4, 36, 4, 10)
        ));

        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if ("Search".equals(getText())) {
                    setText("");
                    setForeground(Theme.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (getText().isBlank()) {
                    setText("Search");
                    setForeground(Theme.PLACEHOLDER_GRAY);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        LineIcon icon = new LineIcon(LineIcon.Type.SEARCH, 21, Theme.PLACEHOLDER_GRAY);
        icon.paintIcon(this, g, 12, (getHeight() - 21) / 2);

        g.dispose();
    }
}

// ── Action Button ────────────────────────────────────────────────────────────────
class ActionButton extends JButton {

    ActionButton(String buttonText, LineIcon.Type iconType) {
        setLayout(null);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(new LineIcon(iconType, 17, Theme.WHITE));
        iconLabel.setBounds(15, 10, 17, 17);
        add(iconLabel);

        JLabel label = new JLabel(buttonText);
        label.setFont(Theme.BUTTON_FONT);
        label.setForeground(Theme.WHITE);

        int labelX;
        if ("Add".equals(buttonText)) {
            labelX = 48;
        } else if ("Update".equals(buttonText)) {
            labelX = 38;
        } else if ("Delete".equals(buttonText)) {
            labelX = 40;
        } else {
            labelX = 37;
        }

        label.setBounds(labelX, 7, 58, 23);
        add(label);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { setBackground(new Color(25, 40, 140)); repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { setBackground(Theme.NAVY); repaint(); }
        });
        setBackground(Theme.NAVY);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();

        super.paintComponent(graphics);
    }
}

// ── Ticket Row Data ──────────────────────────────────────────────────────────────
class TicketRow {
    String ticketId;
    String employeeName;
    String date;
    String issue;
    String note;
    String status;

    TicketRow(String ticketId, String employeeName, String date, String issue, String note, String status) {
        this.ticketId = ticketId;
        this.employeeName = employeeName;
        this.date = date;
        this.issue = issue;
        this.note = note;
        this.status = status;
    }
}

// ── Table Panel ──────────────────────────────────────────────────────────────────
class TablePanel extends JPanel {

    private List<TicketRow> allRows;
    private List<TicketRow> visibleRows;
    private int selectedIndex = -1;
    private String filter = "";

    private final int[] columnX = {
            18,
            114,
            232,
            362,
            520,
            758
    };

    TablePanel(List<TicketRow> rows) {
        this.allRows = rows;
        this.visibleRows = rows;
        setOpaque(false);
        setPreferredSize(new Dimension(862, computeHeight()));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowHeight = 53;
                int rowTop = 40;
                int clickedIndex = (e.getY() - rowTop) / rowHeight;
                if (clickedIndex >= 0 && clickedIndex < visibleRows.size()) {
                    selectedIndex = clickedIndex;
                } else {
                    selectedIndex = -1;
                }
                repaint();
            }
        });
    }

    private int computeHeight() {
        int rowTop = 40;
        int rowHeight = 53;
        return rowTop + Math.max(visibleRows.size(), 1) * rowHeight;
    }

    void setRows(List<TicketRow> rows) {
        this.allRows = rows;
        applyFilter();
        selectedIndex = -1;
    }

    void addRow(TicketRow row) {
        allRows.add(row);
        applyFilter();
        revalidate();
        repaint();
    }

    void removeRowAt(int visibleIndex) {
        TicketRow row = visibleRows.get(visibleIndex);
        allRows.remove(row);
        applyFilter();
        selectedIndex = -1;
        revalidate();
        repaint();
    }

    void setFilter(String filter) {
        this.filter = filter;
        applyFilter();
        repaint();
    }

    private void applyFilter() {
        if (filter == null || filter.isBlank()) {
            visibleRows = allRows;
        } else {
            visibleRows = new ArrayList<>();
            for (TicketRow row : allRows) {
                String haystack = (row.ticketId + " " + row.employeeName + " " + row.date + " "
                        + row.issue + " " + row.note + " " + row.status).toLowerCase();
                if (haystack.contains(filter)) {
                    visibleRows.add(row);
                }
            }
        }
        setPreferredSize(new Dimension(862, computeHeight()));
        revalidate();
    }

    int getSelectedRow() {
        return selectedIndex;
    }

    TicketRow getRowAt(int visibleIndex) {
        return visibleRows.get(visibleIndex);
    }

    List<TicketRow> getAllRows() {
        return allRows;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHeader(g);
        drawRows(g);

        g.dispose();
    }

    private void drawHeader(Graphics2D g) {
        g.setFont(Theme.TABLE_HEADER_FONT);
        g.setColor(Theme.BLACK);

        g.drawString("Ticket ID", columnX[0], 14);
        g.drawString("Employee Name", columnX[1], 14);
        g.drawString("Date", columnX[2], 14);
        g.drawString("Issue", columnX[3], 14);
        g.drawString("Note", columnX[4], 14);
        g.drawString("Status", columnX[5], 14);

        g.setColor(Theme.BLACK);
        g.fillRect(0, 36, 860, 4);
    }

    private void drawRows(Graphics2D g) {
        int rowTop = 40;
        int rowHeight = 53;

        for (int index = 0; index < visibleRows.size(); index++) {
            int y = rowTop + index * rowHeight;

            if (index == selectedIndex) {
                g.setColor(new Color(200, 215, 255));
                g.fillRect(0, y, 859, 50);
            } else if (index % 2 == 1) {
                g.setColor(Theme.LIGHT_GRAY_ROW);
                g.fillRect(0, y, 859, 50);
            }

            TicketRow row = visibleRows.get(index);

            g.setFont(Theme.TABLE_BODY_FONT);
            g.setColor(Theme.BLACK);

            int baseline = y + 31;

            g.drawString(row.ticketId, columnX[0], baseline);
            g.drawString(truncate(row.employeeName, 16), columnX[1], baseline);
            g.drawString(row.date, columnX[2], baseline);
            g.drawString(truncate(row.issue, 20), columnX[3], baseline);
            g.drawString(truncate(row.note, 28), columnX[4], baseline);

            StatusBadge badge = new StatusBadge(row.status);
            badge.paint(g, 756, y + 13);
        }
    }

    private String truncate(String text, int maxLen) {
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen - 1) + "…";
    }
}

// ── Status Badge ─────────────────────────────────────────────────────────────────
class StatusBadge {

    private final String status;

    StatusBadge(String status) {
        this.status = status;
    }

    void paint(Graphics2D g, int x, int y) {
        Color badgeColor;
        Color textColor;

        if ("Resolved".equals(status)) {
            badgeColor = Theme.STATUS_RESOLVED;
            textColor = Theme.WHITE;
        } else {
            badgeColor = Theme.STATUS_PENDING;
            textColor = Theme.BLACK;
        }

        int width = 70;
        int height = 25;

        g.setColor(badgeColor);
        g.fillRoundRect(x, y, width, height, 25, 25);

        g.setFont(Theme.BADGE_FONT);
        g.setColor(textColor);

        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(status)) / 2;
        int textY = y + 16;

        g.drawString(status, textX, textY);
    }
}

// ── Rounded Border ───────────────────────────────────────────────────────────────
class RoundedBorder implements Border {

    private final Color color;
    private final int radius;

    RoundedBorder(Color color, int radius) {
        this.color = color;
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(4, 4, 4, 4);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g.dispose();
    }
}

// ── Line Icon ────────────────────────────────────────────────────────────────────
class LineIcon implements Icon {

    enum Type {
        DASHBOARD,
        EMPLOYEES,
        PAYROLL,
        REQUESTS,
        ATTENDANCE,
        HELP,
        LOGOUT,
        SEARCH,
        ADD,
        EDIT,
        DELETE,
        REFRESH
    }

    private final Type type;
    private final int size;
    private final Color color;

    LineIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();

        g.translate(x, y);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (type) {
            case DASHBOARD:
                drawDashboard(g);
                break;
            case EMPLOYEES:
                drawEmployees(g);
                break;
            case PAYROLL:
                drawPayroll(g);
                break;
            case REQUESTS:
                drawRequests(g);
                break;
            case ATTENDANCE:
                drawAttendance(g);
                break;
            case HELP:
                drawHelp(g);
                break;
            case LOGOUT:
                drawLogout(g);
                break;
            case SEARCH:
                drawSearch(g);
                break;
            case ADD:
                drawAdd(g);
                break;
            case EDIT:
                drawEdit(g);
                break;
            case DELETE:
                drawDelete(g);
                break;
            case REFRESH:
                drawRefresh(g);
                break;
            default:
                break;
        }

        g.dispose();
    }

    private void drawDashboard(Graphics2D g) {
        g.drawRoundRect(1, 1, 8, 8, 2, 2);
        g.drawRoundRect(13, 1, 8, 8, 2, 2);
        g.drawRoundRect(1, 13, 8, 8, 2, 2);
        g.drawRoundRect(13, 13, 8, 8, 2, 2);
    }

    private void drawEmployees(Graphics2D g) {
        g.drawOval(3, 2, 8, 8);
        g.drawArc(1, 12, 13, 9, 0, 180);
        g.drawLine(16, 5, 22, 5);
        g.drawLine(16, 11, 22, 11);
        g.drawLine(16, 17, 22, 17);
    }

    private void drawPayroll(Graphics2D g) {
        g.drawRoundRect(2, 2, 18, 18, 2, 2);
        g.drawRect(6, 6, 4, 4);
        g.drawRect(13, 6, 4, 4);
        g.drawRect(6, 13, 4, 4);
        g.drawLine(13, 14, 17, 14);
        g.drawLine(13, 17, 17, 17);
    }

    private void drawRequests(Graphics2D g) {
        g.drawRoundRect(2, 2, 18, 18, 1, 1);
        g.drawLine(6, 7, 16, 7);
        g.drawLine(6, 12, 16, 12);
        g.drawLine(6, 17, 12, 17);
        g.drawLine(5, 3, 5, 0);
        g.drawLine(17, 3, 17, 0);
    }

    private void drawAttendance(Graphics2D g) {
        g.drawRoundRect(2, 4, 18, 17, 1, 1);
        g.drawLine(2, 8, 20, 8);
        g.drawLine(6, 1, 6, 6);
        g.drawLine(8, 1, 8, 6);
        g.drawLine(10, 1, 10, 6);
        g.drawLine(12, 1, 12, 6);
        g.drawLine(14, 1, 14, 6);
        g.drawLine(16, 1, 16, 6);
    }

    private void drawHelp(Graphics2D g) {
        Path2D cloud = new Path2D.Double();
        cloud.moveTo(5, 17);
        cloud.curveTo(2, 17, 1, 15, 2, 13);
        cloud.curveTo(2, 10, 5, 9, 7, 10);
        cloud.curveTo(8, 6, 13, 5, 15, 9);
        cloud.curveTo(18, 9, 21, 11, 21, 14);
        cloud.curveTo(21, 16, 19, 17, 17, 17);
        cloud.closePath();
        g.draw(cloud);
    }

    private void drawLogout(Graphics2D g) {
        g.drawRect(3, 2, 11, 18);
        g.drawLine(14, 11, 22, 11);
        g.drawLine(18, 7, 22, 11);
        g.drawLine(18, 15, 22, 11);
    }

    private void drawSearch(Graphics2D g) {
        g.drawOval(1, 1, 13, 13);
        g.drawLine(12, 12, 20, 20);
    }

    private void drawAdd(Graphics2D g) {
        g.drawLine(8, 2, 8, 16);
        g.drawLine(1, 9, 15, 9);
    }

    private void drawEdit(Graphics2D g) {
        g.drawLine(3, 14, 13, 4);
        g.drawLine(6, 17, 16, 7);
        g.drawLine(13, 4, 16, 7);
        g.drawLine(3, 14, 2, 18);
        g.drawLine(2, 18, 6, 17);
    }

    private void drawDelete(Graphics2D g) {
        g.drawRect(4, 5, 11, 13);
        g.drawLine(2, 5, 17, 5);
        g.drawLine(6, 3, 13, 3);
        g.drawLine(7, 8, 7, 16);
        g.drawLine(11, 8, 11, 16);
    }

    private void drawRefresh(Graphics2D g) {
        g.drawArc(3, 3, 14, 14, 45, 270);
        g.drawLine(15, 2, 17, 7);
        g.drawLine(15, 2, 11, 4);
    }
}
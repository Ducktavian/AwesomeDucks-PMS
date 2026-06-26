package com.motorph.ui.helpcenter;

import com.motorph.ui.helpcenter.HelpCenterPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * Detail / edit view for a single IT dispute ticket.
 * Matches the two-column form layout with Approve / Reject / Confirm actions.
 * Call {@link #load(ITDisputeList.DisputeEntry)} before showing this panel.
 */
public class DisputeDetail extends JPanel {

    // ── Palette (from the screenshot) ────────────────────────────────────────
    private static final Color NAVY        = new Color(13,  36,  89);
    private static final Color MUTED       = new Color(120, 130, 150);
    private static final Color DIVIDER     = new Color(220, 225, 235);
    private static final Color FIELD_BG    = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(180, 185, 200);
    private static final Color APPROVE_CLR = new Color(34, 197, 94);   // green
    private static final Color REJECT_CLR  = new Color(239, 68,  68);  // red
    private static final Color CONFIRM_CLR = NAVY;

    // ── Editable fields ───────────────────────────────────────────────────────
    private final JTextField ticketIdField    = styledField();
    private final JTextField dateField        = styledField();
    private final JTextField employeeField    = styledField();
    private final JTextField departmentField  = styledField();
    private final JTextArea  descArea         = styledTextArea();
    private final JComboBox<String> statusBox = styledCombo(
            new String[]{"", "Pending", "Resolved"});

    private final Runnable onBack;

    public DisputeDetail(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    /** Populate all fields from the selected dispute entry. */
    public void load(HelpCenterPanel.DisputeEntry entry) {
        ticketIdField  .setText(entry.ticketId);
        dateField      .setText(entry.date);
        employeeField  .setText(entry.employeeName);
        departmentField.setText(entry.department);
        descArea       .setText(entry.description);
        statusBox      .setSelectedItem(entry.status);
        revalidate();
        repaint();
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, DIVIDER),
                new EmptyBorder(10, 24, 10, 24)));

        // Back link
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setForeground(NAVY);
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> onBack.run());

        // User chip (right)
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

        // Circle avatar
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NAVY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);

        userChip.add(nameBlock);
        userChip.add(avatar);

        bar.add(backBtn,  BorderLayout.WEST);
        bar.add(userChip, BorderLayout.EAST);
        return bar;
    }

    // ── Main content ──────────────────────────────────────────────────────────

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Two-column form area
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets  = new Insets(0, 0, 16, 20);
        c.anchor  = GridBagConstraints.NORTHWEST;
        c.fill    = GridBagConstraints.HORIZONTAL;

        // ── Left column ──────────────────────────────────
        int row = 0;

        // Ticket ID
        c.gridx = 0; c.gridy = row; c.weightx = 0.45; c.weighty = 0;
        form.add(labeledField("Ticket ID", ticketIdField, false), c);

        row++;
        // Date
        c.gridx = 0; c.gridy = row;
        form.add(labeledField("Date*", dateField, false), c);

        row++;
        // Employee Name
        c.gridx = 0; c.gridy = row;
        form.add(labeledField("Employee Name*", employeeField, false), c);

        row++;
        // Department
        c.gridx = 0; c.gridy = row;
        form.add(labeledField("Department*", departmentField, false), c);

        // ── Right column ─────────────────────────────────
        // Description (spans rows 0-2)
        c.gridx = 1; c.gridy = 0; c.gridheight = 3; c.weightx = 0.55; c.fill = GridBagConstraints.BOTH; c.weighty = 1;
        JPanel descPanel = new JPanel(new BorderLayout(0, 4));
        descPanel.setOpaque(false);
        JLabel descLbl = new JLabel("Description*");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLbl.setForeground(new Color(50, 60, 80));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        descScroll.setPreferredSize(new Dimension(280, 160));
        descPanel.add(descLbl,   BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        form.add(descPanel, c);

        // Status (row 3, right)
        c.gridx = 1; c.gridy = 3; c.gridheight = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weighty = 0;
        form.add(labeledCombo("Status", statusBox), c);

        // ── Action buttons row ────────────────────────────
        row = 4;
        c.gridx = 1; c.gridy = row; c.gridheight = 1; c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE; c.weightx = 0; c.insets = new Insets(8, 0, 0, 0);
        form.add(buildButtons(), c);

        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    // ── Button row ────────────────────────────────────────────────────────────

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        p.add(actionButton("Approve", APPROVE_CLR));
        p.add(actionButton("Reject",  REJECT_CLR));
        p.add(actionButton("Confirm", CONFIRM_CLR));
        return p;
    }

    private JButton actionButton(String label, Color bg) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                             getModel().isRollover() ? bg.brighter() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(90, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Form helpers ──────────────────────────────────────────────────────────

    private JPanel labeledField(String label, JTextField field, boolean required) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(50, 60, 80));
        p.add(lbl,   BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel labeledCombo(String label, JComboBox<String> combo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(50, 60, 80));
        p.add(lbl,   BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    // ── Widget factories ──────────────────────────────────────────────────────

    private static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(FIELD_BG);
        f.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(240, 36));
        return f;
    }

    private static JTextArea styledTextArea() {
        JTextArea ta = new JTextArea(6, 20);
        ta.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ta.setBackground(FIELD_BG);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        return ta;
    }

    private static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBackground(FIELD_BG);
        cb.setPreferredSize(new Dimension(240, 36));
        return cb;
    }
}
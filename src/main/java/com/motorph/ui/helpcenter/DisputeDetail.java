package com.motorph.ui.helpcenter;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

public class DisputeDetail extends JPanel {

    private static final Color NAVY = new Color(13, 36, 89);
    private static final Color FIELD_BG = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(180, 185, 200);
    private static final Color APPROVE_CLR = new Color(34, 197, 94);
    private static final Color REJECT_CLR = new Color(239, 68, 68);
    private static final Color CONFIRM_CLR = NAVY;

    private final JTextField ticketIdField = styledField();
    private final JTextField dateField = styledField();
    private final JTextField employeeField = styledField();
    private final JTextField departmentField = styledField();
    private final JTextArea descArea = styledTextArea();
    private final JComboBox<String> statusBox = styledCombo(
            new String[]{"", "Pending", "Resolved"});

    private final Runnable onBack;

    public DisputeDetail(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(buildContent(), BorderLayout.CENTER);
    }

    public void load(HelpCenterPanel.DisputeEntry entry) {
        ticketIdField.setText(entry.ticketId);
        dateField.setText(entry.date);
        employeeField.setText(entry.employeeName);
        departmentField.setText(entry.department);
        descArea.setText(entry.description);
        statusBox.setSelectedItem(entry.status);
        revalidate();
        repaint();
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setForeground(NAVY);
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(4, 0, 14, 0));
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setOpaque(false);
        backRow.add(backBtn);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 16, 20);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.45;
        form.add(labeledField("Ticket ID", ticketIdField), c);

        c.gridy = 1;
        form.add(labeledField("Date*", dateField), c);

        c.gridy = 2;
        form.add(labeledField("Employee Name*", employeeField), c);

        c.gridy = 3;
        form.add(labeledField("Department*", departmentField), c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 3;
        c.weightx = 0.55;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        JPanel descPanel = new JPanel(new BorderLayout(0, 4));
        descPanel.setOpaque(false);

        JLabel descLbl = new JLabel("Description*");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLbl.setForeground(new Color(50, 60, 80));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        descScroll.setPreferredSize(new Dimension(280, 160));

        descPanel.add(descLbl, BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        form.add(descPanel, c);

        c.gridx = 1;
        c.gridy = 3;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        form.add(labeledCombo("Status", statusBox), c);

        c.gridx = 1;
        c.gridy = 4;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.insets = new Insets(8, 0, 0, 0);
        form.add(buildButtons(), c);

        outer.add(backRow, BorderLayout.NORTH);
        outer.add(form, BorderLayout.CENTER);

        return outer;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        p.add(actionButton("Approve", APPROVE_CLR));
        p.add(actionButton("Reject", REJECT_CLR));
        p.add(actionButton("Confirm", CONFIRM_CLR));

        return p;
    }

    private JButton actionButton(String label, Color bg) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getModel().isPressed()
                        ? bg.darker()
                        : getModel().isRollover() ? bg.brighter() : bg);

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

    private JPanel labeledField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(50, 60, 80));

        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);

        return p;
    }

    private JPanel labeledCombo(String label, JComboBox<String> combo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(50, 60, 80));

        p.add(lbl, BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);

        return p;
    }

    private static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(FIELD_BG);
        f.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
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
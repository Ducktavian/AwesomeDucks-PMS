package com.motorph.ui.helpcenter;

import com.motorph.model.*;
import com.motorph.service.InformationDisputeService;
import com.motorph.service.PayrollDisputeService;
import com.motorph.util.AppContext;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;

public class HelpCenterFormPanel extends JPanel {

    private static final Color NAVY         = new Color(13, 36, 89);
    private static final Color FIELD_BG     = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(180, 185, 200);
    private static final Color RESOLVE_CLR  = new Color(34, 197, 94);
    private static final Color READONLY_BG  = new Color(238, 238, 238);

    private final JTextField  ticketIdField  = styledField();
    private final JTextField  dateField      = styledField();
    private final JTextField  employeeField  = styledField();
    private final JTextField  typeField      = styledField();   // type + category
    private final JTextArea   descArea       = styledTextArea();
    private final JTextField  statusField = styledField();
    private JButton resolveBtn;

    private Dispute currentDispute;

    private final Runnable onBack;

    public HelpCenterFormPanel(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(buildContent(), BorderLayout.CENTER);
    }

    /** Loads a real Dispute from the DB into the view fields. */
    public void load(Dispute d, String employeeName, Role viewerRole) {
        this.currentDispute = d;

        ticketIdField.setText(d.getDisputeId());
        dateField.setText(d.getDateFiled() != null ? d.getDateFiled().toString() : "");
        employeeField.setText(employeeName);

        if (d instanceof InformationDispute id) {
            String cat  = id.getCategory()    != null ? id.getCategory()    : "";
            String tgt  = id.getTargetField() != null ? id.getTargetField() : "";
            String label = HelpCenterPanel.IT_CATEGORIES.contains(cat) ? "Info – IT" : "Info – HR";
            typeField.setText(label + (cat.isBlank() ? "" : ": " + cat)
                    + (tgt.isBlank() ? "" : " (" + tgt + ")"));
        } else if (d instanceof PayrollDispute pd) {
            String ps = pd.getPayslipNumber() != null ? pd.getPayslipNumber() : "";
            typeField.setText("Payroll Dispute" + (ps.isBlank() ? "" : " – " + ps));
        } else {
            typeField.setText(d.getDisputeType().name());
        }

        descArea.setText(d.getReason());
        statusField.setText(d.getStatus() == DisputeStatus.RESOLVED ? "Resolved" : "Pending");

        boolean canResolve = canResolveDispute(d, viewerRole)
                && d.getStatus() != DisputeStatus.RESOLVED;
        resolveBtn.setVisible(canResolve);

        revalidate();
        repaint();
    }

    private boolean canResolveDispute(Dispute d, Role r) {
        if (r == Role.ADMIN) return true;
        if (d instanceof InformationDispute id) {
            String cat = id.getCategory();
            if (r == Role.HR && HelpCenterPanel.HR_CATEGORIES.contains(cat)) return true;
            if (r == Role.IT && HelpCenterPanel.IT_CATEGORIES.contains(cat)) return true;
        }
        if (d instanceof PayrollDispute) {
            return r == Role.FINANCE || r == Role.HR;
        }
        return false;
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setForeground(NAVY);
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(4, 0, 14, 0));
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { if (onBack != null) onBack.run(); });

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setOpaque(false);
        backRow.add(backBtn);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets  = new Insets(0, 0, 16, 20);
        c.anchor  = GridBagConstraints.NORTHWEST;
        c.fill    = GridBagConstraints.HORIZONTAL;

        // Left column
        c.gridx = 0; c.gridy = 0; c.weightx = 0.45;
        makeReadOnly(ticketIdField);
        form.add(labeledField("Ticket ID", ticketIdField), c);

        c.gridy = 1;
        makeReadOnly(dateField);
        form.add(labeledField("Date Filed", dateField), c);

        c.gridy = 2;
        makeReadOnly(employeeField);
        form.add(labeledField("Employee Name", employeeField), c);

        c.gridy = 3;
        makeReadOnly(typeField);
        form.add(labeledField("Type / Category", typeField), c);

        // Right column – description (spans 3 rows)
        c.gridx = 1; c.gridy = 0; c.gridheight = 3;
        c.weightx = 0.55; c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        JPanel descPanel = new JPanel(new BorderLayout(0, 4));
        descPanel.setOpaque(false);

        JLabel descLbl = new JLabel("Reason / Description");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLbl.setForeground(new Color(50, 60, 80));

        makeReadOnly(descArea);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        descScroll.setPreferredSize(new Dimension(280, 160));

        descPanel.add(descLbl, BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        form.add(descPanel, c);

        // Status (read-only display). A disabled JComboBox renders its text in
        // the L&F's washed-out (blue/grey) disabled colour and ignores
        // setForeground, so we use a read-only field to guarantee black text.
        c.gridx = 1; c.gridy = 3; c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL; c.weighty = 0;
        makeReadOnly(statusField);
        form.add(labeledField("Status", statusField), c);

        // Resolve button
        c.gridx = 1; c.gridy = 4;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.insets = new Insets(8, 0, 0, 0);
        resolveBtn = actionButton("Resolve", RESOLVE_CLR);
        resolveBtn.setVisible(false);
        resolveBtn.addActionListener(e -> resolveDispute());
        form.add(resolveBtn, c);

        outer.add(backRow, BorderLayout.NORTH);
        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    private void resolveDispute() {
        if (currentDispute == null) return;
        if (currentDispute.getStatus() == DisputeStatus.RESOLVED) {
            JOptionPane.showMessageDialog(this, "This dispute is already resolved.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark dispute #" + currentDispute.getDisputeId() + " as Resolved?",
                "Confirm Resolve", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            InformationDisputeService infoSvc    = AppContext.getInformationDisputeService();
            PayrollDisputeService     payrollSvc = AppContext.getPayrollDisputeService();

            if (currentDispute instanceof InformationDispute id) {
                infoSvc.resolveDispute(id);
            } else if (currentDispute instanceof PayrollDispute pd) {
                payrollSvc.resolveDispute(pd);
            }
            statusField.setText("Resolved");
            resolveBtn.setVisible(false);
            JOptionPane.showMessageDialog(this, "Dispute resolved successfully.");
            if (onBack != null) onBack.run(); // refresh list and go back
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to resolve: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton actionButton(String label, Color bg) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker()
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
        btn.setPreferredSize(new Dimension(100, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Marks a text component as read-only: greyed background with solid black
     * text. Black reads with far better contrast on the grey fill than the
     * pale blue some Look-and-Feels use for non-editable fields.
     */
    private static void makeReadOnly(JTextComponent c) {
        c.setEditable(false);
        c.setFocusable(false);
        c.setBackground(READONLY_BG);
        c.setForeground(Color.BLACK);
        c.setDisabledTextColor(Color.BLACK);
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

}

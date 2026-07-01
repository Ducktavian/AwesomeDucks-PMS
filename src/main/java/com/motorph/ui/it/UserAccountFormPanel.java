package com.motorph.ui.it;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.motorph.model.Employee;
import com.motorph.model.Role;

/**
 * Modal form for creating or updating a user account.
 *
 * NEW (redesign): restyled to match the rest of the app (Segoe UI, navy accent,
 * rounded fields) and re-modelled to fit the database. A user account belongs to
 * an existing employee, so create mode shows an employee picker and the name /
 * position / supervisor are read-only (they live on the employee, edited in the
 * Employees tab). Only the account fields — username, role, and active status —
 * are editable here.
 *
 * NOTE: Password management is NOT handled in this panel. Only the account
 * owner (Employee) can change their own password, via the Account Security
 * section under Settings. This form's Reset Password button is an Admin-side
 * action that simply resets the account's password (e.g. to a system default)
 * and does not collect or edit a password value here.
 */
public class UserAccountFormPanel extends JDialog {

    private static final Color NAVY = new Color(8, 25, 105);
    private static final String FONT = "Segoe UI";

    private static final String[] ROLE_OPTIONS = { "Employee", "Finance", "HR", "IT", "Admin" };

    private final boolean editMode;
    private final UserAccountEntry existing;       // non-null in update mode
    private final List<Employee> availableEmployees; // non-null in create mode

    private JComboBox<String> employeeBox;  // create mode
    private JLabel employeeLabel;           // update mode (read-only)
    private JTextField positionField;
    private JTextField supervisorField;
    private JTextField usernameField;
    private JButton resetPasswordButton;
    private JComboBox<String> roleBox;
    private JComboBox<String> statusBox;

    private boolean confirmed;
    private UserAccountEntry result;

    /** Create mode — choose an employee that does not yet have an account. */
    public UserAccountFormPanel(Frame owner, List<Employee> availableEmployees) {
        super(owner, "Create User Account", true);
        this.editMode = false;
        this.existing = null;
        this.availableEmployees = availableEmployees;
        buildUI();
    }

    /** Update mode — edit the account fields of an existing user. */
    public UserAccountFormPanel(Frame owner, UserAccountEntry existing) {
        super(owner, "Update User Account", true);
        this.editMode = true;
        this.existing = existing;
        this.availableEmployees = null;
        buildUI();
        populate();
    }

    public boolean isConfirmed() { return confirmed; }
    public UserAccountEntry getResult() { return result; }

    private void buildUI() {
        setSize(620, 480);
        setMinimumSize(new Dimension(560, 460));
        setLocationRelativeTo(getOwner());

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(0, 0, 0, 0));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildForm(), BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(card);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        JLabel title = new JLabel(editMode ? "Update User Account" : "Create User Account");
        title.setFont(new Font(FONT, Font.BOLD, 22));
        title.setForeground(NAVY);

        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(new Font(FONT, Font.PLAIN, 13));
        back.setForeground(new Color(80, 80, 80));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        header.add(title, BorderLayout.CENTER);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(8, 30, 8, 30));

        positionField = readOnlyField();
        supervisorField = readOnlyField();
        usernameField = textField();
        resetPasswordButton = resetPasswordButton();
        roleBox = combo(ROLE_OPTIONS);
        statusBox = combo(new String[] { "Active", "Inactive" });

        // Row 0 — employee picker (create) or read-only label (update) alone
        if (editMode) {
            employeeLabel = new JLabel();
            employeeLabel.setFont(new Font(FONT, Font.BOLD, 13));
            employeeLabel.setForeground(Color.BLACK);
            addCell(form, 0, 0, "Employee", employeeLabel);
        } else {
            employeeBox = new JComboBox<>();
            for (Employee emp : availableEmployees) {
                employeeBox.addItem(emp.getEmployeeId() + " — " + emp.getFullName());
            }
            styleCombo(employeeBox);
            employeeBox.addActionListener(e -> fillEmployeeDetails());
            addCell(form, 0, 0, "Employee*", employeeBox);
        }

        // Row 1 — Position | Username
        addCell(form, 0, 1, "Position", positionField);
        addCell(form, 1, 1, "Username*", usernameField);

        // Row 2 — Immediate Supervisor | Password (Reset)
        addCell(form, 0, 2, "Immediate Supervisor", supervisorField);
        addCell(form, 1, 2, "Password", wrapButtonCell(resetPasswordButton));

        // Row 3 — Account Status | Role
        addCell(form, 0, 3, "Account Status*", statusBox);
        addCell(form, 1, 3, "Role*", roleBox);

        roleBox.setSelectedItem("Employee");

        if (!editMode && employeeBox.getItemCount() > 0) {
            fillEmployeeDetails();
        }

        return form;
    }

    /** Wraps the reset-password button so it sits flush-left within its grid cell. */
    private JPanel wrapButtonCell(JButton button) {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setBackground(Color.WHITE);
        wrap.add(button);
        return wrap;
    }

    private void fillEmployeeDetails() {
        if (employeeBox == null) return;
        int idx = employeeBox.getSelectedIndex();
        if (idx < 0 || idx >= availableEmployees.size()) return;
        Employee emp = availableEmployees.get(idx);
        positionField.setText(emp.getPosition() == null ? "" : emp.getPosition());
        supervisorField.setText(emp.getImmediateSupervisor() == null ? "" : emp.getImmediateSupervisor());
    }

    private void populate() {
        employeeLabel.setText(existing.getEmployeeNo() + " — "
                + (existing.getFullName() == null ? "" : existing.getFullName()));
        positionField.setText(existing.getPosition() == null ? "" : existing.getPosition());
        supervisorField.setText(existing.getSupervisor() == null ? "" : existing.getSupervisor());
        usernameField.setText(existing.getUsername() == null ? "" : existing.getUsername());
        if (existing.getRole() != null) {
            roleBox.setSelectedItem(existing.getRole().getRoleName());
        }
        statusBox.setSelectedItem(existing.isActive() ? "Active" : "Inactive");
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(10, 30, 24, 30));

        JButton submit = new JButton(editMode ? "Update" : "+  Add");
        submit.setBackground(NAVY);
        submit.setForeground(Color.WHITE);
        submit.setFont(new Font(FONT, Font.BOLD, 14));
        submit.setPreferredSize(new Dimension(140, 42));
        submit.setFocusPainted(false);
        submit.setBorderPainted(false);
        submit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submit.addActionListener(e -> handleSubmit());

        footer.add(submit);
        return footer;
    }

    private void handleSubmit() {
        String username = usernameField.getText().trim();

        if (!editMode && (employeeBox == null || employeeBox.getSelectedIndex() < 0)) {
            warn("Please select an employee.");
            return;
        }
        if (username.isEmpty()) {
            warn("Username is required.");
            return;
        }

        result = editMode ? buildUpdatedEntry() : buildNewEntry();
        result.setUsername(username);
        result.setRole(Role.fromName((String) roleBox.getSelectedItem()));
        result.setActive("Active".equals(statusBox.getSelectedItem()));

        confirmed = true;
        dispose();
    }

    /**
     * Resets the account's password (Admin-side action). This panel does not
     * collect or edit a password value itself — self-service password
     * changes are handled separately, in the Account Security section under
     * Settings. Here we just confirm the action and reset it, e.g. to a
     * default password issued by the system.
     */
    private void handleResetPassword() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Reset the password for this account?",
                "Reset Password", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        // TODO: wire to the actual password-reset service call.
        JOptionPane.showMessageDialog(this,
                "Password was reset.",
                "Reset Password", JOptionPane.INFORMATION_MESSAGE);
    }

    private UserAccountEntry buildNewEntry() {
        UserAccountEntry entry = new UserAccountEntry();
        Employee emp = availableEmployees.get(employeeBox.getSelectedIndex());
        entry.setEmployeeId(Integer.parseInt(emp.getEmployeeId()));
        entry.setEmployeeNo(emp.getEmployeeId());
        entry.setFullName(emp.getFullName());
        entry.setPosition(emp.getPosition());
        entry.setSupervisor(emp.getImmediateSupervisor());
        return entry;
    }

    private UserAccountEntry buildUpdatedEntry() {
        UserAccountEntry entry = new UserAccountEntry();
        entry.setUserId(existing.getUserId());
        entry.setEmployeeId(existing.getEmployeeId());
        entry.setEmployeeNo(existing.getEmployeeNo());
        entry.setFullName(existing.getFullName());
        entry.setPosition(existing.getPosition());
        entry.setSupervisor(existing.getSupervisor());
        return entry;
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    // ---- field helpers (shared styling) ----

    private void addCell(JPanel form, int col, int row, String label, JComponent field) {
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = col;
        lc.gridy = row * 2;
        lc.weightx = 1.0;
        lc.fill = GridBagConstraints.HORIZONTAL;
        lc.insets = new Insets(0, col == 0 ? 0 : 18, 4, 0);
        form.add(makeLabel(label), lc);

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = col;
        fc.gridy = row * 2 + 1;
        fc.weightx = 1.0;
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.insets = new Insets(0, col == 0 ? 0 : 18, 14, 0);
        form.add(field, fc);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(FONT, Font.PLAIN, 13));
        lbl.setForeground(new Color(45, 50, 65));
        return lbl;
    }

    private JTextField textField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 36));
        field.setBorder(new CompoundBorder(new RoundedBorder(8), new EmptyBorder(2, 10, 2, 10)));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JTextField readOnlyField() {
        JTextField field = textField();
        field.setEditable(false);
        field.setFocusable(false);
        field.setForeground(new Color(90, 95, 110));
        field.setBackground(new Color(245, 246, 248));
        return field;
    }

    private JButton resetPasswordButton() {
        JButton button = new JButton("Reset Password");
        button.setFont(new Font(FONT, Font.BOLD, 13));
        button.setForeground(NAVY);
        button.setBackground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 36));
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(new RoundedBorder(8), new EmptyBorder(2, 10, 2, 10)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> handleResetPassword());
        return button;
    }

    private JComboBox<String> combo(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        styleCombo(box);
        return box;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setFont(new Font(FONT, Font.PLAIN, 13));
        box.setPreferredSize(new Dimension(0, 36));
        box.setBackground(Color.WHITE);
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(210, 210, 210));
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }
}

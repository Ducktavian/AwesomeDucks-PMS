package com.motorph.ui.IT;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Modal form for creating or updating a user account.
 */
public class CreateUserDialog extends JDialog {

    private static final Color NAVY         = new Color(13,  36,  89);
    private static final Color PANEL_BG     = new Color(235, 237, 242);
    private static final Color FIELD_BORDER = new Color(200, 205, 215);

    private JTextField        firstNameField;
    private JTextField        lastNameField;
    private JComboBox<String> supervisorBox;
    private JComboBox<String> positionBox;
    private JComboBox<String> statusBox;
    private JTextField        usernameField;
    private JPasswordField    passwordField;
    private JPasswordField    confirmPasswordField;
    private JComboBox<String> accountStatusBox;
    private JComboBox<String> roleBox;

    private final boolean editMode;
    private final String  employeeNo;
    private boolean confirmed;
    private UserAccountEntry result;

    public CreateUserDialog(Frame owner) {
        this(owner, null);
    }

    public CreateUserDialog(Frame owner, UserAccountEntry existing) {
        super(owner, existing == null ? "Create Information" : "Update Information", true);
        this.editMode    = existing != null;
        this.employeeNo  = existing != null ? existing.getEmployeeNo() : null;
        setUndecorated(true);
        setSize(900, 620);
        setLocationRelativeTo(owner);
        buildUI();
        if (existing != null) {
            populate(existing);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public UserAccountEntry getResult() {
        return result;
    }

    private void populate(UserAccountEntry entry) {
        firstNameField.setText(entry.getFirstName());
        lastNameField.setText(entry.getLastName());
        usernameField.setText(entry.getUsername());
        selectCombo(supervisorBox, entry.getSupervisor());
        selectCombo(positionBox, entry.getPosition());
        selectCombo(statusBox, entry.getEmploymentStatus());
        selectCombo(accountStatusBox, entry.getAccountStatus());
        selectCombo(roleBox, entry.getRole());
    }

    private void selectCombo(JComboBox<String> box, String value) {
        if (value == null) return;
        for (int i = 0; i < box.getItemCount(); i++) {
            if (value.equalsIgnoreCase(box.getItemAt(i))) {
                box.setSelectedIndex(i);
                return;
            }
        }
        box.addItem(value);
        box.setSelectedItem(value);
    }

    private UserAccountEntry buildEntry(String empNo) {
        UserAccountEntry entry = new UserAccountEntry();
        entry.setEmployeeNo(empNo);
        entry.setFirstName(firstNameField.getText().trim());
        entry.setLastName(lastNameField.getText().trim());
        entry.setUsername(usernameField.getText().trim());
        entry.setSupervisor((String) supervisorBox.getSelectedItem());
        entry.setPosition((String) positionBox.getSelectedItem());
        entry.setEmploymentStatus((String) statusBox.getSelectedItem());
        entry.setAccountStatus((String) accountStatusBox.getSelectedItem());
        entry.setRole((String) roleBox.getSelectedItem());
        return entry;
    }

    private void buildUI() {
        JPanel overlay = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 120));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(true);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);
        card.setBorder(new LineBorder(new Color(180, 185, 195), 1, true));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildForm(),   BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);

        JPanel centerWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        centerWrap.setOpaque(false);
        centerWrap.add(card);
        card.setPreferredSize(new Dimension(820, 520));

        overlay.add(centerWrap, BorderLayout.CENTER);
        setContentPane(overlay);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 8, 20));

        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(new Font("SansSerif", Font.PLAIN, 13));
        back.setForeground(NAVY);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        JLabel title = new JLabel(
            editMode ? "Update Information" : "Create Information",
            SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.BLACK);

        header.add(back,  BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(8, 32, 8, 32));

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0;
        lc.weightx = 1.0;
        lc.fill = GridBagConstraints.HORIZONTAL;
        lc.insets = new Insets(0, 0, 0, 16);

        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 1;
        rc.weightx = 1.0;
        rc.fill = GridBagConstraints.HORIZONTAL;

        firstNameField = new JTextField();
        usernameField  = new JTextField();
        addField(form, lc, 0, "First Name*", firstNameField);
        addField(form, rc, 0, "Username*",   usernameField);

        lastNameField = new JTextField();
        passwordField = new JPasswordField();
        addField(form, lc, 1, "Last Name*", lastNameField);
        addField(form, rc, 1, editMode ? "Password" : "Password*",
            passwordField);

        supervisorBox = new JComboBox<>(new String[] {
            "Maria Santos", "Pedro Reyes", "Ana Lopez", "Carlos Mendez"
        });
        confirmPasswordField = new JPasswordField();
        addField(form, lc, 2, "Immediate Supervisor", supervisorBox);
        addField(form, rc, 2, editMode ? "Confirm Password" : "Confirm Password*",
            confirmPasswordField);

        positionBox = new JComboBox<>(new String[] {
            "Software Engineer", "Team Lead", "IT Manager",
            "HR Director", "Finance Analyst", "System Administrator"
        });
        accountStatusBox = new JComboBox<>(new String[] { "Active", "Deactive" });
        addField(form, lc, 3, "Position",        positionBox);
        addField(form, rc, 3, "Account Status*", accountStatusBox);

        statusBox = new JComboBox<>(new String[] { "Regular", "Probation", "Contract" });
        roleBox = new JComboBox<>(new String[] {
            "Employee", "Finance", "HR", "IT", "Admin"
        });
        addField(form, lc, 4, "Status", statusBox);
        addField(form, rc, 4, "Role",   roleBox);
        roleBox.setSelectedItem("IT");

        styleField(firstNameField);
        styleField(lastNameField);
        styleField(usernameField);
        stylePasswordField(passwordField);
        stylePasswordField(confirmPasswordField);
        styleCombo(supervisorBox);
        styleCombo(positionBox);
        styleCombo(statusBox);
        styleCombo(accountStatusBox);
        styleCombo(roleBox);

        return form;
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row,
            String label, Component field) {
        gbc.gridy = row * 2;
        gbc.insets = new Insets(0, 0, 4, gbc.gridx == 0 ? 16 : 0);
        form.add(makeLabel(label), gbc);

        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 12, gbc.gridx == 0 ? 16 : 0);
        form.add(field, gbc);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(30, 30, 30));
        return lbl;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        field.setBackground(Color.WHITE);
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 36));
        field.setEchoChar('\u2022');
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        field.setBackground(Color.WHITE);
    }

    private void styleCombo(JComboBox<String> box) {
        box.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.setPreferredSize(new Dimension(0, 36));
        box.setBackground(Color.WHITE);
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 20, 0));

        String btnLabel = editMode ? "Update" : "+ Add";
        JButton submitBtn = new JButton(btnLabel) {
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
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setPreferredSize(new Dimension(140, 42));
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setContentAreaFilled(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.addActionListener(e -> handleSubmit());

        footer.add(submitBtn);
        return footer;
    }

    private void handleSubmit() {
        String first   = firstNameField.getText().trim();
        String last    = lastNameField.getText().trim();
        String user    = usernameField.getText().trim();
        String pass    = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (first.isEmpty() || last.isEmpty() || user.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields (*).",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean passwordProvided = !pass.isEmpty() || !confirm.isEmpty();
        if (!editMode || passwordProvided) {
            if (pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill in both password fields.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pass.length() < 8) {
                JOptionPane.showMessageDialog(this,
                    "Password must be at least 8 characters.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this,
                    "Password and Confirm Password do not match.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                confirmPasswordField.setText("");
                return;
            }
        }

        result = buildEntry(editMode ? employeeNo : null);
        confirmed = true;
        dispose();
    }
}

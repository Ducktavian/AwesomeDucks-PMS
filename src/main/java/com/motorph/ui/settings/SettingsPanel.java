package com.motorph.ui.settings;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import com.motorph.util.AppContext;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SettingsPanel extends JPanel {

    private static final int STRENGTH_WEAK   = 1;
    private static final int STRENGTH_FAIR   = 2;
    private static final int STRENGTH_GOOD   = 3;
    private static final int STRENGTH_STRONG = 4;

    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private StrengthBar    strengthBar;
    private JLabel         strengthLabel;

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 252));
        buildUI();
    }

    private void buildUI() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(245, 247, 252));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
                BorderFactory.createEmptyBorder(50, 60, 50, 60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;

        JLabel titleLabel = new JLabel("Account Security", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(15, 15, 15));
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel(
                "Update your password to keep your account secure.", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        gbc.gridy  = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(subtitleLabel, gbc);

        card.add(makeLabel("Current Password*"), labelGbc(gbc, 2));
        currentPasswordField = makePasswordField();
        card.add(makePasswordRow(currentPasswordField), fieldGbc(gbc, 3));

        card.add(makeLabel("New Password*"), labelGbc(gbc, 4));
        newPasswordField = makePasswordField();
        card.add(makePasswordRow(newPasswordField), fieldGbc(gbc, 5));

        JPanel strengthRow = buildStrengthRow();
        gbc.gridy  = 6;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(strengthRow, gbc);

        newPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e)  { refreshStrength(); }
            @Override
            public void removeUpdate(DocumentEvent e)  { refreshStrength(); }
            @Override
            public void changedUpdate(DocumentEvent e) { refreshStrength(); }
        });

        card.add(makeLabel("Confirm New Password*"), labelGbc(gbc, 7));
        confirmPasswordField = makePasswordField();
        card.add(makePasswordRow(confirmPasswordField), fieldGbc(gbc, 8));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnPanel.setOpaque(false);

        JButton updateButton = makeButton("Update", new Color(46, 204, 113), new Color(39, 174, 96));
        updateButton.addActionListener(e -> handleUpdate());

        JButton cancelButton = makeButton("Cancel", new Color(231, 76, 60), new Color(192, 57, 43));
        cancelButton.addActionListener(e -> clearFields());

        btnPanel.add(updateButton);
        btnPanel.add(cancelButton);

        gbc.gridy  = 9;
        gbc.insets = new Insets(20, 0, 0, 0);
        card.add(btnPanel, gbc);

        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.anchor = GridBagConstraints.CENTER;
        center.add(card, cgbc);
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildStrengthRow() {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 14));

        strengthBar   = new StrengthBar();
        strengthLabel = new JLabel("  ");
        strengthLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        strengthLabel.setPreferredSize(new Dimension(55, 14));

        row.add(strengthBar,   BorderLayout.CENTER);
        row.add(strengthLabel, BorderLayout.EAST);
        return row;
    }

    private void refreshStrength() {
        String pw    = new String(newPasswordField.getPassword());
        int    score = scorePassword(pw);
        strengthBar.setScore(score);

        switch (score) {
            case STRENGTH_WEAK:
                strengthLabel.setText("Weak");
                strengthLabel.setForeground(new Color(231, 76, 60));
                break;
            case STRENGTH_FAIR:
                strengthLabel.setText("Fair");
                strengthLabel.setForeground(new Color(230, 126, 34));
                break;
            case STRENGTH_GOOD:
                strengthLabel.setText("Good");
                strengthLabel.setForeground(new Color(52, 152, 219));
                break;
            case STRENGTH_STRONG:
                strengthLabel.setText("Strong");
                strengthLabel.setForeground(new Color(39, 174, 96));
                break;
            default:
                strengthLabel.setText("  ");
                strengthLabel.setForeground(Color.GRAY);
                break;
        }
    }

    private static int scorePassword(String pw) {
        if (pw == null || pw.isEmpty()) return 0;
        int score = 0;
        if (pw.length() >= 8)                                    score++;
        if (pw.length() >= 12)                                   score++;
        if (pw.matches(".*[A-Z].*") && pw.matches(".*[a-z].*")) score++;
        if (pw.matches(".*\\d.*"))                               score++;
        if (pw.matches(".*[^A-Za-z0-9].*"))                     score++;
        return Math.min(4, Math.max(1, score));
    }

    private void handleUpdate() {
        String current = new String(currentPasswordField.getPassword());
        String newPass  = new String(newPasswordField.getPassword());
        String confirm  = new String(confirmPasswordField.getPassword());

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showWarning("All fields are required.");
            return;
        }
        if (current.equals(newPass)) {
            showWarning("New password must be different from your current password.");
            newPasswordField.setText("");
            newPasswordField.requestFocus();
            return;
        }
        if (newPass.length() < 8) {
            showWarning("New password must be at least 8 characters.");
            newPasswordField.requestFocus();
            return;
        }
        if (!newPass.equals(confirm)) {
            showWarning("New password and confirmation do not match.");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        try {
            AppContext.getAuthService().changePassword(current, newPass);
        } catch (IllegalArgumentException ex) {
            // wrong current password
            showWarning(ex.getMessage());
            currentPasswordField.setText("");
            currentPasswordField.requestFocus();
            return;
        } catch (Exception ex) {
            showWarning("Failed to update password: " + ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(
                this, "Password updated successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        clearFields();
    }

    private void clearFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        strengthBar.setScore(0);
        strengthLabel.setText("  ");
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(
                this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(30, 30, 30));
        return lbl;
    }

    private GridBagConstraints labelGbc(GridBagConstraints gbc, int row) {
        gbc.gridy  = row;
        gbc.insets = new Insets(4, 0, 4, 0);
        return gbc;
    }

    private GridBagConstraints fieldGbc(GridBagConstraints gbc, int row) {
        gbc.gridy  = row;
        gbc.insets = new Insets(0, 0, 4, 0);
        return gbc;
    }

    private JPasswordField makePasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(400, 48));
        field.setEchoChar('\u2022');
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(6, new Color(200, 205, 215)),
                BorderFactory.createEmptyBorder(8, 14, 8, 44)));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JPanel makePasswordRow(JPasswordField field) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            public boolean isOpaque() { return false; }
        };

        final boolean[] visible = { false };

        JButton eyeBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth()  / 2;
                int cy = getHeight() / 2;

                g2.setColor(visible[0] ? new Color(46, 204, 113) : new Color(180, 185, 195));
                g2.setStroke(new BasicStroke(1.6f));
                int[] xpts = { cx - 10, cx, cx + 10, cx };
                int[] ypts = { cy, cy - 6, cy, cy + 6 };
                g2.drawPolygon(xpts, ypts, 4);
                g2.fillOval(cx - 3, cy - 3, 6, 6);

                if (!visible[0]) {
                    g2.setColor(new Color(180, 185, 195));
                    g2.setStroke(new BasicStroke(1.8f));
                    g2.drawLine(cx - 8, cy + 6, cx + 8, cy - 6);
                }
                g2.dispose();
            }
        };
        eyeBtn.setPreferredSize(new Dimension(44, 48));
        eyeBtn.setBorderPainted(false);
        eyeBtn.setContentAreaFilled(false);
        eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.setToolTipText("Toggle password visibility");

        eyeBtn.addActionListener(e -> {
            visible[0] = !visible[0];
            field.setEchoChar(visible[0] ? (char) 0 : '\u2022');
            eyeBtn.repaint();
            field.requestFocus();
        });

        wrapper.add(field,  BorderLayout.CENTER);
        wrapper.add(eyeBtn, BorderLayout.EAST);
        return wrapper;
    }

    private JButton makeButton(String text, Color normal, Color hover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() || getModel().isRollover() ? hover : normal);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(150, 46));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static class StrengthBar extends JPanel {
        private int score = 0;

        StrengthBar() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 6));
        }

        void setScore(int s) { score = s; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int segments = 4;
            int gap      = 4;
            int segW     = (getWidth() - gap * (segments - 1)) / segments;
            int h        = getHeight();

            Color[] fill = {
                new Color(231, 76,  60),
                new Color(230, 126, 34),
                new Color(52,  152, 219),
                new Color(39,  174, 96)
            };

            for (int i = 0; i < segments; i++) {
                int x = i * (segW + gap);
                g2.setColor(i < score ? fill[score - 1] : new Color(220, 222, 228));
                g2.fill(new RoundRectangle2D.Float(x, 0, segW, h, h, h));
            }
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
        public void paintBorder(java.awt.Component c, Graphics g,
                                int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(java.awt.Component c) {
            return new Insets(radius, radius, radius, radius);
        }
    }
}

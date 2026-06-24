package com.motorph.ui.login;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

public class ResetPassword extends JDialog {

    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton        setPasswordButton;
    private JLabel         backLabel;

    public ResetPassword(Frame owner) {
        super(owner, "MotorPH Payroll System", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(650, 560)); // ← was 480
        card.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(15, 15, 15));
        gbc.gridx  = 0;
        gbc.gridy  = 0;
        gbc.insets = new Insets(50, 50, 30, 50);
        card.add(titleLabel, gbc);

        JLabel newPasswordLabel = new JLabel("New Password*");
        newPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        newPasswordLabel.setForeground(new Color(30, 30, 30));
        gbc.gridy  = 1;
        gbc.insets = new Insets(4, 50, 4, 50);
        card.add(newPasswordLabel, gbc);

        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        newPasswordField.setPreferredSize(new Dimension(450, 50));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        newPasswordField.setBackground(Color.WHITE);
        gbc.gridy  = 2;
        gbc.insets = new Insets(0, 50, 18, 50);
        card.add(newPasswordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password*");
        confirmPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPasswordLabel.setForeground(new Color(30, 30, 30));
        gbc.gridy  = 3;
        gbc.insets = new Insets(4, 50, 4, 50);
        card.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        confirmPasswordField.setPreferredSize(new Dimension(450, 50));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        confirmPasswordField.setBackground(Color.WHITE);
        gbc.gridy  = 4;
        gbc.insets = new Insets(0, 50, 30, 50);
        card.add(confirmPasswordField, gbc);

        setPasswordButton = new JButton("Set new password") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(10, 20, 100));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(20, 40, 140));
                } else {
                    g2.setColor(new Color(15, 28, 113));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        setPasswordButton.setPreferredSize(new Dimension(500, 55));
        setPasswordButton.setBorderPainted(false);
        setPasswordButton.setContentAreaFilled(false);
        setPasswordButton.setFocusPainted(false);
        setPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPasswordButton.addActionListener(e -> handleSetPassword());
        gbc.gridy  = 5;
        gbc.insets = new Insets(0, 50, 20, 50);
        card.add(setPasswordButton, gbc);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        backPanel.setOpaque(false);
        backLabel = new JLabel("<html><u>Back</u></html>");
        backLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backLabel.setForeground(new Color(80, 80, 80));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                // TODO: new Login(getOwner()).setVisible(true);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                backLabel.setForeground(new Color(60, 60, 180));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                backLabel.setForeground(new Color(80, 80, 80));
            }
        });
        backPanel.add(backLabel);
        gbc.gridy  = 6;
        gbc.insets = new Insets(0, 50, 40, 50);
        card.add(backPanel, gbc);

        backgroundPanel.add(card);
        getRootPane().setDefaultButton(setPasswordButton);
    }

    private void handleSetPassword() {
        String newPassword     = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in both password fields.",
                "Reset Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newPassword.length() < 8) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 8 characters long.",
                "Reset Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match. Please try again.",
                "Reset Password", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        JOptionPane.showMessageDialog(this,
            "Your password has been reset successfully.",
            "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        // TODO: new Login(getOwner()).setVisible(true);
    }

    static class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel() {
            loadBackgroundImage();
        }

        private void loadBackgroundImage() {
            try (InputStream is = getClass().getResourceAsStream(
                    "/com/motorph/img/MotorPHLogin.png")) {
                if (is != null) {
                    backgroundImage = ImageIO.read(is);
                    return;
                }
            } catch (IOException e) {
                System.err.println("Classpath image load failed: " + e.getMessage());
            }

            try {
                File f = new File("../src/main/java/com/motorph/img/MotorPHLogin.png");
                if (f.exists()) {
                    backgroundImage = ImageIO.read(f);
                    return;
                }
            } catch (IOException e) {
                System.err.println("File-path image load failed: " + e.getMessage());
            }

            System.err.println("Background image not found — using gradient fallback.");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth(), h = getHeight();

            if (backgroundImage != null) {
                double scale = Math.max(
                    (double) w / backgroundImage.getWidth(),
                    (double) h / backgroundImage.getHeight()
                );
                int iw = (int)(backgroundImage.getWidth()  * scale);
                int ih = (int)(backgroundImage.getHeight() * scale);
                g2.drawImage(backgroundImage,
                    (w - iw) / 2, (h - ih) / 2, iw, ih, this);
            } else {
                g2.setPaint(new GradientPaint(
                    0, 0,     new Color(185, 218, 240),
                    0, h / 2, new Color(210, 232, 248)
                ));
                g2.fillRect(0, 0, w, h);

                g2.setPaint(new GradientPaint(
                    0, h / 2, new Color(130, 135, 142),
                    0, h,     new Color(95,  98,  105)
                ));
                g2.fillRect(0, h / 2, w, h / 2);
            }
            g2.dispose();
        }
    }

    static class RoundedPanel extends JPanel {
        private final int   radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg     = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 8; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(i, i + 2,
                    getWidth() - i * 2, getHeight() - i * 2,
                    radius * 2, radius * 2);
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6,
                             radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public Insets getInsets() {
            return new Insets(10, 10, 16, 16);
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
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x, y, width - 1, height - 1,
                             radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            ResetPassword dialog = new ResetPassword(null);
            dialog.setVisible(true);
        });
    }
}
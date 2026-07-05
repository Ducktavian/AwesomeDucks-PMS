package com.motorph.ui.login;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;


public class Reset extends JDialog {

    // ── UI fields ──────────────────────────────────────────────────────────────
    private JTextField emailField;
    private JButton    continueButton;
    private JLabel     backLabel;

    // ── Constructor ────────────────────────────────────────────────────────────
    public Reset(Frame owner) {
        super(owner, "MotorPH Payroll System", true); // modal = true
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        // ── Background ──
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // ── White card ──
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(650, 530));
        card.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // ── Title ──
        JLabel titleLabel = new JLabel("Request Password Reset", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(15, 15, 15));
        gbc.gridx  = 0;
        gbc.gridy  = 0;
        gbc.insets = new Insets(50, 50, 40, 50);
        card.add(titleLabel, gbc);

        // ── Email label ──
        JLabel emailLabel = new JLabel("Email address");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(30, 30, 30));
        gbc.gridy  = 1;
        gbc.insets = new Insets(4, 50, 4, 50);
        card.add(emailLabel, gbc);

        // ── Email field ──
        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        emailField.setPreferredSize(new Dimension(500, 50));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        emailField.setBackground(Color.WHITE);
        emailField.setText("123456789@gmail.com");
        emailField.setForeground(new Color(180, 180, 180));
        gbc.gridy  = 2;
        gbc.insets = new Insets(0, 50, 30, 50);
        card.add(emailField, gbc);

        // ── Continue button ──
        continueButton = new JButton("Continue") {
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
        continueButton.setPreferredSize(new Dimension(500, 55));
        continueButton.setBorderPainted(false);
        continueButton.setContentAreaFilled(false);
        continueButton.setFocusPainted(false);
        continueButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        continueButton.addActionListener(e -> handleReset());
        gbc.gridy  = 3;
        gbc.insets = new Insets(0, 50, 20, 50);
        card.add(continueButton, gbc);

        // ── Back link ──
        backLabel = new JLabel("<html><u>Back</u></html>", SwingConstants.CENTER);
        backLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backLabel.setForeground(new Color(110, 110, 110));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Frame owner = (Frame) getOwner();
                dispose();
                if (owner != null) {
                    owner.setVisible(true);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                backLabel.setForeground(new Color(60, 60, 60));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                backLabel.setForeground(new Color(110, 110, 110));
            }
        });
        gbc.gridy  = 4;
        gbc.insets = new Insets(0, 50, 30, 50);
        card.add(backLabel, gbc);

        backgroundPanel.add(card);
        getRootPane().setDefaultButton(continueButton);
    }

    // ── Reset logic ────────────────────────────────────────────────────────────
    private void handleReset() {
        String email = emailField.getText().trim();

        if (email.isEmpty() || email.equals("123456789@gmail.com")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Reset Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
            "Email-based password reset is still under development. Please contact IT/Admin for now.",
            "Feature Not Yet Available", JOptionPane.INFORMATION_MESSAGE);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BackgroundPanel — paints MotorPHLogin.png; falls back to gradient
    // ═══════════════════════════════════════════════════════════════════════════
    static class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel() {
            loadBackgroundImage();
        }

        private void loadBackgroundImage() {
            // 1. Classpath resource (works after Maven build)
            try (InputStream is = getClass().getResourceAsStream(
                    "/com/motorph/img/MotorPHLogin.png")) {
                if (is != null) {
                    backgroundImage = ImageIO.read(is);
                    return;
                }
            } catch (IOException e) {
                System.err.println("Classpath image load failed: " + e.getMessage());
            }

            // 2. File path fallback (dev convenience)
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
                // Sky gradient fallback
                g2.setPaint(new GradientPaint(
                    0, 0,     new Color(185, 218, 240),
                    0, h / 2, new Color(210, 232, 248)
                ));
                g2.fillRect(0, 0, w, h);

                // Road
                g2.setPaint(new GradientPaint(
                    0, h / 2, new Color(130, 135, 142),
                    0, h,     new Color(95,  98,  105)
                ));
                g2.fillRect(0, h / 2, w, h / 2);
            }
            g2.dispose();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // RoundedPanel — white card with soft drop shadow
    // ═══════════════════════════════════════════════════════════════════════════
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

    // ═══════════════════════════════════════════════════════════════════════════
    // RoundedBorder — rounded outline for text fields
    // ═══════════════════════════════════════════════════════════════════════════
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

    // ── Entry point ────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            Reset dialog = new Reset(null);
            dialog.setVisible(true);
        });
    }
}
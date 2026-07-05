package com.motorph.ui.login;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

import com.motorph.model.UserAccount;
import com.motorph.ui.main.MainFrame;
import com.motorph.service.AuthService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

public class Login extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel forgotPasswordLabel;

    private static final int FIELD_WIDTH = 360;
    private static final int FIELD_HEIGHT = 52;

    public Login(Frame owner) {
        super("MotorPH Payroll System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        RoundedPanel card = new RoundedPanel(24, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(460, 405));
        card.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("MotorPH Payroll System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(18, 24, 38));
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 42, 28, 42);
        card.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(new Color(45, 50, 65));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 42, 6, 42);
        card.add(usernameLabel, gbc);

        usernameField = createModernTextField();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 42, 18, 42);
        card.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(new Color(45, 50, 65));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 42, 6, 42);
        card.add(passwordLabel, gbc);

        passwordField = createModernPasswordField();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 42, 8, 42);
        card.add(passwordField, gbc);

        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setOpaque(false);

        forgotPasswordLabel = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(new Color(90, 95, 110));
        forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Login.this.setVisible(false);
                new Reset(Login.this).setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setForeground(new Color(15, 28, 113));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setForeground(new Color(90, 95, 110));
            }
        });

        forgotPanel.add(forgotPasswordLabel);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 42, 24, 42);
        card.add(forgotPanel, gbc);

        loginButton = createLoginButton();
        loginButton.addActionListener(e -> handleLogin());

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 42, 38, 42);
        card.add(loginButton, gbc);

        backgroundPanel.add(card);
        getRootPane().setDefaultButton(loginButton);
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setMinimumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setForeground(new Color(25, 28, 38));
        field.setBackground(Color.WHITE);
        field.setCaretColor(new Color(15, 28, 113));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(215, 218, 228)),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));
        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setMinimumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setForeground(new Color(25, 28, 38));
        field.setBackground(Color.WHITE);
        field.setCaretColor(new Color(15, 28, 113));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(215, 218, 228)),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));
        return field;
    }

    private JButton createLoginButton() {
        JButton button = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(8, 18, 90));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(25, 45, 145));
                } else {
                    g2.setColor(new Color(15, 28, 113));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();

                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        button.setMinimumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        button.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password.",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            AuthService authService = AppContext.getAuthService();
            UserAccount user = authService.login(username, password);

            Session.setCurrentUser(user);
            dispose();

            SwingUtilities.invokeLater(MainFrame::new);

        } catch (Exception ex) {
            // Print the full chain to the console so the real cause is never lost.
            ex.printStackTrace();

            // Surface the underlying cause (e.g. the wrapped SQLException) in the
            // dialog, not just the top-level wrapper message.
            Throwable root = ex;
            while (root.getCause() != null) {
                root = root.getCause();
            }

            String message = ex.getMessage();
            if (root != ex) {
                message += "\n\nCause: " + root.getClass().getSimpleName()
                        + ": " + root.getMessage();
            }

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    static class BackgroundPanel extends JPanel {

        private BufferedImage backgroundImage;

        public BackgroundPanel() {
            loadBackgroundImage();
        }

        private void loadBackgroundImage() {
            String[] possiblePaths = {
                    "src/main/java/com/motorph/img/MotorPHLogin.png",
                    "src/main/resources/com/motorph/img/MotorPHLogin.png",
                    "com/motorph/img/MotorPHLogin.png",
                    "MotorPHLogin.png"
            };

            for (String path : possiblePaths) {
                try {
                    File f = new File(path);

                    if (f.exists()) {
                        backgroundImage = ImageIO.read(f);
                        System.out.println("Background image loaded from: " + f.getAbsolutePath());
                        return;
                    }
                } catch (IOException e) {
                    System.err.println("Failed to load image from: " + path);
                }
            }

            System.err.println("Background image not found — using gradient fallback.");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, this);
            } else {
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(185, 218, 240),
                        0, panelHeight / 2, new Color(210, 232, 248)
                ));
                g2.fillRect(0, 0, panelWidth, panelHeight);

                g2.setPaint(new GradientPaint(
                        0, panelHeight / 2, new Color(130, 135, 142),
                        0, panelHeight, new Color(95, 98, 105)
                ));
                g2.fillRect(0, panelHeight / 2, panelWidth, panelHeight / 2);
            }

            g2.dispose();
        }
    }

    static class RoundedPanel extends JPanel {

        private final int radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 10; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 4));
                g2.fillRoundRect(
                        i,
                        i + 2,
                        getWidth() - i * 2,
                        getHeight() - i * 2,
                        radius * 2,
                        radius * 2
                );
            }

            g2.setColor(bg);
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 8,
                    getHeight() - 8,
                    radius * 2,
                    radius * 2
            );

            g2.dispose();
        }

        @Override
        public Insets getInsets() {
            return new Insets(10, 10, 16, 16);
        }
    }

    static class RoundedBorder extends AbstractBorder {

        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawRoundRect(
                    x,
                    y,
                    width - 1,
                    height - 1,
                    radius * 2,
                    radius * 2
            );
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
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            Login dialog = new Login(null);
            dialog.setVisible(true);
        });
    }
}
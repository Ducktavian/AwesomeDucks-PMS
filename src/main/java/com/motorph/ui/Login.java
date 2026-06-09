// Login UI for MotorPH Payroll System
package com.motorph.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Main application entrypoint for the MotorPH Payroll System login UI.
 */
public class Login extends JFrame {

    public Login() {
        setTitle("MotorPH Payroll System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setMinimumSize(new Dimension(840, 560));
        setLocationRelativeTo(null);
        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout());

        JPanel card = createLoginCard();
        add(card);
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(460, 420));
        card.setBackground(new Color(255, 255, 255, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(32, 32, 32, 32)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 24, 0);

        JLabel title = new JLabel("MotorPH Payroll System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 30));
        card.add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameLabel.setForeground(new Color(70, 70, 70));
        card.add(usernameLabel, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(360, 46));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 12, 10, 12)));
        card.add(usernameField, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(18, 0, 8, 0);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordLabel.setForeground(new Color(70, 70, 70));
        card.add(passwordLabel, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(360, 46));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 12, 10, 12)));
        card.add(passwordField, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(12, 0, 24, 0);
        JLabel forgot = new JLabel("<html><a href=''>Forgot Password?</a></html>");
        forgot.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.setForeground(new Color(40, 88, 163));
        card.add(forgot, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(2, 48, 110));
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(360, 52));
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        card.add(loginButton, gbc);

        return card;
    }

    private static class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int width = getWidth();
            int height = getHeight();
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(235, 240, 245),
                    width, height, new Color(250, 252, 255));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, width, height);

            int spotlightSize = Math.max(width, height);
            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillOval(width / 2 - spotlightSize / 3, height / 4 - spotlightSize / 4, spotlightSize, spotlightSize);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login frame = new Login();
            frame.setVisible(true);
        });
    }
}

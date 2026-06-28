package com.motorph.ui.login;

import com.motorph.util.AppContext;

import javax.swing.*;
import java.awt.*;

public class OtpDialog extends JDialog {

    private final JTextField otpField = new JTextField();
    private boolean verified = false;

    public OtpDialog(Frame owner, String otpForDemo) {
        super(owner, "Two-Factor Authentication", true);

        setSize(420, 260);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(12, 12));

        JLabel title = new JLabel("Enter 6-digit OTP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel demo = new JLabel("Demo OTP: " + otpForDemo, SwingConstants.CENTER);
        demo.setForeground(Color.GRAY);

        otpField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        otpField.setHorizontalAlignment(JTextField.CENTER);

        JButton verifyButton = new JButton("Verify");
        JButton resendButton = new JButton("Resend OTP");
        JButton cancelButton = new JButton("Cancel");

        JPanel center = new JPanel(new GridLayout(3, 1, 8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        center.add(title);
        center.add(demo);
        center.add(otpField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(verifyButton);
        buttons.add(resendButton);
        buttons.add(cancelButton);

        add(center, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        verifyButton.addActionListener(e -> verify());

        resendButton.addActionListener(e -> {
            String newOtp = AppContext.getOtpService().generateOtp();
            demo.setText("Demo OTP: " + newOtp);
            otpField.setText("");
            JOptionPane.showMessageDialog(this, "A new OTP has been generated.");
        });

        cancelButton.addActionListener(e -> {
            verified = false;
            AppContext.getOtpService().clear();
            dispose();
        });

        getRootPane().setDefaultButton(verifyButton);
    }

    private void verify() {
        boolean ok = AppContext.getOtpService().verifyOtp(otpField.getText());

        if (ok) {
            verified = true;
            dispose();
            return;
        }

        if (AppContext.getOtpService().isExpired()) {
            JOptionPane.showMessageDialog(this, "OTP expired. Please resend OTP.");
            return;
        }

        if (!AppContext.getOtpService().hasAttemptsLeft()) {
            JOptionPane.showMessageDialog(this, "Maximum OTP attempts reached.");
            verified = false;
            AppContext.getOtpService().clear();
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Invalid OTP. Attempts left: " + AppContext.getOtpService().getRemainingAttempts()
        );
    }

    public boolean isVerified() {
        return verified;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FEDisputeDetailsPanel extends JPanel {

    private static final Color PAGE_BG = new Color(250, 250, 250);
    private static final Color NAVY = new Color(7, 24, 104);
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color FIELD_BORDER = new Color(85, 85, 85);
    private static final Color ARROW_GRAY = new Color(205, 205, 205);
    private static final Color APPROVE_GREEN = new Color(0, 191, 99);
    private static final Color REJECT_RED = new Color(255, 87, 87);

    private static final Font LABEL_FONT = new Font("Open Sans", Font.PLAIN, 14);
    private static final Font FIELD_FONT = new Font("Open Sans", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Open Sans", Font.PLAIN, 13);
    private static final Font BACK_FONT = new Font("Open Sans", Font.PLAIN, 13);
    private static final Font PROFILE_NAME_FONT = new Font("Open Sans", Font.BOLD, 18);
    private static final Font PROFILE_POSITION_FONT = new Font("Open Sans", Font.PLAIN, 16);

    public FEDisputeDetailsPanel() {
        setLayout(null);
        setBackground(PAGE_BG);
        setPreferredSize(new Dimension(1023, 800));

        addTopRightProfile();
        addBackLink();
        addLeftFields();
        addRightFields();
        addActionButtons();
    }

    private void addTopRightProfile() {
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(825, 48, 70, 22);
        nameLabel.setFont(PROFILE_NAME_FONT);
        nameLabel.setForeground(NAVY);
        add(nameLabel);

        JLabel positionLabel = new JLabel("Position");
        positionLabel.setBounds(820, 73, 80, 20);
        positionLabel.setFont(PROFILE_POSITION_FONT);
        positionLabel.setForeground(MUTED_TEXT);
        add(positionLabel);

        AvatarCircle avatar = new AvatarCircle();
        avatar.setBounds(887, 40, 56, 56);
        add(avatar);
    }

    private void addBackLink() {
        JLabel backLabel = new JLabel("<html><u>Back</u></html>");
        backLabel.setBounds(63, 164, 55, 18);
        backLabel.setFont(BACK_FONT);
        backLabel.setForeground(new Color(75, 75, 75));
        add(backLabel);
    }

    private void addLeftFields() {
        addLabel("Date*", 63, 218, 120, 20);
        addRoundedField(63, 246, 404, 57);

        addLabel("Employee Name*", 63, 330, 180, 20);
        addRoundedField(63, 358, 404, 57);

        addLabel("Department*", 63, 442, 160, 20);
        addRoundedField(63, 470, 404, 57);
    }

    private void addRightFields() {
        addLabel("Description*", 539, 215, 160, 20);

        RoundedTextArea descriptionArea = new RoundedTextArea();
        descriptionArea.setBounds(539, 242, 403, 280);
        add(descriptionArea);

        addLabel("Status", 539, 547, 100, 20);

        StatusDropdown statusDropdown = new StatusDropdown();
        statusDropdown.setBounds(539, 575, 403, 57);
        add(statusDropdown);
    }

    private void addActionButtons() {
        FlatActionButton approveButton = new FlatActionButton("Approve", APPROVE_GREEN);
        approveButton.setBounds(550, 656, 126, 44);
        add(approveButton);

        FlatActionButton rejectButton = new FlatActionButton("Reject", REJECT_RED);
        rejectButton.setBounds(683, 656, 126, 44);
        add(rejectButton);

        FlatActionButton confirmButton = new FlatActionButton("Confirm", NAVY);
        confirmButton.setBounds(817, 656, 126, 44);
        add(confirmButton);
    }

    private void addLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(LABEL_FONT);
        label.setForeground(BLACK);
        add(label);
    }

    private void addRoundedField(int x, int y, int width, int height) {
        RoundedTextField field = new RoundedTextField();
        field.setBounds(x, y, width, height);
        add(field);
    }

    private static class RoundedTextField extends JTextField {

        private RoundedTextField() {
            setOpaque(false);
            setFont(FIELD_FONT);
            setForeground(BLACK);
            setCaretColor(BLACK);
            setBorder(new EmptyBorder(4, 12, 4, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);

            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);

            g2.dispose();
        }
    }

    private static class RoundedTextArea extends JTextArea {

        private RoundedTextArea() {
            setOpaque(false);
            setFont(FIELD_FONT);
            setForeground(BLACK);
            setCaretColor(BLACK);
            setLineWrap(true);
            setWrapStyleWord(true);
            setBorder(new EmptyBorder(10, 12, 10, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);

            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);

            g2.dispose();
        }
    }

    private static class StatusDropdown extends JComponent {

        public StatusDropdown() {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);

            drawArrow(g2);

            g2.dispose();
        }

        private void drawArrow(Graphics2D g2) {
            g2.setColor(ARROW_GRAY);
            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int centerX = getWidth() - 28;
            int centerY = 25;

            g2.drawLine(centerX - 12, centerY - 2, centerX, centerY + 10);
            g2.drawLine(centerX, centerY + 10, centerX + 12, centerY - 2);
        }
    }

    private static class FlatActionButton extends JButton {

        private final Color buttonColor;

        private FlatActionButton(String text, Color buttonColor) {
            super(text);
            this.buttonColor = buttonColor;

            setFont(BUTTON_FONT);
            setForeground(WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = createGraphics(g);

            g2.setColor(buttonColor);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class AvatarCircle extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);
            g2.setColor(NAVY);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static Graphics2D createGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        return g2;
    }
}

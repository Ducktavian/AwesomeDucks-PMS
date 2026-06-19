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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;

public class HRDisputeDetailsPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color BACK_TEXT = new Color(80, 80, 80);
    private static final Color FIELD_BORDER = new Color(55, 55, 55);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color MUTED_TEXT = new Color(150, 150, 150);
    private static final Color APPROVE_GREEN = new Color(0, 194, 113);
    private static final Color REJECT_RED = new Color(255, 82, 82);

    private final RoundedTextField ticketIdField;
    private final RoundedTextField dateField;
    private final RoundedTextField employeeNameField;
    private final RoundedTextField departmentField;
    private final RoundedTextArea descriptionArea;
    private final DropdownField statusField;

    public HRDisputeDetailsPanel() {
        this(null, null, null, null);
    }

    public HRDisputeDetailsPanel(
            Runnable onBack,
            Runnable onApprove,
            Runnable onReject,
            Runnable onConfirm
    ) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(WHITE);

        add(new TopProfilePanel());

        BackLink backLink = new BackLink(onBack);
        backLink.setBounds(63, 164, 45, 18);
        add(backLink);

        ticketIdField = addTextField("Ticket ID", 63, 216, 403, 57);
        dateField = addTextField("Date*", 63, 325, 403, 57);
        employeeNameField = addTextField("Employee Name*", 63, 437, 403, 57);
        departmentField = addTextField("Department*", 63, 549, 403, 57);

        descriptionArea = addTextArea("Description*", 539, 217, 403, 278);
        statusField = addDropdownField("Status", 539, 550, 403, 57);

        FlatButton approveButton = new FlatButton("Approve", APPROVE_GREEN, onApprove);
        approveButton.setBounds(550, 656, 126, 44);
        add(approveButton);

        FlatButton rejectButton = new FlatButton("Reject", REJECT_RED, onReject);
        rejectButton.setBounds(683, 656, 126, 44);
        add(rejectButton);

        FlatButton confirmButton = new FlatButton("Confirm", NAVY, onConfirm);
        confirmButton.setBounds(816, 656, 127, 44);
        add(confirmButton);
    }

    private RoundedTextField addTextField(String labelText, int x, int labelY, int width, int height) {
        JLabel label = createLabel(labelText, x, labelY, 180, 18);
        add(label);

        RoundedTextField field = new RoundedTextField();
        field.setBounds(x, labelY + 26, width, height);
        add(field);

        return field;
    }

    private RoundedTextArea addTextArea(String labelText, int x, int labelY, int width, int height) {
        JLabel label = createLabel(labelText, x, labelY, 180, 18);
        add(label);

        RoundedTextArea area = new RoundedTextArea();
        area.setBounds(x, labelY + 26, width, height);
        add(area);

        return area;
    }

    private DropdownField addDropdownField(String labelText, int x, int labelY, int width, int height) {
        JLabel label = createLabel(labelText, x, labelY, 180, 18);
        add(label);

        DropdownField field = new DropdownField();
        field.setBounds(x, labelY + 26, width, height);
        add(field);

        return field;
    }

    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(BLACK);
        label.setBounds(x, y, width, height);
        return label;
    }

    public String getTicketId() {
        return ticketIdField.getText();
    }

    public String getDate() {
        return dateField.getText();
    }

    public String getEmployeeName() {
        return employeeNameField.getText();
    }

    public String getDepartment() {
        return departmentField.getText();
    }

    public String getDescription() {
        return descriptionArea.getText();
    }

    public String getStatus() {
        return statusField.getText();
    }

    private static class TopProfilePanel extends JPanel {

        public TopProfilePanel() {
            setBounds(817, 40, 126, 58);
            setLayout(null);
            setOpaque(false);

            JLabel name = new JLabel("Name");
            name.setFont(new Font("Segoe UI", Font.BOLD, 18));
            name.setForeground(NAVY);
            name.setHorizontalAlignment(SwingConstants.RIGHT);
            name.setBounds(0, 6, 62, 22);
            add(name);

            JLabel position = new JLabel("Position");
            position.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            position.setForeground(MUTED_TEXT);
            position.setHorizontalAlignment(SwingConstants.RIGHT);
            position.setBounds(0, 31, 62, 20);
            add(position);

            ProfileCircle circle = new ProfileCircle();
            circle.setBounds(69, 0, 56, 56);
            add(circle);
        }
    }

    private static class ProfileCircle extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillOval(0, 0, 56, 56);

            g2.dispose();
        }
    }

    private static class BackLink extends JComponent {

        private final Runnable action;

        public BackLink(Runnable action) {
            this.action = action;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (BackLink.this.action != null) {
                        BackLink.this.action.run();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(BACK_TEXT);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.drawString("Back", 0, 12);
            g2.drawLine(0, 14, 31, 14);

            g2.dispose();
        }
    }

    private static class RoundedTextField extends JTextField {

        public RoundedTextField() {
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(BLACK);
            setCaretColor(BLACK);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class RoundedTextArea extends JTextArea {

        public RoundedTextArea() {
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(BLACK);
            setCaretColor(BLACK);
            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class DropdownField extends JTextField {

        public DropdownField() {
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(BLACK);
            setCaretColor(BLACK);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 45));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(getWidth() - 39, 25);
            arrow.lineTo(getWidth() - 28, 36);
            arrow.lineTo(getWidth() - 17, 25);
            g2.draw(arrow);

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class FlatButton extends JButton {

        private final Color buttonColor;
        private final Runnable action;

        public FlatButton(String text, Color buttonColor, Runnable action) {
            super(text);
            this.buttonColor = buttonColor;
            this.action = action;

            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addActionListener(e -> {
                if (this.action != null) {
                    this.action.run();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(buttonColor);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();

            super.paintComponent(g);
        }
    }
}
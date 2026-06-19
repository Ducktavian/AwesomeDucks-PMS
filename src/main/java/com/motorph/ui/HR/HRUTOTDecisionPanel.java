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

public class HRUTOTDecisionPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color BACK_TEXT = new Color(80, 80, 80);
    private static final Color FIELD_BORDER = new Color(55, 55, 55);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color APPROVE_GREEN = new Color(0, 194, 113);
    private static final Color REJECT_RED = new Color(255, 82, 82);

    private final RoundedTextField otUtIdField;
    private final RoundedTextField dateField;
    private final RoundedTextField startTimeField;
    private final RoundedTextField endTimeField;
    private final RoundedTextField reasonField;
    private final RoundedTextArea notesArea;
    private final StatusDropdownField statusField;

    public HRUTOTDecisionPanel() {
        this(null, null, null);
    }

    public HRUTOTDecisionPanel(Runnable onBack, Runnable onApprove, Runnable onReject) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(WHITE);

        BackLink backLink = new BackLink(onBack);
        backLink.setBounds(63, 164, 45, 18);
        add(backLink);

        otUtIdField = addTextField("OT/UT ID", 63, 215, 408, 57);
        dateField = addTextField("Date", 63, 330, 403, 57);
        startTimeField = addTextField("Start Time", 63, 443, 403, 57);
        endTimeField = addTextField("End Time", 63, 554, 403, 57);

        reasonField = addTextField("Reason", 539, 216, 404, 57);
        notesArea = addTextArea("Notes", 539, 329, 404, 168);

        JLabel statusLabel = createLabel("Status", 539, 551, 150, 18);
        add(statusLabel);

        statusField = new StatusDropdownField();
        statusField.setBounds(539, 576, 404, 57);
        add(statusField);

        FlatButton approveButton = new FlatButton("Approve", APPROVE_GREEN, onApprove);
        approveButton.setBounds(684, 656, 126, 44);
        add(approveButton);

        FlatButton rejectButton = new FlatButton("Reject", REJECT_RED, onReject);
        rejectButton.setBounds(816, 656, 127, 44);
        add(rejectButton);
    }

    private RoundedTextField addTextField(String labelText, int x, int labelY, int width, int height) {
        JLabel label = createLabel(labelText, x, labelY, 180, 18);
        add(label);

        RoundedTextField field = new RoundedTextField();
        field.setBounds(x, labelY + 25, width, height);
        add(field);

        return field;
    }

    private RoundedTextArea addTextArea(String labelText, int x, int labelY, int width, int height) {
        JLabel label = createLabel(labelText, x, labelY, 180, 18);
        add(label);

        RoundedTextArea area = new RoundedTextArea();
        area.setBounds(x, labelY + 25, width, height);
        add(area);

        return area;
    }

    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(BLACK);
        label.setBounds(x, y, width, height);
        return label;
    }

    public String getOtUtId() {
        return otUtIdField.getText();
    }

    public String getDate() {
        return dateField.getText();
    }

    public String getStartTime() {
        return startTimeField.getText();
    }

    public String getEndTime() {
        return endTimeField.getText();
    }

    public String getReason() {
        return reasonField.getText();
    }

    public String getNotes() {
        return notesArea.getText();
    }

    public String getStatus() {
        return statusField.getSelectedStatus();
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

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.setColor(BACK_TEXT);
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

    private static class StatusDropdownField extends JPanel {

        private String selectedStatus = "";

        public StatusDropdownField() {
            setLayout(null);
            setBackground(WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cycleStatus();
                    repaint();
                }
            });
        }

        private void cycleStatus() {
            if (selectedStatus.equals("")) {
                selectedStatus = "Approved";
            } else if (selectedStatus.equals("Approved")) {
                selectedStatus = "Rejected";
            } else if (selectedStatus.equals("Rejected")) {
                selectedStatus = "Pending";
            } else {
                selectedStatus = "";
            }
        }

        public String getSelectedStatus() {
            return selectedStatus;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            if (!selectedStatus.isEmpty()) {
                g2.setColor(BLACK);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.drawString(selectedStatus, 10, 34);
            }

            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(getWidth() - 38, 23);
            arrow.lineTo(getWidth() - 28, 33);
            arrow.lineTo(getWidth() - 18, 23);
            g2.draw(arrow);

            g2.dispose();
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
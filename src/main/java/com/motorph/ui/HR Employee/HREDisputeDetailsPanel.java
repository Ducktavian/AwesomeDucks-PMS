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
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class HREDisputeDetailsPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color WHITE = Color.WHITE;
    private static final Color NAVY = new Color(6, 20, 104);
    private static final Color TEXT_BLACK = new Color(15, 15, 15);
    private static final Color MUTED_GRAY = new Color(145, 145, 145);
    private static final Color FIELD_BORDER = new Color(70, 70, 70);
    private static final Color LIGHT_ICON = new Color(205, 205, 205);

    private static final Color APPROVE_GREEN = new Color(0, 191, 96);
    private static final Color REJECT_RED = new Color(255, 83, 83);

    private static final String HEADER_FONT = "Segoe UI";
    private static final String TEXT_FONT = "Open Sans";

    public HREDisputeDetailsPanel() {
        setLayout(null);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        add(createProfileArea());
        add(createBackLink());

        add(createLabel("Date*", 63, 217, 180, 22));
        add(createInputField(63, 246, 403, 57));

        add(createLabel("Employee Name*", 63, 329, 180, 22));
        add(createInputField(63, 358, 403, 57));

        add(createLabel("Department**", 63, 442, 180, 22));
        add(createInputField(63, 470, 403, 57));

        add(createLabel("Description*", 539, 216, 180, 22));
        add(createDescriptionArea());

        add(createLabel("Status", 539, 548, 180, 22));
        add(createStatusDropdown());

        add(createBottomButtons());
    }

    private JComponent createProfileArea() {
        JPanel profile = new JPanel(null);
        profile.setOpaque(false);
        profile.setBounds(820, 40, 125, 60);

        JLabel name = new JLabel("Name");
        name.setFont(new Font(HEADER_FONT, Font.BOLD, 18));
        name.setForeground(NAVY);
        name.setBounds(0, 4, 70, 24);
        profile.add(name);

        JLabel position = new JLabel("Position");
        position.setFont(new Font(TEXT_FONT, Font.PLAIN, 16));
        position.setForeground(MUTED_GRAY);
        position.setBounds(0, 28, 80, 22);
        profile.add(position);

        CircleAvatar avatar = new CircleAvatar();
        avatar.setBounds(66, 0, 57, 57);
        profile.add(avatar);

        return profile;
    }

    private JLabel createBackLink() {
        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(new Font(TEXT_FONT, Font.PLAIN, 13));
        back.setForeground(new Color(70, 70, 70));
        back.setBounds(63, 162, 55, 22);
        return back;
    }

    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(TEXT_FONT, Font.PLAIN, 14));
        label.setForeground(TEXT_BLACK);
        label.setBounds(x, y, width, height);
        return label;
    }

    private JComponent createInputField(int x, int y, int width, int height) {
        JTextField field = new JTextField();
        field.setBounds(x, y, width, height);
        field.setFont(new Font(TEXT_FONT, Font.PLAIN, 14));
        field.setForeground(TEXT_BLACK);
        field.setBackground(WHITE);
        field.setCaretColor(TEXT_BLACK);
        field.setBorder(new RoundedLineBorder(FIELD_BORDER, 1, 6));
        field.setMargin(new Insets(0, 10, 0, 10));
        return field;
    }

    private JComponent createDescriptionArea() {
        JTextArea area = new JTextArea();
        area.setBounds(539, 242, 403, 280);
        area.setFont(new Font(TEXT_FONT, Font.PLAIN, 14));
        area.setForeground(TEXT_BLACK);
        area.setBackground(WHITE);
        area.setCaretColor(TEXT_BLACK);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new RoundedLineBorder(FIELD_BORDER, 1, 6));
        area.setMargin(new Insets(10, 10, 10, 10));
        return area;
    }

    private JComponent createStatusDropdown() {
        JPanel dropdown = new JPanel(null);
        dropdown.setBackground(WHITE);
        dropdown.setBorder(new RoundedLineBorder(FIELD_BORDER, 1, 6));
        dropdown.setBounds(539, 575, 403, 57);

        ArrowDownIcon arrow = new ArrowDownIcon();
        arrow.setBounds(359, 18, 26, 22);
        dropdown.add(arrow);

        return dropdown;
    }

    private JComponent createBottomButtons() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(550, 656, 392, 44);

        FlatButton approve = new FlatButton("Approve", APPROVE_GREEN);
        approve.setBounds(0, 0, 127, 44);
        panel.add(approve);

        FlatButton reject = new FlatButton("Reject", REJECT_RED);
        reject.setBounds(133, 0, 126, 44);
        panel.add(reject);

        FlatButton confirm = new FlatButton("Confirm", NAVY);
        confirm.setBounds(266, 0, 126, 44);
        panel.add(confirm);

        return panel;
    }

    private static class FlatButton extends JButton {

        private final Color backgroundColor;

        public FlatButton(String text, Color backgroundColor) {
            super(text);
            this.backgroundColor = backgroundColor;

            setFont(new Font(TEXT_FONT, Font.PLAIN, 13));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class ArrowDownIcon extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(LIGHT_ICON);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.drawLine(3, 5, 13, 16);
            g2.drawLine(13, 16, 23, 5);

            g2.dispose();
        }
    }

    private static class CircleAvatar extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));

            g2.dispose();
        }
    }

    private static class RoundedLineBorder extends AbstractBorder {

        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedLineBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            for (int i = 0; i < thickness; i++) {
                g2.drawRoundRect(
                        x + i,
                        y + i,
                        width - 1 - (i * 2),
                        height - 1 - (i * 2),
                        radius,
                        radius
                );
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(6, 8, 6, 8);
        }

        @Override
        public Insets getBorderInsets(Component component, Insets insets) {
            insets.top = 6;
            insets.left = 8;
            insets.bottom = 6;
            insets.right = 8;
            return insets;
        }
    }
}
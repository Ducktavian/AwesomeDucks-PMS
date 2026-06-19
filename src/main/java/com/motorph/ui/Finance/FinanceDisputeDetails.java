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
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public final class FinanceDisputeDetails extends JPanel {

    private static final int PAGE_WIDTH = 1023;
    private static final int PAGE_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color FIELD_BORDER = new Color(84, 84, 84);
    private static final Color LINK_GRAY = new Color(84, 84, 84);
    private static final Color APPROVE_GREEN = new Color(0, 191, 99);
    private static final Color REJECT_RED = new Color(255, 87, 87);

    public FinanceDisputeDetails() {
        setLayout(null);
        setOpaque(true);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));

        buildProfileHeader();
        buildBackLink();
        buildFormFields();
        buildActionButtons();
    }

    private void buildProfileHeader() {
        JLabel name = createLabel(
                "Name",
                750, 45, 128, 24,
                headerFont(18, Font.BOLD),
                NAVY,
                SwingConstants.RIGHT
        );
        add(name);

        JLabel position = createLabel(
                "Position",
                750, 70, 128, 22,
                textFont(16, Font.PLAIN),
                MUTED_TEXT,
                SwingConstants.RIGHT
        );
        add(position);

        AvatarCircle avatar = new AvatarCircle();
        avatar.setBounds(887, 40, 56, 56);
        add(avatar);
    }

    private void buildBackLink() {
        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setBounds(63, 160, 55, 24);
        back.setFont(textFont(13, Font.PLAIN));
        back.setForeground(LINK_GRAY);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(back);
    }

    private void buildFormFields() {
        add(createLabel("Ticket ID", 64, 213, 140, 24, textFont(14, Font.PLAIN), BLACK, SwingConstants.LEFT));
        add(createTextField(64, 242, 403, 58));

        add(createLabel("Date*", 64, 323, 140, 24, textFont(14, Font.PLAIN), BLACK, SwingConstants.LEFT));
        add(createTextField(64, 349, 403, 58));

        add(createLabel("Employee Name*", 64, 434, 180, 24, textFont(14, Font.PLAIN), BLACK, SwingConstants.LEFT));
        add(createTextField(64, 461, 403, 58));

        add(createLabel("Department*", 64, 547, 160, 24, textFont(14, Font.PLAIN), BLACK, SwingConstants.LEFT));
        add(createTextField(64, 573, 403, 58));

        add(createLabel("Description*", 540, 213, 180, 24, textFont(14, Font.PLAIN), BLACK, SwingConstants.LEFT));
        add(createTextAreaField(540, 242, 403, 280));

        add(createLabel("Status", 540, 547, 160, 24, textFont(14, Font.PLAIN), BLACK, SwingConstants.LEFT));

        DropdownField status = new DropdownField();
        status.setBounds(540, 575, 403, 57);
        add(status);
    }

    private void buildActionButtons() {
        ActionButton approve = new ActionButton("Approve", APPROVE_GREEN);
        approve.setBounds(550, 656, 126, 44);
        add(approve);

        ActionButton reject = new ActionButton("Reject", REJECT_RED);
        reject.setBounds(683, 656, 126, 44);
        add(reject);

        ActionButton confirm = new ActionButton("Confirm", NAVY);
        confirm.setBounds(817, 656, 126, 44);
        add(confirm);
    }

    private RoundedTextField createTextField(int x, int y, int width, int height) {
        RoundedTextField field = new RoundedTextField();
        field.setBounds(x, y, width, height);
        return field;
    }

    private RoundedTextArea createTextAreaField(int x, int y, int width, int height) {
        RoundedTextArea field = new RoundedTextArea();
        field.setBounds(x, y, width, height);
        return field;
    }

    private JLabel createLabel(
            String text,
            int x,
            int y,
            int width,
            int height,
            Font font,
            Color color,
            int alignment
    ) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(font);
        label.setForeground(color);
        label.setHorizontalAlignment(alignment);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(false);
        return label;
    }

    private static Font headerFont(int size, int style) {
        return new Font("Segoe UI", style, size);
    }

    private static Font textFont(int size, int style) {
        return new Font("Open Sans", style, size);
    }

    private static final class RoundedTextField extends JTextField {

        private RoundedTextField() {
            setOpaque(false);
            setFont(textFont(14, Font.PLAIN));
            setForeground(BLACK);
            setCaretColor(BLACK);
            setBorder(new EmptyBorder(4, 10, 4, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            paintFieldBackground(g, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            paintFieldBorder(g, getWidth(), getHeight());
        }
    }

    private static final class RoundedTextArea extends JTextArea {

        private RoundedTextArea() {
            setOpaque(false);
            setFont(textFont(14, Font.PLAIN));
            setForeground(BLACK);
            setCaretColor(BLACK);
            setLineWrap(true);
            setWrapStyleWord(true);
            setBorder(new EmptyBorder(8, 10, 8, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            paintFieldBackground(g, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            paintFieldBorder(g, getWidth(), getHeight());
        }
    }

    private static void paintFieldBackground(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(WHITE);
        g2.fill(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, 7, 7));

        g2.dispose();
    }

    private static void paintFieldBorder(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(FIELD_BORDER);
        g2.setStroke(new BasicStroke(1.3f));
        g2.draw(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, 7, 7));

        g2.dispose();
    }

    private static final class DropdownField extends JComponent {

        private DropdownField() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 7, 7));

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1.3f));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 7, 7));

            g2.setColor(new Color(205, 205, 205));
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(getWidth() - 40, 23);
            arrow.lineTo(getWidth() - 27, 36);
            arrow.lineTo(getWidth() - 14, 23);
            g2.draw(arrow);

            g2.dispose();
        }
    }

    private static final class ActionButton extends JButton {

        private final String label;
        private final Color buttonColor;

        private ActionButton(String label, Color buttonColor) {
            this.label = label;
            this.buttonColor = buttonColor;

            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(buttonColor);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(WHITE);
            g2.setFont(textFont(13, Font.PLAIN));

            FontMetrics metrics = g2.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(label)) / 2;
            int textY = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;

            g2.drawString(label, textX, textY);

            g2.dispose();
        }
    }

    private static final class AvatarCircle extends JComponent {

        private AvatarCircle() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillOval(0, 0, getWidth(), getHeight());

            g2.dispose();
        }
    }
}
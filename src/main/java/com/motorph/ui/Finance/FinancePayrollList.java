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
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

public final class FinancePayrollList extends JPanel {

    private static final int PAGE_WIDTH = 1023;
    private static final int PAGE_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(10, 10, 10);
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color BORDER = new Color(214, 214, 214);
    private static final Color LIGHT_BORDER = new Color(232, 232, 232);
    private static final Color PLACEHOLDER = new Color(207, 207, 207);
    private static final Color TABLE_ROW_GRAY = new Color(217, 217, 217);

    public FinancePayrollList() {
        setLayout(null);
        setOpaque(true);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));

        buildSearchField();
        buildProfileHeader();
        buildRoleDropdown();
        buildActionButtons();
        buildTableHeader();
        buildTableRows();
    }

    private void buildSearchField() {
        SearchField searchField = new SearchField();
        searchField.setBounds(79, 98, 304, 38);
        add(searchField);
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

    private void buildRoleDropdown() {
        RoleDropdown dropdown = new RoleDropdown();
        dropdown.setBounds(79, 159, 107, 36);
        add(dropdown);
    }

    private void buildActionButtons() {
        IconButton addButton = new IconButton("Add", ButtonIcon.PLUS);
        addButton.setBounds(576, 159, 88, 36);
        add(addButton);

        IconButton updateButton = new IconButton("Update", ButtonIcon.PENCIL);
        updateButton.setBounds(668, 159, 88, 36);
        add(updateButton);

        IconButton deleteButton = new IconButton("Delete", ButtonIcon.TRASH);
        deleteButton.setBounds(761, 159, 88, 36);
        add(deleteButton);

        IconButton refreshButton = new IconButton("Refresh", ButtonIcon.REFRESH);
        refreshButton.setBounds(854, 159, 89, 36);
        add(refreshButton);
    }

    private void buildTableHeader() {
        add(createLabel("Payslip ID", 81, 216, 96, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Employee ID", 190, 216, 116, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Start Date", 312, 216, 98, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("End Date", 415, 216, 94, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Gross Pay", 519, 216, 88, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Deduction", 620, 216, 98, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Allowance", 724, 216, 109, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));
        add(createLabel("Net Pay", 839, 216, 78, 25, textFont(13, Font.BOLD), BLACK, SwingConstants.CENTER));

        JPanel divider = new JPanel();
        divider.setBackground(BLACK);
        divider.setBounds(81, 253, 859, 4);
        add(divider);
    }

    private void buildTableRows() {
        addRowPlaceholder(81, 309, 862, 50);
        addRowPlaceholder(81, 412, 862, 57);
        addRowPlaceholder(81, 521, 862, 50);
        addRowPlaceholder(81, 628, 862, 49);
    }

    private void addRowPlaceholder(int x, int y, int width, int height) {
        JPanel row = new JPanel();
        row.setOpaque(true);
        row.setBackground(TABLE_ROW_GRAY);
        row.setBounds(x, y, width, height);
        add(row);
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

    private enum ButtonIcon {
        PLUS,
        PENCIL,
        TRASH,
        REFRESH
    }

    private static final class SearchField extends JTextField {

        private SearchField() {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 37, 0, 10));
            setFont(textFont(20, Font.PLAIN));
            setForeground(BLACK);
            setCaretColor(BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 6, 6));

            g2.setColor(BORDER);
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 6, 6));

            drawSearchIcon(g2);

            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty()) {
                Graphics2D placeholderGraphics = (Graphics2D) g.create();
                placeholderGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                placeholderGraphics.setFont(textFont(20, Font.PLAIN));
                placeholderGraphics.setColor(PLACEHOLDER);
                placeholderGraphics.drawString("Search", 37, 26);
                placeholderGraphics.dispose();
            }
        }

        private void drawSearchIcon(Graphics2D g2) {
            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.drawOval(12, 11, 13, 13);
            g2.drawLine(23, 23, 30, 30);
        }
    }

    private static final class RoleDropdown extends JComponent {

        private RoleDropdown() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(LIGHT_BORDER);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g2.setFont(textFont(13, Font.PLAIN));
            g2.setColor(PLACEHOLDER);
            g2.drawString("Finance", 11, 23);

            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(83, 14, 90, 21);
            g2.drawLine(97, 14, 90, 21);

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

    private static final class IconButton extends JButton {

        private final String label;
        private final ButtonIcon icon;

        private IconButton(String label, ButtonIcon icon) {
            this.label = label;
            this.icon = icon;

            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(textFont(13, Font.PLAIN));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            drawIcon(g2, icon, 18, 18);

            g2.setFont(textFont(13, Font.PLAIN));
            FontMetrics metrics = g2.getFontMetrics();

            int textX = icon == ButtonIcon.PLUS ? 48 : 39;
            int textY = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;

            g2.drawString(label, textX, textY);

            g2.dispose();
        }

        private void drawIcon(Graphics2D g2, ButtonIcon icon, int centerX, int centerY) {
            switch (icon) {
                case PLUS:
                    g2.drawLine(centerX, centerY - 7, centerX, centerY + 7);
                    g2.drawLine(centerX - 7, centerY, centerX + 7, centerY);
                    break;

                case PENCIL:
                    g2.drawLine(centerX - 6, centerY + 6, centerX + 6, centerY - 6);
                    g2.drawLine(centerX - 4, centerY + 8, centerX - 8, centerY + 9);
                    g2.drawLine(centerX - 8, centerY + 9, centerX - 7, centerY + 5);
                    g2.drawLine(centerX + 3, centerY - 8, centerX + 8, centerY - 3);
                    g2.drawLine(centerX + 6, centerY - 10, centerX + 10, centerY - 6);
                    break;

                case TRASH:
                    g2.drawRect(centerX - 6, centerY - 3, 12, 12);
                    g2.drawLine(centerX - 8, centerY - 6, centerX + 8, centerY - 6);
                    g2.drawLine(centerX - 3, centerY - 9, centerX + 3, centerY - 9);
                    g2.drawLine(centerX - 3, centerY, centerX - 3, centerY + 7);
                    g2.drawLine(centerX, centerY, centerX, centerY + 7);
                    g2.drawLine(centerX + 3, centerY, centerX + 3, centerY + 7);
                    break;

                case REFRESH:
                    g2.draw(new Arc2D.Double(centerX - 7, centerY - 7, 14, 14, 40, 285, Arc2D.OPEN));
                    g2.drawLine(centerX + 5, centerY - 8, centerX + 9, centerY - 8);
                    g2.drawLine(centerX + 9, centerY - 8, centerX + 8, centerY - 4);
                    break;
            }
        }
    }
}
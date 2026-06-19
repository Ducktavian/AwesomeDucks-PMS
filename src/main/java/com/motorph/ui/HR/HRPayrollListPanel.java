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
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.*;

public class HRPayrollListPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color ROW_GRAY = new Color(217, 217, 217);
    private static final Color LIGHT_BORDER = new Color(220, 220, 220);
    private static final Color PLACEHOLDER = new Color(205, 205, 205);
    private static final Color MUTED_TEXT = new Color(150, 150, 150);

    public HRPayrollListPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(WHITE);

        add(new TopProfilePanel());

        SearchBox searchBox = new SearchBox();
        searchBox.setBounds(79, 97, 303, 39);
        add(searchBox);

        DropdownPanel roleDropdown = new DropdownPanel("HR");
        roleDropdown.setBounds(78, 159, 108, 36);
        add(roleDropdown);

        ActionButton refreshButton = new ActionButton("Refresh");
        refreshButton.setBounds(855, 159, 88, 36);
        add(refreshButton);

        PayrollTablePanel table = new PayrollTablePanel();
        table.setBounds(78, 219, 865, 470);
        add(table);
    }

    private static class TopProfilePanel extends JPanel {

        public TopProfilePanel() {
            setBounds(817, 40, 126, 58);
            setLayout(null);
            setOpaque(false);

            JLabel name = label("Name", 18, Font.BOLD, NAVY);
            name.setHorizontalAlignment(SwingConstants.RIGHT);
            name.setBounds(0, 6, 62, 22);
            add(name);

            JLabel position = label("Position", 16, Font.PLAIN, MUTED_TEXT);
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

    private static class SearchBox extends JPanel {

        public SearchBox() {
            setLayout(null);
            setBackground(WHITE);
            setBorder(new LineBorder(LIGHT_BORDER, 1));

            JLabel placeholder = label("Search", 20, Font.PLAIN, PLACEHOLDER);
            placeholder.setBounds(36, 6, 120, 26);
            add(placeholder);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(11, 10, 13, 13);
            g2.drawLine(22, 21, 29, 28);

            g2.dispose();
        }
    }

    private static class DropdownPanel extends JPanel {

        public DropdownPanel(String text) {
            setLayout(null);
            setBackground(WHITE);
            setBorder(new LineBorder(new Color(238, 238, 238), 1));

            JLabel dropdownText = label(text, 13, Font.PLAIN, PLACEHOLDER);
            dropdownText.setBounds(11, 8, 60, 18);
            add(dropdownText);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(84, 15);
            arrow.lineTo(91, 22);
            arrow.lineTo(98, 15);
            g2.draw(arrow);

            g2.dispose();
        }
    }

    private static class ActionButton extends JPanel {

        public ActionButton(String text) {
            setLayout(null);
            setBackground(NAVY);

            JLabel label = label(text, 14, Font.PLAIN, WHITE);
            label.setBounds(37, 8, 55, 18);
            add(label);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Arc2D arc = new Arc2D.Double(17, 10, 15, 15, 40, 280, Arc2D.OPEN);
            g2.draw(arc);

            Path2D arrow = new Path2D.Double();
            arrow.moveTo(30, 8);
            arrow.lineTo(34, 8);
            arrow.lineTo(33, 12);
            g2.draw(arrow);

            g2.dispose();
        }
    }

    private static class PayrollTablePanel extends JPanel {

        public PayrollTablePanel() {
            setLayout(null);
            setBackground(WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawHeader(g2);
            drawPlaceholderRows(g2);

            g2.dispose();
        }

        private void drawHeader(Graphics2D g2) {
            g2.setColor(BLACK);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));

            g2.drawString("Payslip ID", 20, 10);
            g2.drawString("Employee ID", 128, 10);
            g2.drawString("Start Date", 247, 10);
            g2.drawString("End Date", 356, 10);
            g2.drawString("Gross Pay", 451, 10);
            g2.drawString("Deduction", 558, 10);
            g2.drawString("Allowance", 667, 10);
            g2.drawString("Net Pay", 774, 10);

            g2.fillRect(3, 34, 859, 3);
        }

        private void drawPlaceholderRows(Graphics2D g2) {
            g2.setColor(ROW_GRAY);

            g2.fillRect(3, 91, 862, 49);
            g2.fillRect(0, 193, 865, 57);
            g2.fillRect(3, 302, 862, 50);
            g2.fillRect(0, 409, 865, 49);
        }
    }

    private static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        label.setOpaque(false);
        return label;
    }
}
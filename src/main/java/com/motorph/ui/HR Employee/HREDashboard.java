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
import java.awt.geom.Ellipse2D;

public class HREDashboard extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color BACKGROUND = Color.WHITE;
    private static final Color NAVY = new Color(7, 21, 104);
    private static final Color TEXT_NAVY = new Color(5, 9, 72);
    private static final Color LIGHT_GRAY = new Color(225, 225, 225);
    private static final Color MUTED_GRAY = new Color(150, 150, 150);
    private static final Color GRID_GRAY = new Color(214, 214, 214);

    private static final String HEADER_FONT = "Segoe UI";
    private static final String TEXT_FONT = "Open Sans";

    public HREDashboard() {
        setLayout(null);
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        add(createRoleDropdown());
        add(createProfileArea());

        add(createPayrollPeriodCard());
        add(createRequestCard(
                "Leave Request",
                "101",
                "Pending Leave\nRequest",
                361,
                157
        ));
        add(createRequestCard(
                "Overtime Request",
                "201",
                "Pending Overtime\nRequest",
                664,
                157
        ));

        HoursWorkedChart chart = new HoursWorkedChart();
        chart.setBounds(57, 327, 886, 385);
        add(chart);
    }

    private JComponent createRoleDropdown() {
        JPanel dropdown = new JPanel(null);
        dropdown.setBackground(Color.WHITE);
        dropdown.setBorder(new LineBorder(LIGHT_GRAY, 1));
        dropdown.setBounds(57, 107, 108, 36);

        JLabel label = new JLabel("Employee");
        label.setFont(textFont(Font.PLAIN, 13));
        label.setForeground(new Color(210, 210, 210));
        label.setBounds(10, 0, 70, 36);
        dropdown.add(label);

        JLabel arrow = new JLabel("⌄");
        arrow.setFont(textFont(Font.PLAIN, 20));
        arrow.setForeground(new Color(210, 210, 210));
        arrow.setHorizontalAlignment(SwingConstants.CENTER);
        arrow.setBounds(78, 1, 28, 32);
        dropdown.add(arrow);

        return dropdown;
    }

    private JComponent createProfileArea() {
        JPanel profile = new JPanel(null);
        profile.setOpaque(false);
        profile.setBounds(820, 40, 125, 60);

        JLabel name = new JLabel("Name");
        name.setFont(headerFont(Font.BOLD, 18));
        name.setForeground(TEXT_NAVY);
        name.setBounds(0, 5, 65, 24);
        profile.add(name);

        JLabel position = new JLabel("Position");
        position.setFont(textFont(Font.PLAIN, 16));
        position.setForeground(MUTED_GRAY);
        position.setBounds(0, 29, 75, 22);
        profile.add(position);

        CircleAvatar avatar = new CircleAvatar();
        avatar.setBounds(66, 0, 57, 57);
        profile.add(avatar);

        return profile;
    }

    private JComponent createPayrollPeriodCard() {
        JPanel card = createCardBase(57, 157);

        JLabel title = new JLabel("On-going Payroll Period");
        title.setFont(textFont(Font.BOLD, 13));
        title.setForeground(Color.WHITE);
        title.setBounds(19, 18, 180, 20);
        card.add(title);

        JLabel month = new JLabel("Jun");
        month.setFont(headerFont(Font.BOLD, 54));
        month.setForeground(Color.WHITE);
        month.setBounds(18, 46, 100, 67);
        card.add(month);

        JLabel date = new JLabel("1–15, 2026");
        date.setFont(textFont(Font.BOLD, 18));
        date.setForeground(Color.WHITE);
        date.setBounds(118, 67, 130, 28);
        card.add(date);

        return card;
    }

    private JComponent createRequestCard(String titleText, String numberText, String descriptionText, int x, int y) {
        JPanel card = createCardBase(x, y);

        JLabel title = new JLabel(titleText);
        title.setFont(textFont(Font.BOLD, 13));
        title.setForeground(Color.WHITE);
        title.setBounds(19, 18, 190, 20);
        card.add(title);

        JLabel number = new JLabel(numberText);
        number.setFont(headerFont(Font.BOLD, 54));
        number.setForeground(Color.WHITE);
        number.setBounds(18, 48, 95, 65);
        card.add(number);

        JLabel description = new JLabel(toHtmlMultiline(descriptionText));
        description.setFont(textFont(Font.BOLD, 16));
        description.setForeground(Color.WHITE);
        description.setBounds(108, 52, 145, 56);
        card.add(description);

        return card;
    }

    private JPanel createCardBase(int x, int y) {
        JPanel card = new JPanel(null);
        card.setBackground(NAVY);
        card.setBounds(x, y, 279, 137);
        return card;
    }

    private String toHtmlMultiline(String text) {
        return "<html>" + text.replace("\n", "<br>") + "</html>";
    }

    private Font headerFont(int style, int size) {
        return new Font(HEADER_FONT, style, size);
    }

    private Font textFont(int style, int size) {
        return new Font(TEXT_FONT, style, size);
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

    private class HoursWorkedChart extends JPanel {

        private final int[] values = {8, 7, 9, 7, 8, 7, 9, 7};
        private final String[] dates = {
                "06/01", "06/02", "06/03", "06/04",
                "06/05", "06/06", "06/07", "06/08"
        };

        HoursWorkedChart() {
            setOpaque(false);
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawTitle(g2);
            drawGridAndYAxis(g2);
            drawBars(g2);
            drawXAxisLabels(g2);

            g2.dispose();
        }

        private void drawTitle(Graphics2D g2) {
            g2.setColor(Color.BLACK);
            g2.setFont(textFont(Font.BOLD, 16));
            g2.drawString("Hours Worked", 0, 17);
        }

        private void drawGridAndYAxis(Graphics2D g2) {
            int chartLeft = 44;
            int chartRight = 865;
            int chartTop = 47;
            int chartBottom = 354;
            int chartHeight = chartBottom - chartTop;

            g2.setFont(textFont(Font.PLAIN, 13));
            g2.setStroke(new BasicStroke(1));

            for (int value = 10; value >= 0; value -= 2) {
                int y = chartBottom - (int) ((value / 10.0) * chartHeight);

                g2.setColor(Color.BLACK);
                String label = String.valueOf(value);
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(label);
                g2.drawString(label, 32 - labelWidth, y + 5);

                if (value > 0) {
                    g2.setColor(GRID_GRAY);
                    g2.drawLine(chartLeft, y, chartRight, y);
                }
            }
        }

        private void drawBars(Graphics2D g2) {
            int chartLeft = 44;
            int chartBottom = 354;
            int chartHeight = 307;

            int barWidth = 88;
            int gap = 17;
            int arc = 12;

            g2.setColor(NAVY);

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / 10.0) * chartHeight);
                int x = chartLeft + i * (barWidth + gap);
                int y = chartBottom - barHeight;

                g2.fillRoundRect(x, y, barWidth, barHeight + arc, arc, arc);

                int squareBottomFixHeight = 12;
                g2.fillRect(x, chartBottom - squareBottomFixHeight, barWidth, squareBottomFixHeight);
            }
        }

        private void drawXAxisLabels(Graphics2D g2) {
            int chartLeft = 44;
            int chartBottom = 354;

            int barWidth = 88;
            int gap = 17;

            g2.setColor(Color.BLACK);
            g2.setFont(textFont(Font.PLAIN, 14));

            FontMetrics metrics = g2.getFontMetrics();

            for (int i = 0; i < dates.length; i++) {
                int x = chartLeft + i * (barWidth + gap);
                int centerX = x + barWidth / 2;

                String date = dates[i];
                int textWidth = metrics.stringWidth(date);

                g2.drawString(date, centerX - textWidth / 2, chartBottom + 18);
            }
        }
    }
}
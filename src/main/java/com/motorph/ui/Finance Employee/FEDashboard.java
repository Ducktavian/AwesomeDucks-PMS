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
import java.awt.geom.RoundRectangle2D;

public class FEDashboard extends JPanel {

    private static final Color NAVY = new Color(7, 24, 104);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(20, 20, 20);
    private static final Color TEXT_MUTED = new Color(145, 145, 145);

    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font TEXT_FONT = new Font("Open Sans", Font.PLAIN, 15);
    private static final Font TEXT_BOLD = new Font("Open Sans", Font.BOLD, 15);

    public FEDashboard() {
        setLayout(null);
        setBackground(WHITE);
        setPreferredSize(new Dimension(1023, 800));

        addEmployeeDropdown();
        addProfileHeader();
        addDashboardCards();
        addHoursWorkedChart();
    }

    private void addEmployeeDropdown() {
        JComboBox<String> employeeDropdown = new JComboBox<>(new String[]{"Employee"});
        employeeDropdown.setBounds(58, 107, 107, 36);
        employeeDropdown.setFont(new Font("Open Sans", Font.PLAIN, 13));
        employeeDropdown.setForeground(new Color(210, 210, 210));
        employeeDropdown.setBackground(WHITE);
        employeeDropdown.setBorder(BorderFactory.createLineBorder(new Color(232, 232, 232)));
        employeeDropdown.setFocusable(false);

        add(employeeDropdown);
    }

    private void addProfileHeader() {
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(825, 47, 70, 22);
        nameLabel.setFont(new Font("Open Sans", Font.BOLD, 18));
        nameLabel.setForeground(NAVY);
        add(nameLabel);

        JLabel positionLabel = new JLabel("Position");
        positionLabel.setBounds(820, 72, 80, 20);
        positionLabel.setFont(new Font("Open Sans", Font.PLAIN, 16));
        positionLabel.setForeground(TEXT_MUTED);
        add(positionLabel);

        ProfileCircle circle = new ProfileCircle();
        circle.setBounds(887, 40, 56, 56);
        add(circle);
    }

    private void addDashboardCards() {
        PayrollPeriodCard payrollCard = new PayrollPeriodCard();
        payrollCard.setBounds(58, 157, 278, 137);
        add(payrollCard);

        RequestCard leaveCard = new RequestCard(
                "Leave Request",
                "101",
                "Pending Leave\nRequest"
        );
        leaveCard.setBounds(361, 157, 279, 137);
        add(leaveCard);

        RequestCard overtimeCard = new RequestCard(
                "Overtime Request",
                "201",
                "Pending Overtime\nRequest"
        );
        overtimeCard.setBounds(664, 157, 279, 137);
        add(overtimeCard);
    }

    private void addHoursWorkedChart() {
        JLabel chartTitle = new JLabel("Hours Worked");
        chartTitle.setBounds(58, 329, 180, 22);
        chartTitle.setFont(TEXT_BOLD);
        chartTitle.setForeground(TEXT_DARK);
        add(chartTitle);

        BarChartPanel chart = new BarChartPanel();
        chart.setBounds(76, 366, 847, 340);
        add(chart);
    }

    private static class ProfileCircle extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fillOval(0, 0, 56, 56);

            g2.dispose();
        }
    }

    private static class PayrollPeriodCard extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(WHITE);
            g2.setFont(new Font("Open Sans", Font.BOLD, 13));
            g2.drawString("On-going Payroll Period", 19, 33);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 54));
            g2.drawString("Jun", 18, 95);

            g2.setFont(new Font("Open Sans", Font.BOLD, 19));
            g2.drawString("1–15, 2026", 119, 86);

            g2.dispose();
        }
    }

    private static class RequestCard extends JComponent {

        private final String title;
        private final String number;
        private final String description;

        public RequestCard(String title, String number, String description) {
            this.title = title;
            this.number = number;
            this.description = description;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);

            g2.setColor(NAVY);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(WHITE);
            g2.setFont(new Font("Open Sans", Font.BOLD, 13));
            g2.drawString(title, 19, 33);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 54));
            g2.drawString(number, 18, 94);

            g2.setFont(new Font("Open Sans", Font.BOLD, 16));

            String[] lines = description.split("\n");
            int textX = 108;
            int textY = 72;

            for (String line : lines) {
                g2.drawString(line, textX, textY);
                textY += 23;
            }

            g2.dispose();
        }
    }

    private static class BarChartPanel extends JComponent {

        private final int[] values = {8, 7, 9, 7, 8, 7, 9, 7};
        private final String[] labels = {
                "06/01", "06/02", "06/03", "06/04",
                "06/05", "06/06", "06/07", "06/08"
        };

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = createGraphics(g);

            int chartLeft = 25;
            int chartTop = 8;
            int chartBottom = 315;
            int chartRight = getWidth();

            int chartHeight = chartBottom - chartTop;

            drawGridAndYAxis(g2, chartLeft, chartTop, chartRight, chartBottom, chartHeight);
            drawBars(g2, chartLeft, chartTop, chartBottom, chartHeight);
            drawXAxisLabels(g2, chartLeft, chartBottom);

            g2.dispose();
        }

        private void drawGridAndYAxis(Graphics2D g2, int left, int top, int right, int bottom, int height) {
            g2.setFont(new Font("Open Sans", Font.PLAIN, 14));
            g2.setColor(TEXT_DARK);

            int[] yValues = {10, 8, 6, 4, 2, 0};

            for (int value : yValues) {
                int y = top + (int) ((10 - value) / 10.0 * height);

                String label = value == 0 ? "O" : String.valueOf(value);
                g2.drawString(label, 0, y + 5);

                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(left, y, right, y);

                g2.setColor(TEXT_DARK);
            }
        }

        private void drawBars(Graphics2D g2, int left, int top, int bottom, int height) {
            g2.setColor(NAVY);

            int barWidth = 88;
            int gap = 17;

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) (values[i] / 10.0 * height);
                int x = left + (i * (barWidth + gap));
                int y = bottom - barHeight;

                Shape roundedBar = new RoundRectangle2D.Double(
                        x,
                        y,
                        barWidth,
                        barHeight + 16,
                        12,
                        12
                );

                g2.fill(roundedBar);

                g2.fillRect(x, bottom - 10, barWidth, 10);
            }
        }

        private void drawXAxisLabels(Graphics2D g2, int left, int bottom) {
            g2.setColor(TEXT_DARK);
            g2.setFont(new Font("Open Sans", Font.PLAIN, 14));

            int barWidth = 88;
            int gap = 17;

            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i < labels.length; i++) {
                int x = left + (i * (barWidth + gap));
                int labelWidth = fm.stringWidth(labels[i]);

                g2.drawString(labels[i], x + (barWidth - labelWidth) / 2, bottom + 18);
            }
        }
    }

    private static Graphics2D createGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        return g2;
    }
}
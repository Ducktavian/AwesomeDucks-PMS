package com.motorph.ui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Objects;

public class DashboardPanel extends JPanel {

    private static final Color NAVY = new Color(5, 22, 103);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED = new Color(150, 150, 150);
    private static final Color GREEN = new Color(0, 185, 105);
    private static final Color RED = new Color(255, 82, 82);

    private JComboBox<String> employeeDropdown;
    private JLabel nameLabel;
    private JLabel positionLabel;
    private Circle avatar;

    private MetricCard totalEmployeesCard;
    private MetricCard ongoingQuarterCard;
    private MetricCard upcomingQuarterCard;

    private JLabel financialTitle;
    private LegendDot revenueDot;
    private JLabel revenueLabel;
    private LegendDot expenseDot;
    private JLabel expenseLabel;
    private FinancialChart chart;

    public DashboardPanel() {
        setLayout(null);
        setBackground(WHITE);

        buildComponents();
    }

    private void buildComponents() {
        employeeDropdown = new JComboBox<>(new String[]{"Employee"});
        employeeDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        employeeDropdown.setForeground(new Color(150, 150, 150));
        employeeDropdown.setBackground(WHITE);
        employeeDropdown.setFocusable(false);
        add(employeeDropdown);

        nameLabel = new JLabel("Name", SwingConstants.RIGHT);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(NAVY);
        add(nameLabel);

        positionLabel = new JLabel("Position", SwingConstants.RIGHT);
        positionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        positionLabel.setForeground(MUTED);
        add(positionLabel);

        avatar = new Circle(NAVY);
        add(avatar);

        totalEmployeesCard = new MetricCard("Total Number of Employees", "1,001", "Employees");
        ongoingQuarterCard = new MetricCard("On-Going Quarter", "2", "April to June, 2026");
        upcomingQuarterCard = new MetricCard("Upcoming Quarter", "3", "July to September, 2026");

        add(totalEmployeesCard);
        add(ongoingQuarterCard);
        add(upcomingQuarterCard);

        financialTitle = new JLabel("Financial Overview");
        financialTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        financialTitle.setForeground(Color.BLACK);
        add(financialTitle);

        revenueDot = new LegendDot(GREEN);
        revenueLabel = new JLabel("Revenue");
        revenueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        expenseDot = new LegendDot(RED);
        expenseLabel = new JLabel("Total Expenses");
        expenseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        add(revenueDot);
        add(revenueLabel);
        add(expenseDot);
        add(expenseLabel);

        chart = new FinancialChart();
        add(chart);
    }

    @Override
    public void doLayout() {
        super.doLayout();

        int w = getWidth();

        int marginLeft = 58;
        int marginRight = 58;
        int top = 40;

        avatar.setBounds(w - marginRight - 56, top, 56, 56);
        nameLabel.setBounds(w - marginRight - 170, top + 8, 105, 22);
        positionLabel.setBounds(w - marginRight - 170, top + 33, 105, 20);

        employeeDropdown.setBounds(marginLeft, 108, 110, 36);

        int cardY = 158;
        int cardH = 137;
        int gap = 24;
        int availableW = w - marginLeft - marginRight;
        int cardW = (availableW - (gap * 2)) / 3;

        totalEmployeesCard.setBounds(marginLeft, cardY, cardW, cardH);
        ongoingQuarterCard.setBounds(marginLeft + cardW + gap, cardY, cardW, cardH);
        upcomingQuarterCard.setBounds(marginLeft + (cardW + gap) * 2, cardY, cardW, cardH);

        financialTitle.setBounds(marginLeft, 330, 250, 25);

        int legendCenterX = marginLeft + (availableW / 2) - 110;
        revenueDot.setBounds(legendCenterX, 384, 14, 14);
        revenueLabel.setBounds(legendCenterX + 20, 380, 90, 22);

        expenseDot.setBounds(legendCenterX + 105, 384, 14, 14);
        expenseLabel.setBounds(legendCenterX + 125, 380, 130, 22);

        chart.setBounds(marginLeft, 415, availableW, 320);
    }

    private static class MetricCard extends JPanel {

        private final JLabel titleLabel;
        private final JLabel numberLabel;
        private final JLabel descLabel;

        MetricCard(String title, String number, String desc) {
            setLayout(null);
            setBackground(NAVY);

            titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(WHITE);
            add(titleLabel);

            numberLabel = new JLabel(number);
            numberLabel.setFont(new Font("Segoe UI", Font.BOLD, number.length() > 1 ? 42 : 46));
            numberLabel.setForeground(WHITE);
            add(numberLabel);

            descLabel = new JLabel(desc);
            descLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            descLabel.setForeground(WHITE);
            add(descLabel);
        }

        @Override
        public void doLayout() {
            super.doLayout();

            int w = getWidth();

            titleLabel.setBounds(18, 18, w - 36, 22);
            numberLabel.setBounds(18, 54, 135, 55);
            descLabel.setBounds(155, 67, w - 165, 28);

            if (w < 270) {
                descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
            } else {
                descLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            }
        }
    }

    private static class FinancialChart extends JPanel {

        private final int[] revenue = {100000, 200000, 300000, 400000, 1000000};
        private final int[] expenses = {75000, 100000, 200000, 300000, 400000};

        private final String[] yLabels = {
                "1,000,000", "800,000", "600,000", "400,000", "200,000", "0"
        };

        private final String[] xLabels = {
                "Jan 2022", "Jul 2022", "Jan 2023", "Jul 2023", "Jan 2024",
                "Jul 2024", "Jan 2025", "Jul 2025", "Jan 2026"
        };

        FinancialChart() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int left = 92;
            int top = 20;
            int right = getWidth() - 24;
            int bottom = getHeight() - 45;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            for (int i = 0; i < yLabels.length; i++) {
                int y = top + (int) ((bottom - top) * (i / 5.0));

                g2.setColor(new Color(205, 205, 205));
                g2.drawLine(left, y, right, y);

                g2.setColor(Color.BLACK);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(yLabels[i], left - fm.stringWidth(yLabels[i]) - 10, y + 5);
            }

            for (int i = 0; i < xLabels.length; i++) {
                int x = left + (int) ((right - left) * (i / 8.0));
                FontMetrics fm = g2.getFontMetrics();

                g2.setColor(Color.BLACK);
                g2.drawString(xLabels[i], x - fm.stringWidth(xLabels[i]) / 2, bottom + 25);
            }

            drawSeries(g2, revenue, GREEN, left, top, right, bottom);
            drawSeries(g2, expenses, RED, left, top, right, bottom);

            g2.dispose();
        }

        private void drawSeries(Graphics2D g2, int[] values, Color color, int left, int top, int right, int bottom) {
            Point[] points = new Point[values.length];
            int[] indexes = {0, 2, 4, 6, 8};

            for (int i = 0; i < values.length; i++) {
                int x = left + (int) ((right - left) * (indexes[i] / 8.0));
                int y = bottom - (int) ((bottom - top) * (values[i] / 1_000_000.0));
                points[i] = new Point(x, y);
            }

            g2.setColor(color);
            g2.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(createSmoothPath(points));

            for (Point p : points) {
                g2.fillOval(p.x - 6, p.y - 6, 12, 12);
            }
        }

        private Shape createSmoothPath(Point[] points) {
            Path2D path = new Path2D.Double();
            path.moveTo(points[0].x, points[0].y);

            for (int i = 0; i < points.length - 1; i++) {
                Point p0 = points[Math.max(i - 1, 0)];
                Point p1 = points[i];
                Point p2 = points[i + 1];
                Point p3 = points[Math.min(i + 2, points.length - 1)];

                double cp1x = p1.x + (p2.x - p0.x) / 6.0;
                double cp1y = p1.y + (p2.y - p0.y) / 6.0;
                double cp2x = p2.x - (p3.x - p1.x) / 6.0;
                double cp2y = p2.y - (p3.y - p1.y) / 6.0;

                path.curveTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y);
            }

            return path;
        }
    }

    private static class Circle extends JPanel {

        private final Color color;

        Circle(Color color) {
            this.color = Objects.requireNonNull(color);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static class LegendDot extends JPanel {

        private final Color color;

        LegendDot(Color color) {
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(color);
            g.fillOval(0, 0, 14, 14);
        }
    }
}
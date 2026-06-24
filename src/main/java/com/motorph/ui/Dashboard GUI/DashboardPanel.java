package com.motorph.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Objects;

public class DashboardPanel extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color SOFT_GRAY = new Color(216, 216, 216);
    private static final Color FIELD_BORDER = new Color(235, 235, 235);
    private static final Color FIELD_TEXT = new Color(205, 205, 205);
    private static final Color BLACK_TEXT = new Color(8, 8, 8);
    private static final Color MUTED_TEXT = new Color(150, 150, 150);
    private static final Color GREEN = new Color(0, 191, 99);
    private static final Color RED = new Color(255, 87, 87);

    private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_SEMIBOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BOLD_16 = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_CARD_NUMBER = new Font("Segoe UI", Font.BOLD, 50);
    private static final Font FONT_QUARTER_NUMBER = new Font("Segoe UI", Font.BOLD, 50);

    public DashboardPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(WHITE);
        setOpaque(true);

        addEmployeeDropdown();
        addProfilePreview();
        addMetricCards();
        addFinancialOverview();
    }

    private void addEmployeeDropdown() {
        JComboBox<String> employeeDropdown = new JComboBox<>(new String[]{"Employee"});
        employeeDropdown.setBounds(57, 108, 108, 35);
        employeeDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeeDropdown.setForeground(FIELD_TEXT);
        employeeDropdown.setBackground(WHITE);
        employeeDropdown.setFocusable(false);
        employeeDropdown.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1));
        employeeDropdown.setOpaque(true);

        employeeDropdown.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("⌄");
                button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                button.setForeground(new Color(180, 180, 180));
                button.setBackground(WHITE);
                button.setBorder(new EmptyBorder(0, 0, 3, 8));
                button.setFocusable(false);
                button.setContentAreaFilled(false);
                return button;
            }
        });

        add(employeeDropdown);
    }

    private void addProfilePreview() {
        JLabel nameLabel = new JLabel("Name", SwingConstants.RIGHT);
        nameLabel.setBounds(780, 50, 100, 20);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(NAVY);
        add(nameLabel);

        JLabel positionLabel = new JLabel("Position", SwingConstants.RIGHT);
        positionLabel.setBounds(780, 75, 100, 18);
        positionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        positionLabel.setForeground(MUTED_TEXT);
        add(positionLabel);

        CircleAvatar avatar = new CircleAvatar(NAVY);
        avatar.setBounds(887, 40, 56, 56);
        add(avatar);
    }

    private void addMetricCards() {
        MetricCard totalEmployees = new MetricCard("Total Number of Employees");
        totalEmployees.setBounds(57, 157, 279, 137);
        totalEmployees.addLargeNumber("1,001", 17, 50, 130, 64);
        totalEmployees.addInlineLabel("Employees", 158, 72, 100, 25);
        add(totalEmployees);

        MetricCard ongoingQuarter = new MetricCard("On-Going Quarter");
        ongoingQuarter.setBounds(360, 157, 280, 137);
        ongoingQuarter.addLargeNumber("2", 18, 45, 46, 68);
        ongoingQuarter.addInlineLabel("April to June, 2026", 70, 70, 190, 30);
        add(ongoingQuarter);

        MetricCard upcomingQuarter = new MetricCard("Upcoming Quarter");
        upcomingQuarter.setBounds(664, 157, 279, 137);
        upcomingQuarter.addLargeNumber("3", 18, 45, 46, 68);
        upcomingQuarter.addInlineLabel("July to September, 2026", 70, 70, 200, 30);
        add(upcomingQuarter);
    }

    private void addFinancialOverview() {
        JLabel title = new JLabel("Financial Overview");
        title.setBounds(57, 326, 220, 24);
        title.setFont(FONT_BOLD_16);
        title.setForeground(BLACK_TEXT);
        add(title);

        addLegendDot(387, 383, GREEN);

        JLabel revenueLabel = new JLabel("Revenue");
        revenueLabel.setBounds(407, 380, 70, 22);
        revenueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        revenueLabel.setForeground(BLACK_TEXT);
        add(revenueLabel);

        addLegendDot(488, 383, RED);

        JLabel expensesLabel = new JLabel("Total Expenses");
        expensesLabel.setBounds(508, 380, 120, 22);
        expensesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        expensesLabel.setForeground(BLACK_TEXT);
        add(expensesLabel);

        FinancialChart chart = new FinancialChart();
        chart.setBounds(57, 404, 854, 305);
        add(chart);
    }

    private void addLegendDot(int x, int y, Color color) {
        LegendDot dot = new LegendDot(color);
        dot.setBounds(x, y, 14, 14);
        add(dot);
    }

    private static final class MetricCard extends JPanel {

        private MetricCard(String titleText) {
            setLayout(null);
            setBackground(NAVY);
            setOpaque(true);

            JLabel title = new JLabel(titleText);
            title.setBounds(18, 19, 230, 24);
            title.setFont(FONT_SEMIBOLD);
            title.setForeground(WHITE);
            add(title);
        }

        private void addLargeNumber(String value, int x, int y, int width, int height) {
            JLabel label = new JLabel(value);
            label.setBounds(x, y, width, height);
            label.setFont(value.length() > 1 ? FONT_CARD_NUMBER : FONT_QUARTER_NUMBER);
            label.setForeground(WHITE);
            add(label);
        }

        private void addInlineLabel(String text, int x, int y, int width, int height) {
            JLabel label = new JLabel(text);
            label.setBounds(x, y, width, height);
            label.setFont(FONT_BOLD_16);
            label.setForeground(WHITE);
            add(label);
        }
    }

    private static final class CircleAvatar extends JPanel {
        private final Color color;

        private CircleAvatar(Color color) {
            this.color = Objects.requireNonNull(color);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));

            g2.dispose();
        }
    }

    private static final class LegendDot extends JPanel {
        private final Color color;

        private LegendDot(Color color) {
            this.color = Objects.requireNonNull(color);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.fillOval(0, 0, 14, 14);

            g2.dispose();
        }
    }

    private static final class FinancialChart extends JPanel {

        private static final int PLOT_LEFT = 99;
        private static final int PLOT_TOP = 22;
        private static final int PLOT_RIGHT = 854;
        private static final int PLOT_BOTTOM = 274;
        private static final int MAX_VALUE = 1_000_000;

        private static final String[] Y_LABELS = {
                "1,000,000", "800,000", "600,000", "400,000", "200,000", "0"
        };

        private static final String[] X_LABELS = {
                "Jan 2022", "Jul 2022", "Jan 2023", "Jul 2023", "Jan 2024",
                "Jul 2024", "Jan 2025", "Jul 2025", "Jan 2026"
        };

        private static final int[] YEAR_INDEXES = {0, 2, 4, 6, 8};

        private static final int[] REVENUE = {
                100_000, 200_000, 300_000, 400_000, 1_000_000
        };

        private static final int[] EXPENSES = {
                75_000, 100_000, 200_000, 300_000, 400_000
        };

        private FinancialChart() {
            setOpaque(false);
            setBackground(WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            paintGridAndLabels(g2);
            paintSeries(g2, REVENUE, GREEN);
            paintSeries(g2, EXPENSES, RED);

            g2.dispose();
        }

        private void paintGridAndLabels(Graphics2D g2) {
            g2.setFont(FONT_REGULAR);
            FontMetrics metrics = g2.getFontMetrics();
            g2.setStroke(new BasicStroke(1f));

            for (int i = 0; i < Y_LABELS.length; i++) {
                int y = PLOT_TOP + Math.round(i * ((PLOT_BOTTOM - PLOT_TOP) / 5f));

                g2.setColor(SOFT_GRAY);
                g2.drawLine(PLOT_LEFT, y, PLOT_RIGHT, y);

                g2.setColor(BLACK_TEXT);
                int labelWidth = metrics.stringWidth(Y_LABELS[i]);
                g2.drawString(Y_LABELS[i], 89 - labelWidth, y + 5);
            }

            g2.setFont(FONT_REGULAR);
            metrics = g2.getFontMetrics();

            for (int i = 0; i < X_LABELS.length; i++) {
                int x = getXForIndex(i);
                int labelWidth = metrics.stringWidth(X_LABELS[i]);

                g2.setColor(BLACK_TEXT);
                g2.drawString(X_LABELS[i], x - labelWidth / 2, 295);
            }
        }

        private void paintSeries(Graphics2D g2, int[] values, Color color) {
            Point[] points = new Point[values.length];

            for (int i = 0; i < values.length; i++) {
                points[i] = new Point(getXForIndex(YEAR_INDEXES[i]), getYForValue(values[i]));
            }

            g2.setColor(color);
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(createSmoothPath(points));

            for (Point point : points) {
                g2.fillOval(point.x - 7, point.y - 7, 14, 14);
            }
        }

        private int getXForIndex(int index) {
            float step = (PLOT_RIGHT - PLOT_LEFT) / 8f;
            return Math.round(PLOT_LEFT + (index * step));
        }

        private int getYForValue(int value) {
            float ratio = value / (float) MAX_VALUE;
            return Math.round(PLOT_BOTTOM - (ratio * (PLOT_BOTTOM - PLOT_TOP)));
        }

        private Shape createSmoothPath(Point[] points) {
            Path2D.Double path = new Path2D.Double();
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
}

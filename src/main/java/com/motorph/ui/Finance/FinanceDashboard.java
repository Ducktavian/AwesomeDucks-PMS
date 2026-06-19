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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

public final class FinanceDashboard extends JPanel {

    private static final int PAGE_WIDTH = 1023;
    private static final int PAGE_HEIGHT = 800;

    private static final Color NAVY = new Color(2, 19, 98);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(15, 15, 15);
    private static final Color MUTED_TEXT = new Color(145, 145, 145);
    private static final Color LIGHT_BORDER = new Color(232, 232, 232);
    private static final Color DROPDOWN_TEXT = new Color(205, 205, 205);

    private static final Color REVENUE_GREEN = new Color(0, 191, 99);
    private static final Color EXPENSE_RED = new Color(255, 87, 87);

    public FinanceDashboard() {
        setLayout(null);
        setOpaque(true);
        setBackground(WHITE);
        setPreferredSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));

        buildRoleDropdown();
        buildProfileHeader();
        buildSummaryCards();
        buildFinancialOverview();
    }

    private void buildRoleDropdown() {
        RoleDropdown dropdown = new RoleDropdown();
        dropdown.setBounds(57, 107, 108, 36);
        add(dropdown);
    }

    private void buildProfileHeader() {
        JLabel name = createLabel(
                "Name",
                750, 47, 128, 23,
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

    private void buildSummaryCards() {
        JPanel payrollCard = createCard(58, 158, 278, 136);
        payrollCard.add(createLabel(
                "Payroll",
                18, 17, 110, 24,
                textFont(13, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        ));
        payrollCard.add(createLabel(
                "100",
                16, 49, 102, 70,
                headerFont(52, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        ));

        JLabel pending = createLabel(
                "<html>Pending<br>Payroll</html>",
                127, 61, 120, 50,
                textFont(16, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        );
        pending.setVerticalAlignment(SwingConstants.TOP);
        payrollCard.add(pending);
        add(payrollCard);

        JPanel ongoingCard = createCard(361, 158, 278, 136);
        ongoingCard.add(createLabel(
                "On-Going Payroll Period",
                18, 17, 210, 24,
                textFont(13, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        ));
        ongoingCard.add(createLabel(
                "Jun",
                18, 48, 90, 70,
                headerFont(50, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        ));
        ongoingCard.add(createLabel(
                "1 – 15, 2026",
                117, 65, 140, 34,
                textFont(17, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        ));
        add(ongoingCard);

        JPanel upcomingCard = createCard(664, 158, 279, 136);
        upcomingCard.add(createLabel(
                "Upcoming Payroll Period",
                18, 17, 220, 24,
                textFont(13, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        ));
        upcomingCard.add(createLabel(
                "Jun",
                18, 48, 90, 70,
                headerFont(50, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        ));
        upcomingCard.add(createLabel(
                "16 – 30, 2026",
                119, 65, 150, 34,
                textFont(17, Font.BOLD),
                WHITE,
                SwingConstants.LEFT
        ));
        add(upcomingCard);
    }

    private void buildFinancialOverview() {
        JLabel title = createLabel(
                "Financial Overview",
                57, 326, 230, 28,
                textFont(16, Font.BOLD),
                BLACK,
                SwingConstants.LEFT
        );
        add(title);

        FinancialChart chart = new FinancialChart();
        chart.setBounds(57, 375, 856, 340);
        add(chart);
    }

    private JPanel createCard(int x, int y, int width, int height) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, width, height);
        card.setOpaque(true);
        card.setBackground(NAVY);
        return card;
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

    private static final class RoleDropdown extends JComponent {

        private RoleDropdown() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(LIGHT_BORDER);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g2.setFont(textFont(13, Font.PLAIN));
            g2.setColor(DROPDOWN_TEXT);
            g2.drawString("Finance", 10, 24);

            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(DROPDOWN_TEXT);

            int arrowX = 82;
            int arrowY = 15;

            g2.drawLine(arrowX, arrowY, arrowX + 7, arrowY + 7);
            g2.drawLine(arrowX + 14, arrowY, arrowX + 7, arrowY + 7);

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

    private static final class FinancialChart extends JComponent {

        private static final Color GRID = new Color(216, 216, 216);
        private static final Color AXIS = new Color(159, 159, 159);

        private static final int PLOT_LEFT = 99;
        private static final int PLOT_RIGHT = 854;
        private static final int PLOT_TOP = 51;
        private static final int PLOT_BOTTOM = 303;

        private static final int DATA_LEFT = 128;
        private static final int DATA_RIGHT = 825;

        private final String[] xLabels = {
                "Jan 2022", "Jul 2022",
                "Jan 2023", "Jul 2023",
                "Jan 2024", "Jul 2024",
                "Jan 2025", "Jul 2025",
                "Jan 2026"
        };

        private final int[] yValues = {
                1_000_000,
                800_000,
                600_000,
                400_000,
                200_000,
                0
        };

        private final double[] revenue = {
                100_000,
                200_000,
                300_000,
                400_000,
                1_000_000
        };

        private final double[] expenses = {
                80_000,
                100_000,
                200_000,
                300_000,
                400_000
        };

        private FinancialChart() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawLegend(g2);
            drawGridAndAxis(g2);
            drawAxisLabels(g2);
            drawDataLines(g2);

            g2.dispose();
        }

        private void drawLegend(Graphics2D g2) {
            g2.setFont(textFont(13, Font.PLAIN));
            g2.setColor(REVENUE_GREEN);
            g2.fillOval(330, 8, 14, 14);

            g2.setColor(BLACK);
            g2.drawString("Revenue", 350, 20);

            g2.setColor(EXPENSE_RED);
            g2.fillOval(431, 8, 14, 14);

            g2.setColor(BLACK);
            g2.drawString("Total Expenses", 451, 20);
        }

        private void drawGridAndAxis(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1f));

            for (int value : yValues) {
                int y = yFor(value);

                g2.setColor(value == 0 ? AXIS : GRID);
                g2.draw(new Line2D.Double(PLOT_LEFT, y, PLOT_RIGHT, y));
            }
        }

        private void drawAxisLabels(Graphics2D g2) {
            g2.setFont(textFont(14, Font.PLAIN));
            g2.setColor(BLACK);

            for (int value : yValues) {
                int y = yFor(value);
                String label = formatMoney(value);
                drawRightAligned(g2, label, 88, y);
            }

            double step = (DATA_RIGHT - DATA_LEFT) / 8.0;
            int baseline = 326;

            for (int i = 0; i < xLabels.length; i++) {
                int x = (int) Math.round(DATA_LEFT + (step * i));
                drawCentered(g2, xLabels[i], x, baseline);
            }
        }

        private void drawDataLines(Graphics2D g2) {
            int[] xPoints = getDataXPoints();
            int[] revenueY = getDataYPoints(revenue);
            int[] expenseY = getDataYPoints(expenses);

            g2.setStroke(new BasicStroke(5.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.setColor(EXPENSE_RED);
            drawSmoothLine(g2, xPoints, expenseY);

            g2.setColor(REVENUE_GREEN);
            drawSmoothLine(g2, xPoints, revenueY);

            drawMarkers(g2, xPoints, expenseY, EXPENSE_RED);
            drawMarkers(g2, xPoints, revenueY, REVENUE_GREEN);
        }

        private int[] getDataXPoints() {
            int[] points = new int[5];
            double step = (DATA_RIGHT - DATA_LEFT) / 4.0;

            for (int i = 0; i < points.length; i++) {
                points[i] = (int) Math.round(DATA_LEFT + (step * i));
            }

            return points;
        }

        private int[] getDataYPoints(double[] values) {
            int[] points = new int[values.length];

            for (int i = 0; i < values.length; i++) {
                points[i] = yFor(values[i]);
            }

            return points;
        }

        private int yFor(double value) {
            double ratio = value / 1_000_000.0;
            return (int) Math.round(PLOT_BOTTOM - (ratio * (PLOT_BOTTOM - PLOT_TOP)));
        }

        private String formatMoney(int value) {
            if (value == 0) {
                return "0";
            }

            return String.format("%,d", value);
        }

        private void drawRightAligned(Graphics2D g2, String text, int rightX, int centerY) {
            FontMetrics metrics = g2.getFontMetrics();
            int x = rightX - metrics.stringWidth(text);
            int y = centerY + ((metrics.getAscent() - metrics.getDescent()) / 2);
            g2.drawString(text, x, y);
        }

        private void drawCentered(Graphics2D g2, String text, int centerX, int baselineY) {
            FontMetrics metrics = g2.getFontMetrics();
            int x = centerX - (metrics.stringWidth(text) / 2);
            g2.drawString(text, x, baselineY);
        }

        private void drawMarkers(Graphics2D g2, int[] xPoints, int[] yPoints, Color color) {
            g2.setColor(color);

            for (int i = 0; i < xPoints.length; i++) {
                g2.fillOval(xPoints[i] - 7, yPoints[i] - 7, 14, 14);
            }
        }

        private void drawSmoothLine(Graphics2D g2, int[] x, int[] y) {
            Path2D path = new Path2D.Double();
            path.moveTo(x[0], y[0]);

            for (int i = 0; i < x.length - 1; i++) {
                double x0 = i == 0 ? x[i] : x[i - 1];
                double y0 = i == 0 ? y[i] : y[i - 1];

                double x1 = x[i];
                double y1 = y[i];

                double x2 = x[i + 1];
                double y2 = y[i + 1];

                double x3 = i + 2 < x.length ? x[i + 2] : x[i + 1];
                double y3 = i + 2 < y.length ? y[i + 2] : y[i + 1];

                double control1X = x1 + ((x2 - x0) / 6.0);
                double control1Y = y1 + ((y2 - y0) / 6.0);

                double control2X = x2 - ((x3 - x1) / 6.0);
                double control2Y = y2 - ((y3 - y1) / 6.0);

                path.curveTo(control1X, control1Y, control2X, control2Y, x2, y2);
            }

            g2.draw(path);
        }
    }
}
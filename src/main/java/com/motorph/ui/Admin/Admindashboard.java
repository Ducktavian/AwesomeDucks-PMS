package com.motorph.ui.Admin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * DashboardPanel
 * Package : ui.admin
 * File    : ui/admin/DashboardPanel.java
 *
 * Shown when "Dashboard" is active in MainFrame.
 * Contains: sub-top-bar, 3 navy stat cards, financial line chart.
 *
 * FIX: All palette constants are now static so FinancialChart
 *      (a static inner class) can reference them without an
 *      outer-class instance.
 */
public class Admindashboard extends JPanel {

    // ── Palette  (static so the static inner class FinancialChart can use them)
    public static final Color NAVY       = new Color(13,  36,  89);
    public static final Color CONTENT_BG = Color.WHITE;
    public static final Color CARD_BG    = Color.WHITE;
    public static final Color MUTED      = new Color(120, 130, 150);
    public static final Color DIVIDER    = new Color(220, 225, 235);
    public static final Color GREEN_LINE = new Color(40,  180,  80);
    public static final Color RED_LINE   = new Color(220,  70,  60);

    // ─────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────
    public Admindashboard() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(CONTENT_BG);
        body.setBorder(new EmptyBorder(18, 24, 24, 24));

        body.add(buildStatCards());
        body.add(Box.createVerticalStrut(22));
        body.add(buildFinancialOverview());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(CONTENT_BG);
        scroll.getViewport().setBackground(CONTENT_BG);
        add(scroll, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────
    // STAT CARDS  (3 navy cards)
    // ─────────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(navyCard("Total Number of Employees", "1,001", "Employees"));
        row.add(navyCard("On-Going Quarter",           "2",     "April to June, 2026"));
        row.add(navyCard("Upcoming Quarter",           "3",     "July to September, 2026"));
        return row;
    }

    private JPanel navyCard(String title, String value, String sub) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(3, 5, getWidth() - 6, getHeight() - 4, 12, 12);
                g2.setColor(NAVY);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 10, 10);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLbl.setForeground(new Color(180, 200, 235));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel valRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        valRow.setOpaque(false);
        valRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 36));
        valLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subLbl.setForeground(new Color(200, 215, 240));

        valRow.add(valLbl);
        valRow.add(subLbl);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valRow);
        return card;
    }

    // ─────────────────────────────────────────────
    // FINANCIAL OVERVIEW  (card wrapper + chart)
    // ─────────────────────────────────────────────
    private JPanel buildFinancialOverview() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(CARD_BG);
        section.setBorder(new CompoundBorder(
            new LineBorder(DIVIDER, 1, true),
            new EmptyBorder(20, 24, 20, 24)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        JLabel hdr = new JLabel("Financial Overview");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 14));
        hdr.setForeground(NAVY);
        section.add(hdr, BorderLayout.NORTH);
        section.add(new FinancialChart(), BorderLayout.CENTER);
        return section;
    }

    // ─────────────────────────────────────────────
    // STATIC INNER CLASS — custom painted line chart
    // All colour refs use DashboardPanel.CONSTANT (fully qualified)
    // so there is no dependency on an outer-class instance.
    // ─────────────────────────────────────────────
    public static class FinancialChart extends JPanel {

        private static final String[] X_LABELS = {
            "Jan 2022", "Jul 2022", "Jan 2023", "Jul 2023",
            "Jan 2024", "Jul 2024", "Jan 2025", "Jul 2025", "Jan 2026"
        };

        private static final double[] REVENUE = {
             80_000, 120_000, 200_000, 260_000,
            300_000, 390_000, 430_000, 600_000, 1_000_000
        };

        private static final double[] EXPENSES = {
             60_000,  90_000, 120_000, 155_000,
            195_000, 240_000, 280_000, 340_000,   400_000
        };

        private static final double Y_MAX  = 1_100_000;
        private static final double Y_STEP =   200_000;

        public FinancialChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 270));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();
            int left = 72, right = 20, top = 40, bottom = 36;
            int cW = W - left - right;
            int cH = H - top  - bottom;
            int n  = X_LABELS.length;

            drawLegend(g2, left, 12);
            drawGrid(g2, left, top, cW, cH);
            drawXLabels(g2, left, top, cW, cH, n);

            // Revenue (green)
            int[] rx = xPoints(left, cW, n);
            int[] ry = yPoints(top, cH, REVENUE, n);
            drawFill(g2, rx, ry, n, top + cH, Admindashboard.GREEN_LINE);
            drawLine(g2, rx, ry, n, Admindashboard.GREEN_LINE, 2.4f);
            drawDots(g2, rx, ry, n, Admindashboard.GREEN_LINE);

            // Expenses (red)
            int[] ex = xPoints(left, cW, n);
            int[] ey = yPoints(top, cH, EXPENSES, n);
            drawFill(g2, ex, ey, n, top + cH, Admindashboard.RED_LINE);
            drawLine(g2, ex, ey, n, Admindashboard.RED_LINE, 2.4f);
            drawDots(g2, ex, ey, n, Admindashboard.RED_LINE);

            g2.dispose();
        }

        // ── helpers ──────────────────────────────

        private int[] xPoints(int left, int cW, int n) {
            int[] xs = new int[n];
            for (int i = 0; i < n; i++)
                xs[i] = left + (int)((double) i / (n - 1) * cW);
            return xs;
        }

        private int[] yPoints(int top, int cH, double[] data, int n) {
            int[] ys = new int[n];
            for (int i = 0; i < n; i++)
                ys[i] = top + cH - (int)(data[i] / Y_MAX * cH);
            return ys;
        }

        private void drawGrid(Graphics2D g2, int left, int top, int cW, int cH) {
            int steps = (int)(Y_MAX / Y_STEP);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (int i = 0; i <= steps; i++) {
                double val = i * Y_STEP;
                int y = top + cH - (int)(val / Y_MAX * cH);
                g2.setColor(new Color(225, 230, 240));
                g2.drawLine(left, y, left + cW, y);
                g2.setColor(Admindashboard.MUTED);
                String lbl = (i == 0) ? "0" : formatK((long) val);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lbl, left - fm.stringWidth(lbl) - 6, y + 4);
            }
        }

        private void drawXLabels(Graphics2D g2, int left, int top,
                                  int cW, int cH, int n) {
            g2.setColor(Admindashboard.MUTED);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            FontMetrics fm = g2.getFontMetrics();
            for (int i = 0; i < n; i++) {
                int x = left + (int)((double) i / (n - 1) * cW);
                g2.drawString(X_LABELS[i],
                              x - fm.stringWidth(X_LABELS[i]) / 2,
                              top + cH + 18);
            }
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(Admindashboard.GREEN_LINE);
            g2.fillOval(x, y - 5, 9, 9);
            g2.setColor(Admindashboard.MUTED);
            g2.drawString("Revenue", x + 13, y + 4);
            g2.setColor(Admindashboard.RED_LINE);
            g2.fillOval(x + 80, y - 5, 9, 9);
            g2.setColor(Admindashboard.MUTED);
            g2.drawString("Total Expenses", x + 93, y + 4);
        }

        private void drawLine(Graphics2D g2, int[] xs, int[] ys,
                              int n, Color c, float stroke) {
            g2.setColor(c);
            g2.setStroke(new BasicStroke(stroke,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 1; i < n; i++)
                g2.drawLine(xs[i-1], ys[i-1], xs[i], ys[i]);
            g2.setStroke(new BasicStroke(1));
        }

        private void drawFill(Graphics2D g2, int[] xs, int[] ys,
                              int n, int baseY, Color c) {
            Polygon poly = new Polygon();
            for (int i = 0; i < n; i++) poly.addPoint(xs[i], ys[i]);
            poly.addPoint(xs[n - 1], baseY);
            poly.addPoint(xs[0],     baseY);
            Paint saved = g2.getPaint();
            g2.setPaint(new GradientPaint(
                0, ys[n - 1], new Color(c.getRed(), c.getGreen(), c.getBlue(), 40),
                0, baseY,     new Color(c.getRed(), c.getGreen(), c.getBlue(),  5)));
            g2.fill(poly);
            g2.setPaint(saved);
        }

        private void drawDots(Graphics2D g2, int[] xs, int[] ys, int n, Color c) {
            for (int i = 0; i < n; i++) {
                g2.setColor(c);
                g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
                g2.setColor(Color.WHITE);
                g2.fillOval(xs[i] - 2, ys[i] - 2, 4, 4);
            }
        }

        private String formatK(long v) {
            if (v >= 1_000_000) return (v / 1_000_000) + ",000,000";
            if (v >= 1_000)     return (v / 1_000) + ",000";
            return String.valueOf(v);
        }
    }
}
package com.motorph.ui.IT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

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
 * IT role dashboard shown when "Dashboard" is active in MainFrame.
 * Contains: sub-top-bar, 3 navy stat cards, stacked bar chart (Hours Worked).
 */
public class ITDashboard extends JPanel {

    public static final Color NAVY       = new Color(13,  36,  89);
    public static final Color CONTENT_BG = Color.WHITE;
    public static final Color CARD_BG    = Color.WHITE;
    public static final Color MUTED      = new Color(120, 130, 150);
    public static final Color DIVIDER    = new Color(220, 225, 235);
    public static final Color GREEN      = new Color(40,  180,  80);
    public static final Color YELLOW     = new Color(240, 190,  40);
    public static final Color CHART_BORDER = new Color(130, 100, 200);

    public ITDashboard() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(CONTENT_BG);
        body.setBorder(new EmptyBorder(18, 24, 24, 24));

        body.add(buildStatCards());
        body.add(Box.createVerticalStrut(22));
        body.add(buildHoursWorkedSection());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(CONTENT_BG);
        scroll.getViewport().setBackground(CONTENT_BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(navyCard("Total Number of Employees", "1,001", "Employees"));
        row.add(navyCard("Help Center",               "101",   "Pending Tickets"));
        row.add(navyCard("Help Center",               "201",   "Resolved Tickets"));
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

    private JPanel buildHoursWorkedSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(CARD_BG);
        section.setBorder(new CompoundBorder(
            new LineBorder(CHART_BORDER, 1, true),
            new EmptyBorder(20, 24, 20, 24)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        JLabel hdr = new JLabel("Hours Worked");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 14));
        hdr.setForeground(NAVY);
        section.add(hdr, BorderLayout.NORTH);
        section.add(new HoursWorkedChart(), BorderLayout.CENTER);
        return section;
    }

    public static class HoursWorkedChart extends JPanel {

        private static final String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May" };

        private static final int[] RESOLVED = { 28, 20, 24, 22, 30 };
        private static final int[] PENDING  = {  8,  8, 10,  9,  8 };

        private static final int Y_MAX  = 40;
        private static final int Y_STEP = 10;

        public HoursWorkedChart() {
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
            int left = 48, right = 20, top = 40, bottom = 36;
            int cW = W - left - right;
            int cH = H - top  - bottom;
            int n  = MONTHS.length;

            drawLegend(g2, left, 12);
            drawGrid(g2, left, top, cW, cH);
            drawXLabels(g2, left, top, cW, cH, n);
            drawBars(g2, left, top, cW, cH, n);

            g2.dispose();
        }

        private void drawGrid(Graphics2D g2, int left, int top, int cW, int cH) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            int steps = Y_MAX / Y_STEP;
            for (int i = 0; i <= steps; i++) {
                int val = i * Y_STEP;
                int y = top + cH - (int)((double) val / Y_MAX * cH);
                g2.setColor(new Color(225, 230, 240));
                g2.drawLine(left, y, left + cW, y);
                g2.setColor(ITDashboard.MUTED);
                String lbl = String.valueOf(val);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lbl, left - fm.stringWidth(lbl) - 6, y + 4);
            }
        }

        private void drawXLabels(Graphics2D g2, int left, int top,
                                  int cW, int cH, int n) {
            g2.setColor(ITDashboard.MUTED);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            FontMetrics fm = g2.getFontMetrics();
            int barSlot = cW / n;
            for (int i = 0; i < n; i++) {
                int cx = left + i * barSlot + barSlot / 2;
                g2.drawString(MONTHS[i],
                              cx - fm.stringWidth(MONTHS[i]) / 2,
                              top + cH + 18);
            }
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(ITDashboard.GREEN);
            g2.fillOval(x, y - 5, 9, 9);
            g2.setColor(ITDashboard.MUTED);
            g2.drawString("Resolved", x + 13, y + 4);
            g2.setColor(ITDashboard.YELLOW);
            g2.fillOval(x + 80, y - 5, 9, 9);
            g2.setColor(ITDashboard.MUTED);
            g2.drawString("Pending", x + 93, y + 4);
        }

        private void drawBars(Graphics2D g2, int left, int top, int cW, int cH, int n) {
            int barSlot = cW / n;
            int barW    = (int)(barSlot * 0.55);
            int baseY   = top + cH;
            int radius  = 6;

            for (int i = 0; i < n; i++) {
                int cx      = left + i * barSlot + (barSlot - barW) / 2;
                int resH    = (int)((double) RESOLVED[i] / Y_MAX * cH);
                int pendH   = (int)((double) PENDING[i]  / Y_MAX * cH);
                int resTop  = baseY - resH;
                int pendTop = resTop - pendH;

                g2.setColor(ITDashboard.GREEN);
                g2.fillRect(cx, resTop, barW, resH);

                g2.setColor(ITDashboard.YELLOW);
                if (pendH > radius) {
                    g2.fillRect(cx, pendTop + radius, barW, pendH - radius);
                }
                g2.fill(new RoundRectangle2D.Float(
                    cx, pendTop, barW, Math.max(pendH, radius), radius, radius));
            }
        }
    }
}

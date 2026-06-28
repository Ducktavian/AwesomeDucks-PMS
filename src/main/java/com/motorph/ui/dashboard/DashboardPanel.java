package com.motorph.ui.dashboard;

import java.awt.*;
import javax.swing.*;

public class DashboardPanel extends JPanel {

    private static final Color NAVY = new Color(7, 24, 105);

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel(null);
        content.setBackground(Color.WHITE);

        content.add(createRoleDropdown());

        content.add(createCard("Total Number of Employees", "1,001", null, 120, 110));
        content.add(createCard("On-Going Quarter", "Q2", "April to June 2026", 445, 110));
        content.add(createCard("Upcoming Quarter", "Q3", "July to September 2026", 770, 110));

        JLabel title = new JLabel("Financial Overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBounds(120, 280, 250, 30);
        content.add(title);

        content.add(createChartPanel());

        return content;
    }

    private JComboBox<String> createRoleDropdown() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"Admin"});
        combo.setBounds(995, 55, 106, 36);
        combo.setForeground(Color.GRAY);
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
        return combo;
    }

    private JPanel createCard(String title, String value, String subtitle, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 280, 138);
        card.setBackground(NAVY);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLabel.setBounds(18, 15, 240, 25);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 52));
        valueLabel.setBounds(18, 45, 240, 60);

        card.add(titleLabel);
        card.add(valueLabel);

        if (subtitle != null) {
            JLabel subLabel = new JLabel(subtitle);
            subLabel.setForeground(Color.WHITE);
            subLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            subLabel.setBounds(20, 98, 240, 25);
            card.add(subLabel);
        }

        return card;
    }

    private JPanel createChartPanel() {
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int left = 100;
                int top = 85;
                int width = 755;
                int height = 252;

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));

                String[] yLabels = {
                    "1,000,000", "800,000", "600,000",
                    "400,000", "200,000", "0"
                };

                for (int i = 0; i < yLabels.length; i++) {
                    int y = top + (i * height / 5);
                    g2.drawString(yLabels[i], 20, y + 5);

                    g2.setColor(new Color(200, 200, 200));
                    g2.drawLine(left, y, left + width, y);
                    g2.setColor(Color.BLACK);
                }

                String[] xLabels = {
                    "Jan 2022", "Jul 2022", "Jan 2023", "Jul 2023",
                    "Jan 2024", "Jul 2024", "Jan 2025", "Jul 2025", "Jan 2026"
                };

                for (int i = 0; i < xLabels.length; i++) {
                    int x = left + (i * width / 8);
                    g2.drawString(xLabels[i], x - 28, top + height + 22);
                }

                int[] revenueX = {
                    left + 28, left + 205, left + 380,
                    left + 552, left + 725
                };

                int[] revenueY = {
                    top + 226, top + 202, top + 176,
                    top + 151, top
                };

                int[] expenseX = revenueX;

                int[] expenseY = {
                    top + 232, top + 226, top + 202,
                    top + 176, top + 151
                };

                drawLine(g2, expenseX, expenseY, new Color(150, 150, 150));
                drawLine(g2, revenueX, revenueY, NAVY);

                drawLegend(g2, left + 230, 50);
            }
        };

        chart.setBackground(Color.WHITE);
        chart.setBounds(120, 320, 900, 380);
        return chart;
    }

    private void drawLine(Graphics2D g2, int[] x, int[] y, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 0; i < x.length - 1; i++) {
            g2.drawLine(x[i], y[i], x[i + 1], y[i + 1]);
        }

        for (int i = 0; i < x.length; i++) {
            g2.fillOval(x[i] - 7, y[i] - 7, 14, 14);
        }
    }

    private void drawLegend(Graphics2D g2, int x, int y) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));

        g2.setColor(NAVY);
        g2.fillOval(x, y, 14, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Revenue", x + 22, y + 12);

        g2.setColor(new Color(150, 150, 150));
        g2.fillOval(x + 102, y, 14, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Total Expenses", x + 124, y + 12);
    }
}
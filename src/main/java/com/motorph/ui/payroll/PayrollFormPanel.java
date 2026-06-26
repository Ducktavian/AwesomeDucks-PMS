package com.motorph.ui.payroll;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.*;

public class PayrollFormPanel extends JPanel {

    private static final Color NAVY = new Color(5, 24, 108);
    private static final Color BG = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(150, 150, 150);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final Color TEXT_MUTED = new Color(120, 120, 120);
    private static final String FONT = "Segoe UI";

    private final Runnable onBack;

    public PayrollFormPanel(Runnable onBack) {
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(createTopBar(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 80));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(190, 190, 190)));

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        profile.setOpaque(false);
        profile.setBorder(new EmptyBorder(0, 0, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("<html><u>Name</u></html>");
        name.setForeground(NAVY);
        name.setFont(new Font(FONT, Font.BOLD, 16));
        name.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel position = new JLabel("Position");
        position.setForeground(Color.GRAY);
        position.setFont(new Font(FONT, Font.PLAIN, 13));
        position.setAlignmentX(Component.RIGHT_ALIGNMENT);

        textPanel.add(name);
        textPanel.add(position);

        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(47, 47));
        avatar.setIcon(new CircleIcon(NAVY, 47));

        profile.add(textPanel);
        profile.add(avatar);

        topBar.add(profile, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createMainPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(52, 64, 40, 78));

        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(new Font(FONT, Font.PLAIN, 16));
        back.setForeground(new Color(80, 80, 80));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onBack != null) {
                    onBack.run();
                }
            }
        });

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setOpaque(false);
        backRow.add(back);

        outer.add(backRow, BorderLayout.NORTH);
        outer.add(createFormWrapper(), BorderLayout.CENTER);

        return outer;
    }

    private JPanel createFormWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(22, 0, 0, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;

        JPanel leftColumn = createLeftColumn();
        JPanel rightColumn = createRightColumn();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 44);
        form.add(leftColumn, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 44, 0, 0);
        form.add(rightColumn, gbc);

        wrapper.add(form, BorderLayout.NORTH);
        wrapper.add(createButtonRow(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createLeftColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        int row = 0;

        addFormRow(col, row++, "Employee Name:", createTextField());
        addFormRow(col, row++, "Employee ID:", createTextField());

        addSpacer(col, row++, 16);
        addSectionTitle(col, row++, "Earnings");

        addFormRow(col, row++, "Basic Salary", createTextField());
        addFormRow(col, row++, "Hours Worked", createTextField());
        addFormRow(col, row++, "Hourly Rate", createTextField());
        addFormRow(col, row++, "Overtime", createTextField());
        addFormRow(col, row++, "Holiday", createTextField());

        addSpacer(col, row++, 16);
        addSectionTitle(col, row++, "Benefits");

        addFormRow(col, row++, "Rice Subsidy", createTextField());
        addFormRow(col, row++, "Phone Allowance", createTextField());
        addFormRow(col, row++, "Clothing Allowance", createTextField());
        addFormRow(col, row++, "Bonus Type", createBonusRow());

        addSpacer(col, row++, 12);
        addEmphasisFormRow(col, row++, "Gross Pay", createTextField());

        return col;
    }

    private JPanel createRightColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        int row = 0;

        addFormRow(col, row++, "Payroll Date:", createTextField());
        addFormRow(col, row++, "Payroll Period:", createTextField());

        addSpacer(col, row++, 16);
        addSectionTitle(col, row++, "Deductions");

        addFormRow(col, row++, "Withholding Tax", createTextField());
        addFormRow(col, row++, "SSS", createTextField());
        addFormRow(col, row++, "PhilHealth", createTextField());
        addFormRow(col, row++, "PAG-IBIG", createTextField());

        addSpacer(col, row++, 22);
        addEmphasisFormRow(col, row++, "Total Deductions", createTextField());

        addSpacer(col, row++, 16);
        addEmphasisFormRow(col, row++, "Net Pay", createTextField());

        return col;
    }

    private void addSectionTitle(JPanel parent, int row, String title) {
        GridBagConstraints gbc = baseGbc(row);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel label = new JLabel(title);
        label.setFont(new Font(FONT, Font.BOLD, 18));
        label.setForeground(TEXT_DARK);

        parent.add(label, gbc);
    }

    private void addFormRow(JPanel parent, int row, String labelText, JComponent field) {
        GridBagConstraints labelGbc = baseGbc(row);
        labelGbc.gridx = 0;
        labelGbc.weightx = 0;
        labelGbc.insets = new Insets(0, 0, 10, 24);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(165, 32));
        label.setFont(new Font(FONT, labelText.endsWith(":") ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(TEXT_DARK);
        label.setVerticalAlignment(SwingConstants.CENTER);

        parent.add(label, labelGbc);

        GridBagConstraints fieldGbc = baseGbc(row);
        fieldGbc.gridx = 1;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets = new Insets(0, 0, 10, 0);
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;

        parent.add(field, fieldGbc);
    }

    private void addSpacer(JPanel parent, int row, int height) {
        GridBagConstraints gbc = baseGbc(row);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(1, height));

        parent.add(spacer, gbc);
    }

    private GridBagConstraints baseGbc(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(320, 32));
        field.setMinimumSize(new Dimension(220, 32));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(NAVY);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(8, FIELD_BORDER),
                new EmptyBorder(3, 10, 3, 10)
        ));
        return field;
    }

    private JPanel createBonusRow() {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JComboBox<String> bonusType = new JComboBox<>(new String[]{
            "Bonus Type", "Performance", "Holiday", "13th Month", "Other"
        });
        bonusType.setFont(new Font(FONT, Font.PLAIN, 12));
        bonusType.setPreferredSize(new Dimension(125, 32));
        bonusType.setBackground(Color.WHITE);
        bonusType.setFocusable(false);

        JTextField amount = createTextField();

        row.add(bonusType, BorderLayout.WEST);
        row.add(amount, BorderLayout.CENTER);

        return row;
    }

    private JPanel createButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 0, 0));

        JButton savePdf = navyButton("⇩  Save PDF");
        JButton submit = navyButton("Submit");

        row.add(savePdf);
        row.add(submit);

        return row;
    }

    private JButton navyButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                if (getModel().isPressed()) {
                    g2.setColor(new Color(3, 15, 78));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(18, 42, 135));
                } else {
                    g2.setColor(NAVY);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font(FONT, Font.PLAIN, 13));

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(150, 42));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private static class CircleIcon implements Icon {
        private final Color color;
        private final int size;

        CircleIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.fillOval(x, y, size, size);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawRoundRect(
                    x,
                    y,
                    width - 1,
                    height - 1,
                    radius,
                    radius
            );
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }
    
    private void addEmphasisFormRow(JPanel parent, int row, String labelText, JComponent field) {
        GridBagConstraints labelGbc = baseGbc(row);
        labelGbc.gridx = 0;
        labelGbc.weightx = 0;
        labelGbc.insets = new Insets(0, 0, 10, 24);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(165, 32));
        label.setFont(new Font(FONT, Font.BOLD, 16));
        label.setForeground(TEXT_DARK);
        label.setVerticalAlignment(SwingConstants.CENTER);

        parent.add(label, labelGbc);

        GridBagConstraints fieldGbc = baseGbc(row);
        fieldGbc.gridx = 1;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets = new Insets(0, 0, 10, 0);
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;

        parent.add(field, fieldGbc);
    }
}
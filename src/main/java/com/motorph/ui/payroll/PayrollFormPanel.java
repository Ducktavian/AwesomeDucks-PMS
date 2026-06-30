package com.motorph.ui.payroll;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

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

        add(createMainPanel(), BorderLayout.CENTER);
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
        wrapper.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 1.0;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.anchor = GridBagConstraints.NORTHWEST;
        leftGbc.insets = new Insets(0, 0, 0, 55);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1.0;
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.anchor = GridBagConstraints.NORTHWEST;
        rightGbc.insets = new Insets(0, 0, 0, 0);

        form.add(createLeftColumn(), leftGbc);
        form.add(createRightColumn(), rightGbc);

        wrapper.add(form, BorderLayout.CENTER);
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
        
        GridBagConstraints pushDown = new GridBagConstraints();
        pushDown.gridx = 0;
        pushDown.gridy = row;
        pushDown.gridwidth = 2;
        pushDown.weighty = 1.0;
        pushDown.fill = GridBagConstraints.VERTICAL;
        col.add(Box.createVerticalGlue(), pushDown);

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
        
        GridBagConstraints pushDown = new GridBagConstraints();
        pushDown.gridx = 0;
        pushDown.gridy = row;
        pushDown.gridwidth = 2;
        pushDown.weighty = 1.0;
        pushDown.fill = GridBagConstraints.VERTICAL;
        col.add(Box.createVerticalGlue(), pushDown);

        return col;
    }     

    private void addSectionTitle(JPanel parent, int row, String title) {
        GridBagConstraints gbc = baseGbc(row);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel label = new JLabel(title);
        label.setFont(new Font(FONT, Font.BOLD, 15));
        label.setForeground(TEXT_DARK);

        parent.add(label, gbc);
    }

    private void addFormRow(JPanel parent, int row, String labelText, JComponent field) {
        GridBagConstraints labelGbc = baseGbc(row);
        labelGbc.gridx = 0;
        labelGbc.weightx = 0;
        labelGbc.insets = new Insets(0, 0, 10, 24);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(145, 26));
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
        gbc.weighty = 0;
        return gbc;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.PLAIN, 12));
        field.setPreferredSize(new Dimension(180, 26));
        field.setMinimumSize(new Dimension(120, 26));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(NAVY);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(7, FIELD_BORDER),
                new EmptyBorder(2, 8, 2, 8)
        ));
        return field;
    }

    private JPanel createBonusRow() {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JComboBox<String> bonusType = new JComboBox<>(new String[]{
            "Bonus Type","Performance", "13th Month", "Other"
        });
        bonusType.setFont(new Font(FONT, Font.PLAIN, 11));
        bonusType.setPreferredSize(new Dimension(105, 26));
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

        button.setPreferredSize(new Dimension(125, 36));
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
        labelGbc.insets = new Insets(0, 0, 7, 18);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(145, 26));
        label.setFont(new Font(FONT, Font.BOLD, 16));
        label.setForeground(TEXT_DARK);
        label.setVerticalAlignment(SwingConstants.CENTER);

        parent.add(label, labelGbc);

        GridBagConstraints fieldGbc = baseGbc(row);
        fieldGbc.gridx = 1;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets = new Insets(0, 0, 7, 0);
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;

        parent.add(field, fieldGbc);
    }
    
    public PayrollFormPanel(Runnable onBack, boolean viewOnly) {
        this(onBack);

        if (viewOnly) {
            hideSubmitButton();
            setFieldsEditable(false);
        }
    }
    
    private void hideSubmitButton() {
        hideButtonRecursive(this, "Submit");
    }

    private void hideButtonRecursive(Component component, String buttonText) {
        if (component instanceof JButton) {
            JButton button = (JButton) component;

            if (button.getText() != null && button.getText().equalsIgnoreCase(buttonText)) {
                button.setVisible(false);
            }
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                hideButtonRecursive(child, buttonText);
            }
        }
    }

    private void setFieldsEditable(boolean editable) {
        setEditableRecursive(this, editable);
    }

    private void setEditableRecursive(Component component, boolean editable) {
        if (component instanceof JTextField) {
            ((JTextField) component).setEditable(editable);
        } else if (component instanceof JTextArea) {
            ((JTextArea) component).setEditable(editable);
        } else if (component instanceof JComboBox) {
            component.setEnabled(editable);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setEditableRecursive(child, editable);
            }
        }
    }
}
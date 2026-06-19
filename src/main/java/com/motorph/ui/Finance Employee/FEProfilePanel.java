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
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FEProfilePanel extends JPanel {

    private static final Color PAGE_BACKGROUND = new Color(245, 245, 245);
    private static final Color NAVY = new Color(7, 24, 104);
    private static final Color TEXT_BLACK = new Color(10, 10, 10);
    private static final Color LABEL_COLOR = new Color(45, 45, 45);
    private static final Color BORDER_COLOR = new Color(108, 108, 108);
    private static final Color PLACEHOLDER_COLOR = new Color(190, 190, 190);
    private static final Color FIELD_BACKGROUND = Color.WHITE;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 52);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Open Sans", Font.PLAIN, 12);
    private static final Font FIELD_FONT = new Font("Open Sans", Font.PLAIN, 12);
    private static final Font PROFILE_NAME_FONT = new Font("Open Sans", Font.BOLD, 18);
    private static final Font PROFILE_POSITION_FONT = new Font("Open Sans", Font.PLAIN, 14);

    public FEProfilePanel() {
        setLayout(null);
        setBackground(PAGE_BACKGROUND);
        setPreferredSize(new Dimension(1022, 800));

        initComponents();
    }

    private void initComponents() {
        addPageTitle();
        addTopRightProfile();
        addSections();
    }

    private void addPageTitle() {
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_BLACK);
        titleLabel.setBounds(77, 126, 340, 62);
        add(titleLabel);
    }

    private void addTopRightProfile() {
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(PROFILE_NAME_FONT);
        nameLabel.setForeground(NAVY);
        nameLabel.setBounds(822, 48, 70, 22);
        add(nameLabel);

        JLabel positionLabel = new JLabel("Position");
        positionLabel.setFont(PROFILE_POSITION_FONT);
        positionLabel.setForeground(new Color(140, 140, 140));
        positionLabel.setBounds(818, 73, 80, 20);
        add(positionLabel);

        AvatarCircle avatarCircle = new AvatarCircle();
        avatarCircle.setBounds(885, 40, 56, 56);
        add(avatarCircle);
    }

    private void addSections() {
        SectionPanel basicInformation = new SectionPanel(
                "Basic Information",
                new FieldDefinition[]{
                        new FieldDefinition("Employee ID"),
                        new FieldDefinition("First Name"),
                        new FieldDefinition("Last Name"),
                        new FieldDefinition("Department"),
                        new FieldDefinition("Position"),
                        new FieldDefinition("Immediate Supervisor"),
                        new FieldDefinition("Role"),
                        new FieldDefinition("Status")
                }
        );
        basicInformation.setBounds(78, 242, 174, 410);
        add(basicInformation);

        SectionPanel personalDetail = new SectionPanel(
                "Personal Detail",
                new FieldDefinition[]{
                        new FieldDefinition("Gender"),
                        new FieldDefinition("Birthdate", "MM-DD-YYYY", JTextField.CENTER),
                        new FieldDefinition("Cellphone No."),
                        new FieldDefinition("Telephone No."),
                        new FieldDefinition("E-mail"),
                        new FieldDefinition("Address")
                }
        );
        personalDetail.setBounds(304, 242, 174, 312);
        add(personalDetail);

        SectionPanel governmentId = new SectionPanel(
                "Government ID",
                new FieldDefinition[]{
                        new FieldDefinition("SSS No.", "XX-XXXXXXX-Y", JTextField.CENTER),
                        new FieldDefinition("PhilHealth No.", "XX-XXXXXXXXX-X", JTextField.CENTER),
                        new FieldDefinition("PAG-IBIG No.", "XXXX-XXXX-XXXX", JTextField.CENTER),
                        new FieldDefinition("TIN", "XXX-XXX-XXX-XXX", JTextField.CENTER)
                }
        );
        governmentId.setBounds(530, 242, 174, 214);
        add(governmentId);

        SectionPanel compensation = new SectionPanel(
                "Compensation",
                new FieldDefinition[]{
                        new FieldDefinition("Basic Salary"),
                        new FieldDefinition("Gross Semi-Monthly Rate"),
                        new FieldDefinition("Hourly Rate"),
                        new FieldDefinition("Rice Subsidy"),
                        new FieldDefinition("Phone Allowance"),
                        new FieldDefinition("Clothing Allowance")
                }
        );
        compensation.setBounds(756, 242, 174, 312);
        add(compensation);
    }

    private static class SectionPanel extends JPanel {

        private static final int FIELD_WIDTH = 174;
        private static final int FIELD_HEIGHT = 25;
        private static final int FIRST_LABEL_Y = 33;
        private static final int ROW_GAP = 50;

        public SectionPanel(String title, FieldDefinition[] fields) {
            setLayout(null);
            setOpaque(false);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(SECTION_FONT);
            titleLabel.setForeground(TEXT_BLACK);
            titleLabel.setBounds(0, 0, 180, 25);
            add(titleLabel);

            int currentY = FIRST_LABEL_Y;

            for (FieldDefinition field : fields) {
                JLabel label = new JLabel(field.label);
                label.setFont(LABEL_FONT);
                label.setForeground(LABEL_COLOR);
                label.setBounds(0, currentY, FIELD_WIDTH, 15);
                add(label);

                RoundedHintTextField textField = new RoundedHintTextField(field.placeholder, field.alignment);
                textField.setFont(FIELD_FONT);
                textField.setBounds(0, currentY + 15, FIELD_WIDTH, FIELD_HEIGHT);
                add(textField);

                currentY += ROW_GAP;
            }
        }
    }

    private static class FieldDefinition {
        private final String label;
        private final String placeholder;
        private final int alignment;

        public FieldDefinition(String label) {
            this(label, "", JTextField.LEFT);
        }

        public FieldDefinition(String label, String placeholder, int alignment) {
            this.label = label;
            this.placeholder = placeholder;
            this.alignment = alignment;
        }
    }

    private static class RoundedHintTextField extends JTextField {

        private final String hint;

        public RoundedHintTextField(String hint, int alignment) {
            this.hint = hint == null ? "" : hint;
            setHorizontalAlignment(alignment);
            setOpaque(false);
            setBorder(new EmptyBorder(2, 10, 2, 10));
            setBackground(FIELD_BACKGROUND);
            setForeground(TEXT_BLACK);
            setCaretColor(TEXT_BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(FIELD_BACKGROUND);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty() && !hint.isEmpty()) {
                Graphics2D g2Hint = (Graphics2D) g.create();
                g2Hint.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2Hint.setColor(PLACEHOLDER_COLOR);
                g2Hint.setFont(getFont());

                FontMetrics fm = g2Hint.getFontMetrics();
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 1;

                int textX;
                if (getHorizontalAlignment() == JTextField.CENTER) {
                    textX = (getWidth() - fm.stringWidth(hint)) / 2;
                } else if (getHorizontalAlignment() == JTextField.RIGHT) {
                    textX = getWidth() - fm.stringWidth(hint) - 10;
                } else {
                    textX = 10;
                }

                g2Hint.drawString(hint, textX, textY);
                g2Hint.dispose();
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g2.dispose();
        }
    }

    private static class AvatarCircle extends JComponent {
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
}
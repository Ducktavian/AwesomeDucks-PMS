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
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class HREProfile extends JPanel {

    private static final int PANEL_WIDTH = 1023;
    private static final int PANEL_HEIGHT = 800;

    private static final Color BACKGROUND = Color.WHITE;
    private static final Color NAVY = new Color(7, 21, 104);
    private static final Color TEXT_NAVY = new Color(5, 9, 72);
    private static final Color BORDER_GRAY = new Color(85, 85, 85);
    private static final Color PLACEHOLDER_GRAY = new Color(205, 205, 205);
    private static final Color MUTED_GRAY = new Color(150, 150, 150);

    private static final String HEADER_FONT = "Segoe UI";
    private static final String TEXT_FONT = "Open Sans";

    private static final int FIELD_WIDTH = 174;
    private static final int FIELD_HEIGHT = 25;

    public HREProfile() {
        setLayout(null);
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        add(createProfileArea());
        add(createTitle());

        add(createBasicInformationSection());
        add(createPersonalDetailSection());
        add(createGovernmentIdSection());
        add(createCompensationSection());
    }

    private JLabel createTitle() {
        JLabel title = new JLabel("My Profile");
        title.setFont(headerFont(Font.BOLD, 52));
        title.setForeground(Color.BLACK);
        title.setBounds(78, 125, 330, 65);
        return title;
    }

    private JComponent createProfileArea() {
        JPanel profile = new JPanel(null);
        profile.setOpaque(false);
        profile.setBounds(820, 40, 125, 60);

        JLabel name = new JLabel("Name");
        name.setFont(headerFont(Font.BOLD, 18));
        name.setForeground(TEXT_NAVY);
        name.setBounds(0, 5, 65, 24);
        profile.add(name);

        JLabel position = new JLabel("Position");
        position.setFont(textFont(Font.PLAIN, 16));
        position.setForeground(MUTED_GRAY);
        position.setBounds(0, 29, 75, 22);
        profile.add(position);

        CircleAvatar avatar = new CircleAvatar();
        avatar.setBounds(66, 0, 57, 57);
        profile.add(avatar);

        return profile;
    }

    private JPanel createBasicInformationSection() {
        JPanel section = createSection(78, 239, 190, 430);

        addSectionTitle(section, "Basic Information", 0, 0);

        int y = 32;
        addField(section, "Employee ID", "", y);
        addField(section, "First Name", "", y += 50);
        addField(section, "Last Name", "", y += 50);
        addField(section, "Department", "", y += 50);
        addField(section, "Position", "", y += 50);
        addField(section, "Immediate Supervisor", "", y += 50);
        addField(section, "Role", "", y += 50);
        addField(section, "Status", "", y += 50);

        return section;
    }

    private JPanel createPersonalDetailSection() {
        JPanel section = createSection(305, 239, 190, 330);

        addSectionTitle(section, "Personal Detail", 0, 0);

        int y = 32;
        addField(section, "Gender", "", y);
        addField(section, "Birthdate", "MM-DD-YYYY", y += 50);
        addField(section, "Cellphone No.", "", y += 50);
        addField(section, "Telephone No.", "", y += 50);
        addField(section, "E-mail", "", y += 50);
        addField(section, "Address", "", y += 50);

        return section;
    }

    private JPanel createGovernmentIdSection() {
        JPanel section = createSection(531, 239, 190, 230);

        addSectionTitle(section, "Government ID", 0, 0);

        int y = 32;
        addField(section, "SSS No.", "XX-XXXXXXX-Y", y);
        addField(section, "PhilHealth No.", "XX-XXXXXXXXX-X", y += 50);
        addField(section, "PAG-IBIG No.", "XXXX-XXXX-XXXX", y += 50);
        addField(section, "TIN", "XXX-XXX-XXX-XXX", y += 50);

        return section;
    }

    private JPanel createCompensationSection() {
        JPanel section = createSection(757, 239, 190, 330);

        addSectionTitle(section, "Compensation", 0, 0);

        int y = 32;
        addField(section, "Basic Salary", "", y);
        addField(section, "Gross Semi-Monthly Rate", "", y += 50);
        addField(section, "Hourly Rate", "", y += 50);
        addField(section, "Rice Subsidy", "", y += 50);
        addField(section, "Phone Allowance", "", y += 50);
        addField(section, "Clothing Allowance", "", y += 50);

        return section;
    }

    private JPanel createSection(int x, int y, int width, int height) {
        JPanel section = new JPanel(null);
        section.setOpaque(false);
        section.setBounds(x, y, width, height);
        return section;
    }

    private void addSectionTitle(JPanel parent, String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(textFont(Font.BOLD, 19));
        label.setForeground(Color.BLACK);
        label.setBounds(x, y, 190, 25);
        parent.add(label);
    }

    private void addField(JPanel parent, String labelText, String placeholder, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(textFont(Font.PLAIN, 11));
        label.setForeground(Color.BLACK);
        label.setBounds(0, y, 180, 14);
        parent.add(label);

        PlaceholderTextField field = new PlaceholderTextField(placeholder);
        field.setBounds(0, y + 16, FIELD_WIDTH, FIELD_HEIGHT);
        parent.add(field);
    }

    private Font headerFont(int style, int size) {
        return new Font(HEADER_FONT, style, size);
    }

    private Font textFont(int style, int size) {
        return new Font(TEXT_FONT, style, size);
    }

    private static class CircleAvatar extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(NAVY);
            g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));

            g2.dispose();
        }
    }

    private static class PlaceholderTextField extends JTextField {

        private final String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;

            setFont(new Font(TEXT_FONT, Font.PLAIN, 11));
            setForeground(Color.BLACK);
            setBackground(Color.WHITE);
            setCaretColor(Color.BLACK);
            setBorder(new LineBorder(BORDER_GRAY, 1, true));
            setMargin(new Insets(0, 6, 0, 6));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setFont(getFont());
                g2.setColor(PLACEHOLDER_GRAY);

                FontMetrics metrics = g2.getFontMetrics();
                int textWidth = metrics.stringWidth(placeholder);
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                g2.drawString(placeholder, x, y);
                g2.dispose();
            }
        }
    }
}
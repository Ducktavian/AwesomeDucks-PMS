/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Attendance;

/**
 *
 * @author 
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;

public class SubmitAttendance {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH - Attendance Submission");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(new AttendanceSubmissionScreen());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

final class AttendanceSubmissionScreen extends JPanel {
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 800;

    AttendanceSubmissionScreen() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(null);
        setBackground(Color.WHITE);

        addAt(new SidebarPanel(), 0, 0, 257, 800);
        addAt(new ProfileCard(), 1077, 63, 123, 58);

        JLabel back = new JLabel("<html><u>Back</u></html>");
        back.setFont(UIStyle.textFont(Font.PLAIN, 13));
        back.setForeground(UIStyle.LINK_GRAY);
        addAt(back, 320, 161, 60, 25);

        addAt(new FieldLabel("Employee ID"), 320, 214, 150, 22);
        addAt(new FormInput("", FieldIconType.NONE), 321, 242, 403, 57);

        addAt(new FieldLabel("Date"), 320, 326, 150, 22);
        addAt(new FormInput("mm/dd/yyyy", FieldIconType.CALENDAR), 321, 354, 403, 57);

        addAt(new FieldLabel("Time In"), 796, 214, 150, 22);
        addAt(new FormInput("5:00 PM", FieldIconType.CLOCK), 797, 243, 403, 57);

        addAt(new FieldLabel("Time Out"), 796, 326, 150, 22);
        addAt(new FormInput("9:00 PM", FieldIconType.CLOCK), 797, 354, 403, 57);

        addAt(new SubmitButton("Submit"), 1073, 656, 127, 44);
    }

    private void addAt(Component component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        add(component);
    }
}

final class SidebarPanel extends JPanel {
    SidebarPanel() {
        setLayout(null);
        setBackground(UIStyle.NAVY);

        JLabel brand = new JLabel("MotorPH");
        brand.setForeground(Color.WHITE);
        brand.setFont(UIStyle.headerFont(Font.BOLD));
        brand.setBounds(48, 61, 150, 40);
        add(brand);

        addAt(new NavigationItem(NavIcon.DASHBOARD, "Dashboard", false, 18), 48, 151, 170, 36);
        addAt(new NavigationItem(NavIcon.EMPLOYEES, "Employees", false, 18), 48, 209, 170, 36);
        addAt(new NavigationItem(NavIcon.PAYROLL, "Payroll", false, 18), 48, 250, 170, 36);
        addAt(new NavigationItem(NavIcon.REQUESTS, "Requests", true, 18), 48, 291, 170, 36);
        addAt(new NavigationItem(NavIcon.ATTENDANCE, "Attendance", false, 18), 48, 333, 170, 36);

        addAt(new NavigationItem(NavIcon.HELP, "Help Center", false, 15), 49, 654, 170, 32);
        addAt(new NavigationItem(NavIcon.LOGOUT, "Log Out", false, 15), 50, 696, 170, 32);
    }

    private void addAt(Component component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        add(component);
    }
}

final class NavigationItem extends JComponent {
    private final NavIcon icon;
    private final String label;
    private final boolean selected;
    private final int fontSize;

    NavigationItem(NavIcon icon, String label, boolean selected, int fontSize) {
        this.icon = icon;
        this.label = label;
        this.selected = selected;
        this.fontSize = fontSize;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableQuality(g);

        new SidebarVectorIcon(icon, Color.WHITE, 22, 22)
                .paintIcon(this, g, 0, (getHeight() - 22) / 2);

        g.setColor(Color.WHITE);
        g.setFont(UIStyle.textFont(selected ? Font.BOLD : Font.PLAIN, fontSize));

        FontMetrics metrics = g.getFontMetrics();
        int baseline = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(label, 47, baseline);

        g.dispose();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Payroll;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FinancePayrollCalculator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinancePayrollCalculatorFrame frame = new FinancePayrollCalculatorFrame();
            frame.setVisible(true);
        });
    }
}

final class FinancePayrollCalculatorFrame extends JFrame {
    public FinancePayrollCalculatorFrame() {
        setTitle("MotorPH - Finance Payroll");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        FinancePayrollCalculatorPanel panel = new FinancePayrollCalculatorPanel();
        setContentPane(panel);

        pack();
        setLocationRelativeTo(null);
    }
}

final class FinancePayrollCalculatorPanel extends JPanel {
    public FinancePayrollCalculatorPanel() {
        setPreferredSize(new Dimension(1280, 800));
        setLayout(null);
        setBackground(Color.WHITE);

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, 256, 800);
        add(sidebar);

        ProfilePanel profilePanel = new ProfilePanel();
        profilePanel.setBounds(1070, 61, 135, 62);
        add(profilePanel);

        JLabel backLabel = new JLabel("<html><u>Back</u></html>");
        backLabel.setFont(UIStyle.PARAGRAPH_FONT);
        backLabel.setForeground(UIStyle.BACK_TEXT);
        backLabel.setBounds(320, 148, 70, 25);
        add(backLabel);

        DisabledSavePdfButton savePdfButton = new DisabledSavePdfButton();
        savePdfButton.setBounds(1100, 141, 100, 35);
        add(savePdfButton);

        SalaryCalculatorPanel calculatorPanel = new SalaryCalculatorPanel();
        calculatorPanel.setBounds(320, 186, 880, 484);
        add(calculatorPanel);

        DarkButton submitButton = new DarkButton("Submit", null);
        submitButton.setBounds(1073, 699, 127, 45);
        add(submitButton);
    }
}

final class SidebarPanel extends JPanel {
    public SidebarPanel() {
        setLayout(null);
        setBackground(UIStyle.NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setForeground(Color.WHITE);
        logo.setFont(UIStyle.LOGO_FONT);
        logo.setBounds(48, 61, 160, 38);
        add(logo);

        addNavItem("Dashboard", UiIcon.DASHBOARD, false, 48, 154);
        addNavItem("Employees", UiIcon.EMPLOYEES, false, 48, 210);
        addNavItem("Payroll", UiIcon.PAYROLL, false, 48, 250);
        addNavItem("Requests", UiIcon.REQUESTS, true, 48, 294);
        addNavItem("Attendance", UiIcon.ATTENDANCE, false, 48, 339);

        addNavItem("Help Center", UiIcon.HELP, false, 48, 660);
        addNavItem("Log Out", UiIcon.LOGOUT, false, 48, 702);
    }

    private void addNavItem(String text, UiIcon icon, boolean active, int x, int y) {
        NavItem item = new NavItem(text, icon, active);
        item.setBounds(x, y, 175, 30);
        add(item);
    }
}

final class NavItem extends JComponent {
    private final String text;
    private final UiIcon icon;
    private final boolean active;

    public NavItem(String text, UiIcon icon, boolean active) {
        this.text = text;
        this.icon = icon;
        this.active = active;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        VectorIcon.draw(g, icon, 0, 4, 22, Color.WHITE);

        g.setColor(Color.WHITE);
        g.setFont(active ? UIStyle.NAV_ACTIVE_FONT : UIStyle.NAV_FONT);
        g.drawString(text, 48, 20);

        g.dispose();
    }
}

final class ProfilePanel extends JComponent {
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(UIStyle.NAVY);
        g.setFont(UIStyle.PROFILE_NAME_FONT);
        g.drawString("Name", 12, 26);

        g.setColor(UIStyle.PROFILE_POSITION_GRAY);
        g.setFont(UIStyle.PROFILE_POSITION_FONT);
        g.drawString("Position", 8, 48);

        g.setColor(UIStyle.NAVY);
        g.fillOval(74, 2, 56, 56);

        g.dispose();
    }
}

final class SalaryCalculatorPanel extends JPanel {
    public SalaryCalculatorPanel() {
        setLayout(null);
        setOpaque(false);

        JLabel title = createLabel("Salary Calculator", UIStyle.HEADING_2_FONT, Color.BLACK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(0, 19, 880, 30);
        add(title);

        addLabel("Employee Name:", 44, 55, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 167, 55, 199, 21);

        addLabel("Employee ID:", 44, 82, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 167, 82, 199, 21);

        addLabel("Payroll Date:", 512, 52, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        InputField payrollDate = new InputField("MM-DD-YYYY");
        add(payrollDate, 635, 52, 199, 21);

        addLabel("Payroll Period:", 512, 79, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new DropdownField(), 635, 79, 199, 22);

        JLabel earnings = createLabel("Earnings", UIStyle.SECTION_FONT, Color.BLACK);
        earnings.setHorizontalAlignment(SwingConstants.CENTER);
        earnings.setBounds(118, 118, 200, 24);
        add(earnings);

        addLabel("Basic Salary", 48, 151, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 170, 151, 199, 21);

        addLabel("Hours Worked", 48, 178, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 170, 178, 199, 21);

        addLabel("Hourly Rate", 48, 204, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 170, 204, 199, 21);

        addLabel("Overtime", 48, 231, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 170, 231, 199, 21);

        addLabel("Holiday", 48, 259, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 170, 259, 199, 21);

        JLabel benefits = createLabel("Benefits", UIStyle.SECTION_FONT, Color.BLACK);
        benefits.setHorizontalAlignment(SwingConstants.CENTER);
        benefits.setBounds(118, 288, 200, 24);
        add(benefits);

        addLabel("Rice Subsidy", 44, 322, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 167, 321, 199, 21);

        addLabel("Phone Allowance", 44, 349, 130, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 167, 347, 199, 21);

        addLabel("Clothing Allowance", 44, 375, 140, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 167, 373, 199, 21);

        add(new DropdownField("Bonus Type"), 44, 402, 111, 21);
        add(new InputField(), 167, 402, 199, 21);

        addLabel("Gross Pay", 44, 445, 120, 22, UIStyle.BOLD_SMALL_FONT, Color.BLACK);
        add(new InputField(), 167, 441, 199, 21);

        JLabel deductions = createLabel("Deductions", UIStyle.SECTION_FONT, Color.BLACK);
        deductions.setHorizontalAlignment(SwingConstants.CENTER);
        deductions.setBounds(630, 120, 200, 24);
        add(deductions);

        addLabel("Withholding Tax", 510, 150, 130, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 633, 150, 199, 21);

        addLabel("SSS", 510, 176, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 633, 176, 199, 21);

        addLabel("PhilHealth", 510, 202, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 633, 202, 199, 21);

        addLabel("PAG-IBIG", 510, 228, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 633, 228, 199, 21);

        addLabel("Undertime", 510, 254, 120, 22, UIStyle.SMALL_TEXT_FONT, Color.BLACK);
        add(new InputField(), 633, 254, 199, 21);

        addLabel("Total Deductions", 510, 285, 140, 22, UIStyle.BOLD_SMALL_FONT, Color.BLACK);
        add(new InputField(), 635, 283, 198, 21);

        addLabel("Net Pay", 508, 395, 120, 22, UIStyle.BOLD_SMALL_FONT, Color.BLACK);
        add(new InputField(), 632, 393, 198, 21);
    }

    private void add(Component component, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        add(component);
    }

    private void addLabel(String text, int x, int y, int width, int height, Font font, Color color) {
        JLabel label = createLabel(text, font, color);
        label.setBounds(x, y, width, height);
        add(label);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(UIStyle.PANEL_BORDER);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.dispose();
    }
}

final class InputField extends JTextField {
    private final String placeholder;

    public InputField() {
        this("");
    }

    public InputField(String placeholder) {
        this.placeholder = placeholder;
        setBorder(null);
        setOpaque(false);
        setFont(UIStyle.INPUT_FONT);
        setForeground(Color.BLACK);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        g.setColor(UIStyle.FIELD_BORDER);
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        if (getText().isEmpty() && !placeholder.isEmpty()) {
            g.setFont(UIStyle.PLACEHOLDER_SMALL_FONT);
            g.setColor(UIStyle.PLACEHOLDER_GRAY);
            FontMetrics metrics = g.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(placeholder)) / 2;
            int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(placeholder, textX, textY);
        }

        g.dispose();
        super.paintComponent(graphics);
    }
}

final class DropdownField extends JComponent {
    private final String placeholder;

    public DropdownField() {
        this("");
    }

    public DropdownField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        g.setColor(UIStyle.FIELD_BORDER);
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        if (!placeholder.isEmpty()) {
            g.setFont(UIStyle.PLACEHOLDER_SMALL_FONT);
            g.setColor(UIStyle.PLACEHOLDER_GRAY);
            g.drawString(placeholder, 10, 15);
        }

        VectorIcon.draw(g, UiIcon.CHEVRON, getWidth() - 24, 6, 15, UIStyle.PLACEHOLDER_GRAY);

        g.dispose();
    }
}

final class DarkButton extends JComponent {
    private final String text;
    private final UiIcon icon;
    private boolean hovered;

    public DarkButton(String text, UiIcon icon) {
        this.text = text;
        this.icon = icon;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(hovered ? UIStyle.NAVY_HOVER : UIStyle.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(UIStyle.BUTTON_FONT);

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        if (icon != null) {
            VectorIcon.draw(g, icon, 13, 10, 17, Color.WHITE);
            g.drawString(text, 47, textY);
        } else {
            g.drawString(text, (getWidth() - textWidth) / 2, textY);
        }

        g.dispose();
    }
}

final class DisabledSavePdfButton extends JComponent {
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        UIStyle.enableAntialiasing(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.setColor(UIStyle.DISABLED_BORDER);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        VectorIcon.draw(g, UiIcon.DOWNLOAD, 10, 11, 14, UIStyle.DISABLED_TEXT);

        g.setFont(UIStyle.DISABLED_BUTTON_FONT);
        g.setColor(UIStyle.DISABLED_TEXT);
        g.drawString("Save PDF", 29, 22);

        g.dispose();
    }
}

enum UiIcon {
    DASHBOARD,
    EMPLOYEES,
    PAYROLL,
    REQUESTS,
    ATTENDANCE,
    HELP,
    LOGOUT,
    CHEVRON,
    DOWNLOAD
}

final class VectorIcon {
    private VectorIcon() {
    }

    public static void draw(Graphics2D g, UiIcon icon, int x, int y, int size, Color color) {
        Graphics2D copy = (Graphics2D) g.create();
        UIStyle.enableAntialiasing(copy);

        copy.setColor(color);
        copy.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (icon) {
            case DASHBOARD -> drawDashboard(copy, x, y, size);
            case EMPLOYEES -> drawEmployees(copy, x, y, size);
            case PAYROLL -> drawPayroll(copy, x, y, size);
            case REQUESTS -> drawRequests(copy, x, y, size);
            case ATTENDANCE -> drawAttendance(copy, x, y, size);
            case HELP -> drawHelp(copy, x, y, size);
            case LOGOUT -> drawLogout(copy, x, y, size);
            case CHEVRON -> drawChevron(copy, x, y, size);
            case DOWNLOAD -> drawDownload(copy, x, y, size);
        }

        copy.dispose();
    }

    private static void drawDashboard(Graphics2D g, int x, int y, int s) {
        int box = s / 3;
        int gap = s / 5;

        g.drawRoundRect(x, y, box, box, 2, 2);
        g.drawRoundRect(x + box + gap, y, box, box, 2, 2);
        g.drawRoundRect(x, y + box + gap, box, box, 2, 2);
        g.drawRoundRect(x + box + gap, y + box + gap, box, box, 2, 2);
    }

    private static void drawEmployees(Graphics2D g, int x, int y, int s) {
        g.drawOval(x + 2, y + 1, 9, 9);
        g.drawArc(x, y + 10, 14, 11, 0, 180);
        g.drawLine(x + 17, y + 5, x + s, y + 5);
        g.drawLine(x + 17, y + 10, x + s, y + 10);
        g.drawLine(x + 17, y + 15, x + s - 4, y + 15);
    }

    private static void drawPayroll(Graphics2D g, int x, int y, int s) {
        g.drawRoundRect(x + 1, y + 1, s - 4, s - 3, 2, 2);
        g.drawRect(x + 5, y + 5, 4, 4);
        g.drawLine(x + 12, y + 6, x + s - 6, y + 6);
        g.drawLine(x + 5, y + 13, x + s - 6, y + 13);
        g.drawLine(x + 5, y + 17, x + s - 8, y + 17);
    }

    private static void drawRequests(Graphics2D g, int x, int y, int s) {
        g.drawRect(x + 1, y + 1, s - 4, s - 3);
        g.drawLine(x + 5, y + 7, x + s - 7, y + 7);
        g.drawLine(x + 5, y + 12, x + s - 7, y + 12);
        g.drawLine(x + 5, y + 17, x + s - 10, y + 17);
    }

    private static void drawAttendance(Graphics2D g, int x, int y, int s) {
        g.drawRoundRect(x + 1, y + 3, s - 3, s - 3, 2, 2);
        g.drawLine(x + 1, y + 8, x + s - 2, y + 8);
        g.drawLine(x + 5, y, x + 5, y + 5);
        g.drawLine(x + s - 6, y, x + s - 6, y + 5);
        g.drawLine(x + 6, y + 13, x + 8, y + 13);
        g.drawLine(x + 12, y + 13, x + 14, y + 13);
        g.drawLine(x + 6, y + 18, x + 8, y + 18);
    }

    private static void drawHelp(Graphics2D g, int x, int y, int s) {
        g.drawArc(x + 1, y + 8, 8, 8, 90, 180);
        g.drawArc(x + 6, y + 3, 12, 12, 20, 210);
        g.drawArc(x + 13, y + 8, 8, 8, -90, 180);
        g.drawLine(x + 4, y + 16, x + s - 4, y + 16);
    }

    private static void drawLogout(Graphics2D g, int x, int y, int s) {
        g.drawRect(x + 3, y + 1, 12, s - 3);
        g.drawLine(x + 15, y + 7, x + s - 2, y + 7);
        g.drawLine(x + s - 6, y + 3, x + s - 2, y + 7);
        g.drawLine(x + s - 6, y + 11, x + s - 2, y + 7);
        g.drawLine(x + 15, y + 16, x + 18, y + 16);
    }

    private static void drawChevron(Graphics2D g, int x, int y, int s) {
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x + 1, y + 3, x + s / 2, y + s - 4);
        g.drawLine(x + s / 2, y + s - 4, x + s - 1, y + 3);
    }

    private static void drawDownload(Graphics2D g, int x, int y, int s) {
        g.drawLine(x + s / 2, y + 1, x + s / 2, y + s - 5);
        g.drawLine(x + s / 2, y + s - 5, x + 4, y + s - 9);
        g.drawLine(x + s / 2, y + s - 5, x + s - 4, y + s - 9);
        g.drawLine(x + 3, y + s - 2, x + s - 3, y + s - 2);
    }
}

final class UIStyle {
    private UIStyle() {
    }

    static final Color NAVY = new Color(2, 19, 98);
    static final Color NAVY_HOVER = new Color(5, 27, 118);
    static final Color BACK_TEXT = new Color(80, 80, 80);

    static final Color PANEL_BORDER = new Color(185, 185, 185);
    static final Color FIELD_BORDER = new Color(80, 80, 80);
    static final Color PLACEHOLDER_GRAY = new Color(200, 200, 200);

    static final Color PROFILE_POSITION_GRAY = new Color(150, 150, 150);
    static final Color DISABLED_TEXT = new Color(210, 210, 210);
    static final Color DISABLED_BORDER = new Color(210, 210, 210);

    static final Font LOGO_FONT = new Font("Segoe UI", Font.BOLD, 28);

    static final Font NAV_FONT = new Font("Open Sans", Font.PLAIN, 18);
    static final Font NAV_ACTIVE_FONT = new Font("Open Sans", Font.BOLD, 18);

    static final Font HEADING_2_FONT = new Font("Open Sans", Font.BOLD, 18);
    static final Font PARAGRAPH_FONT = new Font("Open Sans", Font.PLAIN, 15);

    static final Font SECTION_FONT = new Font("Open Sans", Font.BOLD, 15);
    static final Font SMALL_TEXT_FONT = new Font("Open Sans", Font.PLAIN, 13);
    static final Font BOLD_SMALL_FONT = new Font("Open Sans", Font.BOLD, 13);

    static final Font INPUT_FONT = new Font("Open Sans", Font.PLAIN, 12);
    static final Font PLACEHOLDER_SMALL_FONT = new Font("Open Sans", Font.PLAIN, 12);

    static final Font BUTTON_FONT = new Font("Open Sans", Font.PLAIN, 13);
    static final Font DISABLED_BUTTON_FONT = new Font("Open Sans", Font.PLAIN, 13);

    static final Font PROFILE_NAME_FONT = new Font("Open Sans", Font.BOLD, 18);
    static final Font PROFILE_POSITION_FONT = new Font("Open Sans", Font.PLAIN, 16);

    static void enableAntialiasing(Graphics2D g) {
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
    }
}

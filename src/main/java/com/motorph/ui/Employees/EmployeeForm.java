/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Employees;

/**
 *
 * @author Admin
 */
import com.motorph.ui.Employees.SidebarPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public final class EMPLOYEEForm {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH - Employee Form");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setUndecorated(true);
            frame.setContentPane(new EmployeeFormView());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

final class EmployeeFormView extends JPanel {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 800;

    EmployeeFormView() {
        setLayout(null);
        setBackground(AppColors.WHITE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, 257, 800);
        add(sidebar);

        ProfileBadge profileBadge = new ProfileBadge("Name", "Position");
        profileBadge.setBounds(1077, 63, 123, 60);
        add(profileBadge);

        BackLink backLink = new BackLink("Back");
        backLink.setBounds(320, 160, 60, 24);
        add(backLink);

        add(createBasicInformationSection());
        add(createPersonalDetailSection());
        add(createGovernmentIdSection());
        add(createCompensationSection());

        SubmitButton submitButton = new SubmitButton("Submit");
        submitButton.setBounds(1087, 682, 113, 38);
        add(submitButton);
    }

    private FormSection createBasicInformationSection() {
        List<FieldSpec> fields = new ArrayList<>();
        fields.add(new FieldSpec("Employee ID", null, 47));
        fields.add(new FieldSpec("First Name", null, 96));
        fields.add(new FieldSpec("Last Name", null, 146));
        fields.add(new FieldSpec("Department", null, 198));
        fields.add(new FieldSpec("Position", null, 247));
        fields.add(new FieldSpec("Immediate Supervisor", null, 297));
        fields.add(new FieldSpec("Role", null, 346));
        fields.add(new FieldSpec("Status", null, 395));

        FormSection section = new FormSection("Basic Information", fields);
        section.setBounds(320, 218, 174, 430);
        return section;
    }

    private FormSection createPersonalDetailSection() {
        List<FieldSpec> fields = new ArrayList<>();
        fields.add(new FieldSpec("Gender", null, 47));
        fields.add(new FieldSpec("Birthdate", "MM-DD-YYYY", 96));
        fields.add(new FieldSpec("Cellphone No.", null, 146));
        fields.add(new FieldSpec("Telephone No.", null, 196));
        fields.add(new FieldSpec("E-mail", null, 247));
        fields.add(new FieldSpec("Address", null, 297));

        FormSection section = new FormSection("Personal Detail", fields);
        section.setBounds(546, 218, 174, 330);
        return section;
    }

    private FormSection createGovernmentIdSection() {
        List<FieldSpec> fields = new ArrayList<>();
        fields.add(new FieldSpec("SSS No.", "XX-XXXXXXX-Y", 47));
        fields.add(new FieldSpec("PhilHealth No.", "XX-XXXXXXXXX-X", 97));
        fields.add(new FieldSpec("PAG-IBIG No.", "XXXX-XXXX-XXXX", 146));
        fields.add(new FieldSpec("TIN", "XXX-XXX-XXX-XXX", 196));

        FormSection section = new FormSection("Government ID", fields);
        section.setBounds(772, 218, 174, 240);
        return section;
    }

    private FormSection createCompensationSection() {
        List<FieldSpec> fields = new ArrayList<>();
        fields.add(new FieldSpec("Basic Salary", null, 47));
        fields.add(new FieldSpec("Gross Semi-Monthly Rate", null, 97));
        fields.add(new FieldSpec("Hourly Rate", null, 146));
        fields.add(new FieldSpec("Rice Subsidy", null, 196));
        fields.add(new FieldSpec("Phone Allowance", null, 247));
        fields.add(new FieldSpec("Clothing Allowance", null, 296));

        FormSection section = new FormSection("Compensation", fields);
        section.setBounds(998, 218, 174, 330);
        return section;
    }
}

final class AppColors {
    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = Color.BLACK;
    static final Color DARK_TEXT = new Color(0, 0, 0);
    static final Color PROFILE_NAME = new Color(12, 0, 72);
    static final Color PROFILE_POSITION = new Color(145, 145, 145);
    static final Color LINK_GRAY = new Color(84, 84, 84);
    static final Color FIELD_BORDER = new Color(84, 84, 84);
    static final Color PLACEHOLDER = new Color(214, 214, 214);

    private AppColors() {
    }
}

final class AppFonts {
    static final int HEADING_1 = 28;
    static final int HEADING_2 = 18;
    static final int PARAGRAPH = 15;

    private AppFonts() {
    }

    static Font headerBold(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    static Font textPlain(int size) {
        return new Font("Open Sans", Font.PLAIN, size);
    }

    static Font textBold(int size) {
        return new Font("Open Sans", Font.BOLD, size);
    }
}

enum IconType {
    DASHBOARD,
    EMPLOYEES,
    PAYROLL,
    REQUESTS,
    ATTENDANCE,
    HELP,
    LOGOUT
}

final class NavItem {
    private final String label;
    private final IconType iconType;
    private final int centerY;
    private final boolean active;

    NavItem(String label, IconType iconType, int centerY, boolean active) {
        this.label = label;
        this.iconType = iconType;
        this.centerY = centerY;
        this.active = active;
    }

    String getLabel() {
        return label;
    }

    IconType getIconType() {
        return iconType;
    }

    int getCenterY() {
        return centerY;
    }

    boolean isActive() {
        return active;
    }
}

final class SidebarPanel extends JPanel {
    private final List<NavItem> items;

    SidebarPanel() {
        setOpaque(false);

        items = new ArrayList<>();
        items.add(new NavItem("Dashboard", IconType.DASHBOARD, 169, false));
        items.add(new NavItem("Employees", IconType.EMPLOYEES, 225, true));
        items.add(new NavItem("Payroll", IconType.PAYROLL, 264, false));
        items.add(new NavItem("Requests", IconType.REQUESTS, 307, false));
        items.add(new NavItem("Attendance", IconType.ATTENDANCE, 351, false));
        items.add(new NavItem("Help Center", IconType.HELP, 671, false));
        items.add(new NavItem("Log Out", IconType.LOGOUT, 713, false));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawLogo(g2);
        drawItems(g2);

        g2.dispose();
    }

    private void drawLogo(Graphics2D g2) {
        g2.setColor(AppColors.WHITE);
        g2.setFont(AppFonts.headerBold(AppFonts.HEADING_1));
        g2.drawString("MotorPH", 48, 89);
    }

    private void drawItems(Graphics2D g2) {
        for (NavItem item : items) {
            drawIcon(g2, item.getIconType(), 49, item.getCenterY());

            g2.setColor(AppColors.WHITE);
            g2.setFont(item.isActive()
                    ? AppFonts.textBold(17)
                    : AppFonts.textPlain(17));

            g2.drawString(item.getLabel(), 96, item.getCenterY() + 6);
        }
    }

    private void drawIcon(Graphics2D g2, IconType type, int x, int centerY) {
        g2.setColor(AppColors.WHITE);
        g2.setStroke(new BasicStroke(1.15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case DASHBOARD:
                drawDashboardIcon(g2, x, centerY);
                break;
            case EMPLOYEES:
                drawEmployeesIcon(g2, x, centerY);
                break;
            case PAYROLL:
                drawPayrollIcon(g2, x, centerY);
                break;
            case REQUESTS:
                drawRequestsIcon(g2, x, centerY);
                break;
            case ATTENDANCE:
                drawAttendanceIcon(g2, x, centerY);
                break;
            case HELP:
                drawHelpIcon(g2, x, centerY);
                break;
            case LOGOUT:
                drawLogoutIcon(g2, x, centerY);
                break;
            default:
                break;
        }
    }

    private void drawDashboardIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 10;
        g2.drawRoundRect(x, y, 8, 8, 2, 2);
        g2.drawRoundRect(x + 13, y, 8, 8, 2, 2);
        g2.drawRoundRect(x, y + 13, 8, 8, 2, 2);
        g2.drawRoundRect(x + 13, y + 13, 8, 8, 2, 2);
    }

    private void drawEmployeesIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 11;
        g2.drawOval(x + 2, y, 9, 9);
        g2.drawArc(x, y + 9, 14, 13, 0, 180);
        g2.drawLine(x + 17, y + 4, x + 24, y + 4);
        g2.drawLine(x + 17, y + 10, x + 24, y + 10);
        g2.drawLine(x + 17, y + 16, x + 24, y + 16);
    }

    private void drawPayrollIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 10;
        g2.drawRoundRect(x, y, 18, 20, 1, 1);
        g2.drawRect(x + 4, y + 4, 3, 3);
        g2.drawLine(x + 10, y + 5, x + 15, y + 5);
        g2.drawRect(x + 4, y + 10, 3, 3);
        g2.drawLine(x + 10, y + 11, x + 15, y + 11);
        g2.drawRect(x + 4, y + 15, 3, 3);
        g2.drawLine(x + 10, y + 16, x + 15, y + 16);
    }

    private void drawRequestsIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 10;
        g2.drawRect(x, y, 19, 20);
        g2.drawLine(x + 3, y + 6, x + 16, y + 6);
        g2.drawLine(x + 5, y + 11, x + 14, y + 11);
        g2.drawLine(x + 7, y + 15, x + 12, y + 15);
        g2.drawLine(x + 4, y - 3, x + 4, y + 2);
        g2.drawLine(x + 15, y - 3, x + 15, y + 2);
    }

    private void drawAttendanceIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 11;
        g2.drawRoundRect(x, y + 3, 19, 17, 1, 1);
        g2.drawLine(x, y + 8, x + 19, y + 8);
        g2.drawLine(x + 4, y, x + 4, y + 5);
        g2.drawLine(x + 8, y, x + 8, y + 5);
        g2.drawLine(x + 12, y, x + 12, y + 5);
        g2.drawLine(x + 16, y, x + 16, y + 5);
    }

    private void drawHelpIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 9;

        Path2D cloud = new Path2D.Double();
        cloud.moveTo(x + 1, y + 12);
        cloud.curveTo(x + 1, y + 8, x + 5, y + 7, x + 7, y + 8);
        cloud.curveTo(x + 8, y + 4, x + 13, y + 3, x + 15, y + 7);
        cloud.curveTo(x + 19, y + 6, x + 22, y + 9, x + 21, y + 13);
        cloud.lineTo(x + 4, y + 13);
        cloud.curveTo(x + 2, y + 13, x + 1, y + 13, x + 1, y + 12);
        g2.draw(cloud);
    }

    private void drawLogoutIcon(Graphics2D g2, int x, int centerY) {
        int y = centerY - 10;

        g2.drawRect(x + 2, y, 12, 20);
        g2.drawLine(x + 14, y + 10, x + 22, y + 10);
        g2.drawLine(x + 18, y + 6, x + 22, y + 10);
        g2.drawLine(x + 18, y + 14, x + 22, y + 10);
        g2.drawLine(x + 8, y + 5, x + 8, y + 15);
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

final class ProfileBadge extends JPanel {
    private final String name;
    private final String position;

    ProfileBadge(String name, String position) {
        this.name = name;
        this.position = position;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.PROFILE_NAME);
        g2.setFont(AppFonts.headerBold(AppFonts.HEADING_2));
        g2.drawString(name, 5, 24);

        g2.setColor(AppColors.PROFILE_POSITION);
        g2.setFont(AppFonts.textPlain(AppFonts.PARAGRAPH));
        g2.drawString(position, 0, 47);

        g2.setColor(AppColors.NAVY);
        g2.fillOval(67, 0, 56, 56);

        g2.dispose();
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

final class BackLink extends JComponent {
    private final String text;

    BackLink(String text) {
        this.text = text;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.LINK_GRAY);
        g2.setFont(AppFonts.textPlain(17));
        g2.drawString(text, 0, 17);

        FontMetrics metrics = g2.getFontMetrics();
        int width = metrics.stringWidth(text);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(0, 20, width, 20);

        g2.dispose();
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

final class FieldSpec {
    private final String label;
    private final String placeholder;
    private final int fieldY;

    FieldSpec(String label, String placeholder, int fieldY) {
        this.label = label;
        this.placeholder = placeholder;
        this.fieldY = fieldY;
    }

    String getLabel() {
        return label;
    }

    String getPlaceholder() {
        return placeholder;
    }

    int getFieldY() {
        return fieldY;
    }
}

final class FormSection extends JPanel {
    private static final int FIELD_WIDTH = 174;
    private static final int FIELD_HEIGHT = 26;
    private static final int LABEL_HEIGHT = 15;

    FormSection(String title, List<FieldSpec> fields) {
        setLayout(null);
        setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppFonts.textBold(AppFonts.HEADING_2));
        titleLabel.setForeground(AppColors.BLACK);
        titleLabel.setBounds(0, 0, 210, 25);
        add(titleLabel);

        for (FieldSpec field : fields) {
            JLabel label = new JLabel(field.getLabel());
            label.setFont(AppFonts.textPlain(10));
            label.setForeground(AppColors.DARK_TEXT);
            label.setBounds(0, field.getFieldY() - 16, 210, LABEL_HEIGHT);
            add(label);

            InputBox input = new InputBox(field.getPlaceholder());
            input.setBounds(0, field.getFieldY(), FIELD_WIDTH, FIELD_HEIGHT);
            add(input);
        }
    }
}

final class InputBox extends JTextField {
    private final String placeholder;

    InputBox(String placeholder) {
        this.placeholder = placeholder;

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 7, 2, 7));
        setFont(AppFonts.textPlain(11));
        setForeground(AppColors.BLACK);
        setCaretColor(AppColors.BLACK);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        super.paintComponent(graphics);

        if (getText().isEmpty() && placeholder != null && !placeholder.isBlank()) {
            g2.setColor(AppColors.PLACEHOLDER);
            g2.setFont(AppFonts.textPlain(10));

            FontMetrics metrics = g2.getFontMetrics();
            int textWidth = metrics.stringWidth(placeholder);
            int x = (getWidth() - textWidth) / 2;
            int y = 17;

            g2.drawString(placeholder, x, y);
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.FIELD_BORDER);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        g2.dispose();
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

final class SubmitButton extends JComponent {
    private final String text;
    private boolean pressed;

    SubmitButton(String text) {
        this.text = text;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        enableQuality(g2);

        g2.setColor(AppColors.NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (pressed) {
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.setColor(AppColors.WHITE);
        g2.setFont(AppFonts.textPlain(14));

        FontMetrics metrics = g2.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textX = (getWidth() - textWidth) / 2;
        int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.drawString(text, textX, textY);

        g2.dispose();
    }

    private void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}

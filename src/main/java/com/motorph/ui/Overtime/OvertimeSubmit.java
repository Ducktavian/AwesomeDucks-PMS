/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Overtime;

/**
 *
 * @author Admin
 */
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Path2D;

public class OvertimeSubmit {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("MotorPH - Submit Overtime");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1280, 800);
                frame.setResizable(false);
                frame.setUndecorated(true);
                frame.setLocationRelativeTo(null);
                frame.setContentPane(new MainPanel());
                frame.setVisible(true);
            }
        });
    }
}

final class Theme {
    private Theme() {}

    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = Color.BLACK;

    static final Color INPUT_BORDER = new Color(64, 64, 64);
    static final Color PLACEHOLDER_GRAY = new Color(214, 214, 214);
    static final Color PROFILE_TEXT = new Color(0, 6, 67);
    static final Color POSITION_GRAY = new Color(150, 150, 150);
    static final Color BACK_TEXT = new Color(70, 70, 70);

    static final Font HEADING_1 = new Font("Segoe UI", Font.BOLD, 28);
    static final Font HEADING_2 = new Font("Segoe UI", Font.BOLD, 18);

    static final Font PARAGRAPH = new Font("Open Sans", Font.PLAIN, 15);
    static final Font PARAGRAPH_BOLD = new Font("Open Sans", Font.BOLD, 15);

    static final Font FORM_LABEL = new Font("Open Sans", Font.PLAIN, 14);
    static final Font PLACEHOLDER = new Font("Open Sans", Font.PLAIN, 13);
    static final Font BUTTON_TEXT = new Font("Open Sans", Font.PLAIN, 13);
    static final Font BACK_LINK = new Font("Open Sans", Font.PLAIN, 13);
}

class MainPanel extends JPanel {

    MainPanel() {
        setLayout(null);
        setBackground(Theme.WHITE);

        SidebarPanel sidebarPanel = new SidebarPanel();
        sidebarPanel.setBounds(0, 0, 257, 800);
        add(sidebarPanel);

        ProfilePanel profilePanel = new ProfilePanel();
        profilePanel.setBounds(1077, 63, 123, 57);
        add(profilePanel);

        OvertimeFormPanel overtimeFormPanel = new OvertimeFormPanel();
        overtimeFormPanel.setBounds(320, 164, 880, 540);
        add(overtimeFormPanel);
    }
}

class SidebarPanel extends JPanel {

    SidebarPanel() {
        setLayout(null);
        setBackground(Theme.NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(Theme.HEADING_1);
        logo.setForeground(Theme.WHITE);
        logo.setBounds(48, 63, 150, 36);
        add(logo);

        addNavigationItem("Dashboard", LineIcon.Type.DASHBOARD, 158, false);
        addNavigationItem("Employees", LineIcon.Type.EMPLOYEES, 216, false);
        addNavigationItem("Payroll", LineIcon.Type.PAYROLL, 255, false);
        addNavigationItem("Requests", LineIcon.Type.REQUESTS, 300, true);
        addNavigationItem("Attendance", LineIcon.Type.ATTENDANCE, 341, false);

        addNavigationItem("Help Center", LineIcon.Type.HELP, 660, false);
        addNavigationItem("Log Out", LineIcon.Type.LOGOUT, 702, false);
    }

    private void addNavigationItem(String text, LineIcon.Type iconType, int y, boolean active) {
        NavigationItem item = new NavigationItem(text, iconType, active);
        item.setBounds(48, y, 170, 32);
        add(item);
    }
}

class NavigationItem extends JPanel {

    NavigationItem(String text, LineIcon.Type iconType, boolean active) {
        setLayout(null);
        setOpaque(false);

        JLabel icon = new JLabel(new LineIcon(iconType, 22, Theme.WHITE));
        icon.setBounds(0, 5, 22, 22);
        add(icon);

        JLabel label = new JLabel(text);
        label.setFont(active ? Theme.PARAGRAPH_BOLD : Theme.PARAGRAPH);
        label.setForeground(Theme.WHITE);
        label.setBounds(48, 0, 130, 32);
        add(label);
    }
}

class ProfilePanel extends JPanel {

    ProfilePanel() {
        setLayout(null);
        setOpaque(false);

        JLabel nameLabel = new JLabel("Name", SwingConstants.RIGHT);
        nameLabel.setFont(Theme.HEADING_2);
        nameLabel.setForeground(Theme.PROFILE_TEXT);
        nameLabel.setBounds(0, 8, 58, 22);
        add(nameLabel);

        JLabel positionLabel = new JLabel("Position", SwingConstants.RIGHT);
        positionLabel.setFont(Theme.PARAGRAPH);
        positionLabel.setForeground(Theme.POSITION_GRAY);
        positionLabel.setBounds(0, 31, 58, 22);
        add(positionLabel);

        AvatarCircle avatarCircle = new AvatarCircle();
        avatarCircle.setBounds(67, 0, 56, 56);
        add(avatarCircle);
    }
}

class AvatarCircle extends JPanel {

    AvatarCircle() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Theme.NAVY);
        g.fillOval(0, 0, 56, 56);

        g.dispose();
    }
}

class OvertimeFormPanel extends JPanel {

    private static final int LEFT_X = 0;
    private static final int RIGHT_X = 476;
    private static final int FIELD_WIDTH = 404;
    private static final int FIELD_HEIGHT = 57;

    OvertimeFormPanel() {
        setLayout(null);
        setOpaque(false);

        JLabel backLink = new JLabel("<html><u>Back</u></html>");
        backLink.setFont(Theme.BACK_LINK);
        backLink.setForeground(Theme.BACK_TEXT);
        backLink.setBounds(0, 0, 70, 20);
        add(backLink);

        addDateSection();
        addTimeSection();
        addReasonSection();
        addNotesSection();
        addStatusSection();
        addSubmitButton();
    }

    private void addDateSection() {
        FormLabel dateLabel = new FormLabel("Date");
        dateLabel.setBounds(LEFT_X, 50, 160, 24);
        add(dateLabel);

        InputFieldBox dateField = new InputFieldBox("mm/dd/yyyy", LineIcon.Type.CALENDAR);
        dateField.setBounds(LEFT_X, 78, FIELD_WIDTH, FIELD_HEIGHT);
        add(dateField);
    }

    private void addTimeSection() {
        FormLabel startTimeLabel = new FormLabel("Start Time");
        startTimeLabel.setBounds(LEFT_X, 162, 160, 24);
        add(startTimeLabel);

        InputFieldBox startTimeField = new InputFieldBox("5:00 PM", LineIcon.Type.CLOCK);
        startTimeField.setBounds(LEFT_X, 190, FIELD_WIDTH, FIELD_HEIGHT);
        add(startTimeField);

        FormLabel endTimeLabel = new FormLabel("End Time");
        endTimeLabel.setBounds(LEFT_X, 273, 160, 24);
        add(endTimeLabel);

        InputFieldBox endTimeField = new InputFieldBox("9:00 PM", LineIcon.Type.CLOCK);
        endTimeField.setBounds(LEFT_X, 301, FIELD_WIDTH, FIELD_HEIGHT);
        add(endTimeField);
    }

    private void addReasonSection() {
        FormLabel reasonLabel = new FormLabel("Reason");
        reasonLabel.setBounds(RIGHT_X, 50, 160, 24);
        add(reasonLabel);

        SelectFieldBox reasonBox = new SelectFieldBox();
        reasonBox.setBounds(RIGHT_X, 78, FIELD_WIDTH, FIELD_HEIGHT);
        add(reasonBox);
    }

    private void addNotesSection() {
        FormLabel notesLabel = new FormLabel("Notes");
        notesLabel.setBounds(RIGHT_X, 162, 160, 24);
        add(notesLabel);

        NotesBox notesBox = new NotesBox();
        notesBox.setBounds(RIGHT_X, 190, FIELD_WIDTH, 167);
        add(notesBox);
    }

    private void addStatusSection() {
        FormLabel statusLabel = new FormLabel("Status");
        statusLabel.setBounds(RIGHT_X, 385, 160, 24);
        add(statusLabel);

        SelectFieldBox statusBox = new SelectFieldBox();
        statusBox.setBounds(RIGHT_X, 413, FIELD_WIDTH, FIELD_HEIGHT);
        add(statusBox);
    }

    private void addSubmitButton() {
        SubmitButton submitButton = new SubmitButton("Submit");
        submitButton.setBounds(753, 492, 127, 44);
        add(submitButton);
    }
}

class FormLabel extends JLabel {

    FormLabel(String text) {
        super(text);
        setFont(Theme.FORM_LABEL);
        setForeground(Theme.BLACK);
    }
}

class InputFieldBox extends JPanel {

    InputFieldBox(String placeholder, LineIcon.Type iconType) {
        setLayout(null);
        setBackground(Theme.WHITE);
        setBorder(new RoundedBorder(Theme.INPUT_BORDER, 5));

        JLabel placeholderLabel = new JLabel(placeholder);
        placeholderLabel.setFont(Theme.PLACEHOLDER);
        placeholderLabel.setForeground(Theme.PLACEHOLDER_GRAY);
        placeholderLabel.setBounds(16, 18, 180, 22);
        add(placeholderLabel);

        JLabel iconLabel = new JLabel(new LineIcon(iconType, 28, Theme.PLACEHOLDER_GRAY));
        iconLabel.setBounds(360, 14, 28, 28);
        add(iconLabel);
    }
}

class SelectFieldBox extends JPanel {

    SelectFieldBox() {
        setLayout(null);
        setBackground(Theme.WHITE);
        setBorder(new RoundedBorder(Theme.INPUT_BORDER, 5));

        JLabel arrowLabel = new JLabel(new LineIcon(LineIcon.Type.CHEVRON_DOWN, 32, Theme.PLACEHOLDER_GRAY));
        arrowLabel.setBounds(356, 14, 32, 32);
        add(arrowLabel);
    }
}

class NotesBox extends JPanel {

    NotesBox() {
        setLayout(null);
        setBackground(Theme.WHITE);
        setBorder(new RoundedBorder(Theme.INPUT_BORDER, 5));
    }
}

class SubmitButton extends JButton {

    SubmitButton(String text) {
        setLayout(null);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(Theme.BUTTON_TEXT);
        label.setForeground(Theme.WHITE);
        label.setBounds(0, 0, 127, 44);
        add(label);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(Theme.NAVY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();

        super.paintComponent(graphics);
    }
}

class RoundedBorder implements Border {

    private final Color color;
    private final int radius;

    RoundedBorder(Color color, int radius) {
        this.color = color;
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(1, 1, 1, 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(color);
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g.dispose();
    }
}

class LineIcon implements Icon {

    enum Type {
        DASHBOARD,
        EMPLOYEES,
        PAYROLL,
        REQUESTS,
        ATTENDANCE,
        HELP,
        LOGOUT,
        CALENDAR,
        CLOCK,
        CHEVRON_DOWN
    }

    private final Type type;
    private final int size;
    private final Color color;

    LineIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();

        g.translate(x, y);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (type == Type.DASHBOARD) {
            drawDashboard(g);
        } else if (type == Type.EMPLOYEES) {
            drawEmployees(g);
        } else if (type == Type.PAYROLL) {
            drawPayroll(g);
        } else if (type == Type.REQUESTS) {
            drawRequests(g);
        } else if (type == Type.ATTENDANCE) {
            drawAttendance(g);
        } else if (type == Type.HELP) {
            drawHelp(g);
        } else if (type == Type.LOGOUT) {
            drawLogout(g);
        } else if (type == Type.CALENDAR) {
            drawCalendar(g);
        } else if (type == Type.CLOCK) {
            drawClock(g);
        } else if (type == Type.CHEVRON_DOWN) {
            drawChevronDown(g);
        }

        g.dispose();
    }

    private void drawDashboard(Graphics2D g) {
        g.drawRoundRect(1, 1, 8, 8, 2, 2);
        g.drawRoundRect(13, 1, 8, 8, 2, 2);
        g.drawRoundRect(1, 13, 8, 8, 2, 2);
        g.drawRoundRect(13, 13, 8, 8, 2, 2);
    }

    private void drawEmployees(Graphics2D g) {
        g.drawOval(3, 2, 8, 8);
        g.drawArc(1, 12, 13, 9, 0, 180);
        g.drawLine(16, 5, 22, 5);
        g.drawLine(16, 11, 22, 11);
        g.drawLine(16, 17, 22, 17);
    }

    private void drawPayroll(Graphics2D g) {
        g.drawRoundRect(2, 2, 18, 18, 2, 2);
        g.drawRect(6, 6, 4, 4);
        g.drawRect(13, 6, 4, 4);
        g.drawRect(6, 13, 4, 4);
        g.drawLine(13, 14, 17, 14);
        g.drawLine(13, 17, 17, 17);
    }

    private void drawRequests(Graphics2D g) {
        g.drawRoundRect(2, 2, 18, 18, 1, 1);
        g.drawLine(6, 7, 16, 7);
        g.drawLine(6, 12, 16, 12);
        g.drawLine(6, 17, 12, 17);
        g.drawLine(5, 3, 5, 0);
        g.drawLine(17, 3, 17, 0);
    }

    private void drawAttendance(Graphics2D g) {
        g.drawRoundRect(2, 4, 18, 17, 1, 1);
        g.drawLine(2, 8, 20, 8);
        g.drawLine(6, 1, 6, 6);
        g.drawLine(8, 1, 8, 6);
        g.drawLine(10, 1, 10, 6);
        g.drawLine(12, 1, 12, 6);
        g.drawLine(14, 1, 14, 6);
        g.drawLine(16, 1, 16, 6);
    }

    private void drawHelp(Graphics2D g) {
        Path2D cloud = new Path2D.Double();
        cloud.moveTo(5, 17);
        cloud.curveTo(2, 17, 1, 15, 2, 13);
        cloud.curveTo(2, 10, 5, 9, 7, 10);
        cloud.curveTo(8, 6, 13, 5, 15, 9);
        cloud.curveTo(18, 9, 21, 11, 21, 14);
        cloud.curveTo(21, 16, 19, 17, 17, 17);
        cloud.closePath();
        g.draw(cloud);
    }

    private void drawLogout(Graphics2D g) {
        g.drawRect(3, 2, 11, 18);
        g.drawLine(14, 11, 22, 11);
        g.drawLine(18, 7, 22, 11);
        g.drawLine(18, 15, 22, 11);
    }

    private void drawCalendar(Graphics2D g) {
        int w = size - 3;
        int h = size - 4;

        g.drawRoundRect(2, 4, w - 2, h - 2, 2, 2);
        g.drawLine(2, 10, w, 10);

        g.drawLine(7, 2, 7, 7);
        g.drawLine(size - 8, 2, size - 8, 7);

        g.fillRect(7, 14, 2, 2);
        g.fillRect(13, 14, 2, 2);
        g.fillRect(19, 14, 2, 2);

        g.fillRect(7, 19, 2, 2);
        g.fillRect(13, 19, 2, 2);
        g.fillRect(19, 19, 2, 2);
    }

    private void drawClock(Graphics2D g) {
        int diameter = size - 3;
        int center = diameter / 2 + 1;

        g.drawOval(1, 1, diameter, diameter);
        g.drawLine(center, center, center, 7);
        g.drawLine(center, center, center + 6, center + 4);
    }

    private void drawChevronDown(Graphics2D g) {
        g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(6, 11, 16, 21);
        g.drawLine(16, 21, 26, 11);
    }
}

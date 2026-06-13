/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Leave;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;

public final class SubmitLeave {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            SubmitLeaveFrame frame = new SubmitLeaveFrame();
            frame.setVisible(true);
        });
    }
}

final class Theme {
    private Theme() {}

    static final int WINDOW_WIDTH = 1280;
    static final int WINDOW_HEIGHT = 800;
    static final int SIDEBAR_WIDTH = 257;

    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = new Color(20, 20, 20);
    static final Color INPUT_BORDER = new Color(70, 70, 70);
    static final Color PLACEHOLDER = new Color(214, 214, 214);
    static final Color PROFILE_GRAY = new Color(150, 150, 150);
    static final Color LINK_GRAY = new Color(90, 90, 90);

    static final String HEADER_FONT = "Segoe UI";
    static final String TEXT_FONT = "Open Sans";

    static final int HEADING_1 = 28;
    static final int HEADING_2 = 18;
    static final int PARAGRAPH = 15;

    static Font header(int style, int size) {
        return new Font(HEADER_FONT, style, size);
    }

    static Font text(int style, int size) {
        return new Font(TEXT_FONT, style, size);
    }

    static void applyQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}

final class SubmitLeaveFrame extends JFrame {
    SubmitLeaveFrame() {
        super("MotorPH - Submit Leave");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
        setResizable(false);
        setUndecorated(true);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(null);
        root.setBackground(Theme.WHITE);
        setContentPane(root);

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, Theme.SIDEBAR_WIDTH, Theme.WINDOW_HEIGHT);
        root.add(sidebar);

        MainContentPanel mainContent = new MainContentPanel();
        mainContent.setBounds(
                Theme.SIDEBAR_WIDTH,
                0,
                Theme.WINDOW_WIDTH - Theme.SIDEBAR_WIDTH,
                Theme.WINDOW_HEIGHT
        );
        root.add(mainContent);
    }
}

final class MainContentPanel extends JPanel {
    MainContentPanel() {
        setLayout(null);
        setBackground(Theme.WHITE);

        ProfilePanel profile = new ProfilePanel();
        profile.setBounds(820, 63, 126, 58);
        add(profile);

        BackLink backLink = new BackLink("Back");
        backLink.setBounds(63, 162, 60, 18);
        add(backLink);

        FormLabel startDateLabel = new FormLabel("Start Date");
        startDateLabel.setBounds(63, 213, 130, 22);
        add(startDateLabel);

        DateInputBox startDateInput = new DateInputBox("mm/dd/yyyy");
        startDateInput.setBounds(63, 242, 404, 57);
        add(startDateInput);

        FormLabel endDateLabel = new FormLabel("End Date");
        endDateLabel.setBounds(63, 325, 130, 22);
        add(endDateLabel);

        DateInputBox endDateInput = new DateInputBox("mm/dd/yyyy");
        endDateInput.setBounds(63, 354, 404, 57);
        add(endDateInput);

        FormLabel reasonLabel = new FormLabel("Reason");
        reasonLabel.setBounds(539, 213, 130, 22);
        add(reasonLabel);

        DropdownBox reasonDropdown = new DropdownBox();
        reasonDropdown.setBounds(539, 242, 404, 57);
        add(reasonDropdown);

        FormLabel notesLabel = new FormLabel("Notes");
        notesLabel.setBounds(539, 325, 130, 22);
        add(notesLabel);

        NotesBox notesBox = new NotesBox();
        notesBox.setBounds(539, 354, 404, 168);
        add(notesBox);

        FormLabel statusLabel = new FormLabel("Status");
        statusLabel.setBounds(539, 548, 130, 22);
        add(statusLabel);

        DropdownBox statusDropdown = new DropdownBox();
        statusDropdown.setBounds(539, 576, 404, 57);
        add(statusDropdown);

        SubmitButton submitButton = new SubmitButton("Submit");
        submitButton.setBounds(816, 656, 127, 44);
        add(submitButton);
    }
}

final class SidebarPanel extends JPanel {
    SidebarPanel() {
        setOpaque(true);
        setBackground(Theme.NAVY);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        Theme.applyQuality(g2);

        g2.setColor(Theme.WHITE);
        g2.setFont(Theme.header(Font.BOLD, Theme.HEADING_1));
        g2.drawString("MotorPH", 48, 89);

        drawNavItem(g2, IconType.DASHBOARD, "Dashboard", 48, 158, 95, 174, false);
        drawNavItem(g2, IconType.EMPLOYEES, "Employees", 48, 216, 95, 231, false);
        drawNavItem(g2, IconType.PAYROLL, "Payroll", 48, 254, 95, 268, false);
        drawNavItem(g2, IconType.REQUESTS, "Requests", 48, 296, 95, 310, true);
        drawNavItem(g2, IconType.ATTENDANCE, "Attendance", 48, 339, 95, 353, false);

        drawNavItem(g2, IconType.HELP, "Help Center", 48, 664, 80, 676, false);
        drawNavItem(g2, IconType.LOGOUT, "Log Out", 49, 699, 80, 715, false);

        g2.dispose();
    }

    private void drawNavItem(
            Graphics2D g2,
            IconType iconType,
            String text,
            int iconX,
            int iconY,
            int textX,
            int baseline,
            boolean active
    ) {
        IconPainter.draw(g2, iconType, iconX, iconY, 22, Theme.WHITE);

        g2.setColor(Theme.WHITE);
        g2.setFont(Theme.text(active ? Font.BOLD : Font.PLAIN, Theme.HEADING_2));
        g2.drawString(text, textX, baseline);
    }
}

final class ProfilePanel extends JComponent {
    ProfilePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        Theme.applyQuality(g2);

        g2.setColor(Theme.NAVY);
        g2.setFont(Theme.header(Font.BOLD, Theme.HEADING_2));
        g2.drawString("Name", 5, 24);

        g2.setColor(Theme.PROFILE_GRAY);
        g2.setFont(Theme.text(Font.PLAIN, Theme.PARAGRAPH));
        g2.drawString("Position", 0, 48);

        g2.setColor(Theme.NAVY);
        g2.fillOval(66, 0, 56, 56);

        g2.dispose();
    }
}

final class BackLink extends JComponent {
    private final String text;

    BackLink(String text) {
        this.text = text;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        Theme.applyQuality(g2);

        g2.setColor(Theme.LINK_GRAY);
        g2.setFont(Theme.text(Font.PLAIN, 13));
        g2.drawString(text, 0, 13);

        FontMetrics metrics = g2.getFontMetrics();
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(0, 15, metrics.stringWidth(text), 15);

        g2.dispose();
    }
}

final class FormLabel extends JComponent {
    private final String text;

    FormLabel(String text) {
        this.text = text;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        Theme.applyQuality(g2);

        g2.setColor(Theme.BLACK);
        g2.setFont(Theme.text(Font.PLAIN, 14));
        g2.drawString(text, 0, 15);

        g2.dispose();
    }
}

final class DateInputBox extends JComponent {
    private final String placeholder;

    DateInputBox(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        Theme.applyQuality(g2);

        drawInputBackground(g2);

        g2.setColor(Theme.PLACEHOLDER);
        g2.setFont(Theme.text(Font.PLAIN, 14));
        g2.drawString(placeholder, 15, 34);

        IconPainter.draw(g2, IconType.CALENDAR, getWidth() - 43, 16, 26, Theme.PLACEHOLDER);

        g2.dispose();
    }

    private void drawInputBackground(Graphics2D g2) {
        g2.setColor(Theme.WHITE);
        g2.fill(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 5, 5));

        g2.setColor(Theme.INPUT_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 5, 5));
    }
}

final class DropdownBox extends JComponent {
    DropdownBox() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        Theme.applyQuality(g2);

        g2.setColor(Theme.WHITE);
        g2.fill(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 5, 5));

        g2.setColor(Theme.INPUT_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 5, 5));

        IconPainter.draw(g2, IconType.CHEVRON_DOWN, getWidth() - 42, 21, 26, Theme.PLACEHOLDER);

        g2.dispose();
    }
}

final class NotesBox extends JComponent {
    NotesBox() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        Theme.applyQuality(g2);

        g2.setColor(Theme.WHITE);
        g2.fill(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 5, 5));

        g2.setColor(Theme.INPUT_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 5, 5));

        g2.dispose();
    }
}

final class SubmitButton extends JComponent {
    private final String text;
    private boolean hovered;

    SubmitButton(String text) {
        this.text = text;
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

        Graphics2D g2 = (Graphics2D) graphics.create();
        Theme.applyQuality(g2);

        g2.setColor(hovered ? new Color(4, 24, 115) : Theme.NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Theme.WHITE);
        g2.setFont(Theme.text(Font.PLAIN, 13));

        FontMetrics metrics = g2.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(text)) / 2;
        int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}

enum IconType {
    DASHBOARD,
    EMPLOYEES,
    PAYROLL,
    REQUESTS,
    ATTENDANCE,
    HELP,
    LOGOUT,
    CALENDAR,
    CHEVRON_DOWN
}

final class IconPainter {
    private IconPainter() {}

    static void draw(Graphics2D source, IconType type, int x, int y, int size, Color color) {
        Graphics2D g2 = (Graphics2D) source.create();

        Theme.applyQuality(g2);
        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(
                Math.max(1f, size / 18f),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        switch (type) {
            case DASHBOARD:
                drawDashboard(g2, size);
                break;
            case EMPLOYEES:
                drawEmployees(g2, size);
                break;
            case PAYROLL:
                drawPayroll(g2, size);
                break;
            case REQUESTS:
                drawRequests(g2, size);
                break;
            case ATTENDANCE:
                drawAttendance(g2, size);
                break;
            case HELP:
                drawHelp(g2, size);
                break;
            case LOGOUT:
                drawLogout(g2, size);
                break;
            case CALENDAR:
                drawCalendar(g2, size);
                break;
            case CHEVRON_DOWN:
                drawChevronDown(g2, size);
                break;
            default:
                break;
        }

        g2.dispose();
    }

    private static void drawDashboard(Graphics2D g2, int size) {
        int box = size / 3;
        int radius = Math.max(2, size / 9);

        g2.drawRoundRect(1, 1, box, box, radius, radius);
        g2.drawRoundRect(size / 2, 1, box, box, radius, radius);
        g2.drawRoundRect(1, size / 2, box, box, radius, radius);
        g2.drawRoundRect(size / 2, size / 2, box, box, radius, radius);
    }

    private static void drawEmployees(Graphics2D g2, int size) {
        g2.drawOval(2, 2, size / 3, size / 3);
        g2.drawArc(0, size / 2 - 1, size / 2, size / 2, 0, 180);

        g2.drawLine((int) (size * 0.65), 4, size - 1, 4);
        g2.drawLine((int) (size * 0.65), size / 2, size - 1, size / 2);
        g2.drawLine((int) (size * 0.65), (int) (size * 0.78), size - 2, (int) (size * 0.78));
    }

    private static void drawPayroll(Graphics2D g2, int size) {
        g2.drawRoundRect(2, 2, size - 5, size - 4, 2, 2);
        g2.drawLine(6, 7, size - 7, 7);

        g2.drawRect(6, 11, 3, 3);
        g2.drawRect(12, 11, 3, 3);
        g2.drawRect(6, 17, 3, 3);
        g2.drawRect(12, 17, 3, 3);
    }

    private static void drawRequests(Graphics2D g2, int size) {
        g2.drawRect(2, 2, size - 5, size - 4);
        g2.drawLine(6, 7, size - 8, 7);
        g2.drawLine(6, size / 2, size - 8, size / 2);
        g2.drawLine(7, size - 7, size - 9, size - 7);
    }

    private static void drawAttendance(Graphics2D g2, int size) {
        g2.drawRect(2, 4, size - 5, size - 5);
        g2.drawLine(2, 9, size - 3, 9);
        g2.drawLine(6, 1, 6, 6);
        g2.drawLine(size - 7, 1, size - 7, 6);

        for (int i = 6; i <= size - 8; i += 5) {
            g2.drawLine(i, 13, i + 1, 13);
        }
    }

    private static void drawHelp(Graphics2D g2, int size) {
        g2.draw(new Arc2D.Double(1, size / 2.0 - 3, size / 3.0, size / 3.0, 90, 180, Arc2D.OPEN));
        g2.draw(new Arc2D.Double(size / 4.0, 3, size / 3.0, size / 2.0, 100, -200, Arc2D.OPEN));
        g2.draw(new Arc2D.Double(size / 2.0, size / 3.0 - 1, size / 3.0, size / 3.0, 70, -160, Arc2D.OPEN));
        g2.drawLine(4, (int) (size * .72), size - 4, (int) (size * .72));
    }

    private static void drawLogout(Graphics2D g2, int size) {
        g2.drawRect(2, 2, size / 2, size - 4);
        g2.drawLine(size / 2, size / 2, size - 3, size / 2);
        g2.drawLine(size - 8, size / 2 - 5, size - 3, size / 2);
        g2.drawLine(size - 8, size / 2 + 5, size - 3, size / 2);
    }

    private static void drawCalendar(Graphics2D g2, int size) {
        int w = size - 2;
        int h = size - 3;

        g2.drawRoundRect(1, 3, w, h, 2, 2);
        g2.drawLine(1, 9, w + 1, 9);

        g2.drawLine(7, 1, 7, 6);
        g2.drawLine(size - 7, 1, size - 7, 6);

        int dotSize = 2;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                int px = 6 + col * 5;
                int py = 13 + row * 5;
                g2.fillRect(px, py, dotSize, dotSize);
            }
        }
    }

    private static void drawChevronDown(Graphics2D g2, int size) {
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int leftX = 1;
        int centerX = size / 2;
        int rightX = size - 1;
        int topY = 4;
        int bottomY = 16;

        g2.drawLine(leftX, topY, centerX, bottomY);
        g2.drawLine(rightX, topY, centerX, bottomY);
    }
}

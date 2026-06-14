/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.ui.Undertime;

/**
 *
 * @author Admin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.List;

public class DecisionUndertime extends JFrame {

    public DecisionUndertime() {
        setTitle("MotorPH - Undertime Decision");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        RootPanel rootPanel = new RootPanel();
        setContentPane(rootPanel);
        pack();
        setLocationRelativeTo(null);

        rootPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "exit");

        rootPanel.getActionMap().put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DecisionUndertime().setVisible(true));
    }

    static final class RootPanel extends JPanel {

        RootPanel() {
            setPreferredSize(new Dimension(1280, 800));
            setLayout(null);
            setBackground(Color.WHITE);

            SidebarPanel sidebarPanel = new SidebarPanel();
            sidebarPanel.setBounds(0, 0, 257, 800);
            add(sidebarPanel);

            MainContentPanel mainContentPanel = new MainContentPanel();
            mainContentPanel.setBounds(257, 0, 1023, 800);
            add(mainContentPanel);
        }
    }

    static final class SidebarPanel extends JPanel {

        private final List<NavItem> mainItems = Arrays.asList(
                new NavItem("Dashboard", IconType.DASHBOARD, 49, 158, 96, 174, false),
                new NavItem("Employees", IconType.EMPLOYEES, 49, 216, 96, 230, false),
                new NavItem("Payroll", IconType.PAYROLL, 49, 254, 96, 269, false),
                new NavItem("Requests", IconType.REQUESTS, 49, 296, 96, 312, true),
                new NavItem("Attendance", IconType.ATTENDANCE, 49, 339, 96, 355, false)
        );

        private final List<NavItem> bottomItems = Arrays.asList(
                new NavItem("Help Center", IconType.HELP, 49, 663, 80, 676, false),
                new NavItem("Log Out", IconType.LOGOUT, 49, 699, 80, 718, false)
        );

        SidebarPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = GraphicsHelper.prepare(graphics);

            g.setColor(Theme.NAVY);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(Theme.logoFont());
            g.drawString("MotorPH", 48, 89);

            for (NavItem item : mainItems) {
                drawNavigationItem(g, item);
            }

            for (NavItem item : bottomItems) {
                drawNavigationItem(g, item);
            }

            g.dispose();
        }

        private void drawNavigationItem(Graphics2D g, NavItem item) {
            IconPainter.paint(g, item.iconType, item.iconX, item.iconY, 22, Color.WHITE);

            g.setColor(Color.WHITE);
            g.setFont(item.active ? Theme.navBoldFont() : Theme.navFont());
            g.drawString(item.text, item.textX, item.textBaseline);
        }
    }

    static final class MainContentPanel extends JPanel {

        MainContentPanel() {
            setLayout(null);
            setBackground(Color.WHITE);

            ProfileHeader profileHeader = new ProfileHeader();
            profileHeader.setBounds(820, 63, 125, 60);
            add(profileHeader);

            BackLink backLink = new BackLink();
            backLink.setBounds(63, 163, 40, 18);
            add(backLink);

            addFormLabel("Date", 63, 213);
            InputField dateField = new InputField("mm/dd/yyyy", IconType.CALENDAR);
            dateField.setBounds(63, 242, 404, 57);
            add(dateField);

            addFormLabel("Start Time", 64, 324);
            InputField startTimeField = new InputField("5:00 PM", IconType.CLOCK);
            startTimeField.setBounds(63, 354, 404, 57);
            add(startTimeField);

            addFormLabel("End Time", 64, 435);
            InputField endTimeField = new InputField("9:00 PM", IconType.CLOCK);
            endTimeField.setBounds(63, 465, 404, 57);
            add(endTimeField);

            addFormLabel("Reason", 539, 213);
            DropdownField reasonField = new DropdownField();
            reasonField.setBounds(540, 242, 403, 57);
            add(reasonField);

            addFormLabel("Notes", 539, 324);
            NotesField notesField = new NotesField();
            notesField.setBounds(540, 354, 403, 168);
            add(notesField);

            addFormLabel("Status", 539, 547);
            DropdownField statusField = new DropdownField();
            statusField.setBounds(540, 577, 403, 56);
            add(statusField);

            DecisionButton approveButton = new DecisionButton("Approve", Theme.APPROVE_GREEN);
            approveButton.setBounds(684, 656, 126, 44);
            add(approveButton);

            DecisionButton rejectButton = new DecisionButton("Reject", Theme.REJECT_RED);
            rejectButton.setBounds(816, 656, 126, 44);
            add(rejectButton);
        }

        private void addFormLabel(String text, int x, int y) {
            FormLabel label = new FormLabel(text);
            label.setBounds(x, y, 130, 20);
            add(label);
        }
    }

    static final class ProfileHeader extends JComponent {

        ProfileHeader() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = GraphicsHelper.prepare(graphics);

            g.setFont(Theme.profileNameFont());
            g.setColor(Theme.NAVY);
            g.drawString("Name", 5, 24);

            g.setFont(Theme.profilePositionFont());
            g.setColor(Theme.MUTED_TEXT);
            g.drawString("Position", 0, 47);

            g.setColor(Theme.NAVY);
            g.fillOval(67, 0, 56, 56);

            g.dispose();
        }
    }

    static final class BackLink extends JComponent {

        BackLink() {
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = GraphicsHelper.prepare(graphics);

            String text = "Back";

            g.setFont(Theme.smallLinkFont());
            g.setColor(Theme.LINK_GRAY);
            g.drawString(text, 0, 12);

            FontMetrics metrics = g.getFontMetrics();
            g.drawLine(0, 14, metrics.stringWidth(text), 14);

            g.dispose();
        }
    }

    static final class FormLabel extends JLabel {

        FormLabel(String text) {
            super(text);
            setFont(Theme.paragraphFont());
            setForeground(Color.BLACK);
            setOpaque(false);
        }
    }

    static class InputField extends JComponent {

        private final String placeholder;
        private final IconType trailingIcon;

        InputField(String placeholder, IconType trailingIcon) {
            this.placeholder = placeholder;
            this.trailingIcon = trailingIcon;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = GraphicsHelper.prepare(graphics);

            drawFieldBase(g);

            g.setFont(Theme.placeholderFont());
            g.setColor(Theme.PLACEHOLDER);
            g.drawString(placeholder, 15, 35);

            IconPainter.paint(g, trailingIcon, getWidth() - 43, 17, 28, Theme.ICON_LIGHT_GRAY);

            g.dispose();
        }

        protected void drawFieldBase(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g.setColor(Theme.FIELD_BORDER);
            g.setStroke(new BasicStroke(1.25f));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
        }
    }

    static final class DropdownField extends JComponent {

        DropdownField() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = GraphicsHelper.prepare(graphics);

            g.setColor(Color.WHITE);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g.setColor(Theme.FIELD_BORDER);
            g.setStroke(new BasicStroke(1.25f));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            IconPainter.paint(g, IconType.CHEVRON_DOWN, getWidth() - 42, 23, 27, Theme.ICON_LIGHT_GRAY);

            g.dispose();
        }
    }

    static final class NotesField extends JComponent {

        NotesField() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = GraphicsHelper.prepare(graphics);

            g.setColor(Color.WHITE);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g.setColor(Theme.FIELD_BORDER);
            g.setStroke(new BasicStroke(1.25f));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

            g.dispose();
        }
    }

    static final class DecisionButton extends JComponent {

        private final String text;
        private final Color background;

        DecisionButton(String text, Color background) {
            this.text = text;
            this.background = background;
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = GraphicsHelper.prepare(graphics);

            g.setColor(background);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setFont(Theme.buttonFont());
            g.setColor(Color.WHITE);

            FontMetrics metrics = g.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(text)) / 2;
            int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

            g.drawString(text, textX, textY);

            g.dispose();
        }
    }

    static final class NavItem {

        final String text;
        final IconType iconType;
        final int iconX;
        final int iconY;
        final int textX;
        final int textBaseline;
        final boolean active;

        NavItem(
                String text,
                IconType iconType,
                int iconX,
                int iconY,
                int textX,
                int textBaseline,
                boolean active
        ) {
            this.text = text;
            this.iconType = iconType;
            this.iconX = iconX;
            this.iconY = iconY;
            this.textX = textX;
            this.textBaseline = textBaseline;
            this.active = active;
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
        CLOCK,
        CHEVRON_DOWN
    }

    static final class IconPainter {

        private IconPainter() {
        }

        static void paint(Graphics2D g, IconType type, int x, int y, int size, Color color) {
            Graphics2D copy = (Graphics2D) g.create();

            copy.translate(x, y);
            copy.setColor(color);
            copy.setStroke(new BasicStroke(1.35f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case DASHBOARD:
                    drawDashboard(copy);
                    break;
                case EMPLOYEES:
                    drawEmployees(copy);
                    break;
                case PAYROLL:
                    drawPayroll(copy);
                    break;
                case REQUESTS:
                    drawRequests(copy);
                    break;
                case ATTENDANCE:
                    drawAttendance(copy);
                    break;
                case HELP:
                    drawHelp(copy);
                    break;
                case LOGOUT:
                    drawLogout(copy);
                    break;
                case CALENDAR:
                    drawCalendar(copy);
                    break;
                case CLOCK:
                    drawClock(copy);
                    break;
                case CHEVRON_DOWN:
                    drawChevronDown(copy);
                    break;
                default:
                    break;
            }

            copy.dispose();
        }

        private static void drawDashboard(Graphics2D g) {
            g.drawRoundRect(0, 0, 8, 8, 2, 2);
            g.drawRoundRect(13, 0, 8, 8, 2, 2);
            g.drawRoundRect(0, 13, 8, 8, 2, 2);
            g.drawRoundRect(13, 13, 8, 8, 2, 2);
        }

        private static void drawEmployees(Graphics2D g) {
            g.drawOval(1, 1, 9, 9);
            g.draw(new Arc2D.Double(0, 10, 13, 12, 20, 140, Arc2D.OPEN));
            g.drawLine(15, 4, 22, 4);
            g.drawLine(15, 10, 22, 10);
            g.drawLine(15, 16, 22, 16);
        }

        private static void drawPayroll(Graphics2D g) {
            g.drawRoundRect(1, 1, 18, 19, 2, 2);
            g.drawLine(5, 6, 15, 6);
            g.drawLine(5, 11, 8, 11);
            g.drawLine(11, 11, 15, 11);
            g.drawLine(5, 16, 8, 16);
            g.drawLine(11, 16, 15, 16);
        }

        private static void drawRequests(Graphics2D g) {
            g.drawRoundRect(1, 1, 18, 19, 2, 2);
            g.drawLine(5, 6, 15, 6);
            g.drawLine(5, 11, 15, 11);
            g.drawLine(5, 16, 11, 16);
        }

        private static void drawAttendance(Graphics2D g) {
            g.drawRoundRect(1, 3, 19, 18, 2, 2);
            g.drawLine(1, 8, 20, 8);
            g.drawLine(6, 0, 6, 5);
            g.drawLine(15, 0, 15, 5);
            g.drawLine(5, 12, 8, 12);
            g.drawLine(11, 12, 14, 12);
            g.drawLine(5, 16, 8, 16);
        }

        private static void drawHelp(Graphics2D g) {
            Path2D cloud = new Path2D.Double();

            cloud.moveTo(3, 14);
            cloud.curveTo(1, 14, 0, 12, 1, 10);
            cloud.curveTo(1, 8, 3, 7, 5, 7);
            cloud.curveTo(6, 4, 9, 3, 12, 5);
            cloud.curveTo(14, 3, 18, 5, 18, 9);
            cloud.curveTo(21, 9, 22, 11, 21, 13);
            cloud.curveTo(21, 15, 19, 16, 17, 16);
            cloud.lineTo(4, 16);
            cloud.curveTo(4, 16, 3, 15, 3, 14);

            g.draw(cloud);
        }

        private static void drawLogout(Graphics2D g) {
            g.drawRect(2, 1, 13, 20);
            g.drawLine(15, 7, 22, 7);
            g.drawLine(22, 7, 18, 3);
            g.drawLine(22, 7, 18, 11);
            g.drawLine(7, 11, 18, 11);
        }

        private static void drawCalendar(Graphics2D g) {
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g.drawRoundRect(0, 2, 25, 23, 3, 3);
            g.drawLine(0, 8, 25, 8);
            g.drawLine(6, 0, 6, 5);
            g.drawLine(19, 0, 19, 5);

            g.fillOval(5, 12, 2, 2);
            g.fillOval(11, 12, 2, 2);
            g.fillOval(17, 12, 2, 2);
            g.fillOval(5, 17, 2, 2);
            g.fillOval(11, 17, 2, 2);
            g.fillOval(17, 17, 2, 2);
        }

        private static void drawClock(Graphics2D g) {
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g.drawOval(0, 0, 25, 25);
            g.drawLine(13, 5, 13, 13);
            g.drawLine(13, 13, 19, 16);
        }

        private static void drawChevronDown(Graphics2D g) {
            g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(0, 0, 13, 13);
            g.drawLine(13, 13, 26, 0);
        }
    }

    static final class GraphicsHelper {

        private GraphicsHelper() {
        }

        static Graphics2D prepare(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            return g;
        }
    }

    static final class Theme {

        static final Color NAVY = new Color(2, 19, 98);
        static final Color FIELD_BORDER = new Color(74, 74, 74);
        static final Color PLACEHOLDER = new Color(217, 217, 217);
        static final Color ICON_LIGHT_GRAY = new Color(207, 207, 207);
        static final Color MUTED_TEXT = new Color(154, 154, 154);
        static final Color LINK_GRAY = new Color(83, 83, 83);
        static final Color APPROVE_GREEN = new Color(0, 191, 99);
        static final Color REJECT_RED = new Color(255, 87, 87);

        private Theme() {
        }

        private static Font font(String family, int style, int size) {
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] availableFonts = environment.getAvailableFontFamilyNames();

            for (String availableFont : availableFonts) {
                if (availableFont.equalsIgnoreCase(family)) {
                    return new Font(availableFont, style, size);
                }
            }

            return new Font("SansSerif", style, size);
        }

        static Font logoFont() {
            return font("Segoe UI", Font.BOLD, 28);
        }

        static Font navFont() {
            return font("Open Sans", Font.PLAIN, 18);
        }

        static Font navBoldFont() {
            return font("Open Sans", Font.BOLD, 18);
        }

        static Font paragraphFont() {
            return font("Open Sans", Font.PLAIN, 15);
        }

        static Font smallLinkFont() {
            return font("Open Sans", Font.PLAIN, 13);
        }

        static Font placeholderFont() {
            return font("Open Sans", Font.PLAIN, 13);
        }

        static Font profileNameFont() {
            return font("Segoe UI", Font.BOLD, 18);
        }

        static Font profilePositionFont() {
            return font("Open Sans", Font.PLAIN, 15);
        }

        static Font buttonFont() {
            return font("Open Sans", Font.PLAIN, 13);
        }
    }
}

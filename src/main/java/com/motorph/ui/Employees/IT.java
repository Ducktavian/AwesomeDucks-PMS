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
import com.motorph.ui.Employees.SearchBox;
import com.motorph.ui.Employees.ProfileBlock;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class IT {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

final class UIConstants {
    static final int FRAME_WIDTH = 1280;
    static final int FRAME_HEIGHT = 800;

    static final int SIDEBAR_WIDTH = 257;

    static final Color NAVY = new Color(2, 19, 98);
    static final Color WHITE = Color.WHITE;
    static final Color BLACK = Color.BLACK;
    static final Color ROW_GRAY = new Color(217, 217, 217);
    static final Color LIGHT_BORDER = new Color(207, 207, 207);
    static final Color PLACEHOLDER = new Color(210, 210, 210);
    static final Color SUBTEXT = new Color(150, 150, 150);

    static final String HEADER_FONT = "Segoe UI";
    static final String TEXT_FONT = "Open Sans";

    static final Font FONT_H1 = new Font(HEADER_FONT, Font.BOLD, 28);
    static final Font FONT_H2 = new Font(TEXT_FONT, Font.PLAIN, 18);
    static final Font FONT_H2_BOLD = new Font(TEXT_FONT, Font.BOLD, 18);
    static final Font FONT_PARAGRAPH = new Font(TEXT_FONT, Font.PLAIN, 15);
    static final Font FONT_PARAGRAPH_BOLD = new Font(TEXT_FONT, Font.BOLD, 15);

    private UIConstants() {}
}

class MainFrame extends JFrame {

    MainFrame() {
        setTitle("MotorPH - IT Employees");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(null);
        root.setPreferredSize(new Dimension(UIConstants.FRAME_WIDTH, UIConstants.FRAME_HEIGHT));
        root.setBackground(UIConstants.WHITE);

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setBounds(0, 0, UIConstants.SIDEBAR_WIDTH, UIConstants.FRAME_HEIGHT);

        ContentPanel content = new ContentPanel();
        content.setBounds(
                UIConstants.SIDEBAR_WIDTH,
                0,
                UIConstants.FRAME_WIDTH - UIConstants.SIDEBAR_WIDTH,
                UIConstants.FRAME_HEIGHT
        );

        root.add(sidebar);
        root.add(content);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }
}

class SidebarPanel extends JPanel {

    SidebarPanel() {
        setLayout(null);
        setBackground(UIConstants.NAVY);

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(UIConstants.FONT_H1);
        logo.setForeground(UIConstants.WHITE);
        logo.setBounds(48, 63, 150, 40);
        add(logo);

        addNavigationItems();
        addBottomItems();
    }

    private void addNavigationItems() {
        NavigationItem dashboard = new NavigationItem(
                new DashboardIcon(22, UIConstants.WHITE),
                "Dashboard",
                false
        );
        dashboard.setBounds(48, 154, 170, 34);
        add(dashboard);

        NavigationItem employees = new NavigationItem(
                new EmployeeIcon(22, UIConstants.WHITE),
                "Employees",
                true
        );
        employees.setBounds(48, 211, 180, 34);
        add(employees);

        NavigationItem payroll = new NavigationItem(
                new PayrollIcon(22, UIConstants.WHITE),
                "Payroll",
                false
        );
        payroll.setBounds(48, 249, 170, 34);
        add(payroll);

        NavigationItem requests = new NavigationItem(
                new RequestIcon(22, UIConstants.WHITE),
                "Requests",
                false
        );
        requests.setBounds(48, 291, 170, 34);
        add(requests);

        NavigationItem attendance = new NavigationItem(
                new AttendanceIcon(22, UIConstants.WHITE),
                "Attendance",
                false
        );
        attendance.setBounds(48, 335, 180, 34);
        add(attendance);
    }

    private void addBottomItems() {
        NavigationItem help = new NavigationItem(
                new HelpIcon(22, UIConstants.WHITE),
                "Help Center",
                false
        );
        help.setBounds(48, 654, 180, 34);
        add(help);

        NavigationItem logout = new NavigationItem(
                new LogoutIcon(22, UIConstants.WHITE),
                "Log Out",
                false
        );
        logout.setBounds(49, 696, 160, 34);
        add(logout);
    }
}

class NavigationItem extends JPanel {

    NavigationItem(Icon icon, String text, boolean active) {
        setLayout(null);
        setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBounds(0, 5, 24, 24);
        add(iconLabel);

        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(UIConstants.WHITE);
        textLabel.setFont(active ? UIConstants.FONT_H2_BOLD : UIConstants.FONT_H2);
        textLabel.setBounds(47, 3, 130, 28);
        add(textLabel);
    }
}

class ContentPanel extends JPanel {

    ContentPanel() {
        setLayout(null);
        setBackground(UIConstants.WHITE);

        SearchBox searchBox = new SearchBox();
        searchBox.setBounds(78, 97, 305, 39);
        add(searchBox);

        ProfileBlock profileBlock = new ProfileBlock();
        profileBlock.setBounds(820, 40, 123, 58);
        add(profileBlock);

        addActionButtons();

        EmployeeTableSkeleton table = new EmployeeTableSkeleton();
        table.setBounds(78, 216, 865, 500);
        add(table);
    }

    private void addActionButtons() {
        ActionButton addButton = new ActionButton("Add", new AddIcon(16, UIConstants.WHITE));
        addButton.setBounds(576, 159, 88, 37);
        add(addButton);

        ActionButton updateButton = new ActionButton("Update", new EditIcon(16, UIConstants.WHITE));
        updateButton.setBounds(669, 159, 88, 37);
        add(updateButton);

        ActionButton deleteButton = new ActionButton("Delete", new DeleteIcon(16, UIConstants.WHITE));
        deleteButton.setBounds(762, 159, 88, 37);
        add(deleteButton);

        ActionButton refreshButton = new ActionButton("Refresh", new RefreshIcon(16, UIConstants.WHITE));
        refreshButton.setBounds(855, 159, 88, 37);
        add(refreshButton);
    }
}

class ProfileBlock extends JPanel {

    ProfileBlock() {
        setLayout(null);
        setOpaque(false);

        JLabel name = new JLabel("Name");
        name.setFont(UIConstants.FONT_H2_BOLD);
        name.setForeground(UIConstants.NAVY);
        name.setBounds(5, 5, 70, 20);
        add(name);

        JLabel position = new JLabel("Position");
        position.setFont(UIConstants.FONT_PARAGRAPH);
        position.setForeground(UIConstants.SUBTEXT);
        position.setBounds(0, 28, 80, 22);
        add(position);

        AvatarCircle avatar = new AvatarCircle();
        avatar.setBounds(66, 0, 58, 58);
        add(avatar);
    }
}

class AvatarCircle extends JComponent {

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(UIConstants.NAVY);
        g2.fillOval(1, 0, 56, 56);

        g2.dispose();
    }
}

class SearchBox extends JPanel {

    private final JTextField textField;

    SearchBox() {
        setLayout(null);
        setOpaque(false);

        textField = new JTextField();
        textField.setBorder(new EmptyBorder(0, 36, 0, 8));
        textField.setFont(UIConstants.FONT_H2);
        textField.setForeground(Color.BLACK);
        textField.setOpaque(false);
        textField.setText("Search");
        textField.setForeground(UIConstants.PLACEHOLDER);
        textField.setBounds(0, 0, 305, 39);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                if ("Search".equals(textField.getText())) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (textField.getText().isEmpty()) {
                    textField.setText("Search");
                    textField.setForeground(UIConstants.PLACEHOLDER);
                }
            }
        });

        add(textField);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D fieldShape = new RoundRectangle2D.Double(
                0.5,
                0.5,
                getWidth() - 1,
                getHeight() - 1,
                5,
                5
        );

        g2.setColor(UIConstants.WHITE);
        g2.fill(fieldShape);

        g2.setColor(UIConstants.LIGHT_BORDER);
        g2.draw(fieldShape);

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(UIConstants.PLACEHOLDER);
        g2.drawOval(12, 11, 13, 13);
        g2.drawLine(23, 23, 30, 30);

        g2.dispose();
    }
}

class ActionButton extends JButton {

    ActionButton(String text, Icon icon) {
        super(text, icon);

        setFont(new Font(UIConstants.TEXT_FONT, Font.PLAIN, 13));
        setForeground(UIConstants.WHITE);
        setBackground(UIConstants.NAVY);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setIconTextGap(9);
        setHorizontalAlignment(SwingConstants.CENTER);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setColor(UIConstants.NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();

        super.paintComponent(graphics);
    }
}

class EmployeeTableSkeleton extends JPanel {

    EmployeeTableSkeleton() {
        setLayout(null);
        setOpaque(false);

        addHeaderLabel("Employee No.", 18, 0, 120, 44);
        addHeaderLabel("Name", 145, 0, 90, 44);
        addHeaderLabel("Status", 285, 0, 90, 44);
        addHeaderLabel("Position", 445, 0, 100, 44);

        JLabel supervisor = new JLabel("<html>Immediate<br>Supervisor</html>");
        supervisor.setFont(new Font(UIConstants.TEXT_FONT, Font.BOLD, 13));
        supervisor.setForeground(UIConstants.BLACK);
        supervisor.setBounds(604, 0, 140, 44);
        add(supervisor);

        addHeaderLabel("Role", 742, 0, 80, 44);
    }

    private void addHeaderLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UIConstants.TEXT_FONT, Font.BOLD, 13));
        label.setForeground(UIConstants.BLACK);
        label.setBounds(x, y, width, height);
        add(label);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();

        g2.setColor(UIConstants.BLACK);
        g2.fillRect(2, 55, 860, 3);

        g2.setColor(UIConstants.ROW_GRAY);
        g2.fillRect(2, 114, 863, 55);
        g2.fillRect(0, 224, 865, 57);
        g2.fillRect(2, 333, 863, 51);
        g2.fillRect(0, 436, 865, 57);

        g2.dispose();
    }
}

abstract class LineIcon implements Icon {

    private final int size;
    private final Color color;

    LineIcon(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    protected int size() {
        return size;
    }

    protected Color color() {
        return color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    protected Graphics2D prepare(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);
        return g2;
    }
}

class DashboardIcon extends LineIcon {

    DashboardIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawRoundRect(1, 1, 8, 8, 2, 2);
        g2.drawRoundRect(13, 1, 8, 8, 2, 2);
        g2.drawRoundRect(1, 13, 8, 8, 2, 2);
        g2.drawRoundRect(13, 13, 8, 8, 2, 2);

        g2.dispose();
    }
}

class EmployeeIcon extends LineIcon {

    EmployeeIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawOval(3, 2, 9, 9);
        g2.drawArc(0, 12, 16, 10, 0, 180);

        g2.drawLine(17, 5, 22, 5);
        g2.drawLine(17, 11, 22, 11);
        g2.drawLine(17, 17, 22, 17);

        g2.dispose();
    }
}

class PayrollIcon extends LineIcon {

    PayrollIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawRoundRect(3, 2, 16, 19, 1, 1);
        g2.drawRect(6, 5, 4, 4);
        g2.drawLine(12, 6, 17, 6);
        g2.drawLine(6, 12, 17, 12);
        g2.drawLine(6, 16, 17, 16);

        g2.dispose();
    }
}

class RequestIcon extends LineIcon {

    RequestIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawRoundRect(3, 2, 16, 19, 1, 1);
        g2.drawLine(6, 7, 16, 7);
        g2.drawLine(6, 12, 16, 12);
        g2.drawLine(6, 17, 13, 17);

        g2.dispose();
    }
}

class AttendanceIcon extends LineIcon {

    AttendanceIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawRoundRect(2, 4, 18, 16, 1, 1);
        g2.drawLine(2, 8, 20, 8);

        g2.drawLine(6, 1, 6, 6);
        g2.drawLine(10, 1, 10, 6);
        g2.drawLine(14, 1, 14, 6);
        g2.drawLine(18, 1, 18, 6);

        g2.dispose();
    }
}

class HelpIcon extends LineIcon {

    HelpIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        Path2D cloud = new Path2D.Double();
        cloud.moveTo(4, 16);
        cloud.curveTo(1, 16, 1, 12, 5, 12);
        cloud.curveTo(5, 8, 10, 7, 12, 10);
        cloud.curveTo(15, 8, 19, 10, 19, 14);
        cloud.curveTo(21, 14, 22, 16, 20, 18);
        cloud.lineTo(5, 18);
        cloud.curveTo(4, 18, 4, 17, 4, 16);
        g2.draw(cloud);

        g2.dispose();
    }
}

class LogoutIcon extends LineIcon {

    LogoutIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawRect(3, 2, 12, 18);
        g2.drawLine(15, 11, 22, 11);
        g2.drawLine(18, 7, 22, 11);
        g2.drawLine(18, 15, 22, 11);

        g2.dispose();
    }
}

class AddIcon extends LineIcon {

    AddIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.setStroke(new BasicStroke(1.6f));
        g2.drawLine(8, 2, 8, 14);
        g2.drawLine(2, 8, 14, 8);

        g2.dispose();
    }
}

class EditIcon extends LineIcon {

    EditIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawLine(3, 13, 4, 16);
        g2.drawLine(4, 16, 7, 15);
        g2.drawLine(3, 13, 12, 4);
        g2.drawLine(7, 15, 16, 6);
        g2.drawLine(12, 4, 14, 2);
        g2.drawLine(16, 6, 14, 2);

        g2.dispose();
    }
}

class DeleteIcon extends LineIcon {

    DeleteIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawLine(3, 5, 15, 5);
        g2.drawLine(6, 3, 12, 3);
        g2.drawRect(5, 6, 9, 10);
        g2.drawLine(7, 8, 7, 14);
        g2.drawLine(10, 8, 10, 14);
        g2.drawLine(13, 8, 13, 14);

        g2.dispose();
    }
}

class RefreshIcon extends LineIcon {

    RefreshIcon(int size, Color color) {
        super(size, color);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g2 = prepare(graphics);
        g2.translate(x, y);

        g2.drawArc(2, 2, 13, 13, 45, 270);
        g2.drawLine(13, 1, 15, 5);
        g2.drawLine(13, 1, 9, 2);

        g2.dispose();
    }
}

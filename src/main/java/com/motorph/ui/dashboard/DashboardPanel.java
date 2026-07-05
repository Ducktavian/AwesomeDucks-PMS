package com.motorph.ui.dashboard;

import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.motorph.model.Dispute;
import com.motorph.model.DisputeStatus;
import com.motorph.model.Employee;
import com.motorph.model.FinancialSummary;
import com.motorph.model.LeaveRequest;
import com.motorph.model.OvertimeRequest;
import com.motorph.model.PayPeriod;
import com.motorph.model.Request;
import com.motorph.model.RequestStatus;
import com.motorph.model.Role;
import com.motorph.model.UndertimeRequest;
import com.motorph.service.EmployeeService;
import com.motorph.service.InformationDisputeService;
import com.motorph.service.PayrollDisputeService;
import com.motorph.service.PayrollService;
import com.motorph.service.RequestService;
import com.motorph.util.AppContext;
import com.motorph.util.Session;

public class DashboardPanel extends JPanel {

    private static final Color NAVY = new Color(7, 24, 105);
    private static final Color GRID_GRAY = new Color(200, 200, 200);
    private static final Color EXPENSE_GRAY = new Color(150, 150, 150);
    private static final String FONT = "Segoe UI";

    private final EmployeeService employeeService = AppContext.getEmployeeService();
    private final PayrollService payrollService = AppContext.getPayrollService();
    private final RequestService requestService = AppContext.getRequestService();
    private final InformationDisputeService informationDisputeService = AppContext.getInformationDisputeService();
    private final PayrollDisputeService payrollDisputeService = AppContext.getPayrollDisputeService();

    private JPanel contentSwitcher;
    private CardLayout cardLayout;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(40, 55, 35, 55));

        String currentRole = getCurrentUserRole();

        JComboBox<String> roleDropdown = new JComboBox<>(getAllowedDashboards(currentRole));
        roleDropdown.setPreferredSize(new Dimension(130, 36));
        roleDropdown.setFont(new Font(FONT, Font.PLAIN, 14));
        roleDropdown.setBackground(Color.WHITE);
        roleDropdown.setFocusable(false);

        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        dropdownPanel.setOpaque(false);
        dropdownPanel.add(roleDropdown);

        cardLayout = new CardLayout();
        contentSwitcher = new JPanel(cardLayout);
        contentSwitcher.setOpaque(false);

        contentSwitcher.add(createAdminDashboard(), "Admin");
        contentSwitcher.add(createFinanceDashboard(), "Finance");
        contentSwitcher.add(createHRDashboard(), "HR");
        contentSwitcher.add(createITDashboard(), "IT");
        contentSwitcher.add(createEmployeeDashboard(), "Employee");

        roleDropdown.addActionListener(e -> {
            String selected = roleDropdown.getSelectedItem().toString();
            cardLayout.show(contentSwitcher, selected);
        });

        wrapper.add(dropdownPanel, BorderLayout.NORTH);
        wrapper.add(contentSwitcher, BorderLayout.CENTER);

        String firstDashboard = roleDropdown.getSelectedItem().toString();
        SwingUtilities.invokeLater(() -> cardLayout.show(contentSwitcher, firstDashboard));

        return wrapper;
    }

    private String getCurrentUserRole() {
        if (Session.getCurrentUser() == null || Session.getCurrentUser().getRole() == null) {
            return "Employee";
        }

        return roleToDashboard(Session.getCurrentUser().getRole());
    }

    private String roleToDashboard(Role role) {
        if (role == null) {
            return "Employee";
        }

        return switch (role) {
            case ADMIN -> "Admin";
            case HR -> "HR";
            case IT -> "IT";
            case FINANCE -> "Finance";
            default -> "Employee";
        };
    }

    private String[] getAllowedDashboards(String role) {
        switch (role) {
            case "Admin":
                return new String[]{"Admin", "Finance", "HR", "IT", "Employee"};
            case "Finance":
                return new String[]{"Finance", "Employee"};
            case "HR":
                return new String[]{"HR", "Employee"};
            case "IT":
                return new String[]{"IT", "Employee"};
            case "Employee":
            default:
                return new String[]{"Employee"};
        }
    }

    private JPanel createAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        int totalEmployees = employeeService.getAllEmployees().size();

        QuarterInfo currentQuarter = getCurrentQuarter();
        QuarterInfo upcomingQuarter = getUpcomingQuarter(currentQuarter);

        JPanel cardsPanel = createCardsPanel();

        cardsPanel.add(createCard(
                "Total Number of Employees",
                NumberFormat.getNumberInstance(Locale.US).format(totalEmployees),
                null
        ));

        cardsPanel.add(createCard(
                "On-Going Quarter",
                currentQuarter.name,
                currentQuarter.months + " " + currentQuarter.year
        ));

        cardsPanel.add(createCard(
                "Upcoming Quarter",
                upcomingQuarter.name,
                upcomingQuarter.months + " " + upcomingQuarter.year
        ));

        JPanel chartArea = new JPanel(new BorderLayout());
        chartArea.setOpaque(false);
        chartArea.add(createSectionTitle("Financial Overview"), BorderLayout.NORTH);
        chartArea.add(new FinancialChartPanel(payrollService.getFinancialSummary()), BorderLayout.CENTER);

        panel.add(cardsPanel, BorderLayout.NORTH);
        panel.add(chartArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFinanceDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        int pendingPayroll = payrollService.getPendingPayrollCount();
        PayPeriod currentPeriod = payrollService.getCurrentPayPeriod();
        PayPeriod upcomingPeriod = payrollService.getUpcomingPayPeriod();

        JPanel cardsPanel = createCardsPanel();
        cardsPanel.add(createCard("Pending Payroll", String.valueOf(pendingPayroll), null));
        cardsPanel.add(createCard("On-Going Period",
                payPeriodMonthLabel(currentPeriod), payPeriodDateRange(currentPeriod)));
        cardsPanel.add(createCard("Upcoming Period",
                payPeriodMonthLabel(upcomingPeriod), payPeriodDateRange(upcomingPeriod)));

        JPanel chartArea = new JPanel(new BorderLayout());
        chartArea.setOpaque(false);
        chartArea.add(createSectionTitle("Financial Overview"), BorderLayout.NORTH);
        chartArea.add(new FinancialChartPanel(payrollService.getFinancialSummary()), BorderLayout.CENTER);

        panel.add(cardsPanel, BorderLayout.NORTH);
        panel.add(chartArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEmployeeDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        PayPeriod currentPeriod = payrollService.getCurrentPayPeriod();

        JPanel cardsPanel = createCardsPanel();
        cardsPanel.add(createCard("On-Going Payroll Period",
                payPeriodMonthLabel(currentPeriod), payPeriodDateRange(currentPeriod)));
        cardsPanel.add(createMultiValueCard(
                "Total Hours Worked",
                new String[]{"80", "10", "0"},
                new String[]{"Regular", "Overtime", "Holiday"},
                3
        ));
        cardsPanel.add(createMultiValueCard(
                "Available Leave",
                new String[]{"15", "15"},
                new String[]{"Vacation Leave", "Sick Leave"},
                2
        ));

        JPanel chartArea = new JPanel(new BorderLayout());
        chartArea.setOpaque(false);
        chartArea.add(createSectionTitle("Attendance History"), BorderLayout.NORTH);
        chartArea.add(new AttendanceChartPanel(), BorderLayout.CENTER);

        panel.add(cardsPanel, BorderLayout.NORTH);
        panel.add(chartArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createITDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        int totalEmployees = employeeService.getAllEmployees().size();

        // Help center tickets are stored as disputes (information + payroll) in the
        // `dispute` table - count them by resolution status for the IT cards.
        List<Dispute> allDisputes = new ArrayList<>();
        allDisputes.addAll(informationDisputeService.findAll());
        allDisputes.addAll(payrollDisputeService.findAll());

        long pendingTickets = allDisputes.stream()
                .filter(d -> d.getStatus() == DisputeStatus.UNRESOLVED)
                .count();
        long resolvedTickets = allDisputes.stream()
                .filter(d -> d.getStatus() == DisputeStatus.RESOLVED)
                .count();

        JPanel cardsPanel = createCardsPanel();
        cardsPanel.add(createCard("Total Number of Employees",
                NumberFormat.getNumberInstance(Locale.US).format(totalEmployees), null));
        cardsPanel.add(createCard("Pending Tickets", String.valueOf(pendingTickets), null));
        cardsPanel.add(createCard("Resolved Tickets", String.valueOf(resolvedTickets), null));

        JPanel chartArea = new JPanel(new BorderLayout());
        chartArea.setOpaque(false);
        chartArea.add(createSectionTitle("IT Support History"), BorderLayout.NORTH);
        chartArea.add(new ITSupportChartPanel(), BorderLayout.CENTER);

        panel.add(cardsPanel, BorderLayout.NORTH);
        panel.add(chartArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHRDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        int totalEmployees = employeeService.getAllEmployees().size();
        List<Request> allRequests = requestService.findAll();

        JPanel cardsPanel = createCardsPanel();
        cardsPanel.add(createCard("Total Number of Employees",
                NumberFormat.getNumberInstance(Locale.US).format(totalEmployees), null));
        cardsPanel.add(createMultiValueCard(
                "Leave Request",
                countLeaveRequestsByStatus(allRequests),
                new String[]{"Pending", "Approved", "Rejected"},
                3
        ));
        cardsPanel.add(createMultiValueCard(
                "Other Requests",
                countPendingWorkTimeRequests(allRequests),
                new String[]{"Pending OT", "Pending UT"},
                2
        ));

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        tablesPanel.setOpaque(false);
        tablesPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        tablesPanel.add(createHRTablePanel("On Leave Today", "Reason", employeesOnLeaveToday(allRequests)));
        tablesPanel.add(createHRTablePanel("On Overtime Today", "Reason", employeesOnOvertimeToday(allRequests)));

        panel.add(cardsPanel, BorderLayout.NORTH);
        panel.add(tablesPanel, BorderLayout.CENTER);

        return panel;
    }

    private String payPeriodMonthLabel(PayPeriod period) {
        return period != null ? period.getPeriodStartDate().format(DateTimeFormatter.ofPattern("MMMM")) : "N/A";
    }

    private String payPeriodDateRange(PayPeriod period) {
        if (period == null) {
            return null;
        }

        LocalDate start = period.getPeriodStartDate();
        LocalDate end = period.getPeriodEndDate();

        if (start.getMonth() == end.getMonth() && start.getYear() == end.getYear()) {
            return start.getDayOfMonth() + "-" + end.getDayOfMonth() + ", " + end.getYear();
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d");
        return start.format(fmt) + " - " + end.format(fmt) + ", " + end.getYear();
    }

    private String[] countLeaveRequestsByStatus(List<Request> allRequests) {
        int pending = 0, approved = 0, rejected = 0;

        for (Request request : allRequests) {
            if (!(request instanceof LeaveRequest)) {
                continue;
            }

            switch (request.getStatus()) {
                case PENDING -> pending++;
                case APPROVED -> approved++;
                case REJECTED -> rejected++;
                default -> { }
            }
        }

        return new String[]{String.valueOf(pending), String.valueOf(approved), String.valueOf(rejected)};
    }

    private String[] countPendingWorkTimeRequests(List<Request> allRequests) {
        int pendingOvertime = 0, pendingUndertime = 0;

        for (Request request : allRequests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                continue;
            }

            if (request instanceof OvertimeRequest) {
                pendingOvertime++;
            } else if (request instanceof UndertimeRequest) {
                pendingUndertime++;
            }
        }

        return new String[]{String.valueOf(pendingOvertime), String.valueOf(pendingUndertime)};
    }

    private List<String[]> employeesOnLeaveToday(List<Request> allRequests) {
        LocalDate today = LocalDate.now();
        List<String[]> rows = new ArrayList<>();

        for (Request request : allRequests) {
            if (!(request instanceof LeaveRequest leaveRequest)) {
                continue;
            }

            if (leaveRequest.getStatus() != RequestStatus.APPROVED) {
                continue;
            }

            if (today.isBefore(leaveRequest.getStartDate()) || today.isAfter(leaveRequest.getEndDate())) {
                continue;
            }

            Employee employee = employeeService.findEmployee(String.valueOf(leaveRequest.getEmployeeId()));
            if (employee == null) {
                continue;
            }

            rows.add(new String[]{employee.getFullName(), employee.getDepartment(), leaveRequest.getLeaveType().getLeaveTypeName()});
        }

        return rows;
    }

    private List<String[]> employeesOnOvertimeToday(List<Request> allRequests) {
        LocalDate today = LocalDate.now();
        List<String[]> rows = new ArrayList<>();

        for (Request request : allRequests) {
            if (!(request instanceof OvertimeRequest overtimeRequest)) {
                continue;
            }

            if (overtimeRequest.getStatus() != RequestStatus.APPROVED) {
                continue;
            }

            if (!overtimeRequest.getOvertimeDate().equals(today)) {
                continue;
            }

            Employee employee = employeeService.findEmployee(String.valueOf(overtimeRequest.getEmployeeId()));
            if (employee == null) {
                continue;
            }

            rows.add(new String[]{employee.getFullName(), employee.getDepartment(), overtimeRequest.getReason()});
        }

        return rows;
    }

    private QuarterInfo getCurrentQuarter() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int quarter = ((month - 1) / 3) + 1;

        return buildQuarterInfo(quarter, now.getYear());
    }

    private QuarterInfo getUpcomingQuarter(QuarterInfo current) {
        int nextQuarter = current.number + 1;
        int year = current.year;

        if (nextQuarter > 4) {
            nextQuarter = 1;
            year++;
        }

        return buildQuarterInfo(nextQuarter, year);
    }

    private QuarterInfo buildQuarterInfo(int quarter, int year) {
        return switch (quarter) {
            case 1 -> new QuarterInfo(1, "Q1", "January to March", year);
            case 2 -> new QuarterInfo(2, "Q2", "April to June", year);
            case 3 -> new QuarterInfo(3, "Q3", "July to September", year);
            case 4 -> new QuarterInfo(4, "Q4", "October to December", year);
            default -> new QuarterInfo(1, "Q1", "January to March", year);
        };
    }

    private static class QuarterInfo {
        int number;
        String name;
        String months;
        int year;

        QuarterInfo(int number, String name, String months, int year) {
            this.number = number;
            this.name = name;
            this.months = months;
            this.year = year;
        }
    }

    private JPanel createCardsPanel() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 24, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        return cardsPanel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font(FONT, Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        return title;
    }

    private JPanel createCard(String title, String value, String subtitle) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(NAVY);
        card.setPreferredSize(new Dimension(260, 138));
        card.setMinimumSize(new Dimension(210, 125));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font(FONT, Font.BOLD, 13));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font(FONT, Font.BOLD, 52));

        gbc.gridy = 0;
        gbc.insets = new Insets(12, 20, 0, 20);
        card.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(8, 20, 0, 20);
        card.add(valueLabel, gbc);

        if (subtitle != null) {
            JLabel subLabel = new JLabel(subtitle);
            subLabel.setForeground(Color.WHITE);
            subLabel.setFont(new Font(FONT, Font.BOLD, 13));

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 20, 12, 20);
            card.add(subLabel, gbc);
        }

        return card;
    }

    private JPanel createMultiValueCard(String titleText, String[] numbers, String[] labels, int columns) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(NAVY);
        card.setPreferredSize(new Dimension(260, 138));
        card.setMinimumSize(new Dimension(210, 125));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setFont(new Font(FONT, Font.BOLD, 13));

        JPanel values = new JPanel(new GridLayout(2, columns, 8, 0));
        values.setOpaque(false);

        for (String number : numbers) {
            JLabel numberLabel = new JLabel(number, SwingConstants.LEFT);
            numberLabel.setForeground(Color.WHITE);
            numberLabel.setFont(new Font(FONT, Font.BOLD, 52));
            values.add(numberLabel);
        }

        for (String label : labels) {
            JLabel labelText = new JLabel(label, SwingConstants.LEFT);
            labelText.setForeground(Color.WHITE);
            labelText.setFont(new Font(FONT, Font.BOLD, 13));
            values.add(labelText);
        }

        card.add(title, BorderLayout.NORTH);
        card.add(values, BorderLayout.CENTER);

        return card;
    }

    private JPanel createHRTablePanel(String titleText, String thirdColumnName, List<String[]> rows) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font(FONT, Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] columns = {"Name", "Department", thirdColumnName};

        Object[][] data = rows.stream()
                .map(row -> (Object[]) row)
                .toArray(Object[][]::new);

        DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);
        table.setFont(new Font(FONT, Font.PLAIN, 12));
        table.setRowHeight(55);
        table.setShowGrid(true);
        table.setGridColor(Color.WHITE);
        table.setIntercellSpacing(new Dimension(1, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 220, 220));
        table.setFocusable(false);

        table.getTableHeader().setFont(new Font(FONT, Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLACK));

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setFont(new Font(FONT, Font.PLAIN, 12));
                c.setForeground(Color.BLACK);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(215, 215, 215));
                }

                setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private class FinancialChartPanel extends JPanel {

        private final FinancialSummary data;

        public FinancialChartPanel(FinancialSummary data) {
            this.data = data;
            setBackground(Color.WHITE);
            setMinimumSize(new Dimension(650, 320));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = setupGraphics(g);

            int left = 100;
            int top = 75;
            int bottom = 55;
            int right = 35;

            int chartWidth = Math.max(300, getWidth() - left - right);
            int chartHeight = Math.max(200, getHeight() - top - bottom);

            drawFinancialLegend(g2, getWidth() / 2 - 115, 25);

            if (data == null || data.getPeriodLabels().isEmpty()) {
                g2.setColor(Color.BLACK);
                g2.drawString("No financial data available.", left, top + 40);
                return;
            }

            int[] revenueValues = data.getRevenue().stream().mapToInt(Integer::intValue).toArray();
            int[] expenseValues = data.getExpenses().stream().mapToInt(Integer::intValue).toArray();
            String[] xLabels = data.getPeriodLabels().toArray(new String[0]);

            int maxValue = 1;

            for (int value : revenueValues) {
                maxValue = Math.max(maxValue, value);
            }

            for (int value : expenseValues) {
                maxValue = Math.max(maxValue, value);
            }

            maxValue = ((maxValue / 100000) + 1) * 100000;

            drawLineGrid(g2, createMoneyLabels(maxValue), left, top, chartWidth, chartHeight);
            drawXLabels(g2, xLabels, left, top, chartWidth, chartHeight);

            drawDataLine(g2, revenueValues, left, top, chartWidth, chartHeight, maxValue, NAVY);
            drawDataLine(g2, expenseValues, left, top, chartWidth, chartHeight, maxValue, EXPENSE_GRAY);
        }
    }

    private class AttendanceChartPanel extends JPanel {
        public AttendanceChartPanel() {
            setBackground(Color.WHITE);
            setMinimumSize(new Dimension(650, 320));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = setupGraphics(g);

            int left = 45;
            int top = 25;
            int bottom = 45;
            int right = 25;

            int chartWidth = Math.max(300, getWidth() - left - right);
            int chartHeight = Math.max(200, getHeight() - top - bottom);

            drawLineGrid(g2, new String[]{"10", "8", "6", "4", "2", "0"},
                    left, top, chartWidth, chartHeight);

            String[] xLabels = {"06/01", "06/02", "06/03", "06/04", "06/05", "06/06", "06/07", "06/08"};
            int[] values = {8, 7, 9, 7, 8, 7, 9, 7};

            int barGap = 16;
            int barWidth = Math.max(35, (chartWidth / values.length) - barGap);

            g2.setColor(NAVY);

            for (int i = 0; i < values.length; i++) {
                int xCenter = left + (i * chartWidth / values.length) + chartWidth / values.length / 2;
                int barHeight = (int) ((values[i] / 10.0) * chartHeight);
                int x = xCenter - barWidth / 2;
                int y = top + chartHeight - barHeight;

                g2.fillRoundRect(x, y, barWidth, barHeight, 12, 12);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font(FONT, Font.PLAIN, 14));
                g2.drawString(xLabels[i], xCenter - 20, top + chartHeight + 25);

                g2.setColor(NAVY);
            }
        }
    }

    private class ITSupportChartPanel extends JPanel {
        public ITSupportChartPanel() {
            setBackground(Color.WHITE);
            setMinimumSize(new Dimension(650, 320));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = setupGraphics(g);

            int left = 45;
            int top = 45;
            int bottom = 45;
            int right = 25;

            int chartWidth = Math.max(300, getWidth() - left - right);
            int chartHeight = Math.max(200, getHeight() - top - bottom);

            drawITLegend(g2, getWidth() / 2 - 90, 15);

            drawLineGrid(g2, new String[]{"40", "30", "20", "10", "0"},
                    left, top, chartWidth, chartHeight);

            String[] months = {"Jan", "Feb", "Mar", "Apr", "May"};
            int[] resolved = {24, 16, 12, 8, 10};
            int[] pending = {15, 14, 10, 8, 30};

            int maxValue = 40;
            int groupWidth = chartWidth / months.length;
            int barWidth = Math.max(70, groupWidth - 26);

            for (int i = 0; i < months.length; i++) {
                int xCenter = left + (i * groupWidth) + groupWidth / 2;
                int x = xCenter - barWidth / 2;

                int resolvedHeight = (int) ((resolved[i] / (double) maxValue) * chartHeight);
                int pendingHeight = (int) ((pending[i] / (double) maxValue) * chartHeight);

                int resolvedY = top + chartHeight - resolvedHeight;
                int pendingY = resolvedY - pendingHeight;

                g2.setColor(NAVY);
                g2.fillRect(x, resolvedY, barWidth, resolvedHeight);

                g2.setColor(EXPENSE_GRAY);
                g2.fillRoundRect(x, pendingY, barWidth, pendingHeight + 12, 18, 18);
                g2.fillRect(x, pendingY + 12, barWidth, pendingHeight);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font(FONT, Font.PLAIN, 14));
                g2.drawString(months[i], xCenter - 12, top + chartHeight + 25);
            }
        }
    }

    private String[] createMoneyLabels(int maxValue) {
        String[] labels = new String[6];

        for (int i = 0; i < labels.length; i++) {
            int value = maxValue - (i * maxValue / 5);
            labels[i] = NumberFormat.getNumberInstance(Locale.US).format(value);
        }

        return labels;
    }

    private Graphics2D setupGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font(FONT, Font.PLAIN, 14));
        return g2;
    }

    private void drawLineGrid(Graphics2D g2, String[] yLabels, int left, int top, int chartWidth, int chartHeight) {
        for (int i = 0; i < yLabels.length; i++) {
            int y = top + (i * chartHeight / (yLabels.length - 1));

            g2.setColor(Color.BLACK);
            g2.drawString(yLabels[i], 0, y + 5);

            g2.setColor(GRID_GRAY);
            g2.drawLine(left, y, left + chartWidth, y);
        }
    }

    private void drawXLabels(Graphics2D g2, String[] xLabels, int left, int top, int chartWidth, int chartHeight) {
        g2.setColor(Color.BLACK);

        if (xLabels.length == 1) {
            g2.drawString(xLabels[0], left + chartWidth / 2 - 28, top + chartHeight + 25);
            return;
        }

        for (int i = 0; i < xLabels.length; i++) {
            int x = left + (i * chartWidth / (xLabels.length - 1));
            g2.drawString(xLabels[i], x - 28, top + chartHeight + 25);
        }
    }

    private void drawDataLine(Graphics2D g2, int[] values, int left, int top,
                              int chartWidth, int chartHeight, int maxValue, Color color) {
        if (values == null || values.length == 0) {
            return;
        }

        int[] x = new int[values.length];
        int[] y = new int[values.length];

        if (values.length == 1) {
            x[0] = left + chartWidth / 2;
            y[0] = top + chartHeight - (int) ((values[0] / (double) maxValue) * chartHeight);

            g2.setColor(color);
            g2.fillOval(x[0] - 7, y[0] - 7, 14, 14);
            return;
        }

        for (int i = 0; i < values.length; i++) {
            x[i] = left + (i * chartWidth / (values.length - 1));
            y[i] = top + chartHeight - (int) ((values[i] / (double) maxValue) * chartHeight);
        }

        g2.setColor(color);
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 0; i < values.length - 1; i++) {
            g2.drawLine(x[i], y[i], x[i + 1], y[i + 1]);
        }

        for (int i = 0; i < values.length; i++) {
            g2.fillOval(x[i] - 7, y[i] - 7, 14, 14);
        }
    }

    private void drawFinancialLegend(Graphics2D g2, int x, int y) {
        g2.setFont(new Font(FONT, Font.PLAIN, 14));

        g2.setColor(NAVY);
        g2.fillOval(x, y, 14, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Revenue", x + 22, y + 12);

        g2.setColor(EXPENSE_GRAY);
        g2.fillOval(x + 115, y, 14, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Total Expenses", x + 137, y + 12);
    }

    private void drawITLegend(Graphics2D g2, int x, int y) {
        g2.setFont(new Font(FONT, Font.PLAIN, 14));

        g2.setColor(NAVY);
        g2.fillOval(x, y, 14, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Resolved", x + 22, y + 12);

        g2.setColor(EXPENSE_GRAY);
        g2.fillOval(x + 100, y, 14, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Pending", x + 122, y + 12);
    }
}
package com.motorph.model;

import java.util.List;

public class FinancialSummary {

    private final List<String> periodLabels;
    private final List<Integer> revenue;
    private final List<Integer> expenses;

    public FinancialSummary(List<String> periodLabels, List<Integer> revenue, List<Integer> expenses) {
        this.periodLabels = periodLabels;
        this.revenue = revenue;
        this.expenses = expenses;
    }

    public List<String> getPeriodLabels() { return periodLabels; }
    public List<Integer> getRevenue() { return revenue; }
    public List<Integer> getExpenses() { return expenses; }
}

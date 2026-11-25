package com.daybook.report;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyReport(
        String month,
        Totals totals,
        List<DayTotal> days,
        List<Share> expenseByCategory,
        List<Share> incomeByCategory,
        List<Share> incomeByPayMethod) {

    public record Totals(BigDecimal income, BigDecimal expenses, BigDecimal net) {}

    public record DayTotal(int day, BigDecimal income, BigDecimal expenses) {}

    /** {@code key} is the stored category or pay method ("Rent", "Cash"); the app translates it. */
    public record Share(String key, BigDecimal amount, int percent) {}
}

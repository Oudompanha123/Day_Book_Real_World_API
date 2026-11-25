package com.daybook.report;

import com.daybook.common.Kind;
import com.daybook.entry.Entry;
import com.daybook.entry.EntryRepository;
import com.daybook.report.MonthlyReport.DayTotal;
import com.daybook.report.MonthlyReport.Share;
import com.daybook.report.MonthlyReport.Totals;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final EntryRepository entries;

    public ReportService(EntryRepository entries) {
        this.entries = entries;
    }

    @Transactional(readOnly = true)
    public MonthlyReport monthly(long userId, YearMonth month) {
        List<Entry> list = entries.findByUserIdAndEntryDateBetweenOrderByEntryDateDescCreatedAtDesc(
                userId, month.atDay(1), month.atEndOfMonth());

        int days = month.lengthOfMonth();
        BigDecimal[] dayIn = filled(days);
        BigDecimal[] dayOut = filled(days);
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        Map<String, BigDecimal> expenseByCat = new HashMap<>();
        Map<String, BigDecimal> incomeByCat = new HashMap<>();
        Map<String, BigDecimal> incomeByPay = new HashMap<>();

        for (Entry e : list) {
            int i = e.getEntryDate().getDayOfMonth() - 1;
            if (e.getKind() == Kind.IN) {
                income = income.add(e.getAmount());
                dayIn[i] = dayIn[i].add(e.getAmount());
                incomeByCat.merge(e.getCategory(), e.getAmount(), BigDecimal::add);
                incomeByPay.merge(e.getPayMethod(), e.getAmount(), BigDecimal::add);
            } else {
                expenses = expenses.add(e.getAmount());
                dayOut[i] = dayOut[i].add(e.getAmount());
                expenseByCat.merge(e.getCategory(), e.getAmount(), BigDecimal::add);
            }
        }

        List<DayTotal> daily = new ArrayList<>(days);
        for (int i = 0; i < days; i++) daily.add(new DayTotal(i + 1, dayIn[i], dayOut[i]));

        return new MonthlyReport(
                month.toString(),
                new Totals(income, expenses, income.subtract(expenses)),
                daily,
                shares(expenseByCat, expenses),
                shares(incomeByCat, income),
                shares(incomeByPay, income));
    }

    private static BigDecimal[] filled(int n) {
        BigDecimal[] a = new BigDecimal[n];
        java.util.Arrays.fill(a, BigDecimal.ZERO);
        return a;
    }

    private static List<Share> shares(Map<String, BigDecimal> map, BigDecimal total) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(en -> new Share(en.getKey(), en.getValue(), percent(en.getValue(), total)))
                .toList();
    }

    private static int percent(BigDecimal part, BigDecimal total) {
        if (total.signum() == 0) return 0;
        return part.multiply(BigDecimal.valueOf(100)).divide(total, 0, RoundingMode.HALF_UP).intValue();
    }
}

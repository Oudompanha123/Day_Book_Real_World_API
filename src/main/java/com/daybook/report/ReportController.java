package com.daybook.report;

import com.daybook.common.CurrentUser;
import java.time.YearMonth;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    /** GET /api/reports/monthly?month=2026-11 */
    @GetMapping("/monthly")
    MonthlyReport monthly(@AuthenticationPrincipal Jwt jwt, @RequestParam YearMonth month) {
        return service.monthly(CurrentUser.id(jwt), month);
    }
}

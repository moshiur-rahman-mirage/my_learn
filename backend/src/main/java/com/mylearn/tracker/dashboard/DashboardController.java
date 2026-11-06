package com.mylearn.tracker.dashboard;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.dashboard.ActivityCalculator.Activity;
import com.mylearn.tracker.dashboard.DashboardService.DashboardSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DateTimeException;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboard;
    private final TodayService today;

    public DashboardController(DashboardService dashboard, TodayService today) {
        this.dashboard = dashboard;
        this.today = today;
    }

    /** @param tz IANA time zone of the viewer, for "time spent today" */
    @GetMapping("/today")
    public TodayService.Today today(@RequestParam(required = false) String tz) {
        return today.today(zoneOf(tz));
    }

    /**
     * @param days 7..3660, or 0 for all history
     * @param tz   IANA time zone of the viewer (e.g. Asia/Dhaka); defaults to the server's
     */
    @GetMapping("/activity")
    public Activity activity(@RequestParam(defaultValue = "90") int days,
                             @RequestParam(required = false) String tz,
                             @RequestParam(required = false) Long planId) {
        if (days < 0 || days > ActivityCalculator.MAX_DAYS) {
            throw new BusinessRuleException("days must be between 0 (all) and " + ActivityCalculator.MAX_DAYS);
        }
        return dashboard.activity(days, zoneOf(tz), planId);
    }

    private static ZoneId zoneOf(String tz) {
        if (tz == null || tz.isBlank()) {
            return ZoneId.systemDefault();
        }
        try {
            return ZoneId.of(tz);
        } catch (DateTimeException e) {
            throw new BusinessRuleException("Unknown time zone: " + tz);
        }
    }

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return dashboard.summary();
    }
}

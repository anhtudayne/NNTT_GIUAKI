package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import trungtamngoaingu.hcmute.dto.DashboardSummaryDTO;
import trungtamngoaingu.hcmute.dto.RevenueByMonthDTO;
import trungtamngoaingu.hcmute.service.DashboardService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Endpoint trả về các KPI tổng quan cho Dashboard.
     * Ví dụ: GET /api/dashboard/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    /**
     * Doanh thu theo từng tháng trong một năm.
     * Ví dụ:
     *  - GET /api/dashboard/revenue-by-month?year=2026
     *  - Nếu không truyền year, mặc định dùng năm hiện tại.
     */
    @GetMapping("/revenue-by-month")
    public ResponseEntity<List<RevenueByMonthDTO>> getRevenueByMonth(
            @RequestParam(value = "year", required = false) Integer yearParam) {

        int year = (yearParam != null) ? yearParam : LocalDate.now().getYear();
        return ResponseEntity.ok(dashboardService.getRevenueByMonth(year));
    }
}


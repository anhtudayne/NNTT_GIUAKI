package trungtamngoaingu.hcmute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO tóm tắt các chỉ số chính cho Dashboard.
 */
@Data
@AllArgsConstructor
public class DashboardSummaryDTO {
    private long totalActiveStudents;
    private long totalTeachers;
    private long totalStaff;
    private long totalOngoingClasses;

    private BigDecimal totalRevenueAllTime;
    private BigDecimal totalRevenueCurrentMonth;
    private BigDecimal totalRevenueCurrentYear;

    private long totalCertificatesIssued;
    private double averageScoreAllResults;
}


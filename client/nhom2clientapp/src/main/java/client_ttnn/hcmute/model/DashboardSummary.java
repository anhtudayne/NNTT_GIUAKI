package client_ttnn.hcmute.model;

import java.math.BigDecimal;

/**
 * Model phản chiếu DashboardSummaryDTO trên server.
 */
public class DashboardSummary {
    private long totalActiveStudents;
    private long totalTeachers;
    private long totalStaff;
    private long totalOngoingClasses;

    private BigDecimal totalRevenueAllTime;
    private BigDecimal totalRevenueCurrentMonth;
    private BigDecimal totalRevenueCurrentYear;

    private long totalCertificatesIssued;
    private double averageScoreAllResults;

    public long getTotalActiveStudents() {
        return totalActiveStudents;
    }

    public void setTotalActiveStudents(long totalActiveStudents) {
        this.totalActiveStudents = totalActiveStudents;
    }

    public long getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(long totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public long getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(long totalStaff) {
        this.totalStaff = totalStaff;
    }

    public long getTotalOngoingClasses() {
        return totalOngoingClasses;
    }

    public void setTotalOngoingClasses(long totalOngoingClasses) {
        this.totalOngoingClasses = totalOngoingClasses;
    }

    public BigDecimal getTotalRevenueAllTime() {
        return totalRevenueAllTime;
    }

    public void setTotalRevenueAllTime(BigDecimal totalRevenueAllTime) {
        this.totalRevenueAllTime = totalRevenueAllTime;
    }

    public BigDecimal getTotalRevenueCurrentMonth() {
        return totalRevenueCurrentMonth;
    }

    public void setTotalRevenueCurrentMonth(BigDecimal totalRevenueCurrentMonth) {
        this.totalRevenueCurrentMonth = totalRevenueCurrentMonth;
    }

    public BigDecimal getTotalRevenueCurrentYear() {
        return totalRevenueCurrentYear;
    }

    public void setTotalRevenueCurrentYear(BigDecimal totalRevenueCurrentYear) {
        this.totalRevenueCurrentYear = totalRevenueCurrentYear;
    }

    public long getTotalCertificatesIssued() {
        return totalCertificatesIssued;
    }

    public void setTotalCertificatesIssued(long totalCertificatesIssued) {
        this.totalCertificatesIssued = totalCertificatesIssued;
    }

    public double getAverageScoreAllResults() {
        return averageScoreAllResults;
    }

    public void setAverageScoreAllResults(double averageScoreAllResults) {
        this.averageScoreAllResults = averageScoreAllResults;
    }
}


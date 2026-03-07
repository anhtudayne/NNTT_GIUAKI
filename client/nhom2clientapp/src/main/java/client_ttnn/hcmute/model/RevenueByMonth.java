package client_ttnn.hcmute.model;

import java.math.BigDecimal;

/**
 * Model phản chiếu RevenueByMonthDTO trên server.
 */
public class RevenueByMonth {
    private int year;
    private int month;
    private BigDecimal totalRevenue;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}


package trungtamngoaingu.hcmute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO thể hiện doanh thu theo từng tháng trong một năm.
 */
@Data
@AllArgsConstructor
public class RevenueByMonthDTO {
    private int year;
    private int month; // 1-12
    private BigDecimal totalRevenue;
}


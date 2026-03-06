package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.dto.DashboardSummaryDTO;
import trungtamngoaingu.hcmute.dto.RevenueByMonthDTO;
import trungtamngoaingu.hcmute.entity.*;
import trungtamngoaingu.hcmute.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service tổng hợp dữ liệu cho Dashboard, sử dụng Java Stream
 * và chỉ gọi các repository.myGetAll() theo đúng quy ước.
 */
@Service
public class DashboardService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    /**
     * Tạo DTO tóm tắt KPI cho Dashboard.
     */
    public DashboardSummaryDTO getSummary() {
        // 1. Tổng học viên đang Active
        long totalActiveStudents = studentRepository.myGetAll()
                .stream()
                .filter(s -> s.getStatus() == Student.Status.Active)
                .count();

        // 2. Tổng giáo viên
        long totalTeachers = teacherRepository.myGetAll().size();

        // 3. Tổng staff
        long totalStaff = staffRepository.myGetAll().size();

        // 4. Tổng lớp đang Ongoing
        long totalOngoingClasses = classRepository.myGetAll()
                .stream()
                .filter(c -> c.getStatus() == trungtamngoaingu.hcmute.entity.Class.Status.Ongoing)
                .count();

        // 5. Doanh thu: tính từ Payment có status Success
        List<Payment> allPayments = paymentRepository.myGetAll();
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        BigDecimal totalRevenueAllTime = sumPayments(allPayments);
        BigDecimal totalRevenueCurrentYear = sumPayments(
                allPayments.stream()
                        .filter(p -> isSameYear(p.getPaymentDate(), currentYear))
                        .collect(Collectors.toList())
        );
        BigDecimal totalRevenueCurrentMonth = sumPayments(
                allPayments.stream()
                        .filter(p -> isSameYearMonth(p.getPaymentDate(), currentYear, currentMonth))
                        .collect(Collectors.toList())
        );

        // 6. Tổng certificate đã cấp
        long totalCertificatesIssued = certificateRepository.myGetAll().size();

        // 7. Điểm trung bình toàn bộ kết quả học tập
        double averageScoreAllResults = resultRepository.myGetAll()
                .stream()
                .filter(r -> r.getScore() != null)
                .mapToDouble(r -> r.getScore().doubleValue())
                .average()
                .orElse(0.0);

        return new DashboardSummaryDTO(
                totalActiveStudents,
                totalTeachers,
                totalStaff,
                totalOngoingClasses,
                totalRevenueAllTime,
                totalRevenueCurrentMonth,
                totalRevenueCurrentYear,
                totalCertificatesIssued,
                averageScoreAllResults
        );
    }

    /**
     * Doanh thu theo từng tháng trong một năm (1..12).
     */
    public List<RevenueByMonthDTO> getRevenueByMonth(int year) {
        List<Payment> allPayments = paymentRepository.myGetAll()
                .stream()
                .filter(p -> Payment.Status.Success.equals(p.getStatus()))
                .filter(p -> p.getPaymentDate() != null && p.getPaymentDate().getYear() == year)
                .collect(Collectors.toList());

        Map<YearMonth, BigDecimal> revenueByMonth = allPayments.stream()
                .collect(Collectors.groupingBy(
                        p -> YearMonth.from(p.getPaymentDate()),
                        Collectors.mapping(
                                Payment::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, (a, b) -> {
                                    if (a == null) a = BigDecimal.ZERO;
                                    if (b == null) b = BigDecimal.ZERO;
                                    return a.add(b);
                                })
                        )
                ));

        List<RevenueByMonthDTO> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);
            BigDecimal revenue = revenueByMonth.getOrDefault(ym, BigDecimal.ZERO);
            result.add(new RevenueByMonthDTO(year, month, revenue));
        }

        // Sắp xếp theo tháng tăng dần (thường không cần, nhưng để chắc chắn)
        result.sort(Comparator.comparingInt(RevenueByMonthDTO::getMonth));

        return result;
    }

    private BigDecimal sumPayments(List<Payment> payments) {
        return payments.stream()
                .filter(p -> Payment.Status.Success.equals(p.getStatus()))
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isSameYear(LocalDate date, int year) {
        return date != null && date.getYear() == year;
    }

    private boolean isSameYearMonth(LocalDate date, int year, int month) {
        return date != null && date.getYear() == year && date.getMonthValue() == month;
    }
}


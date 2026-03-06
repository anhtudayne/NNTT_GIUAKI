package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Payment;
import trungtamngoaingu.hcmute.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.myGetAll();
    }

    public Optional<Payment> getPaymentById(Integer id) {
        return paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment updatePayment(Integer id, Payment payment) {
        if (paymentRepository.existsById(id)) {
            payment.setPaymentId(id);
            return paymentRepository.save(payment);
        }
        return null;
    }

    public void deletePayment(Integer id) {
        paymentRepository.deleteById(id);
    }

    /**
     * Lọc payment theo StudentID.
     */
    public List<Payment> getPaymentsByStudentId(Integer studentId) {
        if (studentId == null) {
            return paymentRepository.myGetAll();
        }
        return paymentRepository.myGetAll()
                .stream()
                .filter(p -> p.getStudent() != null
                        && p.getStudent().getStudentId() != null
                        && p.getStudent().getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /**
     * Lọc payment theo EnrollmentID.
     */
    public List<Payment> getPaymentsByEnrollmentId(Integer enrollmentId) {
        if (enrollmentId == null) {
            return paymentRepository.myGetAll();
        }
        return paymentRepository.myGetAll()
                .stream()
                .filter(p -> p.getEnrollment() != null
                        && p.getEnrollment().getEnrollmentId() != null
                        && p.getEnrollment().getEnrollmentId().equals(enrollmentId))
                .collect(Collectors.toList());
    }

    /**
     * Lọc payment theo phương thức thanh toán.
     */
    public List<Payment> getPaymentsByMethod(Payment.PaymentMethod method) {
        if (method == null) {
            return paymentRepository.myGetAll();
        }
        return paymentRepository.myGetAll()
                .stream()
                .filter(p -> method.equals(p.getPaymentMethod()))
                .collect(Collectors.toList());
    }

    /**
     * Lọc payment theo trạng thái.
     */
    public List<Payment> getPaymentsByStatus(Payment.Status status) {
        if (status == null) {
            return paymentRepository.myGetAll();
        }
        return paymentRepository.myGetAll()
                .stream()
                .filter(p -> status.equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Lọc payment theo khoảng ngày thanh toán.
     */
    public List<Payment> getPaymentsByDateRange(LocalDate from, LocalDate to) {
        return paymentRepository.myGetAll()
                .stream()
                .filter(p -> {
                    LocalDate date = p.getPaymentDate();
                    if (date == null) return false;
                    boolean afterFrom = (from == null) || !date.isBefore(from);
                    boolean beforeTo = (to == null) || !date.isAfter(to);
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Tính tổng số tiền thanh toán (Success) cho một học viên.
     */
    public BigDecimal getTotalPaidByStudent(Integer studentId) {
        return getPaymentsByStudentId(studentId)
                .stream()
                .filter(p -> Payment.Status.Success.equals(p.getStatus()))
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Tính tổng số tiền thanh toán (Success) cho một enrollment.
     */
    public BigDecimal getTotalPaidByEnrollment(Integer enrollmentId) {
        return getPaymentsByEnrollmentId(enrollmentId)
                .stream()
                .filter(p -> Payment.Status.Success.equals(p.getStatus()))
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

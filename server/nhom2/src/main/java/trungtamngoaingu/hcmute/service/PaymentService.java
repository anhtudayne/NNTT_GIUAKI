package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Payment;
import trungtamngoaingu.hcmute.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceService invoiceService;

    public List<Payment> getAllPayments() {
        return paymentRepository.myGetAll();
    }

    public Page<Payment> getPaymentsPaged(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    public Optional<Payment> getPaymentById(Integer id) {
        return paymentRepository.findById(id);
    }

    /** Lấy tất cả thanh toán thuộc một hóa đơn. */
    public List<Payment> getPaymentsByInvoiceId(Integer invoiceId) {
        if (invoiceId == null) return List.of();
        return paymentRepository.findByInvoice_InvoiceId(invoiceId);
    }

    public Payment createPayment(Payment payment) {
        Payment saved = paymentRepository.save(payment);
        if (saved.getInvoice() != null) {
            BigDecimal net = netPaidEffect(saved);
            invoiceService.applyDeltaNetPaid(saved.getInvoice().getInvoiceId(), net);
            invoiceService.recalculateInvoiceStatus(saved.getInvoice().getInvoiceId());
        }
        return saved;
    }

    public Payment updatePayment(Integer id, Payment payment) {
        if (!paymentRepository.existsById(id)) return null;
        Payment old = paymentRepository.findById(id).orElse(null);
        Integer oldInvoiceId = old != null && old.getInvoice() != null ? old.getInvoice().getInvoiceId() : null;
        BigDecimal oldNet = old != null ? netPaidEffect(old) : BigDecimal.ZERO;
        payment.setPaymentId(id);
        Payment saved = paymentRepository.save(payment);
        Integer newInvoiceId = saved.getInvoice() != null ? saved.getInvoice().getInvoiceId() : null;
        BigDecimal newNet = netPaidEffect(saved);

        if (oldInvoiceId != null && (newInvoiceId == null || !oldInvoiceId.equals(newInvoiceId))) {
            // revert effect on old invoice
            invoiceService.applyDeltaNetPaid(oldInvoiceId, oldNet.negate());
            invoiceService.recalculateInvoiceStatus(oldInvoiceId);
        }
        if (newInvoiceId != null) {
            // apply delta effect on new invoice
            BigDecimal delta = newNet.subtract(oldInvoiceId != null && oldInvoiceId.equals(newInvoiceId) ? oldNet : BigDecimal.ZERO);
            invoiceService.applyDeltaNetPaid(newInvoiceId, delta);
            invoiceService.recalculateInvoiceStatus(newInvoiceId);
        }
        return saved;
    }

    public void deletePayment(Integer id) {
        Payment old = paymentRepository.findById(id).orElse(null);
        Integer invoiceId = old != null && old.getInvoice() != null ? old.getInvoice().getInvoiceId() : null;
        BigDecimal oldNet = old != null ? netPaidEffect(old) : BigDecimal.ZERO;
        paymentRepository.deleteById(id);
        if (invoiceId != null) {
            invoiceService.applyDeltaNetPaid(invoiceId, oldNet.negate());
            invoiceService.recalculateInvoiceStatus(invoiceId);
        }
    }

    /**
     * Quy đổi Payment sang "net paid effect" để cập nhật remaining của hóa đơn:
     * - Success  => +amount (thu vào)
     * - Refunded => -amount (hoàn trả)
     * - Failed   => 0
     */
    private static BigDecimal netPaidEffect(Payment p) {
        if (p == null || p.getAmount() == null || p.getStatus() == null) return BigDecimal.ZERO;
        return switch (p.getStatus()) {
            case Success -> p.getAmount();
            case Refunded -> p.getAmount().negate();
            case Failed -> BigDecimal.ZERO;
        };
    }

    /**
     * Lọc payment theo StudentID.
     */
    public List<Payment> getPaymentsByStudentId(Integer studentId) {
        if (studentId == null) {
            return paymentRepository.myGetAll();
        }
        return paymentRepository.findByStudent_StudentId(studentId);
    }

    /**
     * Lọc payment theo EnrollmentID.
     */
    public List<Payment> getPaymentsByEnrollmentId(Integer enrollmentId) {
        if (enrollmentId == null) {
            return paymentRepository.myGetAll();
        }
        return paymentRepository.findByEnrollment_EnrollmentId(enrollmentId);
    }

    /**
     * Lọc payment theo phương thức thanh toán.
     */
    public List<Payment> getPaymentsByMethod(Payment.PaymentMethod method) {
        if (method == null) {
            return paymentRepository.myGetAll();
        }
        return paymentRepository.findByPaymentMethod(method);
    }

    /**
     * Lọc payment theo trạng thái.
     */
    public List<Payment> getPaymentsByStatus(Payment.Status status) {
        if (status == null) {
            return paymentRepository.myGetAll();
        }
        return paymentRepository.findByStatus(status);
    }

    /**
     * Lọc payment theo khoảng ngày thanh toán.
     */
    public List<Payment> getPaymentsByDateRange(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return paymentRepository.myGetAll();
        }
        if (from == null) {
            // Không có from: lấy từ rất sớm tới to
            return paymentRepository.findByPaymentDateBetween(LocalDate.MIN, to);
        }
        if (to == null) {
            // Không có to: lấy từ from tới rất xa
            return paymentRepository.findByPaymentDateBetween(from, LocalDate.MAX);
        }
        return paymentRepository.findByPaymentDateBetween(from, to);
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

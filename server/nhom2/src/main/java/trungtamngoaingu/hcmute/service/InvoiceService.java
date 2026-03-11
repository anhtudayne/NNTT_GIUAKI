package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Invoice;
import trungtamngoaingu.hcmute.entity.Payment;
import trungtamngoaingu.hcmute.entity.Promotion;
import trungtamngoaingu.hcmute.repository.InvoiceRepository;
import trungtamngoaingu.hcmute.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.myGetAll();
    }

    public Optional<Invoice> getInvoiceById(Integer id) {
        return invoiceRepository.findById(id);
    }

    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Integer id, Invoice invoice) {
        if (invoiceRepository.existsById(id)) {
            invoice.setInvoiceId(id);
            return invoiceRepository.save(invoice);
        }
        return null;
    }

    public void deleteInvoice(Integer id) {
        invoiceRepository.deleteById(id);
    }

    /**
     * Lọc hoá đơn theo StudentID bằng Java Stream trên toàn bộ danh sách.
     */
    public List<Invoice> getInvoicesByStudentId(Integer studentId) {
        if (studentId == null) {
            return invoiceRepository.myGetAll();
        }
        return invoiceRepository.myGetAll()
                .stream()
                .filter(i -> i.getStudent() != null
                        && i.getStudent().getStudentId() != null
                        && i.getStudent().getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /**
     * Lọc hoá đơn theo trạng thái.
     */
    public List<Invoice> getInvoicesByStatus(Invoice.Status status) {
        if (status == null) {
            return invoiceRepository.myGetAll();
        }
        return invoiceRepository.myGetAll()
                .stream()
                .filter(i -> status.equals(i.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Lọc hoá đơn theo khoảng ngày phát hành.
     * Nếu from/to null sẽ được hiểu là không giới hạn về phía đó.
     */
    public List<Invoice> getInvoicesByIssueDateRange(LocalDate from, LocalDate to) {
        return invoiceRepository.myGetAll()
                .stream()
                .filter(i -> {
                    LocalDate issueDate = i.getIssueDate();
                    if (issueDate == null) {
                        return false;
                    }
                    boolean afterFrom = (from == null) || !issueDate.isBefore(from);
                    boolean beforeTo = (to == null) || !issueDate.isAfter(to);
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật trạng thái hóa đơn theo số tiền còn lại (totalAmount = remaining) và lịch sử Payment.
     *
     * Quy ước: Invoice.totalAmount là số tiền CÒN LẠI phải thu.
     * - remaining <= 0 => Paid
     * - remaining > 0 và có ít nhất 1 payment Success => Partial
     * - còn lại => Unpaid
     */
    public void recalculateInvoiceStatus(Integer invoiceId) {
        if (invoiceId == null) return;
        Optional<Invoice> opt = invoiceRepository.findById(invoiceId);
        if (opt.isEmpty()) return;
        Invoice invoice = opt.get();
        BigDecimal remaining = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;

        BigDecimal paidSum = paymentRepository.findByInvoice_InvoiceId(invoiceId)
                .stream()
                .filter(p -> p.getStatus() == Payment.Status.Success)
                .map(Payment::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(Invoice.Status.Paid);
        } else if (paidSum.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(Invoice.Status.Partial);
        } else {
            invoice.setStatus(Invoice.Status.Unpaid);
        }
        invoiceRepository.save(invoice);
    }

    /**
     * Cộng/trừ số tiền còn lại của hóa đơn theo chênh lệch "net paid".
     *
     * deltaNetPaid > 0: thu thêm (Success) => remaining giảm
     * deltaNetPaid < 0: hoàn tiền (Refunded) => remaining tăng
     */
    public void applyDeltaNetPaid(Integer invoiceId, BigDecimal deltaNetPaid) {
        if (invoiceId == null || deltaNetPaid == null) return;
        Optional<Invoice> opt = invoiceRepository.findById(invoiceId);
        if (opt.isEmpty()) return;
        Invoice invoice = opt.get();
        BigDecimal remaining = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;

        // remaining -= deltaNetPaid
        BigDecimal newRemaining = remaining.subtract(deltaNetPaid);
        if (newRemaining.compareTo(BigDecimal.ZERO) < 0) newRemaining = BigDecimal.ZERO;
        invoice.setTotalAmount(newRemaining);
        invoiceRepository.save(invoice);
    }

    /**
     * Áp dụng promotion cho một hoá đơn:
     * - Giảm theo tỷ lệ % trên totalAmount.
     * - Không ghi lại thông tin promotion trong Invoice (nếu cần có thể mở rộng sau).
     */
    public Optional<Invoice> applyPromotion(Invoice invoice, Promotion promotion) {
        if (invoice == null || promotion == null
                || invoice.getTotalAmount() == null
                || promotion.getDiscountPercent() == null) {
            return Optional.empty();
        }

        BigDecimal total = invoice.getTotalAmount();
        BigDecimal percent = promotion.getDiscountPercent();

        // percent = 10.00 => factor = 0.90
        BigDecimal hundred = BigDecimal.valueOf(100);
        BigDecimal factor = BigDecimal.ONE.subtract(percent.divide(hundred));

        BigDecimal discounted = total.multiply(factor);
        if (discounted.compareTo(BigDecimal.ZERO) < 0) {
            discounted = BigDecimal.ZERO;
        }

        invoice.setTotalAmount(discounted);
        Invoice saved = invoiceRepository.save(invoice);
        recalculateInvoiceStatus(saved.getInvoiceId());
        return Optional.of(saved);
    }

    /**
     * Áp dụng promotion cho hoá đơn theo ID.
     */
    public Optional<Invoice> applyPromotion(Integer invoiceId, Promotion promotion) {
        if (invoiceId == null || promotion == null) {
            return Optional.empty();
        }
        return invoiceRepository.findById(invoiceId)
                .flatMap(inv -> applyPromotion(inv, promotion));
    }
}

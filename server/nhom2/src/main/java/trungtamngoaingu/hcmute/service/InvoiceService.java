package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Invoice;
import trungtamngoaingu.hcmute.entity.Promotion;
import trungtamngoaingu.hcmute.repository.InvoiceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

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
        return Optional.of(invoiceRepository.save(invoice));
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

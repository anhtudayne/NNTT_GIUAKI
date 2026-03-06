package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungtamngoaingu.hcmute.entity.Invoice;
import trungtamngoaingu.hcmute.entity.Promotion;
import trungtamngoaingu.hcmute.service.InvoiceService;
import trungtamngoaingu.hcmute.service.PromotionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Integer id) {
        Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        Invoice saved = invoiceService.createInvoice(invoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Integer id, @RequestBody Invoice invoice) {
        Invoice updated = invoiceService.updateInvoice(id, invoice);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lọc hoá đơn theo StudentID.
     * Ví dụ: GET /api/invoices/by-student/1
     */
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Invoice>> getInvoicesByStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStudentId(studentId));
    }

    /**
     * Lọc hoá đơn theo trạng thái.
     * Ví dụ: GET /api/invoices/status/Paid
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable Invoice.Status status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    /**
     * Lọc hoá đơn theo khoảng ngày phát hành.
     * Ví dụ:
     *  - GET /api/invoices/by-date-range?from=2026-02-01&to=2026-03-01
     *  - GET /api/invoices/by-date-range?from=2026-02-01
     *  - GET /api/invoices/by-date-range?to=2026-03-01
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<Invoice>> getInvoicesByDateRange(
            @RequestParam(value = "from", required = false) String fromStr,
            @RequestParam(value = "to", required = false) String toStr) {
        LocalDate from = null;
        LocalDate to = null;

        if (fromStr != null && !fromStr.isBlank()) {
            from = LocalDate.parse(fromStr);
        }
        if (toStr != null && !toStr.isBlank()) {
            to = LocalDate.parse(toStr);
        }

        return ResponseEntity.ok(invoiceService.getInvoicesByIssueDateRange(from, to));
    }

    /**
     * Áp dụng promotion cho một hoá đơn.
     * Ví dụ:
     *  - POST /api/invoices/1/apply-promotion?code=SUMMER2026
     */
    @PostMapping("/{id}/apply-promotion")
    public ResponseEntity<Invoice> applyPromotionToInvoice(
            @PathVariable Integer id,
            @RequestParam("code") String promoCode) {

        Optional<Promotion> promotionOpt = promotionService.validatePromotion(promoCode, LocalDate.now());
        if (promotionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<Invoice> updated = invoiceService.applyPromotion(id, promotionOpt.get());
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

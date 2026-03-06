package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungtamngoaingu.hcmute.entity.Payment;
import trungtamngoaingu.hcmute.service.PaymentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Integer id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment saved = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Integer id, @RequestBody Payment payment) {
        Payment updated = paymentService.updatePayment(id, payment);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lọc payments theo StudentID.
     * Ví dụ: GET /api/payments/by-student/1
     */
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Payment>> getPaymentsByStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByStudentId(studentId));
    }

    /**
     * Lọc payments theo EnrollmentID.
     * Ví dụ: GET /api/payments/by-enrollment/1
     */
    @GetMapping("/by-enrollment/{enrollmentId}")
    public ResponseEntity<List<Payment>> getPaymentsByEnrollment(@PathVariable Integer enrollmentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByEnrollmentId(enrollmentId));
    }

    /**
     * Lọc payments theo phương thức thanh toán.
     * Ví dụ: GET /api/payments/method/Cash
     */
    @GetMapping("/method/{method}")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable Payment.PaymentMethod method) {
        return ResponseEntity.ok(paymentService.getPaymentsByMethod(method));
    }

    /**
     * Lọc payments theo trạng thái.
     * Ví dụ: GET /api/payments/status/Success
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable Payment.Status status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }

    /**
     * Lọc payments theo khoảng thời gian.
     * Ví dụ:
     *  - GET /api/payments/by-date-range?from=2026-02-01&to=2026-03-01
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
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

        return ResponseEntity.ok(paymentService.getPaymentsByDateRange(from, to));
    }
}

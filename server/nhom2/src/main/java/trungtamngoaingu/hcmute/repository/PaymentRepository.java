package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
  @Query("SELECT p FROM Payment p")
  List<Payment> myGetAll();

  /** Lấy tất cả thanh toán thuộc một hóa đơn. */
  List<Payment> findByInvoice_InvoiceId(Integer invoiceId);

  // Tối ưu filter trong PaymentService
  List<Payment> findByStudent_StudentId(Integer studentId);

  List<Payment> findByEnrollment_EnrollmentId(Integer enrollmentId);

  List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);

  List<Payment> findByStatus(Payment.Status status);

  List<Payment> findByPaymentDateBetween(LocalDate from, LocalDate to);

  // Aggregate cho Dashboard
  @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'Success'")
  BigDecimal sumAllSuccessfulPayments();

  @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
         "WHERE p.status = 'Success' " +
         "AND FUNCTION('YEAR', p.paymentDate) = :year")
  BigDecimal sumSuccessfulPaymentsByYear(int year);

  @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
         "WHERE p.status = 'Success' " +
         "AND FUNCTION('YEAR', p.paymentDate) = :year " +
         "AND FUNCTION('MONTH', p.paymentDate) = :month")
  BigDecimal sumSuccessfulPaymentsByYearAndMonth(int year, int month);
}

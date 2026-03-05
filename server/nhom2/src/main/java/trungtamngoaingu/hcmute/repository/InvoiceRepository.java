package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Invoice;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
  @Query("SELECT i FROM Invoice i")
  List<Invoice> myGetAll();
}

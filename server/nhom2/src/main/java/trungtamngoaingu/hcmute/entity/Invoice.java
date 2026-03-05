package trungtamngoaingu.hcmute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "Invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", foreignKey = @ForeignKey(name = "FK_Invoice_Student"))
    private Student student;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Unpaid', 'Partial', 'Paid') DEFAULT 'Unpaid'")
    private Status status;

    public enum Status {
        Unpaid, Partial, Paid
    }
}

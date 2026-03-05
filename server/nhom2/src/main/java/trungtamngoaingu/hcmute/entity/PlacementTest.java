package trungtamngoaingu.hcmute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "PlacementTest")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacementTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", foreignKey = @ForeignKey(name = "FK_PlacementTest_Student"))
    private Student student;

    @Column
    private LocalDate testDate;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(length = 50)
    private String recommendedLevel;
}

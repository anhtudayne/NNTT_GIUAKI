package trungtamngoaingu.hcmute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "Result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", foreignKey = @ForeignKey(name = "FK_Result_Student"))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", foreignKey = @ForeignKey(name = "FK_Result_Class"))
    private Class classEntity;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(length = 10)
    private String grade;

    @Column(columnDefinition = "TEXT")
    private String comment;
}

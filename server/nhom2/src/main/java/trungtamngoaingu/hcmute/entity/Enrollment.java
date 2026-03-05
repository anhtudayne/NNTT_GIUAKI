package trungtamngoaingu.hcmute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Enrollment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", foreignKey = @ForeignKey(name = "FK_Enrollment_Student"))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", foreignKey = @ForeignKey(name = "FK_Enrollment_Class"))
    private Class classEntity;

    @Column
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Registered', 'Studying', 'Dropped', 'Completed') DEFAULT 'Registered'")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Pass', 'Fail', 'Pending') DEFAULT 'Pending'")
    private Result result;

    public enum Status {
        Registered, Studying, Dropped, Completed
    }

    public enum Result {
        Pass, Fail, Pending
    }
}

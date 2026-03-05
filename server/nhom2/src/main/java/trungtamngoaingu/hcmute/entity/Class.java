package trungtamngoaingu.hcmute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Class")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer classId;

    @Column(nullable = false, length = 100)
    private String className;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseID", foreignKey = @ForeignKey(name = "FK_Class_Course"))
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TeacherID", foreignKey = @ForeignKey(name = "FK_Class_Teacher"))
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomID", foreignKey = @ForeignKey(name = "FK_Class_Room"))
    private Room room;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private Integer maxStudent;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Pending', 'Ongoing', 'Completed', 'Canceled') DEFAULT 'Pending'")
    private Status status;

    public enum Status {
        Pending, Ongoing, Completed, Canceled
    }
}

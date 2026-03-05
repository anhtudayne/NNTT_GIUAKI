package trungtamngoaingu.hcmute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Teacher")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teacherId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 100)
    private String specialty;

    @Column
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Active', 'OnLeave', 'Inactive') DEFAULT 'Active'")
    private Status status;

    public enum Status {
        Active, OnLeave, Inactive
    }
}

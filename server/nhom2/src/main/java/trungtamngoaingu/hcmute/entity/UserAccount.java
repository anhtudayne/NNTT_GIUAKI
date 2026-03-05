package trungtamngoaingu.hcmute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UserAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private Integer relatedId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Active', 'Locked') DEFAULT 'Active'")
    private Status status;

    public enum Role {
        Admin, Teacher, Student, Staff
    }

    public enum Status {
        Active, Locked
    }
}

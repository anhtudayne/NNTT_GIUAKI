package com.languagecenter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "Students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(length = 10)
    private String gender;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @Column(length = 20)
    private String status; // Active, Inactive

    // Optional constructor for easy testing
    public Student(String fullName, String email, String status) {
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.registrationDate = new Date();
    }
}

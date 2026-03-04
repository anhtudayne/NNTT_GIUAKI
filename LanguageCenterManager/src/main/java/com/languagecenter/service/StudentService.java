package com.languagecenter.service;

import com.languagecenter.model.Student;
import com.languagecenter.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

public class StudentService {
    private final StudentRepository repository;

    public StudentService() {
        this.repository = new StudentRepository();
    }

    public void initData() {
        repository.initSampleData();
    }

    // Lấy tất cả
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    // Lọc sinh viên theo trạng thái dùng Stream API
    public List<Student> getActiveStudents() {
        return repository.findAll().stream()
                .filter(student -> "Active".equalsIgnoreCase(student.getStatus()))
                .collect(Collectors.toList());
    }

    // Thêm mới
    public void addStudent(Student student) {
        repository.save(student);
    }
}

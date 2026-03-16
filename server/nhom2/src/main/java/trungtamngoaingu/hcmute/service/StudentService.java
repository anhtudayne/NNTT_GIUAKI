package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Student;
import trungtamngoaingu.hcmute.repository.StudentRepository;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.myGetAll();
    }

    public Page<Student> getStudentsPaged(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    // Lấy 1 sinh viên theo ID
    public Optional<Student> getStudentById(Integer id) {
        return studentRepository.findById(id);
    }

    // Tạo mới sinh viên
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    // Cập nhật sinh viên
    public Student updateStudent(Integer id, Student student) {
        if (studentRepository.existsById(id)) {
            student.setStudentId(id);
            return studentRepository.save(student);
        }
        return null;
    }

    public void deleteStudent(Integer id) {
        studentRepository.deleteById(id);
    }

    public List<Student> searchStudentsByName(String name) {
        return studentRepository.searchByName(name);
    }
    // Tìm kiếm sinh viên theo tên

    public List<Student> getActiveStudents() {
        return studentRepository.findByStatus(Student.Status.Active);
    }
}

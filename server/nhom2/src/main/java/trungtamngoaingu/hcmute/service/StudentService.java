package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    // public Optional<Student> getStudentById(Integer id) {
    //     return studentRepository.findById(id);
    // }
    // Lấy 1 sinh viên theo ID
    public Optional<Student> getStudentById(Integer id) {
        return studentRepository.myGetAll().stream()
                .filter(s -> s.getStudentId().equals(id))
                .findFirst();
    }

    // Tạo mới sinh viên
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    // public Student updateStudent(Integer id, Student student) {
    //     if (studentRepository.existsById(id)) {
    //         student.setStudentId(id);
    //         return studentRepository.save(student);
    //     }
    //     return null;
    // }
    // Cập nhật sinh viên
    public Student updateStudent(Integer id, Student student) {
        // Kiểm tra sự tồn tại bằng stream
        boolean exists = studentRepository.myGetAll().stream()
                .anyMatch(s -> s.getStudentId().equals(id));

        if (exists) {
            student.setStudentId(id);
            return studentRepository.save(student);
        }
        return null;
    }

    // public void deleteStudent(Integer id) {
    //     studentRepository.deleteById(id);
    // }
    // Xóa sinh viên (Logic lọc để tìm đối tượng trước khi xóa)
    public void deleteStudent(Integer id) {
        studentRepository.myGetAll().stream()
                .filter(s -> s.getStudentId().equals(id))
                .findFirst()
                .ifPresent(s -> studentRepository.deleteById(s.getStudentId()));
    }

    public List<Student> searchStudentsByName(String name) {
        return studentRepository.searchByName(name);
    }
    // Tìm kiếm sinh viên theo tên
}

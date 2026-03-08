package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Class;
import trungtamngoaingu.hcmute.repository.ClassRepository;
import trungtamngoaingu.hcmute.repository.EnrollmentRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassService {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Class> getAllClasses() {
        return classRepository.myGetAll();
    }

    // 1. Lấy lớp học theo ID bằng Stream
    public Optional<Class> getClassById(Integer id) {
        return classRepository.myGetAll().stream()
                .filter(c -> c.getClassId().equals(id))
                .findFirst();
    }

    // 2. Tạo mới lớp học (vẫn dùng phương thức save của Repository)
    public Class createClass(Class clazz) {
        return classRepository.save(clazz);
    }

    // 3. Cập nhật lớp học bằng cách kiểm tra tồn tại qua Stream anyMatch
    public Class updateClass(Integer id, Class clazz) {
        boolean exists = classRepository.myGetAll().stream()
                .anyMatch(c -> c.getClassId().equals(id));

        if (exists) {
            clazz.setClassId(id);
            return classRepository.save(clazz);
        }
        return null;
    }

    // 4. Xóa lớp học dựa trên kết quả lọc của Stream
    public void deleteClass(Integer id) {
        classRepository.myGetAll().stream()
                .filter(c -> c.getClassId().equals(id))
                .findFirst()
                .ifPresent(c -> classRepository.deleteById(c.getClassId()));
    }

    public List<Class> searchClassesByName(String name) {
        return classRepository.searchByName(name);
    }

    // 5. Lấy danh sách các lớp học mà 1 học sinh đã hoặc đang tham gia
    public List<Class> getClassesByStudentId(Integer studentId) {
        return enrollmentRepository.myGetAll().stream()
                .filter(enrollment -> enrollment.getStudent() != null 
                        && enrollment.getStudent().getStudentId().equals(studentId))
                .map(enrollment -> enrollment.getClassEntity())
                .collect(Collectors.toList());
    }

    // 6. Lấy danh sách các lớp học mà 1 giáo viên đang dạy
    public List<Class> getClassesByTeacherId(Integer teacherId) {
        return classRepository.myGetAll().stream()
                .filter(c -> c.getTeacher() != null 
                        && c.getTeacher().getTeacherId().equals(teacherId))
                .collect(Collectors.toList());
    }
}

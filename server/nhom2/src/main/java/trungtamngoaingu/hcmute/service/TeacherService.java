package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Teacher;
import trungtamngoaingu.hcmute.repository.TeacherRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.myGetAll();
    }

    public Optional<Teacher> getTeacherById(Integer id) {
        return teacherRepository.findById(id);
    }

    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Integer id, Teacher teacher) {
        if (teacherRepository.existsById(id)) {
            teacher.setTeacherId(id);
            return teacherRepository.save(teacher);
        }
        return null;
    }

    public void deleteTeacher(Integer id) {
        teacherRepository.deleteById(id);
    }

    // ===================================================================================
    // NÂNG CAO: Ứng dụng Java 8 Stream API (Lambdas) để filter dữ liệu dưới bộ nhớ (RAM)
    // ===================================================================================

    /**
     * Dùng Stream API để tìm giảng viên theo Tên (FullName)
     * Giải thích:
     * - allTeachers.stream(): Tạo một luồng tuần tự (Sequential Stream) đi qua mọi Giáo viên.
     * - .filter(): Lambda Expression nhận t (Teacher) và trả về true nếu Tên có chứa từ khóa (query). 
     * - .toList(): Trả về ArrayList chứa mọi Object được đánh giá true bởi bộ giải filter.
     */
    public List<Teacher> searchTeachersByName(String keyword) {
        List<Teacher> allTeachers = teacherRepository.findAll();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allTeachers;
        }
        String query = keyword.toLowerCase().trim();

        return allTeachers.stream()
                .filter(t -> t.getFullName() != null && t.getFullName().toLowerCase().contains(query))
                .toList(); // java 16+ thay cho collect(Collectors.toList())
    }

    /**
     * Dùng Stream API để lọc danh sách giảng viên đang làm việc (Active)
     * Giải thích:
     * - Pipeline Data sẽ đi từ collection thông qua filter để so sánh hằng số Enum.
     * - Đây là biểu mẫu chuẩn mực cho Functional Programming tại Java nhằm trích xuất Sublist tốc độ cao trên RAM.
     */
    public List<Teacher> getActiveTeachers() {
        return teacherRepository.findAll().stream()
                .filter(t -> t.getStatus() == Teacher.Status.Active)
                .toList();
    }

    /**
     * Dùng Stream API để tìm các giảng viên dạy một chuyên môn cụ thể (VD: IELTS)
     */
    public List<Teacher> getTeachersBySpecialty(String specialty) {
        List<Teacher> allTeachers = teacherRepository.findAll();
        if (specialty == null || specialty.trim().isEmpty()) {
            return allTeachers;
        }
        String query = specialty.toLowerCase().trim();
        
        return allTeachers.stream()
                .filter(t -> t.getSpecialty() != null && t.getSpecialty().toLowerCase().contains(query))
                .toList();
    }
}

package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Teacher> getTeachersPaged(Pageable pageable) {
        return teacherRepository.findAll(pageable);
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
        if (keyword == null || keyword.trim().isEmpty()) {
            // Trường hợp không có từ khóa, chỉ cần trả về toàn bộ danh sách tối ưu sẵn
            return teacherRepository.myGetAll();
        }
        String query = keyword.trim();
        // Đẩy điều kiện tìm kiếm xuống database (LIKE, ignore case)
        return teacherRepository.findByFullNameContainingIgnoreCase(query);
    }

    /**
     * Dùng Stream API để lọc danh sách giảng viên đang làm việc (Active)
     * Giải thích:
     * - Pipeline Data sẽ đi từ collection thông qua filter để so sánh hằng số Enum.
     * - Đây là biểu mẫu chuẩn mực cho Functional Programming tại Java nhằm trích xuất Sublist tốc độ cao trên RAM.
     */
    public List<Teacher> getActiveTeachers() {
        // Lọc trực tiếp trên database theo Status
        return teacherRepository.findByStatus(Teacher.Status.Active);
    }

    /**
     * Dùng Stream API để tìm các giảng viên dạy một chuyên môn cụ thể (VD: IELTS)
     */
    public List<Teacher> getTeachersBySpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            return teacherRepository.myGetAll();
        }
        String query = specialty.trim();

        return teacherRepository.findBySpecialtyContainingIgnoreCase(query);
    }
}

package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Enrollment;
import trungtamngoaingu.hcmute.repository.EnrollmentRepository;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.myGetAll();
    }

    // 1. Lấy thông tin đăng ký theo ID bằng Stream
    public Optional<Enrollment> getEnrollmentById(Integer id) {
        return enrollmentRepository.myGetAll().stream()
                .filter(e -> e.getEnrollmentId().equals(id))
                .findFirst();
    }

    // 2. Tạo mới đăng ký (vẫn gọi save để ghi xuống Database)
    public Enrollment createEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    // 3. Cập nhật đăng ký bằng cách kiểm tra tồn tại qua Stream
    public Enrollment updateEnrollment(Integer id, Enrollment enrollment) {
        boolean exists = enrollmentRepository.myGetAll().stream()
                .anyMatch(e -> e.getEnrollmentId().equals(id));

        if (exists) {
            enrollment.setEnrollmentId(id);
            return enrollmentRepository.save(enrollment);
        }
        return null;
    }

    // 4. Xóa đăng ký sau khi xác nhận ID tồn tại trong danh sách tổng
    public void deleteEnrollment(Integer id) {
        enrollmentRepository.myGetAll().stream()
                .filter(e -> e.getEnrollmentId().equals(id))
                .findFirst()
                .ifPresent(e -> enrollmentRepository.deleteById(e.getEnrollmentId()));
    }

    // public Optional<Enrollment> getEnrollmentById(Integer id) {
    //     return enrollmentRepository.findById(id);
    // }

    // public Enrollment createEnrollment(Enrollment enrollment) {
    //     return enrollmentRepository.save(enrollment);
    // }

    // public Enrollment updateEnrollment(Integer id, Enrollment enrollment) {
    //     if (enrollmentRepository.existsById(id)) {
    //         enrollment.setEnrollmentId(id);
    //         return enrollmentRepository.save(enrollment);
    //     }
    //     return null;
    // }

    // public void deleteEnrollment(Integer id) {
    //     enrollmentRepository.deleteById(id);
    // }
}

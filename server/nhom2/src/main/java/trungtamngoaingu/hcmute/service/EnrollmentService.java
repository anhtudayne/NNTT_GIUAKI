package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Enrollment> getEnrollmentsPaged(Pageable pageable) {
        return enrollmentRepository.findAll(pageable);
    }

    public Optional<Enrollment> getEnrollmentById(Integer id) {
        return enrollmentRepository.findById(id);
    }

    // 2. Tạo mới đăng ký (vẫn gọi save để ghi xuống Database)
    public Enrollment createEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateEnrollment(Integer id, Enrollment enrollment) {
        if (enrollmentRepository.existsById(id)) {
            enrollment.setEnrollmentId(id);
            return enrollmentRepository.save(enrollment);
        }
        return null;
    }

    public void deleteEnrollment(Integer id) {
        enrollmentRepository.deleteById(id);
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

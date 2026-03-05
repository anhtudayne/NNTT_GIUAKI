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

    public Optional<Enrollment> getEnrollmentById(Integer id) {
        return enrollmentRepository.findById(id);
    }

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
}

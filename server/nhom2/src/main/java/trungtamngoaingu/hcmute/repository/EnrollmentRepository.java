package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Enrollment;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
  @Query("SELECT e FROM Enrollment e")
  List<Enrollment> myGetAll();

  // Tối ưu cho AttendanceService.getStudentsByClassId
  List<Enrollment> findByClassEntity_ClassIdAndStatusIn(Integer classId, List<Enrollment.Status> statuses);

  // Tối ưu cho ClassService.getClassesByStudentId
  List<Enrollment> findByStudent_StudentId(Integer studentId);
}

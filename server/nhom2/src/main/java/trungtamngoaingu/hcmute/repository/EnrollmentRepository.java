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
}

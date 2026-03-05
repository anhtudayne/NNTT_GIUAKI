package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Attendance;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
  // Sử dụng JPQL (truy vấn trên Entity Attendance)
  @Query("SELECT a FROM Attendance a")
  List<Attendance> myGetAll();
}

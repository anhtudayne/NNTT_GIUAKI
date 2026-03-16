package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Teacher;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
  @Query("SELECT t FROM Teacher t")
  List<Teacher> myGetAll();

  // Tối ưu: đẩy filter xuống database thay vì filter toàn bộ trên RAM
  List<Teacher> findByFullNameContainingIgnoreCase(String fullName);

  List<Teacher> findByStatus(Teacher.Status status);

  List<Teacher> findBySpecialtyContainingIgnoreCase(String specialty);
}

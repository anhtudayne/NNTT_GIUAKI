package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Class;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer> {
  @Query("SELECT cl FROM Class cl")
  List<Class> myGetAll();

  @Query("SELECT cl FROM Class cl WHERE LOWER(cl.className) LIKE LOWER(CONCAT('%', :name, '%'))")
  List<Class> searchByName(@Param("name") String name);

  // Tối ưu: truy vấn theo ID và Teacher trực tiếp trên database
  Optional<Class> findByClassId(Integer classId);

  List<Class> findByTeacher_TeacherId(Integer teacherId);

  // Đếm số lượng theo trạng thái cho Dashboard
  long countByStatus(Class.Status status);

  // Tối ưu: query lớp theo danh sách CourseID và Status cho PlacementTest recommendations
  List<Class> findByCourse_CourseIdInAndStatusIn(List<Integer> courseIds, List<Class.Status> statuses);
}

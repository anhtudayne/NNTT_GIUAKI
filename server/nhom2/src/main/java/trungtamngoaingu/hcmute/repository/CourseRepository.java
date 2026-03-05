package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Course;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
  @Query("SELECT co FROM Course co")
  List<Course> myGetAll();
}

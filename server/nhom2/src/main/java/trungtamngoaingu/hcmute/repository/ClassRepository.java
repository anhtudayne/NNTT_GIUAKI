package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Class;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer> {
  @Query("SELECT cl FROM Class cl")
  List<Class> myGetAll();

  @Query("SELECT cl FROM Class cl WHERE LOWER(cl.className) LIKE LOWER(CONCAT('%', :name, '%'))")
  List<Class> searchByName(@Param("name") String name);
}

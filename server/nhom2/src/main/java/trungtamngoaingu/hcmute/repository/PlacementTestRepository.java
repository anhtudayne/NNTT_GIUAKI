package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.PlacementTest;
import java.util.List;

@Repository
public interface PlacementTestRepository extends JpaRepository<PlacementTest, Integer> {
  @Query("SELECT pt FROM PlacementTest pt")
  List<PlacementTest> myGetAll();
}

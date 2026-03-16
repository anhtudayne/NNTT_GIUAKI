package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Result;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {
  @Query("SELECT r FROM Result r")
  List<Result> myGetAll();

  // Tính điểm trung bình trực tiếp trên database
  @Query("SELECT AVG(r.score) FROM Result r WHERE r.score IS NOT NULL")
  Double findAverageScore();
}

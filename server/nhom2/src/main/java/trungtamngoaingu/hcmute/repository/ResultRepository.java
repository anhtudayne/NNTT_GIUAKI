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
}

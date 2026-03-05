package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Branch;
import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
  @Query("SELECT b FROM Branch b")
  List<Branch> myGetAll();
}

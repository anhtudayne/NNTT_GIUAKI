package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Staff;
import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
  @Query("SELECT st FROM Staff st")
  List<Staff> myGetAll();
}
